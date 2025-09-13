package org.firstinspires.ftc.twenty403.commands;

import com.technototes.library.command.Command;
import org.firstinspires.ftc.twenty403.subsystems.DrivebaseSubsystem;

public class EZCmd {

    public static class Drive {

        public static Command NormalMode(DrivebaseSubsystem drive) {
            return Command.create(drive::setNormalMode);
        }

        public static Command SnailMode(DrivebaseSubsystem drive) {
            return Command.create(drive::setSnailMode);
        }

        public static Command TurboMode(DrivebaseSubsystem drive) {
            return Command.create(drive::setTurboMode);
        }

        public static Command ResetGyro(DrivebaseSubsystem drive) {
            return Command.create(drive::setExternalHeading, 0.0);
        }

        public static Command RecordHeading(DrivebaseSubsystem drive) {
            return Command.create(drive::saveHeading);
        }
    }
}
