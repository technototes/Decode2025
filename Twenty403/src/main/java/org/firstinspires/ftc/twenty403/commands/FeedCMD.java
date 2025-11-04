package org.firstinspires.ftc.twenty403.commands;

import com.bylazar.configurables.annotations.Configurable;
import com.technototes.library.command.Command;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import org.firstinspires.ftc.twenty403.Robot;
import org.firstinspires.ftc.twenty403.subsystems.FeedingSubsystem;
import org.firstinspires.ftc.twenty403.subsystems.LauncherSubsystem;

@Configurable
public class FeedCMD {

    public static double FEED_WAIT = 0.4;

    public static SequentialCommandGroup Feed(Robot r) {
        return Command.create(r.launcherSubsystem::Launch)
            .andThen(Command.create(r.feedingSubsystem::moveball).withTimeout(FEED_WAIT))
            .andThen(Command.create(r.feedingSubsystem::moveball).withTimeout(FEED_WAIT))
            .andThen(Command.create(r.feedingSubsystem::moveball).withTimeout(FEED_WAIT));
    }
}
