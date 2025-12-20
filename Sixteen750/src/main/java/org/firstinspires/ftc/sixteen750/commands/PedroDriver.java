package org.firstinspires.ftc.sixteen750.commands;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierPoint;
import com.pedropathing.geometry.Pose;
import com.technototes.library.command.Command;
import com.technototes.library.control.Stick;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.util.Alliance;
import com.technototes.library.util.MathUtils;
import java.util.function.DoubleSupplier;
import org.firstinspires.ftc.sixteen750.Setup;
import org.firstinspires.ftc.sixteen750.Setup.OtherSettings;
import org.firstinspires.ftc.sixteen750.helpers.HeadingHelper;
import org.firstinspires.ftc.sixteen750.subsystems.LimelightSubsystem;

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
public class PedroDriver implements Command, Loggable {

    public static double VISION_TURN_SCALE = 0.01;

    // Methods to bind to buttons (Commands)
    public void ResetGyro() {
        headingOffset = follower.getHeading();
    }

    public void SetSnailSpeed() {
        follower.setMaxPowerScaling(OtherSettings.SNAIL_SPEED);
        turnSpeed = OtherSettings.SNAIL_TURN;
    }

    public void SetNormalSpeed() {
        follower.setMaxPowerScaling(OtherSettings.NORMAL_SPEED);
        turnSpeed = OtherSettings.NORMAL_TURN;
    }

    public void SetTurboSpeed() {
        follower.setMaxPowerScaling(OtherSettings.TURBO_SPEED);
        turnSpeed = OtherSettings.TURBO_TURN;
    }

    private void switchDriveStyle(DrivingStyle style) {
        if (driveStyle == style) {
            return;
        }
        if (driveStyle == DrivingStyle.Hold) {
            // If we're currently holding a position, stop doing so
            follower.startTeleOpDrive();
            holdPose = null;
        }
        driveStyle = style;
        // If we've switched *to* holding a pose, start the follower
        if (style == DrivingStyle.Hold) {
            holdPose = follower.getPose();
            follower.holdPoint(new BezierPoint(holdPose), holdPose.getHeading(), false);
        }
    }

    public void EnableStraightDriving() {
        switchDriveStyle(DrivingStyle.Straight);
    }

    public void EnableSnap90Driving() {
        switchDriveStyle(DrivingStyle.Right);
    }

    public void EnableSquareDriving() {
        switchDriveStyle(DrivingStyle.Square);
    }

    public void HoldCurrentPosition() {
        switchDriveStyle(DrivingStyle.Hold);
    }

    public void EnableVisionDriving() {
        switchDriveStyle(DrivingStyle.Vision);
    }

    public void EnableFreeDriving() {
        switchDriveStyle(DrivingStyle.Free);
    }

    public void SetRobotCentricDriveMode() {
        driveMode = DrivingMode.RobotCentric;
    }

    public void SetFieldCentricDriveMode() {
        driveMode = DrivingMode.FieldCentric;
    }

    public void SaveHeading() {
        HeadingHelper.savePose(follower.getPose());
    }

    // Some just slightly more complex commands:
    public void StayPut() {
        if (prevDriveStyle == DrivingStyle.None) {
            prevDriveStyle = driveStyle;
            prevDriveSpeed = follower.getMaxPowerScaling();
            prevTurnSpeed = turnSpeed;
        }
        HoldCurrentPosition();
        SetTurboSpeed();
    }

    public void ResumeDriving() {
        if (prevDriveStyle == DrivingStyle.None) {
            EnableFreeDriving();
            SetNormalSpeed();
        } else {
            switchDriveStyle(prevDriveStyle);
            follower.setMaxPowerScaling(prevDriveSpeed);
            turnSpeed = prevTurnSpeed;
            prevDriveStyle = DrivingStyle.None;
        }
    }

    // The PedroPath follower, to let us actually make the bot move:
    Follower follower;
    // The sticks (probably each are CommandAxis suppliers)
    // Note that the stick values returned are oriented like this:
    // Up is a negative value, down is a positive value.
    // Left is a negative value, right is a positive value.
    DoubleSupplier x, y, r;
    // This is the target Pose we're trying to reach
    Pose targetPose;
    // The offset heading for field-relative controls
    double headingOffset;
    // The current rotation scaling factor
    public static double turnSpeed; //this is turnspeed FOR EVERYTHING
    public static double visionTurnSpeed; //turnspeed for vision only
    // Camera, for future use:
    LimelightSubsystem limelightSubsystem;
    // used to keep the directions straight
    Alliance alliance;
    // Used to keep track of the previous drive style when using the "StayPut" operation
    private DrivingStyle prevDriveStyle = DrivingStyle.None;
    private double prevDriveSpeed = 0;
    private double prevTurnSpeed = 0;

    public enum DrivingStyle {
        Free, // Bot is free to move in all directions
        Straight, // Bot will only move along the X or Y axis, but not both
        Right, // Bot will hold a right angle while driving
        Square, // Both Straight & Right driving styles
        Hold, // Stay right where you are (just use Pedro)
        Vision, // Bot will use Vision to find the target and aim toward it
        None,
    }

    public enum DrivingMode {
        RobotCentric,
        FieldCentric,
    }

    DrivingStyle driveStyle;
    DrivingMode driveMode;
    Pose holdPose;

    public DrivingStyle getCurrentDriveStyle() {
        return driveStyle;
    }

    public DrivingMode getCurrentDriveMode() {
        return driveMode;
    }

