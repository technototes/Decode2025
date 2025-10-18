package org.firstinspires.ftc.sixteen750.commands.auto;

import com.technototes.library.command.Command;
import com.technototes.library.command.ParallelCommandGroup;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.path.command.TrajectorySequenceCommand;
import org.firstinspires.ftc.sixteen750.AutoConstants;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.commands.PPPathCommand;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;
import org.firstinspires.ftc.sixteen750.subsystems.LauncherSubsystem;

public class Paths {
    public static Command splineTestCommand(Robot r) {
        return new TrajectorySequenceCommand(r.drivebase, AutoConstants.SPLINETEST1_TO_SPLINETEST2);
    }
    public static Command JustShootCommand(Robot r) {
    return new TrajectorySequenceCommand(r.drivebase, AutoConstants.BLUE_SCORING)
    .alongWith(TeleCommands.Launch(r))
            .andThen(new WaitCommand(3))
            //.andThen(TeleCommands.)
            ;
               // return new SequentialCommandGroup(TeleCommands.Launch(new LauncherSubsystem(Hardware h)));
    }

//    public static Command Pedropathcommand(Robot r){
//        return new PPPathCommand()
//    }
}
