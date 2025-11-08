package org.firstinspires.ftc.sixteen750.commands.driving;

import com.technototes.library.command.Command;
import org.firstinspires.ftc.sixteen750.commands.PedroDriver;

public class DrivingCommands {

    public static Command NormalDriving(PedroDriver pd) {
        return pd::SetNormalSpeed;
    }

    public static Command SnailDriving(PedroDriver pd) {
        return pd::SetSnailSpeed;
    }

    public static Command TurboDriving(PedroDriver pd) {
        return pd::SetTurboSpeed;
    }

    public static Command AutoOrient(PedroDriver pd) {
        return pd::EnableVisionDriving;
    }

    public static Command ResetGyro(PedroDriver pd) {
        return pd::ResetGyro;
    }
}
