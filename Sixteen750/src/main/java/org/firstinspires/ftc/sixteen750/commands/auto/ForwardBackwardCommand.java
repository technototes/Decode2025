package org.firstinspires.ftc.sixteen750.commands.auto;

import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.path.command.TrajectorySequenceCommand;
import org.firstinspires.ftc.sixteen750.PathConstants;
import org.firstinspires.ftc.sixteen750.Robot;

public class ForwardBackwardCommand extends SequentialCommandGroup {

    public ForwardBackwardCommand(Robot r) {
        //        super(
        //                new TrajectorySequenceCommand(r.drivebase, WingRed.BACKWARD_TO_FORWARD)
        //                        .andThen(new TrajectorySequenceCommand(r.drivebase, WingRed.FORWARD_TO_BACKWARD))
        //        );
        super(
            new TrajectorySequenceCommand(r.drivebase, PathConstants.BACKWARD_TO_FORWARD).andThen(
                new TrajectorySequenceCommand(r.drivebase, PathConstants.FORWARD_TO_BACKWARD)
            )
        );
    }
}
