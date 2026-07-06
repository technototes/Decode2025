package org.firstinspires.ftc.sixteen750.commands;

import com.technototes.library.command.Command;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import org.firstinspires.ftc.sixteen750.Robot;

public class AutoCommands {

    public static Command AutoLaunching3Balls(Robot r) {
        return new SequentialCommandGroup(
            //TeleCommands.IntakeStop(r),
            TeleCommands.Intake(r),
            // no need to wait for spinup as we will leave the flywheel spinning constantly during auto
            //switched to slow intake to remove the up down up down of the gate aswell as drain less power
            TeleCommands.GateDown(r),
            new WaitCommand(0.52),
            TeleCommands.GateUp(r)

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
            new WaitCommand(.65),
            TeleCommands.GateUp(r),
            TeleCommands.Intake(r)
        )
            .raceWith(new AltAutoOrient(r));

        // want to keep launcher running during auto also no need to stop intake

        //.raceWith(new AltAutoOrient(r));
    }
}
