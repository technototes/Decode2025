package org.firstinspires.ftc.learnbot.commands.driving;

import static org.firstinspires.ftc.learnbot.subsystems.DrivebaseSubsystem.DriveConstants.faceTagMode;

import com.acmerobotics.roadrunner.drive.DriveSignal;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.technototes.library.command.Command;
import com.technototes.library.control.Stick;
import com.technototes.library.logger.Loggable;
import com.technototes.library.util.MathUtils;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import org.firstinspires.ftc.learnbot.Setup;
import org.firstinspires.ftc.learnbot.subsystems.DrivebaseSubsystem;

public class JoystickDriveCommand implements Command, Loggable {

    public DrivebaseSubsystem subsystem;
    public DoubleSupplier x, y, r;
    public BooleanSupplier watchTrigger;
    public double targetHeadingRads;
    public DoubleSupplier driveStraighten;
    public DoubleSupplier drive45;
    public boolean driverDriving;
    public boolean operatorDriving;
    private Limelight3A limelight;

    public JoystickDriveCommand(
        DrivebaseSubsystem sub,
        Stick xyStick,
        Stick rotStick,
        DoubleSupplier strtDrive,
        DoubleSupplier angleDrive
    ) {
        addRequirements(sub);
        subsystem = sub;
        x = xyStick.getXSupplier();
        y = xyStick.getYSupplier();
        r = rotStick.getXSupplier();
        targetHeadingRads = -sub.getExternalHeading();
        driveStraighten = strtDrive;
        drive45 = angleDrive;
        driverDriving = true;
        operatorDriving = false;
    }

    // Use this constructor if you don't want auto-straightening
    public JoystickDriveCommand(DrivebaseSubsystem sub, Stick xyStick, Stick rotStick) {
        this(sub, xyStick, rotStick, null, null);
    }

    // This will make the bot snap to an angle, if the 'straighten' button is pressed
    // Otherwise, it just reads the rotation value from the rotation stick
    private double getRotation(double headingInRads) {
        // Check to see if we're trying to straighten the robot
        double normalized = 0.0;
        boolean straightTrigger;
        boolean fortyfiveTrigger;
        straightTrigger = isTriggered(driveStraighten);
        fortyfiveTrigger = isTriggered(drive45);
        if (faceTagMode) {
            // --- Face AprilTag using Limelight ---
            // LLResult result = limelight.getLatestResult();
            // if (result != null && result.isValid()) {
            //     double tx = result.getTx(); // horizontal offset in degrees
            //     double kP_TagAlign = 0.03;  // tune this gain
            //     return -kP_TagAlign * tx;   // rotate until tx ~ 0
            // } else {
            //     return 0.0; // no target → don't spin
            // }
            return calculateHeadingToCircle(
                subsystem.getPoseEstimate().getX(),
                subsystem.getPoseEstimate().getY()
            );
        }

        if (!straightTrigger && !fortyfiveTrigger) {
            // No straighten override: return the stick value
            // (with some adjustment...)
            return -Math.pow(r.getAsDouble(), 3) * subsystem.speed;
        }
        if (straightTrigger) {
            // headingInRads is [0-2pi]
            double heading = -Math.toDegrees(headingInRads);
            // Snap to the closest 90 or 270 degree angle (for going through the depot)
            double close = MathUtils.closestTo(heading, 0, 90, 180, 270, 360);
            double offBy = close - heading;
            // Normalize the error to -1 to 1
            normalized = Math.max(Math.min(offBy / 45, 1.), -1.);
            // Dead zone of 5 degreesLiftHighJunctionCommand(liftSubsystem)
            if (Math.abs(normalized) < Setup.OtherSettings.STRAIGHTEN_DEAD_ZONE) {
                return 0.0;
            }
        } else {
            // headingInRads is [0-2pi]
            double heading45 = -Math.toDegrees(headingInRads);
            // Snap to the closest 90 or 270 degree angle (for going through the depot)
            double close45 = MathUtils.closestTo(heading45, 45, 135, 225, 315);
            double offBy45 = close45 - heading45;
            // Normalize the error to -1 to 1
            normalized = Math.max(Math.min(offBy45 / 45, 1.), -1.);
            // Dead zone of 5 degreesLiftHighJunctionCommand(liftSubsystem)
            if (Math.abs(normalized) < Setup.OtherSettings.STRAIGHTEN_DEAD_ZONE) {
                return 0.0;
            }
        }
        // Scale it by the cube root, the scale that down by 30%
        // .9 (about 40 degrees off) provides .96 power => .288
        // .1 (about 5 degrees off) provides .46 power => .14
        return Math.cbrt(normalized) * 0.3;
    }

    public static boolean isTriggered(DoubleSupplier ds) {
        if (ds == null || ds.getAsDouble() < DrivebaseSubsystem.DriveConstants.TRIGGER_THRESHOLD) {
            return false;
        }
        return true;
    }

    public static double calculateHeadingToCircle(double robotX, double robotY) {
        // circle x & y are theoretical which might work but i don't know
        double circleX = 0;
        double circleY = 1.5;
        double radius = 101.8234;
        // Vector from circle center to robot
        double dx = robotX - circleX;
        double dy = robotY - circleY;

        // Distance from robot to circle center
        double dist = Math.hypot(dx, dy);

        // Normalize that vector
        double nx = dx / dist;
        double ny = dy / dist;

        // Find the closest point on the circle
        double closestX = circleX + nx * radius;
        double closestY = circleY + ny * radius;

        // Heading from robot to that closest point (radians)
        return Math.atan2(closestY - robotY, closestX - robotX);
    }

    @Override
    public void execute() {
        // If subsystem is busy it is running a trajectory.
        if (!subsystem.isBusy()) {
            double curHeading = -subsystem.getExternalHeading();

            // The math & signs looks wonky, because this makes things field-relative
            // (Remember that "3 O'Clock" is zero degrees)
            double yvalue = -y.getAsDouble();
            double xvalue = -x.getAsDouble();
            if (driveStraighten != null) {
                if (driveStraighten.getAsDouble() > 0.7) {
                    if (Math.abs(yvalue) > Math.abs(xvalue)) xvalue = 0;
                    else yvalue = 0;
                }
            }
            Vector2d input = new Vector2d(
                yvalue * subsystem.speed,
                xvalue * subsystem.speed
            ).rotated(curHeading);

            subsystem.setWeightedDrivePower(
                new Pose2d(input.getX(), input.getY(), getRotation(curHeading))
            );
        }
        subsystem.update();
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean cancel) {
        if (cancel) subsystem.setDriveSignal(new DriveSignal());
    }
}
