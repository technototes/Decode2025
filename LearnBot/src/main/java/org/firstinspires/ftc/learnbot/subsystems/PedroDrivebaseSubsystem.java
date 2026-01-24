package org.firstinspires.ftc.learnbot.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierPoint;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.Subsystem;
import com.technototes.library.util.Alliance;
import com.technototes.library.util.MathUtils;
import com.technototes.library.util.PIDFController;
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

@Configurable
public class PedroDrivebaseSubsystem implements Subsystem, Loggable {

    private static final double HalfPi = Math.PI * 0.5;
    private static final double TwoPi = Math.PI * 2;

    // Encapsulated to allow easy "previous driving style" tracking
    private static class DrivingStyle {

        public DrivingStyle() {
            perspective = DrivingPerspective.FieldCentric;
            rotation = RotationMode.Free;
            translation = TranslationMode.Free;
            rotationSpeed = DrivingConstants.Control.NORMAL_TURN;
            translationSpeed = DrivingConstants.Control.NORMAL_SPEED;
        }

        public DrivingStyle(DrivingStyle other) {
            perspective = other.perspective;
            rotation = other.rotation;
            translation = other.translation;
            rotationSpeed = other.rotationSpeed;
            translationSpeed = other.translationSpeed;
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
        Hold, // Hold the current heading
        Tangential, // Aim toward the translational direction
        Bidirectional, // Like tangential, but will turn to the closest tangential direction
        Vision, // Use Vision to find the target & aim toward it
        Target_NYI, // The controller is used to specify a desired heading
        None,
    }

    // This concerns how the bot is moving around the field
    public enum TranslationMode {
        Free,
        Square,
        Hold,
        Vision, // Drive toward the target using vision
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
    public TargetSubsystem vision;

    // The direction of the 3 axes for manual control: Should be updated by a joystick command
    public double strafe, forward, rotation;

    // These numbers seem a little weird, but the target value is *radians* and the output is
    // 'stick distance'.
    public static PIDFCoefficients pidf = new PIDFCoefficients(0.9, 0.3, 0.05, 0);
    // This is the limit above which we don't consider "error" for the turning PID controller.
    // 4.5 degree 'range' for the error to start accumulating
    public static double WIND_UP_LIMIT = HalfPi * 0.05;

    // These are here to let me change the PIDF on they fly, rather than forcing me to constantly
    // restart the opmode.
    private double P = 0;
    private double I = 0;
    private double D = 0;
    private double F = 0;

    private PIDFController turningPIDF = new PIDFController(pidf);
    private boolean turnPidStarted = false;

    public double rotationTransform(double r) {
        // This gives you more sensitivity at the low end:
        // .25^2 = .0625, .5^2 = .25, .75^2 = .5625, etc...
        return Math.copySign(r * r * driveStyle.rotationSpeed, r);
    }

    // The offset heading for field-relative controls
    double headingOffsetRadians;
    double targetHeading;
    double directedHeading; // used for Target-based rotation mode
    ElapsedTime lastReading; // also used for Target-based rotation mode
    double curHeading;

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

    public PedroDrivebaseSubsystem(Follower f, TargetSubsystem viz, Alliance all) {
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
        directedHeading = Double.NaN;
        lastReading = new ElapsedTime();
        // Snap to these angles
        // You need the final point there for "wrap-around" to work properly
        snapRadians = new double[] { -Math.PI, -HalfPi, 0, HalfPi, Math.PI };
        SetFieldCentricMode();
        SetFreeDriving();
        SetNormalSpeed();
    }

    public PedroDrivebaseSubsystem(Follower f, Alliance all) {
        this(f, null, all);
    }

    public PedroDrivebaseSubsystem(Follower f, TargetSubsystem viz) {
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
            // If we're going to stop holding a position, start Tele back up
            StartTele();
        } else if (px != TranslationMode.Hold && x == TranslationMode.Hold) {
            // If we've switched *to* holding a pose, start the follower
            holdPose = follower.getPose();
            follower.holdPoint(new BezierPoint(holdPose), holdPose.getHeading(), false);
        } else if (pr != RotationMode.Hold && driveStyle.rotation == RotationMode.Hold) {
            // If we're transitioning to a Rotational hold, just set the pos in the holdPose
            holdPose = follower.getPose();
        }
        if (r == RotationMode.Target_NYI) {
            directedHeading = Double.NaN;
        }
        turnPidStarted = false;
    }

