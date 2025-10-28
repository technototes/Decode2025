package org.firstinspires.ftc.learnbot.commands.auto;

import com.technototes.library.command.Command;
import org.firstinspires.ftc.learnbot.subsystems.DrivebaseSubsystem;

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
