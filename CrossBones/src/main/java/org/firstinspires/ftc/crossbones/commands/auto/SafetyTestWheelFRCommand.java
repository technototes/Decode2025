package org.firstinspires.ftc.crossbones.commands.auto;

import com.technototes.library.command.Command;
import org.firstinspires.ftc.crossbones.subsystems.SafetySubsystem;

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
