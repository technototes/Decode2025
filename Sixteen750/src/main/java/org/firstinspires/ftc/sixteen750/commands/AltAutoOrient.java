package org.firstinspires.ftc.sixteen750.commands;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierPoint;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.technototes.library.command.Command;
import com.technototes.library.logger.Log;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.Setup;
import org.firstinspires.ftc.sixteen750.subsystems.LauncherSubsystem;

@Configurable
public class AltAutoOrient implements Command {

    public Robot robot;

    public Pose wantedPose;
    public boolean firsttime = true;

    public AltAutoOrient(Robot r) {
        robot = r;
    }

    @Override
    public boolean isFinished() {
        //return !robot.follower.isBusy();
        return false;
        // I *believe* that a properly tuned robot shouldn't need all this stuff
        /*
        if (
            follower.atParametricEnd() &&
            follower.getHeadingError() < follower.getCurrentPath().getPathEndHeadingConstraint()
        ) {
            return true;
        } else if (
            follower.getVelocity().getMagnitude() <
                follower.getCurrentPath().getPathEndVelocityConstraint() &&
            follower.getPose().distanceFrom(follower.getCurrentPath().endPose()) < 2.54 &&
            follower.getAngularVelocity() < 0.055
        ) {
            return true;
        } else {
            return false;
        }*/
    }

    @Override
    public void execute() {
        if (firsttime) {
            robot.follower.update();
            wantedPose = new Pose(
                robot.follower.getPose().getX(),
                robot.follower.getPose().getY(),
                robot.follower.getPose().getHeading() -
                    Math.toRadians(robot.limelightSubsystem.getTX()) //.getTX .getLimelightRotation()
            );
            robot.follower.holdPoint(new BezierPoint(wantedPose), wantedPose.getHeading(), false);
            LauncherSubsystem.targetPower = 1;
            firsttime = false;
        }
    }

    //    @Override
    //    public void end(boolean s) {
    //        robot.follower.drivetrain.breakFollowing();
    //    }
}
