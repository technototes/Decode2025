package org.firstinspires.ftc.learnbot.commands;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.technototes.library.command.Command;
import com.technototes.library.control.Stick;
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

public class JoystickDriveCommand implements Command, Loggable {

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

    public void EnableStraightDriving() {
        driveStyle = DrivingStyle.Straight;
    }

    public void EnableSnap90Driving() {
        driveStyle = DrivingStyle.Right;
    }

    public void EnableSquareDriving() {
        driveStyle = DrivingStyle.Square;
    }

    public void EnableTangentialDriving() {
        driveStyle = DrivingStyle.Tangential;
    }

    public void EnableVisionDriving() {
        driveStyle = DrivingStyle.Vision_NYI;
    }

    public void EnableFreeDriving() {
        driveStyle = DrivingStyle.Free;
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

    public enum DrivingStyle {
        Free, // Bot is free to move in all directions
        Straight, // Bot will only move along the X or Y axis, but not both
        Right, // Bot will hold a right angle while driving
        Square, // Both Straight & Right driving styles
        Tangential, // Stay tangent to the bot's direction
        Vision_NYI, // Bot will use Vision to find the target and aim toward it
    }

    public enum DrivingMode {
        RobotCentric,
        FieldCentric,
        TargetBased_NYI,
    }

    DrivingStyle driveStyle;
    DrivingMode driveMode;
    public DrivingStyle getCurrentDriveStyle() {
        return driveStyle;
    }
    public DrivingMode getCurrentDriveMode() {
        return driveMode;
    }

    public JoystickDriveCommand(
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
        x = DeadZoneScale(xyStick.getXSupplier());
        y = DeadZoneScale(xyStick.getYSupplier());
        r = DeadZoneScale(rotStick.getXSupplier());
        SetNormalSpeed();
        SetFieldCentricDriveMode();
        EnableFreeDriving();
    }

    public JoystickDriveCommand(Follower fol, Stick xyStick, Stick rotStick) {
        this(fol, xyStick, rotStick, null, Alliance.NONE);
    }

    public JoystickDriveCommand(Follower fol, Stick xyStick, Stick rotStick, Limelight3A ll) {
        this(fol, xyStick, rotStick, ll, Alliance.NONE);
    }

    public JoystickDriveCommand(Follower fol, Stick xyStick, Stick rotStick, Alliance al) {
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
            follower.setTeleOpDrive(
                fwdVal,
                strafeVal,
                getRotation(curHeading, fwdVal, strafeVal),
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
                targetHeading = MathUtils.normalizeRadians(Math.atan2(fwdVal, strafeVal));
                break;
            case Free:
            case Straight:
            case Vision_NYI:
            default:
                if (driveMode != DrivingMode.TargetBased_NYI) {
                    return rotation * DriveSettings.TURN_SCALING;
                } else {
                    targetHeading = 0.0; // TODO: Get the target heading from the target...s
                }
        }
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
}
