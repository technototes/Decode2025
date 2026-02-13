package org.firstinspires.ftc.swervebot.commands;

import com.technototes.library.command.Command;
import com.technototes.library.control.Stick;
import com.technototes.library.logger.Loggable;

import org.firstinspires.ftc.swervebot.swerveutil.SimpleCoaxSwerveDriveSubsystem;

import java.util.function.DoubleSupplier;

public class SwerveDriveCmd implements Command, Loggable {

    public SimpleCoaxSwerveDriveSubsystem subsystem;
    public DoubleSupplier x;
    public DoubleSupplier y;
    public DoubleSupplier r;

    public SwerveDriveCmd(SimpleCoaxSwerveDriveSubsystem sub, Stick xyStick, Stick rStick) {
        addRequirements(sub);
        subsystem = sub;
        x = xyStick.getXSupplier();
        x = xyStick.getYSupplier();
        x = rStick.getXSupplier();
    }

    // This will make the bot snap to an angle, if the 'straighten' button is pressed
    // Otherwise, it just reads the rotation value from the rotation stick

    @Override
    public void execute() {
        double xvalue = -x.getAsDouble();
        double yvalue = y.getAsDouble();
        double rvalue = r.getAsDouble();
        subsystem.updateValues(xvalue, yvalue, rvalue);
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
