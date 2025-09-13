package org.firstinspires.ftc.twenty403.commands;


import com.pedropathing.follower.Follower;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.technototes.library.command.Command;

public class PPTrajectorySequenceCommand implements Command {

    public PathChain pathChain;
    public Follower follower;

    public PPTrajectorySequenceCommand(Follower f, PathChain p) {
        follower = f;
        pathChain = p;
    }
    public PathChain toPathChain(Path path){
        return new PathChain(path);
    }
    public PPTrajectorySequenceCommand(
            Follower f,
            Path p
    ) {
        follower = f;

        pathChain = toPathChain(p);
    }



    @Override
    public void initialize() {
        follower.followPath(pathChain);
    }

    // for getting if end im not sure how to get bot pose yet
    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void execute() {

    }

    @Override
    public void end(boolean cancel) {
        if (cancel) follower.setMaxPowerScaling(0);
    }
}