    public void SetFreeDriving() {
        switchDrivingMode(TranslationMode.Free, RotationMode.Free);
    }

    public void SetFreeRotation() {
        switchDrivingMode(getTranslationMode(), RotationMode.Free);
    }

    // The list of rotation points (in degrees) you want to snap to
    public void SetSnapRotation(double... degrees) {
        snapRadians = new double[degrees.length + 1];
        double lowest = TwoPi;
        for (int i = 0; i < degrees.length; i++) {
            snapRadians[i] = MathUtils.posNegRadians(Math.toRadians(degrees[i]));
            if (snapRadians[i] < lowest) {
                lowest = snapRadians[i];
            }
        }
        // For wrap-around to work properly, we'll add the first location, but past the wrap-around
        // point of the circle. So, if you want to snap to 15 and -170, we'll append 190 so that 175
        // will wind up going to 190 (which then normalizes back to -170). Neat, huh?
        snapRadians[degrees.length] = lowest + TwoPi;
        switchDrivingMode(getTranslationMode(), RotationMode.Snap);
    }

    public void SetSnapRotation() {
        switchDrivingMode(getTranslationMode(), RotationMode.Snap);
    }

    public void SetTangentRotation() {
        switchDrivingMode(getTranslationMode(), RotationMode.Tangential);
    }

    public void SetBidirectionalRotation() {
        switchDrivingMode(getTranslationMode(), RotationMode.Bidirectional);
    }

    public void SetVisionRotation() {
        switchDrivingMode(getTranslationMode(), RotationMode.Vision);
    }

    public void SetHoldRotation() {
        switchDrivingMode(getTranslationMode(), RotationMode.Hold);
    }

    public void HoldCurrentPosition() {
        switchDrivingMode(TranslationMode.Hold, RotationMode.Hold);
    }

    public void SetFreeMotion() {
        switchDrivingMode(TranslationMode.Free, getRotationMode());
    }

    public void SetSquareMotion() {
        switchDrivingMode(TranslationMode.Square, getRotationMode());
    }

    public void SetVisionDriving() {
        prevDriveStyle = new DrivingStyle(driveStyle);
        switchDrivingMode(TranslationMode.Vision, RotationMode.Vision);
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
        driveStyle = new DrivingStyle(prevDriveStyle);
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
            StartTele();
        }
    }

    public void RegisterJoystickRead(double f, double s, double r) {
        this.strafe = s;
        this.forward = f;
        this.rotation = r;
    }

    @Override
    public void periodic() {
        if (pidf.p != P || pidf.i != I || pidf.d != D || pidf.f != F) {
            // Someone changed the PIDF on the panels: Updated it in realtime...
            turningPIDF = new PIDFController(pidf);
            P = pidf.p;
            I = pidf.i;
            D = pidf.d;
            F = pidf.f;
        }
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
        boolean botCentric = driveStyle.perspective == DrivingPerspective.RobotCentric;
        if (driveStyle.translation == TranslationMode.Vision) {
            // For vision *translation* we will move forward or backward to maintain a 'size' of the
            // target. The 'a' part of the result is the percentage of the total image that the
            // target is filling, so the closer you are, the larger the area of the image. It's
            // kinda dopey, but works just fine...
            TargetSubsystem.TargetInfo curTarget = vision.getCurResult();
            if (curTarget != null) {
                forward =
                    (DrivingConstants.Control.VISION_TARGET_SIZE - curTarget.a) *
                    DrivingConstants.Control.VISION_FORWARD_GAIN;
                botCentric = true;
            }
        }
        ShowDriveVectors(forward, strafe, rot, getHeadingOffsetRadians());
        follower.setTeleOpDrive(forward, strafe, rot, botCentric, getHeadingOffsetRadians());
        follower.update();
    }

