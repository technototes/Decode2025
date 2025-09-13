package org.firstinspires.ftc.sixteen750.commands.auto;

import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.path.command.TrajectorySequenceCommand;
import org.firstinspires.ftc.sixteen750.AutoConstants;
import org.firstinspires.ftc.sixteen750.Robot;

public class SideAndBackCommand extends SequentialCommandGroup {

    public SideAndBackCommand(Robot r) {
        super(
            new TrajectorySequenceCommand(
                r.drivebase,
                AutoConstants.SIDE_LEFT_TO_SIDE_RIGHT
            ).andThen(
                new TrajectorySequenceCommand(r.drivebase, AutoConstants.SIDE_RIGHT_TO_SIDE_LEFT)
            )
        );
    }
}
