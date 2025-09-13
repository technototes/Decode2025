package org.firstinspires.ftc.twenty403.commands.driving;

import static org.firstinspires.ftc.twenty403.subsystems.DrivebaseSubsystem.DriveConstants.NORMAL_ROTATION_SCALE;
import static org.firstinspires.ftc.twenty403.subsystems.DrivebaseSubsystem.DriveConstants.SLOW_ROTATION_SCALE;
import static org.firstinspires.ftc.twenty403.subsystems.DrivebaseSubsystem.DriveConstants.TRIGGER_THRESHOLD;

import com.acmerobotics.roadrunner.drive.DriveSignal;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.technototes.library.command.Command;
import com.technototes.library.control.Stick;
import com.technototes.library.logger.Loggable;
import com.technototes.library.util.MathUtils;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.regex.MatchResult;
import org.firstinspires.ftc.twenty403.Setup;
import org.firstinspires.ftc.twenty403.subsystems.DrivebaseSubsystem;

public class JoystickDriveCommand implements Command, Loggable {

    public DrivebaseSubsystem subsystem;
    public DoubleSupplier x, y, r;
    public BooleanSupplier watchTrigger;
    public DoubleSupplier straightDrive;
    public DoubleSupplier drive45;
    public double targetHeadingRads;

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
        straightDrive = strtDrive;
        drive45 = angleDrive;
    }

    // Use this constructor if you don't want auto-straightening
    public JoystickDriveCommand(DrivebaseSubsystem sub, Stick xyStick, Stick rotStick) {
        this(sub, xyStick, rotStick, null, null);
    }

    public static boolean isTriggered(DoubleSupplier ds) {
        if (ds == null || ds.getAsDouble() < DrivebaseSubsystem.DriveConstants.TRIGGER_THRESHOLD) {
            return false;
        }
        return true;
    }

    // This will make the bot snap to an angle, if the 'straighten' button is pressed
    // Otherwise, it just reads the rotation value from the rotation stick
    private double getRotation(double headingInRads) {
        // Check to see if we're trying to straighten the robot
        // Don't straighten in turbo: The bot goes crazy
        boolean straightTrigger = isTriggered(straightDrive);
        boolean fortyfiveTrigger = isTriggered(drive45);
        if (subsystem.isTurboMode() || (!straightTrigger && !fortyfiveTrigger)) {
            // No straighten override: return the stick value
            // (with some adjustment...)
            return -Math.pow(r.getAsDouble(), 3) * NORMAL_ROTATION_SCALE;
        }

        // headingInRads is [0-2pi]
        double heading = -Math.toDegrees(headingInRads);
        // Snap to the closest 90 or 270 degree angle (for going through the depot)
        double close = straightTrigger
            ? MathUtils.closestTo(heading, 0, 90, 180, 270, 360)
            : MathUtils.closestTo(heading, 45, 135, 225, 315);
        double offBy = close - heading;
        // Normalize the error to -1 to 1
        double normalized = Math.max(Math.min(offBy / 45, 1.), -1.);
        // Dead zone of 5 degreesLiftHighJunctionCommand(liftSubsystem)
        if (Math.abs(normalized) < Setup.OtherSettings.STRAIGHTEN_DEAD_ZONE) {
            return 0.0;
        }
        // Scale it by the cube root, the scale that down by 30%
        // .9 (about 40 degrees off) provides .96 power => .288
        // .1 (about 5 degrees off) provides .46 power => .14
        if (subsystem.isSnailMode()) {
            return Math.cbrt(normalized) * SLOW_ROTATION_SCALE;
        } else {
            return (normalized) * NORMAL_ROTATION_SCALE;
        }
    }

    @Override
    public void execute() {
        // If subsystem is busy it is running a trajectory.
        if (!subsystem.isBusy()) {
            double curHeading = -subsystem.getExternalHeading();

            // The math & signs looks wonky, because this makes things field-relative
            // (Remember that "3 O'Clock" is zero degrees)
            // We are making this change for the omni wheels on 20403
            double yvalue = -y.getAsDouble();
            double xvalue = -x.getAsDouble();
            if (straightDrive != null) {
                if (straightDrive.getAsDouble() > TRIGGER_THRESHOLD) {
                    if (Math.abs(yvalue) > Math.abs(xvalue)) xvalue = 0;
                    else yvalue = 0;
                }
            }
            Vector2d input = new Vector2d(
                yvalue * subsystem.speed,
                xvalue * subsystem.speed
            ).rotated(curHeading);
            // TODO:
            // Calculate the magnitude of the motion to scale the speed by...
            // then call subsystem.setMag(mag)
            // find a student who's done physics for this one...
            // (Also: We probably want to pick the larger of the magnitude of the drive sticks,
            // and the rotation stick)
            // We could also use this for implementating "snail mode" and "turbo mode"
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
