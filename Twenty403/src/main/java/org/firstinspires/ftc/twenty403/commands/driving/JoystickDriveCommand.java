package org.firstinspires.ftc.twenty403.commands.driving;

import com.technototes.library.command.Command;
import com.technototes.library.control.Stick;
import com.technototes.library.util.MathUtils;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import org.firstinspires.ftc.twenty403.Robot;
import org.firstinspires.ftc.twenty403.Setup;
import org.firstinspires.ftc.twenty403.subsystems.DrivebaseSubsystem;
import org.firstinspires.ftc.twenty403.subsystems.DrivebaseSubsystem.DriveConstants;

public class JoystickDriveCommand implements Command {

    public DrivebaseSubsystem subsystem;
    public DoubleSupplier x, y, r;
    public BooleanSupplier watchTrigger;
    public DoubleSupplier straightDrive;
    public DoubleSupplier drive45;

    private Robot rob;

    public JoystickDriveCommand(
        Robot robot,
        Stick xyStick,
        Stick rotStick,
        DoubleSupplier strtDrive,
        DoubleSupplier angleDrive
    ) {
        rob = robot;
        addRequirements(robot.drivebaseSubsystem);
        subsystem = robot.drivebaseSubsystem;
        x = xyStick.getXSupplier();
        y = xyStick.getYSupplier();
        r = rotStick.getXSupplier();
        robot.gyro = robot.drivebaseSubsystem.getGyro();
        straightDrive = strtDrive;
        drive45 = angleDrive;
    }

    // Use this constructor if you don't want auto-straightening
    public JoystickDriveCommand(Robot robot, Stick xyStick, Stick rotStick) {
        this(robot, xyStick, rotStick, null, null);
    }

    public static boolean isTriggered(DoubleSupplier ds) {
        return ds != null && ds.getAsDouble() > DriveConstants.TRIGGER_THRESHOLD;
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
            return -Math.pow(r.getAsDouble(), 3) * DriveConstants.NORMAL_ROTATION_SCALE;
        }

        // headingInRads is [0-2pi]
        double heading = Math.toDegrees(headingInRads);
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
            return Math.cbrt(normalized) * DriveConstants.SLOW_ROTATION_SCALE;
        } else {
            return (normalized) * DriveConstants.NORMAL_ROTATION_SCALE;
        }
    }

    @Override
    public void execute() {
        // If subsystem is busy it is running a trajectory.
        double curHeading = subsystem.getGyro();
        rob.gyro = curHeading;

        // The math & signs looks wonky, because this makes things field-relative
        // (Remember that "3 O'Clock" is zero degrees)
        // We are making this change for the omni wheels on 20403
        double yvalue = y.getAsDouble();
        double xvalue = x.getAsDouble();
        if (isTriggered(straightDrive)) {
            if (Math.abs(yvalue) > Math.abs(xvalue)) {
                xvalue = 0;
            } else {
                yvalue = 0;
            }
        }
        rob.rv = getRotation(curHeading);
        rob.yv = yvalue;
        rob.xv = xvalue;
        subsystem.joystickDriveWithGyro(xvalue, yvalue, rob.rv, curHeading);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    /*
    @Override
    public void end(boolean cancel) {
        if (cancel) subsystem.setDriveSignal(new DriveSignal());
    }
    */
}
