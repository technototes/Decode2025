package org.firstinspires.ftc.twenty403.commands;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierPoint;
import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.technototes.library.command.Command;
import com.technototes.library.control.Stick;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.util.Alliance;
import java.util.function.DoubleSupplier;
import org.firstinspires.ftc.twenty403.Setup;
import org.firstinspires.ftc.twenty403.helpers.HeadingHelper;

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

public class PedroJoystickDrive implements Command, Loggable {

    // Methods to bind to buttons (Commands)
    public void ResetGyro() {
        headingOffset = follower.getHeading();
    }

    public void SetSnailSpeed() {
        follower.setMaxPowerScaling(Setup.OtherSettings.SNAIL_SPEED);
        turnSpeed = Setup.OtherSettings.SNAIL_TURN;
    }

    public void SetNormalSpeed() {
        follower.setMaxPowerScaling(Setup.OtherSettings.NORMAL_SPEED);
        turnSpeed = Setup.OtherSettings.NORMAL_TURN;
    }

    public void SetTurboSpeed() {
        follower.setMaxPowerScaling(Setup.OtherSettings.TURBO_SPEED);
        turnSpeed = Setup.OtherSettings.TURBO_TURN;
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

    public void HoldCurrentPosition() {
        switchDriveStyle(DrivingStyle.Hold);
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
    double turnSpeed;
    // Camera, for future use:
    Limelight3A limelight;
    // used to keep the directions straight
    Alliance alliance;
    // Used to keep track of the previous drive style when using the "StayPut" operation
    private DrivingStyle prevDriveStyle = DrivingStyle.None;
    private double prevDriveSpeed = 0;
    private double prevTurnSpeed = 0;

    public enum DrivingStyle {
        Free, // Bot is free to move in all directions
        Straight, // Bot will only move along the X or Y axis, but not both
        Hold, // Stay right where you are (just use Pedro)
        // NOT YET IMPLEMENTED
        Vision_NYI, // Bot will use Vision to find the target and aim toward it
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

    public PedroJoystickDrive(
        Follower fol,
        Stick xyStick,
        Stick rotStick,
        Limelight3A ll,
        Alliance all
    ) {
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

    public PedroJoystickDrive(Follower fol, Stick xyStick, Stick rotStick) {
        this(fol, xyStick, rotStick, null, Alliance.NONE);
    }

    public PedroJoystickDrive(Follower fol, Stick xyStick, Stick rotStick, Limelight3A ll) {
        this(fol, xyStick, rotStick, ll, Alliance.NONE);
    }

    public PedroJoystickDrive(Follower fol, Stick xyStick, Stick rotStick, Alliance al) {
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
        if (driveStyle == DrivingStyle.Straight) {
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
        if (driveStyle == DrivingStyle.Vision_NYI) {
            // TODO: Implement this (turn toward a target based on LimeLight)
            return 0;
        } else {
            return rotation * turnSpeed;
        }
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
            if (Math.abs(val) <= Setup.OtherSettings.STICK_DEAD_ZONE) {
                return 0.0;
            }
            // If the value is outside the dead zone, scale it
            return (
                (val - Math.copySign(Setup.OtherSettings.STICK_DEAD_ZONE, val)) /
                (1.0 - Setup.OtherSettings.STICK_DEAD_ZONE)
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
            case Hold:
                drvMode = "!Hold!";
                break;
            case Vision_NYI:
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
