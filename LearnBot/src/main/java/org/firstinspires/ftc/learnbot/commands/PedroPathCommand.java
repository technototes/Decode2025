package org.firstinspires.ftc.learnbot.commands;

import com.pedropathing.follower.Follower;
import com.pedropathing.paths.PathChain;
import com.technototes.library.command.Command;

public class PedroPathCommand implements Command {

    public PathChain pathChain;
    public Follower follower;

    public boolean currentPose;

    public PedroPathCommand(Follower f, PathChain p) {
        follower = f;
        pathChain = p;
    }

    public PedroPathCommand(Follower f, PathChain p, boolean currPose) {
        follower = f;
        pathChain = p;
        currentPose = currPose;
    }

    @Override
    public void initialize() {
        follower.followPath(pathChain);
        if (currentPose) {
            follower.setStartingPose(follower.getPose());
        }
    }

    // for getting if end im not sure how to get bot pose yet
    @Override
    public boolean isFinished() {
        return !follower.isBusy();
        /*        if (
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

    /*
    @Override
    public void end(boolean cancel) {
        if (cancel) follower.setMaxPowerScaling(0);
    }
    */
}
