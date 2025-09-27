package org.firstinspires.ftc.twenty403.commands;


import com.bylazar.configurables.annotations.Configurable;
import com.technototes.library.command.Command;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import org.firstinspires.ftc.twenty403.Robot;
@Configurable
public class FeedCMD {
    public static double BETWEEN_LAUNCH_WAIT = 5;
    public static double FEED_WAIT = .1;

    public static SequentialCommandGroup Feed(Robot r) {
        return new SequentialCommandGroup(
                Command.create(r.feedingSubsystem::moveball),
                new WaitCommand(FEED_WAIT),
                Command.create(r.feedingSubsystem::stop),
                new WaitCommand(BETWEEN_LAUNCH_WAIT),
                Command.create(r.feedingSubsystem::moveball),
                new WaitCommand(FEED_WAIT),
                Command.create(r.feedingSubsystem::stop),
                new WaitCommand(BETWEEN_LAUNCH_WAIT),
                Command.create(r.feedingSubsystem::moveball),
                new WaitCommand(FEED_WAIT),
                Command.create(r.feedingSubsystem::stop)
        );
    }
}
