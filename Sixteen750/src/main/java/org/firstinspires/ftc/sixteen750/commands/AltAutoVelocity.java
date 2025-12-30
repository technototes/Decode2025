package org.firstinspires.ftc.sixteen750.commands;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.Pose;
import com.technototes.library.command.Command;

import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.Setup;
import org.firstinspires.ftc.sixteen750.subsystems.LimelightSubsystem;

@Configurable
public class AltAutoVelocity implements Command {

    public Robot robot;


    public AltAutoVelocity(Robot r) {
        robot = r;
    }

    @Override
    public boolean isFinished() {
        //return !robot.follower.isBusy();
        return false;

    }

    @Override
    public void execute() {
       robot.launcherSubsystem.Launch();

    }

    //    @Override
    //    public void end(boolean s) {
    //        robot.follower.drivetrain.breakFollowing();
    //    }
}
