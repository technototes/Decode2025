package org.firstinspires.ftc.sixteen750.commands;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.technototes.library.command.Command;

public class PedroPathCommand implements Command {

    public PathChain pathChain;
    public Follower follower;
    public Pose begin;

    public boolean currentPose;

    public PedroPathCommand(Follower f, PathChain p) {
        follower = f;
        pathChain = p;
    }

    public PedroPathCommand(Follower f, Pose startPose, PathChain p) {
        follower = f;
        pathChain = p;
        currentPose = true;
        begin = startPose;
    }

    public PedroPathCommand(Follower f, PathChain p, boolean currPose) {
        follower = f;
        pathChain = p;
        currentPose = currPose;
        begin = null;
    }

    @Override
    public void initialize() {
        if (currentPose) {
            follower.setStartingPose(begin == null ? follower.getPose() : begin);
        }
        follower.followPath(pathChain);
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
