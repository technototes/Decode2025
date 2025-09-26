package org.firstinspires.ftc.sixteen750.commands;

import com.pedropathing.follower.Follower;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathBuilder;
import com.pedropathing.paths.PathChain;
import com.technototes.library.command.Command;

public class PPPathCommand implements Command {

    public PathChain pathChain;
    public Follower follower;
    public PathBuilder pb;

    public boolean currentPose;

    public PPPathCommand(Follower f, PathChain p) {
        follower = f;
        pathChain = p;
    }

    public PathChain toPathChain(Path path) {
        return new PathChain(path);
    }

    public PPPathCommand(Follower f, Path p, boolean currPose) {
        follower = f;
        currentPose = currPose;
        pathChain = toPathChain(p);
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
        }
    }

    @Override
    public void execute() {}

    @Override
    public void end(boolean cancel) {
        if (cancel) follower.setMaxPowerScaling(0);
    }
}
