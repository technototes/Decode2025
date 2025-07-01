package org.firstinspires.ftc.sixteen750.commands.auto;

import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.path.command.TrajectorySequenceCommand;
import org.firstinspires.ftc.sixteen750.AutoConstants;
import org.firstinspires.ftc.sixteen750.Robot;

public class AutoTestingCommand extends SequentialCommandGroup {

    public AutoTestingCommand(Robot r) {
        //        super(
        //                new TrajectorySequenceCommand(r.drivebase, WingRed.BACKWARD_TO_FORWARD)
        //                        .andThen(new TrajectorySequenceCommand(r.drivebase, WingRed.FORWARD_TO_BACKWARD))
        //        );
        super(
            new TrajectorySequenceCommand(r.drivebase, AutoConstants.START_TO_NETSCORING).andThen(
                new TrajectorySequenceCommand(r.drivebase, AutoConstants.NETSCORING_TO_INTAKE1)
            )
        );
    }
}
