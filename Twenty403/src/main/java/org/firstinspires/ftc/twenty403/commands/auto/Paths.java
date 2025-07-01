package org.firstinspires.ftc.twenty403.commands.auto;

import com.technototes.library.command.Command;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.path.command.TrajectorySequenceCommand;
import org.firstinspires.ftc.twenty403.AutoConstants;
import org.firstinspires.ftc.twenty403.Robot;

public class Paths {

    public static Command ObservationScoring(Robot r) {
        return new TrajectorySequenceCommand(
            r.drivebaseSubsystem,
            AutoConstants.PUSH_BOT_OBSERVATION_SIDE_AUTO1
        )
            .andThen(
                new TrajectorySequenceCommand(
                    r.drivebaseSubsystem,
                    AutoConstants.PUSH_BOT_OBSERVATION_SIDE_AUTO2
                )
            )
            .andThen(
                new TrajectorySequenceCommand(
                    r.drivebaseSubsystem,
                    AutoConstants.PUSH_BOT_OBSERVATION_SIDE_AUTO4
                )
            )
            .andThen(
                new TrajectorySequenceCommand(
                    r.drivebaseSubsystem,
                    AutoConstants.PUSH_BOT_OBSERVATION_SIDE_AUTO4HALF
                )
            )
            .andThen(
                new TrajectorySequenceCommand(
                    r.drivebaseSubsystem,
                    AutoConstants.PUSH_BOT_OBSERVATION_SIDE_AUTO5
                )
            )
            .andThen(
                new TrajectorySequenceCommand(
                    r.drivebaseSubsystem,
                    AutoConstants.PUSH_BOT_OBSERVATION_SIDE_AUTO6
                )
            )
            .andThen(
                new TrajectorySequenceCommand(
                    r.drivebaseSubsystem,
                    AutoConstants.PUSH_BOT_OBSERVATION_SIDE_AUTO7
                )
            )
            .andThen(
                new TrajectorySequenceCommand(
                    r.drivebaseSubsystem,
                    AutoConstants.PUSH_BOT_OBSERVATION_SIDE_AUTO8
                )
            )
            .andThen(
                new TrajectorySequenceCommand(
                    r.drivebaseSubsystem,
                    AutoConstants.PUSH_BOT_OBSERVATION_SIDE_AUTO9
                )
            )
            .andThen(
                new TrajectorySequenceCommand(
                    r.drivebaseSubsystem,
                    AutoConstants.PUSH_BOT_OBSERVATION_SIDE_AUTO10
                )
            );
    }

    public static Command SampleScoring(Robot r) {
        return new TrajectorySequenceCommand(
            r.drivebaseSubsystem,
            AutoConstants.START_TO_NETSCORING
        )
            .andThen(new WaitCommand(1))
            .andThen(
                new WaitCommand(0.5)
                    .andThen(
                        new TrajectorySequenceCommand(
                            r.drivebaseSubsystem,
                            AutoConstants.PUSHY_BOT_1
                        )
                    )
                    .andThen(
                        new TrajectorySequenceCommand(
                            r.drivebaseSubsystem,
                            AutoConstants.PUSHY_BOT_2
                        )
                    )
                    .andThen(
                        new TrajectorySequenceCommand(
                            r.drivebaseSubsystem,
                            AutoConstants.PUSHY_BOT_3
                        )
                    )
            );
    }

    public static Command ForwardBackwardSide(Robot r) {
        return new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.FORWARD_BACKWARD1)
            .andThen(
                new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.FORWARD_BACKWARD2)
            )
            .andThen(
                new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.FORWARD_BACKWARD3)
            )
            .andThen(
                new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.FORWARD_BACKWARD4)
            );
    }

    public static Command PrestonPath(Robot r) {
        return new TrajectorySequenceCommand(
            r.drivebaseSubsystem,
            AutoConstants.FORWARD_BACKWARD1
        ).andThen(
            new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.FORWARD_BACKWARD2)
        );
    }

    public static Command FirstObsPath(Robot r) {
        return new SequentialCommandGroup(
            new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.FIRST_TO_SECOND),
            new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.SECOND_TO_THIRD),
            new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.THIRD_TO_FOURTH),
            new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.FOURTH_TO_FIFTH),
            new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.FIFTH_TO_SIXTH),
            new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.SIXTH_TO_SEVENTH),
            new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.SEVENTH1_TO_SEVENTH2),
            new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.SEVENTH2_TO_SEVENTH3),
            new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.SEVENTH3_TO_SEVENTH4),
            new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.SEVENTH4_TO_EIGHTH),
            new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.EIGHTH_TO_EIGHTH1),
            new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.EIGHTH1_TO_EIGHTH2),
            new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.EIGHTH2_TO_EIGHTH3),
            new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.EIGHTH3_TO_EIGHTH4),
            new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.EIGHTH4_TO_NINTH),
            new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.NINTH_TO_TENTH),
            new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.TENTH_TO_ELEVENTH),
            new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.ELEVENTH_TO_TWELFTH),
            new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.ELEVENTH_TO_TWELFTH),
            new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.TWELFTH_TO_THIRTEENTH)
        );
    }

    public static Command ParkPath(Robot r) {
        return new SequentialCommandGroup(
            new TrajectorySequenceCommand(r.drivebaseSubsystem, AutoConstants.PARK_START_TO_END)
        );
    }

    public static Command Obs_Parking(Robot r) {
        return new TrajectorySequenceCommand(
            r.drivebaseSubsystem,
            AutoConstants.OBSERVATION_PARKY_WARKY
        );
    }
}