    double getRotation() {
        // Negative, because pushing left is negative, but that is a positive change in Pedro's
        // coordinate system.
        curHeading = MathUtils.posNegRadians(follower.getHeading() - getHeadingOffsetRadians());
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
            case Hold:
                // Hold the current heading
                if (driveStyle.translation != TranslationMode.Hold && holdPose != null) {
                    targetHeading = MathUtils.posNegRadians(
                        holdPose.getHeading() - getHeadingOffsetRadians()
                    );
                } else {
                    // This is weird: We have a rotational hold, but not a translational hold, and
                    // no holdPose. This is probably an error
                    targetHeading = 0;
                }
                break;
            case Tangential: // Aim toward the translational direction
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
            case Bidirectional: // Align the bot to the translational direction
                // Bidirectional is an angle-focused driving style, but the heading is strictly in
                // the direction of the stick, but with a twist: If you flip the direction you're
                // driving, the bot will drive in reverse, rather than fully rotate.
                if (Math.abs(strafe) > 0 || Math.abs(forward) > 0) {
                    targetHeading = MathUtils.posNegRadians(Math.atan2(strafe, forward));
                    // targetHeading should be within 90 degrees of curHeading, if it's not, flip it
                    if (Math.abs(MathUtils.posNegRadians(targetHeading - curHeading)) > HalfPi) {
                        targetHeading = MathUtils.posNegRadians(targetHeading + Math.PI);
                    }
                } else {
                    return 0;
                }
                break;
            case Vision: // Use Vision to find the target & aim toward it
                TargetSubsystem.TargetInfo visResult = vision == null
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
        // Use the turn PID controller to get to the target heading
        if (!turnPidStarted) {
            turningPIDF.reset();
            turnPidStarted = true;
        }
        if (Math.abs(MathUtils.posNegRadians(targetHeading - curHeading)) > WIND_UP_LIMIT) {
            // The goal is to prevent *massive* early error from making the I value useless.
            turningPIDF.reset();
        }
        turningPIDF.setTarget(targetHeading);
        // We need to handle a weird case: When the current/target heading values are close
        // to the angle wrap-around point, we should switch one of them from -180 to 180 to 0-360
        // or -360 - 0 instead. This minimizes the error. Without this, when your target heading is
        // near that wrap-around location, you'll get some serious bot jiggling, without an obvious
        // reason.
        if (Math.abs(curHeading - targetHeading) > Math.PI) {
            curHeading += Math.signum(targetHeading) * TwoPi;
        }
        return turningPIDF.update(curHeading);
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
            case Target_NYI:
                drvMode = "rTgt";
                break;
            case Free:
                drvMode = "rFre";
                break;
            case Snap:
                drvMode = "rSnp";
                break;
            case Hold:
                drvMode = "rHld";
                break;
            case Tangential:
                drvMode = "rTan";
                break;
            case Bidirectional:
                drvMode = "rBid";
                break;
            case Vision:
                drvMode = "rViz";
                break;
            default:
                drvMode = "rUnk";
                break;
        }
        switch (driveStyle.translation) {
            case Free:
                drvMode += "xFre";
                break;
            case Square:
                drvMode += "xSqu";
                break;
            case Hold:
                drvMode += "xHld";
                break;
            case Vision:
                drvMode += "xViz";
                break;
            default:
                drvMode += "xUnk";
                break;
        }
        switch (driveStyle.perspective) {
            case RobotCentric:
                drvMode += ":Bot";
                break;
            case FieldCentric:
                drvMode += ":Fld";
                break;
            default:
                drvMode += ":[?]";
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

    private void ShowDriveVectors(double fwd, double strafe, double rot, double offset) {
        drvVec = String.format(
            "f %.2f s %.2f r %.2f@%.1f° [%.1f°]",
            fwd,
            strafe,
            rot,
            Math.toDegrees(curHeading),
            Math.toDegrees(MathUtils.posNegRadians(offset))
        );
    }
}
