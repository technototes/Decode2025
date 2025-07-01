package org.firstinspires.ftc.sixteen750.commands.driving;

import com.technototes.library.command.Command;
import org.firstinspires.ftc.sixteen750.subsystems.DrivebaseSubsystem;

public class DrivingCommands {

    public static Command NormalDriving(DrivebaseSubsystem ds) {
        return Command.create(ds::setNormalMode);
    }

    public static Command SnailDriving(DrivebaseSubsystem ds) {
        return Command.create(ds::setSnailMode);
    }

    public static Command TurboDriving(DrivebaseSubsystem ds) {
        return Command.create(ds::setTurboMode);
    }

    public static Command NormalSpeed(DrivebaseSubsystem ds) {
        return Command.create(ds::auto);
    }

    public static Command SlowSpeed(DrivebaseSubsystem ds) {
        return Command.create(ds::slow);
    }

    public static Command FastSpeed(DrivebaseSubsystem ds) {
        return Command.create(ds::fast);
    }

    public static Command ResetGyro(DrivebaseSubsystem ds) {
        return Command.create(ds::setExternalHeading, 0.0);
    }

    public static Command RecordHeading(DrivebaseSubsystem drive) {
        return Command.create(drive::saveHeading);
    }
}
