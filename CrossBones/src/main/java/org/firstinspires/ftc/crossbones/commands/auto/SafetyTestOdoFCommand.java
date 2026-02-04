package org.firstinspires.ftc.crossbones.commands.auto;

import com.technototes.library.command.Command;
import org.firstinspires.ftc.crossbones.subsystems.SafetySubsystem;

public class SafetyTestOdoFCommand implements Command {

    private SafetySubsystem subsystem;

    @Override
    public void execute() {
        subsystem.simulateFail(SafetySubsystem.FailedPart.ODOF);
    }

    public SafetyTestOdoFCommand(SafetySubsystem d) {
        subsystem = d;
    }
}
