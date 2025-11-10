package org.firstinspires.ftc.learnbot.subsystems;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierPoint;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.Subsystem;
import com.technototes.library.util.Alliance;
import com.technototes.library.util.MathUtils;
import org.firstinspires.ftc.learnbot.DrivingConstants;
import org.firstinspires.ftc.learnbot.commands.PedroPathCommand;

public class PedroDrivebaseSubsystem implements Subsystem, Loggable {

    // Encapsulated to allow easy "previous driving style" tracking
    private static class DrivingStyle {

        public DrivingStyle() {
            perspective = DrivingPerspective.FieldCentric;
            rotation = RotationalMode.Free;
            translation = TranslationalMode.Free;
            rotationSpeed = DrivingConstants.Control.NORMAL_TURN;
            translationSpeed = DrivingConstants.Control.NORMAL_SPEED;
        }

        public DrivingPerspective perspective;
        public RotationalMode rotation;
        public TranslationalMode translation;
        public double rotationSpeed;
        public double translationSpeed;
    }

    // This concerns how the bot is rotating (or not)
    public enum RotationalMode {
        Free,
        Snap,
        Hold, // Hold the current heading
        Tangential_BORKED, // Aim toward the translational direction (NOT WORKING)
        Vision, // Use Vision to find the target & aim toward it
        Target_NYI, // The controller is used to specify a desired heading
        None,
    }

    // This concerns how the bot is moving around the field
    public enum TranslationalMode {
        Free,
        Square,
        Hold,
        Vision_NYI, // Drive toward the target using vision
        Target_NYI, // Drive toward an external target (that is movable using the controller)
        None,
    }

    public enum DrivingPerspective {
        RobotCentric,
        FieldCentric,
    }

    // The PedroPath follower, to let us actually make the bot move:
    public Follower follower;
    // The vision subsystem, for vision-based driving stuff
    public VisionSubsystem vision;

    // The direction of the 3 axes for manual control: Should be updated by a joystick command
    public double strafe, forward, rotation;

    // TODO: Make this do something...PID related, maybe?
    public double rotationTransform() {
        return Math.cbrt(rotation) * driveStyle.rotationSpeed;
    }

    // The offset heading for field-relative controls
    double headingOffsetRadians;
    // used to keep the directions straight
    Alliance alliance;

    // Used to keep track of the previous drive style when using the "StayPut" operation
    private DrivingStyle prevDriveStyle;

    public double[] snapRadians;

    DrivingStyle driveStyle;

    Pose holdPose;
    boolean started;

    public RotationalMode getRotationalMode() {
        return driveStyle.rotation;
    }

    public TranslationalMode getTranslationalMode() {
        return driveStyle.translation;
    }

    public DrivingPerspective getPerspective() {
        return driveStyle.perspective;
    }

    private double baseHeadingOffset() {
        return alliance == Alliance.BLUE ? Math.PI : 0;
    }

    private double getHeadingOffsetRadians() {
        return driveStyle.perspective == DrivingPerspective.FieldCentric ? headingOffsetRadians : 0;
    }

    public PedroDrivebaseSubsystem(Follower f, VisionSubsystem viz, Alliance all) {
        started = false;
        follower = f;
        vision = viz;
        alliance = all;
        headingOffsetRadians = baseHeadingOffset();
        holdPose = null;
        forward = 0;
        strafe = 0;
        rotation = 0;
        driveStyle = new DrivingStyle();
        // You need the 360 there for "wrap-around" to work properly
        snapRadians = new double[] { 0, Math.PI / 2, Math.PI, (Math.PI * 3) / 2, 2 * Math.PI };
        SetNormalSpeed();
        SetFieldCentricDriveMode();
        EnableFreeDriving();
    }

    public PedroDrivebaseSubsystem(Follower f, Alliance all) {
        this(f, null, all);
    }

    public PedroDrivebaseSubsystem(Follower f, VisionSubsystem viz) {
        this(f, viz, Alliance.NONE);
    }

    public PedroDrivebaseSubsystem(Follower f) {
        this(f, null, Alliance.NONE);
    }

    // Command to start autonomous driving)
    // Command to start teleop driving
    public void StartTele() {
        started = true;
        follower.startTeleOpDrive();
        follower.update();
    }

    // Methods to bind to buttons (Commands)
    public void ResetGyro() {
        headingOffsetRadians = MathUtils.normalizeRadians(
            follower.getHeading() + baseHeadingOffset()
        );
    }

    public void SetSnailSpeed() {
        follower.setMaxPowerScaling(DrivingConstants.Control.SNAIL_SPEED);
        driveStyle.translationSpeed = DrivingConstants.Control.SNAIL_SPEED;
        driveStyle.rotationSpeed = DrivingConstants.Control.SNAIL_TURN;
    }

