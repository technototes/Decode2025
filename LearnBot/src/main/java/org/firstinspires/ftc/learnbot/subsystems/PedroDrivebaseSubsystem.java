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

/* Recall, the Pedro Path coordinate system:
                 [Refs/score table]
+-------------------------------------------------+
|(0,144)             ^ +y                (144,144)|
|                                                 |
|                    90 deg                       |
|                                                 |
|                                                 |
| <== -x 180 deg     (72,72)        0 deg +x ==>  |
|                                                 |
|                                                 |
| Red Drive Team                  Blue Drive Team |
|                    270 deg                      |
|(0,0)                v -y                 (144,0)|
+-------------------------------------------------+
                   [Audience]
 */

public class PedroDrivebaseSubsystem implements Subsystem, Loggable {

    // Encapsulated to allow easy "previous driving style" tracking
    private static class DrivingStyle {

        public DrivingStyle() {
            perspective = DrivingPerspective.FieldCentric;
            rotation = RotationMode.Free;
            translation = TranslationMode.Free;
            rotationSpeed = DrivingConstants.Control.NORMAL_TURN;
            translationSpeed = DrivingConstants.Control.NORMAL_SPEED;
        }

        public DrivingPerspective perspective;
        public RotationMode rotation;
        public TranslationMode translation;
        public double rotationSpeed;
        public double translationSpeed;
    }

    // This concerns how the bot is rotating (or not)
    public enum RotationMode {
        Free,
        Snap,
        Hold_NYI, // Hold the current heading
        Tangential, // Aim toward the translational direction (NOT WORKING)
        Vision, // Use Vision to find the target & aim toward it
        Target_NYI, // The controller is used to specify a desired heading
        None,
    }

    // This concerns how the bot is moving around the field
    public enum TranslationMode {
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

    // TODO: Make this do something PIDF related, because if you don't, this is *super* jiggly...
    public double rotationTransform(double r) {
        return Math.copySign(r * r * driveStyle.rotationSpeed, r);
    }

    // The offset heading for field-relative controls
    double headingOffsetRadians;
    double targetHeading;
    // used to keep the directions straight
    Alliance alliance;

    // Used to keep track of the previous drive style when using the "StayPut" operation
    private DrivingStyle prevDriveStyle;

    public double[] snapRadians;

    DrivingStyle driveStyle;

    Pose holdPose;
    boolean started;

    public RotationMode getRotationMode() {
        return driveStyle.rotation;
    }

    public void setRotationalMode(RotationMode r) {
        driveStyle.rotation = r;
    }

    public TranslationMode getTranslationMode() {
        return driveStyle.translation;
    }

