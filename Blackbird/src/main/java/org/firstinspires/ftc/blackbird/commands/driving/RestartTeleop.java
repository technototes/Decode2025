package org.firstinspires.ftc.blackbird.commands.driving;

import com.pedropathing.geometry.BezierPoint;
import com.pedropathing.geometry.Pose;
import com.technototes.library.command.Command;
import org.firstinspires.ftc.blackbird.Robot;

public class RestartTeleop implements Command {

    public Robot robot;

    public RestartTeleop(Robot r) {
        robot = r;
    }

    @Override
    public boolean isFinished() {
        return !robot.follower.isBusy();
    }

    @Override
    public void execute() {}

    @Override
    public void end(boolean s) {
        robot.follower.drivetrain.breakFollowing();
    }
}
