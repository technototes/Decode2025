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

    public enum DrivingStyle {
        Free, // Bot is free to move in all directions
        Straight, // Bot will only move along the X or Y axis, but not both
        Right, // Bot will hold a right angle while driving
        Square, // Both Straight & Right driving styles
        Hold, // Stay right where you are (just use Pedro)
        Tangential_BORKED, // Aim tangent (inline) w/the bot's translational direction (NOT WORKING)
        Vision_NYI, // Bot will use Vision to find the target & aim toward it (NOT YET IMPLEMENTED)
        None,
    }

    public enum DrivingMode {
        RobotCentric,
        FieldCentric,
        // NOT YET IMPLEMENTED
        TargetBased_NYI,
    }

    // The PedroPath follower, to let us actually make the bot move:
    public Follower follower;
    // The vision subsystem, for vision-based driving stuff
    public VisionSubsystem vision;

    // The direction of the 3 axes for manual control
    public double strafe, forward, rotation;

    // This is the target Pose we're trying to reach
    Pose targetPose;
    // The offset heading for field-relative controls
    double headingOffset;
    // The current rotation scaling factor
    double turnSpeed;
    // used to keep the directions straight
    Alliance alliance;
    // Used to keep track of the previous drive style when using the "StayPut" operation
    private DrivingStyle prevDriveStyle = DrivingStyle.None;
    private double prevDriveSpeed = 0;
    private double prevTurnSpeed = 0;

    DrivingStyle driveStyle;
    DrivingMode driveMode;
    Pose holdPose;

    public DrivingStyle getCurrentDriveStyle() {
        return driveStyle;
    }

    public DrivingMode getCurrentDriveMode() {
        return driveMode;
    }

    public PedroDrivebaseSubsystem(Follower f, VisionSubsystem viz, Alliance all) {
        follower = f;
        vision = viz;
        headingOffset = 0.0;
        alliance = all;
        holdPose = null;
        forward = 0;
        strafe = 0;
        rotation = 0;
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
        follower.startTeleOpDrive();
    }

    // Methods to bind to buttons (Commands)
    public void ResetGyro() {
        headingOffset = follower.getHeading();
    }

    public void SetSnailSpeed() {
        follower.setMaxPowerScaling(DrivingConstants.Control.SNAIL_SPEED);
        turnSpeed = DrivingConstants.Control.SNAIL_TURN;
    }

    public void SetNormalSpeed() {
        follower.setMaxPowerScaling(DrivingConstants.Control.NORMAL_SPEED);
        turnSpeed = DrivingConstants.Control.NORMAL_TURN;
    }

    public void SetTurboSpeed() {
        follower.setMaxPowerScaling(DrivingConstants.Control.TURBO_SPEED);
        turnSpeed = DrivingConstants.Control.TURBO_TURN;
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

    public void EnableTangentialDriving() {
        switchDriveStyle(DrivingStyle.Tangential_BORKED);
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

    public void SetTargetBasedDriveMode() {
        driveMode = DrivingMode.TargetBased_NYI;
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

    public void RegisterJoystickRead(double f, double s, double r) {
        this.strafe = s;
        this.forward = f;
        this.rotation = r;
    }

    @Override
    public void periodic() {
        // If subsystem is busy it is running a path, just ignore the stick.
        ShowDriveInfo(driveStyle, driveMode, follower);
        if (follower.isBusy() || driveStyle == DrivingStyle.Hold) {
            follower.update();
            drvVec = "busy";
            return;
        }

        // Recall that pushing a stick forward goes *negative* and pushing a stick to the left
        // goes *negative* as well (both are opposite Pedro's coordinate system)
        if (driveStyle == DrivingStyle.Straight || driveStyle == DrivingStyle.Square) {
            // for straight/square driving, we only use one of the two translation directions:
            if (Math.abs(forward) > Math.abs(strafe)) {
                strafe = 0;
            } else {
                forward = 0;
            }
        }
        if (driveMode == DrivingMode.RobotCentric || driveMode == DrivingMode.FieldCentric) {
            double rot = getRotation();
            ShowDriveVectors(forward, strafe, rot, headingOffset);
            follower.setTeleOpDrive(
                forward,
                strafe,
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

    double getRotation() {
        // Negative, because pushing left is negative, but that is a positive change in Pedro's
        // coordinate system.
        double curHeading = follower.getHeading() - headingOffset;
        double targetHeading = 0;
        switch (driveStyle) {
            case Right:
            case Square:
                // Angle-focused driving styles override target-based driving mode
                targetHeading = MathUtils.snapToNearestRadiansMultiple(curHeading, Math.PI / 2);
                break;
            case Tangential_BORKED:
                // Tangential is an angle-focused driving style, but the heading
                // is strictly in the direction of the stick. Logically, this is an attempt to
                // eliminate "actual" strafing: The robot should be oriented to drive forward in the
                // direction of the stick
                if (Math.abs(strafe) > 0 || Math.abs(forward) > 0) {
                    targetHeading = MathUtils.posNegRadians(Math.atan2(forward, strafe));
                    curHeading = MathUtils.posNegRadians(curHeading);
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
                    return rotation * turnSpeed;
                } else {
                    // TODO: implement this (Turn toward the target)
                    targetHeading = 0.0;
                }
        }
        // TODO: Use the Pedro heading PIDF to get this value?
        return (Math.clamp(targetHeading - curHeading, -1, 1) * turnSpeed);
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
            case Tangential_BORKED:
                drvMode = "Tangent[NYI]";
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
            case TargetBased_NYI:
                drvMode += " Target-Based[NYI]";
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
