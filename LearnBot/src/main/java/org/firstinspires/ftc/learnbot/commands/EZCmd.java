package org.firstinspires.ftc.learnbot.commands;

import com.technototes.library.command.Command;
import org.firstinspires.ftc.learnbot.commands.driving.JoystickDriveCommand;

public class EZCmd {

    public static class Drive {

        public static Command NormalMode(JoystickDriveCommand drive) {
            return Command.create(drive::SetNormal);
        }

        public static Command SnailMode(JoystickDriveCommand drive) {
            return Command.create(drive::SetSnail);
        }

        public static Command TurboMode(JoystickDriveCommand drive) {
            return Command.create(drive::SetTurbo);
        }

        public static Command AutoAim(JoystickDriveCommand drive) {
            return Command.create(drive::enableFaceTagMode);
        }

        public static Command ResetGyro(JoystickDriveCommand drive) {
            return Command.create(drive::ResetGyro);
        }

        public static Command RecordHeading(JoystickDriveCommand drive) {
            return Command.create(drive::SaveHeading);
        }
    }
}
