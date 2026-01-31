package org.firstinspires.ftc.learnbot.commands;

import com.technototes.library.command.Command;
import com.technototes.library.control.Stick;
import com.technototes.library.logger.Loggable;
import com.technototes.library.util.MathUtils;
import java.util.function.DoubleSupplier;
import org.firstinspires.ftc.learnbot.DrivingConstants.Control;
import org.firstinspires.ftc.learnbot.subsystems.PedroDrivebaseSubsystem;

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
        x = xyStick.getXSupplier();
        y = xyStick.getYSupplier();
        r = rotStick.getXSupplier();
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
        // The drivebase can do all the filtering & drive mode shenanigans it wants. We're just
        // here to read the joysticks and send the values to the drivebase...
        double fwdVal = -MathUtils.deadZoneScale(y.getAsDouble(), Control.STICK_DEAD_ZONE);
        double strafeVal = -MathUtils.deadZoneScale(x.getAsDouble(), Control.STICK_DEAD_ZONE);
        double rotVal = -MathUtils.deadZoneScale(r.getAsDouble(), Control.STICK_DEAD_ZONE);
        drivebase.RegisterJoystickRead(fwdVal, strafeVal, rotVal);
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
