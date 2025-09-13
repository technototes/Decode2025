package org.firstinspires.ftc.twenty403.commands.auto;

import com.technototes.library.command.Command;
import com.technototes.path.command.TrajectorySequenceCommand;
import org.firstinspires.ftc.twenty403.AutoConstants;
import org.firstinspires.ftc.twenty403.Robot;

public class Testing {

    public static Command ForwardBackward(Robot r) {
        return new TrajectorySequenceCommand(
            r.drivebaseSubsystem,
            AutoConstants.TEST_FORWARD_PATH
        ).andThen(
            new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.TEST_BACKWARD_PATH)
        );
    }

    public static Command LeftToRight(Robot r) {
        return new TrajectorySequenceCommand(
            r.drivebaseSubsystem,
            AutoConstants.TEST_LEFT_TO_RIGHT
        ).andThen(
            new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.TEST_RIGHT_TO_LEFT)
        );
    }

    public static Command FwdBackLeftRight(Robot r) {
        return new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.TEST_FORWARD_PATH)
            .andThen(
                new TrajectorySequenceCommand(
                    r.drivebaseSubsystem,
                    AutoConstants.TEST_BACKWARD_PATH
                )
            )
            .andThen(
                new TrajectorySequenceCommand(
                    r.drivebaseSubsystem,
                    AutoConstants.TEST_LEFT_TO_RIGHT
                )
            )
            .andThen(
                new TrajectorySequenceCommand(
                    r.drivebaseSubsystem,
                    AutoConstants.TEST_RIGHT_TO_LEFT
                )
            );
    }

    public static Command SplineTest1(Robot r) {
        return new TrajectorySequenceCommand(
            r.drivebaseSubsystem,
            AutoConstants.TEST_SPLINE_PATH_1
        );
    }

    public static Command SplineTest2(Robot r) {
        return new TrajectorySequenceCommand(
            r.drivebaseSubsystem,
            AutoConstants.TEST_SPLINE_PATH_2
        );
    }
}
