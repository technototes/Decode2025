package org.firstinspires.ftc.sixteen750.commands;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.technototes.library.command.Command;
import org.firstinspires.ftc.sixteen750.Setup;

public class PedroPathCommand implements Command {

    public PathChain pathChain;
    public Follower follower;
    public Pose begin;
    public double maxPowerScaling;
    public boolean currentPose;

    public PedroPathCommand(Follower f, PathChain p, double maxPower) {
        follower = f;
        pathChain = p;
        maxPowerScaling = maxPower;
        currentPose = false;
        begin = null;
    }

    public PedroPathCommand(Follower f, PathChain p) {
        this(f, p, 0);
    }

    public PedroPathCommand(Follower f, Pose startPose, PathChain p, double maxPower) {
        follower = f;
        pathChain = p;
        currentPose = true;
        begin = startPose;
        maxPowerScaling = maxPower;
    }

    public PedroPathCommand(Follower f, PathChain p, boolean currPose, double maxPower) {
        follower = f;
        pathChain = p;
        currentPose = currPose;
        begin = null;
        maxPowerScaling = maxPower;
    }

    public PedroPathCommand(Follower f, Pose startPose, PathChain p) {
        this(f, startPose, p, 0);
    }

    public PedroPathCommand(Follower f, PathChain p, boolean currPose) {
        this(f, p, currPose, 0);
    }

    @Override
    public void initialize() {
        // I'm not sure we want to do this here...
        follower.setMaxPowerScaling(Setup.OtherSettings.AUTO_SCALING);
        if (currentPose) {
            follower.setStartingPose(begin == null ? follower.getPose() : begin);
        }
        if (maxPowerScaling > 0) {
            follower.followPath(pathChain, maxPowerScaling, false);
        } else {
            follower.followPath(pathChain);
        }
    }

    @Override
    public boolean isFinished() {
        return !follower.isBusy();
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
        follower.update();
    }
}
