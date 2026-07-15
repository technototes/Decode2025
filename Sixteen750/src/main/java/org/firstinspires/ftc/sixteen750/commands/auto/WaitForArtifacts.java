package org.firstinspires.ftc.sixteen750.commands.auto;

import com.technototes.library.command.Command;
import com.technototes.library.command.WaitCommand;
import org.firstinspires.ftc.sixteen750.subsystems.IntakeSubsystem;

public class WaitForArtifacts implements Command {

    IntakeSubsystem intake;

    public WaitForArtifacts(IntakeSubsystem intakeSubsystem) {
        this.intake = intakeSubsystem;
    }

    @Override
    public void execute() {
        intake.Intake();
    }

    // Run this command until we've successfully acquired 3 artifacts...
    @Override
    public boolean isFinished() {
        return intake.intakeFull;
    }

    // I think if the command gets cancelled (it's timed out by an parallel wait command)
    // then we want to leave the intake going? If not, switch to hold?
    // If we don't need to do that, you can delete this function.
    public void end(boolean cancel) {
        if (!cancel) {
            intake.Hold();
        }
    }
}