    public void setTranslationMode(TranslationMode t) {
        driveStyle.translation = t;
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
        targetHeading = 0;
        driveStyle = new DrivingStyle();
        // You need the final point there for "wrap-around" to work properly
        snapRadians = new double[] { -Math.PI, -Math.PI / 2, 0, Math.PI / 2, Math.PI };
        SetFieldCentricMode();
        SetFreeDriving();
        SetNormalSpeed();
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

    private void switchDrivingMode(TranslationMode x, RotationMode r) {
        TranslationMode px = driveStyle.translation;
        RotationMode pr = driveStyle.rotation;

        if (px == x && pr == r) {
            return;
        }
        driveStyle.translation = x;
        driveStyle.rotation = r;
        if (px == TranslationMode.Hold && x != TranslationMode.Hold) {
            // If we're currently holding a position, stop doing so
            StartTele();
        } else if (px != TranslationMode.Hold && x == TranslationMode.Hold) {
            // If we've switched *to* holding a pose, start the follower
            holdPose = follower.getPose();
            follower.holdPoint(new BezierPoint(holdPose), holdPose.getHeading(), false);
        } else if (pr != RotationMode.Hold_NYI && r == RotationMode.Hold_NYI) {
            // If we're transitioning to a Rotational hold, just set the pos in the holdPose
            holdPose = follower.getPose();
        }
    }

    public void SetFreeDriving() {
        switchDrivingMode(TranslationMode.Free, RotationMode.Free);
    }

    public void SetFreeRotation() {
        switchDrivingMode(getTranslationMode(), RotationMode.Free);
    }

    // The list of rotation points (in degrees) you want to snap to,
    // in the range of 0 to 359.9999999 degrees
    public void SetSnapRotation(double... degrees) {
        snapRadians = new double[degrees.length + 1];
        double lowest = Math.PI * 2;
        for (int i = 0; i < degrees.length; i++) {
            snapRadians[i] = MathUtils.posNegRadians(Math.toRadians(degrees[i]));
            if (snapRadians[i] < lowest) {
                lowest = snapRadians[i];
            }
        }
        // For wrap-around to work properly, we'll add the first location, but past the wrap-around
        // point of the circle. So, if you want to snap to 15 and -170, we'll append 190 so that 175
        // will wind up going to 190 (which then normalizes back to -170). Neat, huh?
        snapRadians[degrees.length] = lowest + Math.PI * 2;
        switchDrivingMode(getTranslationMode(), RotationMode.Snap);
    }

    public void SetSnapRotation() {
        switchDrivingMode(getTranslationMode(), RotationMode.Snap);
    }

    public void SetTangentRotation() {
        switchDrivingMode(getTranslationMode(), RotationMode.Tangential);
    }

    public void SetVisionRotation() {
        switchDrivingMode(getTranslationMode(), RotationMode.Vision);
    }

    public void SetHoldRotation() {
        switchDrivingMode(getTranslationMode(), RotationMode.Hold_NYI);
    }

    public void HoldCurrentPosition() {
        switchDrivingMode(TranslationMode.Hold, RotationMode.Hold_NYI);
    }

    public void SetFreeMotion() {
        switchDrivingMode(TranslationMode.Free, getRotationMode());
    }

    public void SetSquareMotion() {
        switchDrivingMode(TranslationMode.Square, getRotationMode());
    }

    public void SetVisionDriving() {
        // switchDrivingMode(TranslationalMode.Vision_NYI, RotationalMode.Vision);
    }

    public void SetRobotCentricMode() {
        driveStyle.perspective = DrivingPerspective.RobotCentric;
    }

    public void SetFieldCentricMode() {
        driveStyle.perspective = DrivingPerspective.FieldCentric;
    }

    // This, when well tuned, should allow the driver to ignore minor bot-bumps and the like.
    public void SetTargetBasedRotation() {
        driveStyle.rotation = RotationMode.Target_NYI;
    }

    // This, when well tuned, should allow the programmers to ignore frictional differences between
    // wheels on the drivebase...
    public void SetTargetBasedMotion() {
        driveStyle.translation = TranslationMode.Target_NYI;
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
            SetFreeDriving();
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
        ShowDriveInfo();
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

        if (driveStyle.translation == TranslationMode.Square) {
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
    }

    double getRotation() {
        // Negative, because pushing left is negative, but that is a positive change in Pedro's
        // coordinate system.
        double curHeading = MathUtils.posNegRadians(
            follower.getHeading() - getHeadingOffsetRadians()
        );
        switch (driveStyle.rotation) {
            case Target_NYI:
            // The controller is used to specify a desired heading: NYI
            // To implement this, we need to know a "degrees/sec" change for each speed.
            // From basic observations: slow: 90 deg/sec; normal: 180 deg/sec, fast: 300 deg/sec
            // Once we have that, we need to have a timer between updates, as well as a decent
            // PIDF for the heading, so that we can get to the desired location quickly
            // and accurately. If the rotation stick goes to zero, we probably just hold the
            // current location.
            case Free:
                return rotationTransform(rotation);
            case Snap:
                // Angle-focused driving styles override target-based driving mode
                targetHeading = MathUtils.closestTo(curHeading, snapRadians);
                break;
            case Hold_NYI:
                // Hold the current heading
                if (driveStyle.translation != TranslationMode.Hold && holdPose != null) {
                    targetHeading = MathUtils.posNegRadians(
                        MathUtils.posNegRadians(holdPose.getHeading()) - getHeadingOffsetRadians()
                    );
                } else {
                    // This is weird: We have a rotational hold, but not a translational hold, and
                    // no holdPose. This is probably an error
                    targetHeading = 0;
                }
                break;
            case Tangential: // Aim toward the translational direction (NOT WORKING)
                // Tangential is an angle-focused driving style, but the heading
                // is strictly in the direction of the stick. Logically, this is an attempt to
                // eliminate "actual" strafing: The robot should be oriented to drive forward in the
                // direction of the stick
                if (Math.abs(strafe) > 0 || Math.abs(forward) > 0) {
                    targetHeading = MathUtils.posNegRadians(Math.atan2(strafe, forward));
                } else {
                    return 0;
                }
                break;
            case Vision: // Use Vision to find the target & aim toward it
                VisionSubsystem.TargetInfo visResult = vision == null
                    ? null
                    : vision.getCurResult();
                if (visResult != null) {
                    targetHeading = MathUtils.posNegRadians(
                        curHeading + Math.toRadians(visResult.x)
                    );
                } else {
                    return rotationTransform(rotation);
                }
                break;
            default:
                return 0;
        }
        // TODO: Use the Pedro heading PIDF to get this value?
        return rotationTransform(
            Math.clamp(MathUtils.posNegRadians(targetHeading - curHeading) * 0.75, -1, 1)
        );
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

    private void ShowDriveInfo() {
        switch (driveStyle.rotation) {
            case Free:
                drvMode = "rFree";
                break;
            case Snap:
                drvMode = "rSnap";
                break;
            case Hold_NYI:
                drvMode = "rHold";
                break;
            case Tangential:
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
        Pose curPose = follower.getPose();
        drvLoc = String.format(
            "X:%.2f Y:%.2f H:%.1f° T:%.1f°",
            curPose.getX(),
            curPose.getY(),
            Math.toDegrees(curPose.getHeading()),
            Math.toDegrees(targetHeading)
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
