package org.firstinspires.ftc.learnbot.commands.auto;

import com.technototes.library.command.Command;
import org.firstinspires.ftc.learnbot.subsystems.SafetySubsystem;

public class SafetyTestWheelRLCommand implements Command {

    private SafetySubsystem subsystem;

    @Override
    public void execute() {
        subsystem.simulateFail(SafetySubsystem.FailedPart.WHEELRL);
    }

    public SafetyTestWheelRLCommand(SafetySubsystem d) {
        subsystem = d;
    }
}
