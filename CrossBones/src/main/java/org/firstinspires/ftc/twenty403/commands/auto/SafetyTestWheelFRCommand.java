package org.firstinspires.ftc.twenty403.commands.auto;

import com.technototes.library.command.Command;
import org.firstinspires.ftc.twenty403.subsystems.SafetySubsystem;

public class SafetyTestWheelFRCommand implements Command {

    private SafetySubsystem subsystem;

    @Override
    public void execute() {
        subsystem.simulateFail(SafetySubsystem.FailedPart.WHEELFR);
    }

    public SafetyTestWheelFRCommand(SafetySubsystem d) {
        subsystem = d;
    }
}
