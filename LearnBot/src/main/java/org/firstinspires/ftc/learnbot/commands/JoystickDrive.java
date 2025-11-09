package org.firstinspires.ftc.learnbot.commands;

import com.technototes.library.command.Command;
import com.technototes.library.control.Stick;
import com.technototes.library.logger.Loggable;
import java.util.function.DoubleSupplier;
import org.firstinspires.ftc.learnbot.DrivingConstants.Control;
import org.firstinspires.ftc.learnbot.subsystems.PedroDrivebaseSubsystem;

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

public class JoystickDrive implements Command, Loggable {

    // The sticks (probably each are CommandAxis suppliers)
    // Note that the stick values returned are oriented like this:
    // Up is a negative value, down is a positive value.
    // Left is a negative value, right is a positive value.
    DoubleSupplier x, y, r;
    PedroDrivebaseSubsystem drivebase;

    public JoystickDrive(PedroDrivebaseSubsystem drive, Stick xyStick, Stick rotStick) {
        // TODO: Throw an exception or log if there's some problem with constants.
        // i.e. DEAD_ZONE is negative, or greater than 1.0
        drivebase = drive;
        x = DeadZoneScale(xyStick.getXSupplier());
        y = DeadZoneScale(xyStick.getYSupplier());
        r = DeadZoneScale(rotStick.getXSupplier());
        addRequirements(drive);
    }

    @Override
    public void initialize() {
        drivebase.StartTele();
    }

    @Override
    public void execute() {
        // Read the stick values, and pass them to the drive base.
        // We invert the signs because both up and left are negative, which is opposite Pedro.
        // The drivebase can do all the filtering & drive mode shenanigans it wants.
        double fwdVal = -y.getAsDouble();
        double strafeVal = -x.getAsDouble();
        double rotVal = -r.getAsDouble();
        drivebase.RegisterJoystickRead(fwdVal, strafeVal, rotVal);
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
            if (Math.abs(val) <= Control.STICK_DEAD_ZONE) {
                return 0.0;
            }
            // If the value is outside the dead zone, scale it
            return (
                (val - Math.copySign(Control.STICK_DEAD_ZONE, val)) /
                (1.0 - Control.STICK_DEAD_ZONE)
            );
        };
    }
}
