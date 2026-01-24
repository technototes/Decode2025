package org.firstinspires.ftc.blackbird.commands;

import com.technototes.library.command.Command;
import org.firstinspires.ftc.blackbird.Robot;

public class LLTurnOff implements Command {

    private Robot robot;

    public LLTurnOff(Robot r) {
        robot = r;
    }

    // for getting if end im not sure how to get bot pose yet
    @Override
    public boolean isFinished() {
        if (robot.limelightSubsystem.startup_done == false) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void execute() {
        robot.limelightSubsystem.LimelightTurnOff();
    }
}
