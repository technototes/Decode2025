package org.firstinspires.ftc.twenty403.commands;

import com.bylazar.configurables.annotations.Configurable;
import com.technototes.library.command.Command;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import org.firstinspires.ftc.twenty403.Robot;
import org.firstinspires.ftc.twenty403.subsystems.FeedingSubsystem;
import org.firstinspires.ftc.twenty403.subsystems.LauncherSubsystem;

import java.util.function.BooleanSupplier;

@Configurable
public class FeedCMD {

    public static double FEED_WAIT = 0.4;
    public static double LAUNCH_STARTUP = 4.3;
    public static double RETURN_TO_PEAK = 3.8;

    public static SequentialCommandGroup Feed(Robot r) {
        return Command.create(r.launcherSubsystem::Launch)
                .andThen(new WaitCommand(LAUNCH_STARTUP))
        .andThen(Command.create(r.feedingSubsystem::moveball))
                .andThen(new WaitCommand(FEED_WAIT))
                .andThen(Command.create(r.feedingSubsystem::stop))
                .andThen(new WaitCommand(RETURN_TO_PEAK))
            .andThen(Command.create(r.feedingSubsystem::moveball))
                .andThen(new WaitCommand(FEED_WAIT))
                .andThen(Command.create(r.feedingSubsystem::stop))
                .andThen(new WaitCommand(RETURN_TO_PEAK))
            .andThen(Command.create(r.feedingSubsystem::moveball))
                .andThen(new WaitCommand(FEED_WAIT))
                .andThen(Command.create(r.feedingSubsystem::stop));
    }
}
