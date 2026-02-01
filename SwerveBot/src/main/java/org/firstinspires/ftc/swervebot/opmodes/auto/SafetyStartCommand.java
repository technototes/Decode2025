package org.firstinspires.ftc.swervebot.opmodes.auto;

import com.technototes.library.command.Command;
import org.firstinspires.ftc.swervebot.subsystems.SafetySubsystem;

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