    public PedroDriver(
        Follower fol,
        Stick xyStick,
        Stick rotStick,
        LimelightSubsystem ls,
        Alliance all
    ) {
        // TODO: Throw an exception or log if there's some problem with constants.
        // i.e. DEAD_ZONE is negative, or greater than 1.0
        limelightSubsystem = ls;
        follower = fol;
        headingOffset = 0.0;
        alliance = all;
        holdPose = null;
        x = DeadZoneScale(xyStick.getXSupplier());
        y = DeadZoneScale(xyStick.getYSupplier());
        r = DeadZoneScale(rotStick.getXSupplier());
        SetNormalSpeed();
        SetFieldCentricDriveMode();
        EnableFreeDriving();
    }

    public PedroDriver(Follower fol, Stick xyStick, Stick rotStick) {
        this(fol, xyStick, rotStick, null, Alliance.NONE);
    }

    public PedroDriver(Follower fol, Stick xyStick, Stick rotStick, LimelightSubsystem ls) {
        this(fol, xyStick, rotStick, ls, Alliance.NONE);
    }

    public PedroDriver(Follower fol, Stick xyStick, Stick rotStick, Alliance al) {
        this(fol, xyStick, rotStick, null, al);
    }

    @Override
    public void initialize() {
        follower.startTeleOpDrive();
    }

    @Override
    public void execute() {
        // If subsystem is busy it is running a path, just ignore the stick.
        ShowDriveInfo(driveStyle, driveMode, follower);
        if (follower.isBusy() || driveStyle == DrivingStyle.Hold) {
            follower.update();
            drvVec = "busy";
            return;
        }

        // Recall that pushing a stick forward goes *negative* and pushing a stick to the left
        // goes *negative* as well (both are opposite Pedro's coordinate system)
        double fwdVal = -y.getAsDouble();
        double strafeVal = -x.getAsDouble();
        if (driveStyle == DrivingStyle.Straight || driveStyle == DrivingStyle.Square) {
            // for straight/square driving, we only use one of the two translation directions:
            if (Math.abs(fwdVal) > Math.abs(strafeVal)) {
                strafeVal = 0;
            } else {
                fwdVal = 0;
            }
        }
        if (driveMode == DrivingMode.RobotCentric || driveMode == DrivingMode.FieldCentric) {
            double rot = getRotation(fwdVal, strafeVal);
            ShowDriveVectors(fwdVal, strafeVal, rot, headingOffset);
            follower.setTeleOpDrive(
                fwdVal,
                strafeVal,
                rot,
                driveMode == DrivingMode.RobotCentric,
                headingOffset
            );
        }

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

        follower.update();
    }

    double getRotation(double fwdVal, double strafeVal) {
        // Negative, because pushing left is negative, but that is a positive change in Pedro's
        // coordinate system.
        double rotation = -r.getAsDouble();
        double curHeading = follower.getHeading() - headingOffset;
        double targetHeading = 0;
        switch (driveStyle) {
            case Right:
            case Square:
                // Angle-focused driving styles override target-based driving mode
                targetHeading = MathUtils.snapToNearestRadiansMultiple(curHeading, Math.PI / 2);
                break;
            case Vision:
                if (Setup.Connected.LIMELIGHTSUBSYSTEM) {
                    // --- Face AprilTag using Limelight ---
                    targetHeading =
                        curHeading - Math.toRadians(limelightSubsystem.getLimelightRotation());
                    // return VISION_TURN_SCALE * LimelightSubsystem.Xangle;
                    //lowkey forgot what kevin said but i think it just sets the target heading to
                    //where the limelight is so that vision can make the bot turn that way
                } else {
                    return rotation;
                }
                break;
            case Free:
            case Straight:
            default:
                return rotation * turnSpeed;
        }
        // TODO: Use the Pedro heading PIDF to get this value?
        return (Math.clamp(targetHeading - curHeading, -1, 1) * turnSpeed);
        //so the line above overrides the joystick and makes it ignore what the human
        //is doing with the joystick and makes it turn a specific way (vision control)
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    // Helper to make dead zones on sticks still allow good scaling
    private DoubleSupplier DeadZoneScale(DoubleSupplier ds) {
        // Okay, we want a small dead zone in the middle of the stick, but that also means that
        // you can't have a value any smaller than that value, so instead, we're going to scale
        // the value after compensating for the dead zone
        return () -> {
            double val = ds.getAsDouble();
            // If the value is inside the dead zone, just make it zero
            if (Math.abs(val) <= OtherSettings.STICK_DEAD_ZONE) {
                return 0.0;
            }
            // If the value is outside the dead zone, scale it
            return (
                (val - Math.copySign(OtherSettings.STICK_DEAD_ZONE, val)) /
                (1.0 - OtherSettings.STICK_DEAD_ZONE)
            );
        };
    }

    // Everything below here is just for displaying/diagnostics

    @Log(name = "DrvMode")
    public static String drvMode = "";

    @Log(name = "DrvVec")
    public static String drvVec = "";

    @Log(name = "Pose")
    public static String drvLoc = "";

    private static void ShowDriveInfo(DrivingStyle driveStyle, DrivingMode driveMode, Follower f) {
        switch (driveStyle) {
            case Free:
                drvMode = "Free";
                break;
            case Straight:
                drvMode = "Straight";
                break;
            case Right:
                drvMode = "Right";
                break;
            case Square:
                drvMode = "Square";
                break;
            case Hold:
                drvMode = "!Hold!";
                break;
            case Vision:
                drvMode = "Vision[NYI]";
                break;
            default:
                drvMode = "Unknown";
                break;
        }
        switch (driveMode) {
            case RobotCentric:
                drvMode += " Bot-Centric";
                break;
            case FieldCentric:
                drvMode += " Field-Centric";
                break;
            default:
                drvMode += " [Unknown]";
                break;
        }
        drvMode += String.format(" Max:%.2f", f.getMaxPowerScaling());
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
