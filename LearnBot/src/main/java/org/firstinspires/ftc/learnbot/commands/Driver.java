package org.firstinspires.ftc.learnbot.commands;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.technototes.library.command.Command;
import com.technototes.library.control.Stick;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.util.Alliance;
import com.technototes.library.util.MathUtils;
import java.util.function.DoubleSupplier;
import org.firstinspires.ftc.learnbot.Setup.DriveSettings;
import org.firstinspires.ftc.learnbot.helpers.HeadingHelper;

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

public class Driver implements Command, Loggable {

    // Methods to bind to buttons (Commands)
    public void ResetGyro() {
        headingOffset = follower.getHeading();
    }

    public void SetSnailSpeed() {
        follower.setMaxPowerScaling(DriveSettings.SNAIL_SPEED);
    }

    public void SetNormalSpeed() {
        follower.setMaxPowerScaling(DriveSettings.NORMAL_SPEED);
    }

    public void SetTurboSpeed() {
        follower.setMaxPowerScaling(DriveSettings.TURBO_SPEED);
    }

    private void switchDriveStyle(DrivingStyle style) {
        if (driveStyle == style) {
            return;
        }
        if (driveStyle == DrivingStyle.HoldPosition) {
            // If we're currently holding a position, stop doing so
            follower.startTeleOpDrive();
        }
        driveStyle = style;
        if (style == DrivingStyle.HoldPosition) {
            holdPose = follower.getPose();
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

    public void EnableTangentialDriving() {
        switchDriveStyle(DrivingStyle.Tangential);
    }

    public void HoldCurrentPosition() {
        switchDriveStyle(DrivingStyle.HoldPosition);
    }

    public void EnableVisionDriving() {
        switchDriveStyle(DrivingStyle.Vision_NYI);
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

    public void SetTargetBasedDriveMode() {
        driveMode = DrivingMode.TargetBased_NYI;
    }

    public void SaveHeading() {
        HeadingHelper.savePose(follower.getPose());
    }

    // Some just slightly more complex commands:
    public void StayPut() {
        if (prevDriveStyle == DrivingStyle.None) {
            prevDriveStyle = driveStyle;
            prevDriveSpeed = follower.getMaxPowerScaling();
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
    // Camera, for future use:
    Limelight3A limelight;
    // used to keep the directions straight
    Alliance alliance;
    // Used to keep track of the previous drive style when using the "StayPut" operation
    private DrivingStyle prevDriveStyle = DrivingStyle.None;
    private double prevDriveSpeed = 0;

    public enum DrivingStyle {
        Free, // Bot is free to move in all directions
        Straight, // Bot will only move along the X or Y axis, but not both
        Right, // Bot will hold a right angle while driving
        Square, // Both Straight & Right driving styles
        Tangential, // Stay tangent to the bot's direction
        HoldPosition, // Stay right where you are (just use Pedro)
        Vision_NYI, // Bot will use Vision to find the target and aim toward it
        None,
    }

    public enum DrivingMode {
        RobotCentric,
        FieldCentric,
        TargetBased_NYI,
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

    public Driver(Follower fol, Stick xyStick, Stick rotStick, Limelight3A ll, Alliance all) {
        // TODO: Throw an exception or log if there's some problem with constants.
        // i.e. DEAD_ZONE is negative, or greater than 1.0
        limelight = ll;
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

    public Driver(Follower fol, Stick xyStick, Stick rotStick) {
        this(fol, xyStick, rotStick, null, Alliance.NONE);
    }

    public Driver(Follower fol, Stick xyStick, Stick rotStick, Limelight3A ll) {
        this(fol, xyStick, rotStick, ll, Alliance.NONE);
    }

    public Driver(Follower fol, Stick xyStick, Stick rotStick, Alliance al) {
        this(fol, xyStick, rotStick, null, al);
    }

    @Override
    public void initialize() {
        follower.startTeleOpDrive();
    }

    @Override
    public void execute() {
        // If subsystem is busy it is running a path, just ignore the stick.
        if (follower.isBusy()) {
            drvMode = "busy";
            return;
        }
        ShowDriveMode(driveStyle, driveMode, follower);
        if (driveStyle == DrivingStyle.HoldPosition) {
            follower.holdPoint(holdPose);
            return;
        }
        double curHeading = follower.getHeading() - headingOffset;

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
            double rot = getRotation(curHeading, fwdVal, strafeVal);
            ShowDriveVectors(fwdVal, strafeVal, rot);
            follower.setTeleOpDrive(
                fwdVal,
                strafeVal,
                rot,
                driveMode == DrivingMode.RobotCentric,
                headingOffset
            );
            follower.update();
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
    }

    double getRotation(double curHeading, double fwdVal, double strafeVal) {
        // Negative, because pushing left is negative, but that is a positive change in Pedro's
        // coordinate system.
        double rotation = -r.getAsDouble();
        double targetHeading = 0;
        switch (driveStyle) {
            case Right:
            case Square:
                // Angle-focused driving styles override target-based driving mode
                targetHeading = MathUtils.snapToNearestRadiansMultiple(curHeading, Math.PI / 2);
                break;
            case Tangential:
                // Tangential is considered an angle-focused driving style, too
                // Get the heading of the indicated vector of (fwd,strafe)
                if (Math.abs(strafeVal) > 0 || Math.abs(fwdVal) > 0) {
                    targetHeading = MathUtils.normalizeRadians(Math.atan2(strafeVal, fwdVal));
                } else {
                    return 0;
                }
                break;
            case Vision_NYI:
                // TODO: Implement this (turn toward a target based on LimeLight)
                return 0;
            case Free:
            case Straight:
            default:
                if (driveMode != DrivingMode.TargetBased_NYI) {
                    return rotation * DriveSettings.TURN_SCALING;
                } else {
                    // TODO: implement this (Turn toward the target)
                    targetHeading = 0.0;
                }
        }
        // TODO: Use the Pedro heading PIDF to get this value?
        return Math.clamp(targetHeading - curHeading, -1, 1) * DriveSettings.TURN_SCALING;
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
            if (Math.abs(val) <= DriveSettings.DEAD_ZONE) {
                return 0.0;
            }
            // If the value is outside the dead zone, scale it
            return (
                (val - Math.copySign(DriveSettings.DEAD_ZONE, val)) /
                (1.0 - DriveSettings.DEAD_ZONE)
            );
        };
    }

    // Everything below here is just for displaying/diagnostics

    @Log(name = "DrvMode")
    public static String drvMode = "";

    @Log(name = "DrvVec")
    public static String drvVec = "";

    private static void ShowDriveMode(DrivingStyle driveStyle, DrivingMode driveMode, Follower f) {
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
            case Tangential:
                drvMode = "Tangential";
                break;
            case HoldPosition:
                drvMode = "Hold Pos";
                break;
            case Vision_NYI:
                drvMode = "Vision(NYI)";
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
            case TargetBased_NYI:
                drvMode += " Target-Based";
                break;
            default:
                drvMode += " [Unknown]";
                break;
        }
        drvMode += String.format(" Max %.2f", f.getMaxPowerScaling());
    }

    private static void ShowDriveVectors(double fwdVal, double strafeVal, double rot) {
        drvVec = String.format("f %.2f s %.2f r %.2f", fwdVal, strafeVal, rot);
    }
}