    public void SetNormalSpeed() {
        follower.setMaxPowerScaling(DrivingConstants.Control.NORMAL_SPEED);
        driveStyle.translationSpeed = DrivingConstants.Control.NORMAL_SPEED;
        driveStyle.rotationSpeed = DrivingConstants.Control.NORMAL_TURN;
    }

    public void SetTurboSpeed() {
        follower.setMaxPowerScaling(DrivingConstants.Control.TURBO_SPEED);
        driveStyle.translationSpeed = DrivingConstants.Control.TURBO_SPEED;
        driveStyle.rotationSpeed = DrivingConstants.Control.TURBO_TURN;
    }

    private void switchDrivingMode(TranslationalMode x, RotationalMode r) {
        TranslationalMode px = driveStyle.translation;
        RotationalMode pr = driveStyle.rotation;

        if (px == x && pr == r) {
            return;
        }
        driveStyle.translation = x;
        driveStyle.rotation = r;
        if (px == TranslationalMode.Hold && x != TranslationalMode.Hold) {
            // If we're currently holding a position, stop doing so
            StartTele();
            holdPose = null;
        } else if (px != TranslationalMode.Hold && x == TranslationalMode.Hold) {
            // If we've switched *to* holding a pose, start the follower
            holdPose = follower.getPose();
            follower.holdPoint(new BezierPoint(holdPose), holdPose.getHeading(), false);
        }
    }

    public void EnableFreeDriving() {
        switchDrivingMode(TranslationalMode.Free, RotationalMode.Free);
    }

    public void EnableStraightDriving() {
        switchDrivingMode(TranslationalMode.Square, RotationalMode.Free);
    }

    public void EnableSnapDriving() {
        switchDrivingMode(getTranslationalMode(), RotationalMode.Snap);
    }

    // The list of rotation points (in degrees) you want to snap to.
    public void EnableSnapDriving(double... degrees) {
        snapRadians = new double[degrees.length + 1];
        for (int i = 0; i < degrees.length; i++) {
            snapRadians[i] = Math.toRadians(degrees[i]);
        }
        // For wrap-around to work properly, we'll add the first location, but past the wrap-around
        // point of the circle. So, if you want to snap to 15 and 170, we'll append 375 so that 344
        // will wind up going to 375 (which then normalizes back to 15). Neat, huh?
        snapRadians[degrees.length] = snapRadians[0] + Math.PI * 2;
        switchDrivingMode(getTranslationalMode(), RotationalMode.Snap);
    }

    public void EnableSquareDriving() {
        switchDrivingMode(TranslationalMode.Square, RotationalMode.Snap);
    }

    public void EnableTangentialDriving() {
        switchDrivingMode(getTranslationalMode(), RotationalMode.Tangential_BORKED);
    }

    public void HoldCurrentPosition() {
        switchDrivingMode(TranslationalMode.Hold, RotationalMode.Hold);
    }

    public void EnableVisionDriving() {
        switchDrivingMode(getTranslationalMode(), RotationalMode.Vision);
    }

    public void SetRobotCentricDriveMode() {
        driveStyle.perspective = DrivingPerspective.RobotCentric;
    }

    public void SetFieldCentricDriveMode() {
        driveStyle.perspective = DrivingPerspective.FieldCentric;
    }

    // This, when well tuned, should allow the driver to ignore minor bot-bumps and the like.
    public void SetTargetBasedRotation() {
        driveStyle.rotation = RotationalMode.Target_NYI;
    }

    // This, when well tuned, should allow the programmers to ignore frictional differences between
    // wheels on the drivebase...
    public void SetTargetBasedTranslation() {
        driveStyle.translation = TranslationalMode.Target_NYI;
    }

    // Some just slightly more complex commands:
    public void StayPut() {
        if (prevDriveStyle == null) {
            prevDriveStyle = driveStyle;
        }
        driveStyle = new DrivingStyle();
        HoldCurrentPosition();
        SetTurboSpeed();
    }

    public void ResumeDriving() {
        if (prevDriveStyle == null) {
            EnableFreeDriving();
            SetNormalSpeed();
        } else {
            driveStyle = prevDriveStyle;
            prevDriveStyle = null;
        }
    }

    public void RegisterJoystickRead(double f, double s, double r) {
        this.strafe = s;
        this.forward = f;
        this.rotation = r;
    }

