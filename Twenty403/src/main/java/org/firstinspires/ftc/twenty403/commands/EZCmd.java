package org.firstinspires.ftc.twenty403.commands;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.technototes.library.command.Command;

import org.firstinspires.ftc.twenty403.commands.driving.JoystickDriveCommand;

public class EZCmd {

    public static class Drive {

        public static Command NormalMode(Follower follower) {
            return Command.create(() -> follower.setMaxPowerScaling(0.85));
        }

        public static Command SnailMode(Follower follower) {
            return Command.create(() -> follower.setMaxPowerScaling(0.4));
        }

        public static Command TurboMode(Follower follower) {
            return Command.create(() -> follower.setMaxPowerScaling(1));
        }

        public static Command AutoAim() {
            return Command.create(() -> JoystickDriveCommand.faceTagMode = !JoystickDriveCommand.faceTagMode);
        }

        public static Command ResetGyro(Follower follower) {
            return Command.create(() -> follower.setPose(new Pose(follower.getPose().getX(), follower.getPose().getY(), 0.0)));
        }


    }
}
