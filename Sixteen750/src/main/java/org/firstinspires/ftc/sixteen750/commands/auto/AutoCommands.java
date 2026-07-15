package org.firstinspires.ftc.sixteen750.commands.auto;

import com.technototes.library.command.Command;
import com.technototes.library.command.ParallelRaceGroup;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.commands.AltAutoOrient;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;
import org.firstinspires.ftc.sixteen750.subsystems.IntakeSubsystem;

public class AutoCommands {

    public static Command AutoLaunching3Balls(Robot r) {
        return new SequentialCommandGroup(
            //TeleCommands.IntakeStop(r),
            TeleCommands.Feed(r),
            // no need to wait for spinup as we will leave the flywheel spinning constantly during auto
            //switched to slow intake to remove the up down up down of the gate aswell as drain less power
            TeleCommands.GateDown(r),
            new WaitCommand(0.5),
            TeleCommands.GateUp(r),
            TeleCommands.Intake(r)

            // want to keep launcher running during auto also no need to stop intake
        )
            //.alongWith(new AltAutoOrient(r));
            .raceWith(new AltAutoOrient(r));
    }

    public static Command AutoLaunching3BallsFar(Robot r) {
        return new SequentialCommandGroup(
            TeleCommands.GateUp(r),
            TeleCommands.Intake(r),
            // no need to wait for spinup as we will leave the flywheel spinning constantly during auto
            //switched to slow intake to remove the up down up down of the gate aswell as drain less power
            TeleCommands.GateDown(r),
            new WaitCommand(.6),
            TeleCommands.GateUp(r),
            TeleCommands.Intake(r)
        )
            .raceWith(new AltAutoOrient(r));

        // want to keep launcher running during auto also no need to stop intake

        //.raceWith(new AltAutoOrient(r));
    }

    abstract static class WaitForArtifacts implements Command {

        @Override
        public boolean isFinished() {
            return IntakeSubsystem.robotFull;
        }

        public static Command Intake(Robot r) {
            return Command.create(r.intakeSubsystem::Intake);
        }
    }

    public Command AutoIntake(Robot r) {
        return new ParallelRaceGroup(new WaitCommand(10), WaitForArtifacts.Intake(r));
    }
}
