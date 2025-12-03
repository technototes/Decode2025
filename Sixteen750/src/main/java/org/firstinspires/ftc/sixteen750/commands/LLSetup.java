package org.firstinspires.ftc.sixteen750.commands;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.technototes.library.command.Command;

import org.firstinspires.ftc.sixteen750.Robot;

public class LLSetup implements Command {


    private Robot robot;
    public LLSetup(Robot r) {
        robot = r;
    }

    // for getting if end im not sure how to get bot pose yet
    @Override
    public boolean isFinished() {
        if (robot.limelightSubsystem.startup_done) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void execute() {
        robot.limelightSubsystem.LimelightStartup();
    }
}
