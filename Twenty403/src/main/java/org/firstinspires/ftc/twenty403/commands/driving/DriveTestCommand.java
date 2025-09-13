package org.firstinspires.ftc.twenty403.commands.driving;

import com.technototes.library.command.Command;
import org.firstinspires.ftc.twenty403.subsystems.DrivebaseSubsystem;

public class DriveTestCommand implements Command {

    public DrivebaseSubsystem subsystem;
    public double p;

    public DriveTestCommand(DrivebaseSubsystem s, double power) {
        subsystem = s;
        p = power;
    }

    @Override
    public void execute() {
        subsystem.setMotorPowers(p, p, p, p);
    }
}
