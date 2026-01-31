package org.firstinspires.ftc.twenty403.commands;

import com.bylazar.configurables.annotations.Configurable;
import com.technototes.library.command.Command;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import java.util.function.BooleanSupplier;
import org.firstinspires.ftc.twenty403.Robot;
import org.firstinspires.ftc.twenty403.subsystems.FeedingSubsystem;
import org.firstinspires.ftc.twenty403.subsystems.LauncherSubsystem;

@Configurable
public class FeedCMD {

    public static double FEED_WAIT = 3.2;
    public static double LAUNCH_STARTUP = 1.7;
    public static double RETURN_TO_PEAK = 1.8;

    public static SequentialCommandGroup Feed(Robot r) {
        return Command.create(r.launcherSubsystem::AutoLaunch)
            .andThen(new WaitCommand(LAUNCH_STARTUP))
            .andThen(Command.create(r.feedingSubsystem::moveball))
            .andThen(new WaitCommand(FEED_WAIT))
            .andThen(Command.create(r.feedingSubsystem::stop));
    }
}
