package org.firstinspires.ftc.sixteen750.commands.auto;

import com.technototes.library.command.Command;
import com.technototes.library.command.SequentialCommandGroup;

import org.firstinspires.ftc.sixteen750.subsystems.DrivebaseSubsystem;

public class DriveAutoCommand implements Command {

    public DrivebaseSubsystem drivebaseSubsystem;
    double p;
    public DriveAutoCommand(DrivebaseSubsystem s, double power) {
        drivebaseSubsystem = s;
        p = power;
    }

    @Override
    public void execute() {
        drivebaseSubsystem.setMotorPowers(p, p, p, p);
    }
}