    @Override
    public void periodic() {
        // If subsystem is busy it is running a path, just ignore the stick.
        ShowDriveInfo(driveStyle, follower);
        if (follower == null) {
            return;
        }
        if (follower.isBusy()) {
            follower.update();
            drvVec = "busy";
            return;
        }
        if (!started) {
            return;
        }

        // Recall that pushing a stick forward goes *negative* and pushing a stick to the left
        // goes *negative* as well (both are opposite Pedro's coordinate system)
        if (driveStyle.translation == TranslationalMode.Square) {
            // for square driving, we only use one of the two translation directions:
            if (Math.abs(forward) > Math.abs(strafe)) {
                strafe = 0;
            } else {
                forward = 0;
            }
        }
        double rot = getRotation();
        ShowDriveVectors(forward, strafe, rot, getHeadingOffsetRadians());
        follower.setTeleOpDrive(
            forward,
            strafe,
            rot,
            driveStyle.perspective == DrivingPerspective.RobotCentric,
            getHeadingOffsetRadians()
        );
        follower.update();

        // target based driving:
        // The idea is that we have a location along the edge of the field that indicates which
        // direction the bot should be move, along with a heading indicating the direction it
        // should be facing.
        // Why?
        // Because then even if the bot is light and gets a little skippy, or if the thing isn't
        // well balanced, the driver doesn't have to constantly correct for the lost grip. The
        // drivebase will compensate automatically. The hypothesis (not yet tested at all) is that
        // different friction between motors, motor speeds, etc... will be less frustrating to deal
        // with, as well..
    }

    double getRotation() {
        // Negative, because pushing left is negative, but that is a positive change in Pedro's
        // coordinate system.
        double curHeading = follower.getHeading() - getHeadingOffsetRadians();
        double targetHeading = 0;
        switch (driveStyle.rotation) {
            case Free:
                return rotationTransform();
            case Snap:
                // Angle-focused driving styles override target-based driving mode
                targetHeading = MathUtils.closestTo(curHeading, snapRadians);
                break;
            case Hold:
                // Hold the current heading
                if (driveStyle.translation != TranslationalMode.Hold) {
                    targetHeading = holdPose.getHeading() - getHeadingOffsetRadians();
                }
                break;
            case Tangential_BORKED: // Aim toward the translational direction (NOT WORKING)
                // Tangential is an angle-focused driving style, but the heading
                // is strictly in the direction of the stick. Logically, this is an attempt to
                // eliminate "actual" strafing: The robot should be oriented to drive forward in the
                // direction of the stick
                if (Math.abs(strafe) > 0 || Math.abs(forward) > 0) {
                    targetHeading = MathUtils.posNegRadians(Math.atan2(strafe, forward));
                    curHeading = MathUtils.posNegRadians(curHeading);
                } else {
                    return 0;
                }
                break;
            case Vision: // Use Vision to find the target & aim toward it
                VisionSubsystem.TargetInfo visResult = vision == null
                    ? null
                    : vision.getCurResult();
                if (visResult != null) {
                    // No idea if this is correct
                    targetHeading = curHeading + Math.toRadians(visResult.x);
                } else {
                    return rotationTransform();
                }
                break;
            case Target_NYI: // The controller is used to specify a desired heading
                return rotationTransform();
            default:
                return 0;
        }
        // TODO: Use the Pedro heading PIDF to get this value?
        return (Math.clamp(targetHeading - curHeading, -1, 1) * driveStyle.rotationSpeed);
    }

    public PedroPathCommand MakePathCommand(PathChain p) {
        return new PedroPathCommand(follower, p);
    }

    // Everything below here is just for displaying/diagnostics

    @Log(name = "DrvMode")
    public static String drvMode = "";

    @Log(name = "DrvVec")
    public static String drvVec = "";

    @Log(name = "Pose")
    public static String drvLoc = "";

    private static void ShowDriveInfo(DrivingStyle driveStyle, Follower f) {
        switch (driveStyle.rotation) {
            case Free:
                drvMode = "rFree";
                break;
            case Snap:
                drvMode = "rSnap";
                break;
            case Hold:
                drvMode = "rHold";
                break;
            case Tangential_BORKED:
                drvMode = "rTangent";
                break;
            case Vision:
                drvMode = "rVision[NYI]";
                break;
            default:
                drvMode = "rUnknown";
                break;
        }
        switch (driveStyle.translation) {
            case Free:
                drvMode += "xFree";
                break;
            case Square:
                drvMode += "xSquare";
                break;
            case Hold:
                drvMode += "xHold";
                break;
            case Vision_NYI:
                drvMode += "xVision[NYI]";
                break;
            default:
                drvMode += "xUnknown";
                break;
        }
        switch (driveStyle.perspective) {
            case RobotCentric:
                drvMode += ":Bot-Centric";
                break;
            case FieldCentric:
                drvMode += ":Field-Centric";
                break;
            default:
                drvMode += ":[Unknown]";
                break;
        }
        drvMode += String.format(" Max:%.2f", driveStyle.translationSpeed);
        Pose curPose = f.getPose();
        drvLoc = String.format(
            "X:%.2f Y:%.2f H:%.1f°",
            curPose.getX(),
            curPose.getY(),
            Math.toDegrees(curPose.getHeading())
        );
    }

    private static void ShowDriveVectors(
        double fwdVal,
        double strafeVal,
        double rot,
        double offset
    ) {
        drvVec = String.format(
            "f %.2f s %.2f r %.2f [%.1f°]",
            fwdVal,
            strafeVal,
            rot,
            Math.toDegrees(offset)
        );
    }
}
