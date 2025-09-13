package org.firstinspires.ftc.twenty403.commands.auto;

import com.technototes.library.command.Command;
import org.firstinspires.ftc.twenty403.subsystems.DrivebaseSubsystem;
import org.firstinspires.ftc.twenty403.subsystems.SafetySubsystem;

public class SafetyStartCommand implements Command {

    private SafetySubsystem subsystem;

    @Override
    public void execute() {
        subsystem.startMonitoring();
    }

    public SafetyStartCommand(SafetySubsystem d) {
        subsystem = d;
        addRequirements(d);
    }
}
