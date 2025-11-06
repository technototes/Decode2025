package org.firstinspires.ftc.learnbot.commands;

import com.technototes.library.command.Command;

public class DriveCmds {

    // These are *all* unnecessary.
    // You can just use "drive::SetNormalSpeed" as a command if you want. But that syntax is
    // confusing for students who are barely starting to understand code, let alone lambda's...

    public static Command NormalMode(JoystickDriveCommand drive) {
        return Command.create(drive::SetNormalSpeed);
    }

    public static Command SnailMode(JoystickDriveCommand drive) {
        return Command.create(drive::SetSnailSpeed);
    }

    public static Command TurboMode(JoystickDriveCommand drive) {
        return Command.create(drive::SetTurboSpeed);
    }

    public static Command AutoAim(JoystickDriveCommand drive) {
        return Command.create(drive::EnableVisionDriving);
    }

    public static Command ResetGyro(JoystickDriveCommand drive) {
        return Command.create(drive::ResetGyro);
    }

    public static Command RecordHeading(JoystickDriveCommand drive) {
        return Command.create(drive::SaveHeading);
    }
}
