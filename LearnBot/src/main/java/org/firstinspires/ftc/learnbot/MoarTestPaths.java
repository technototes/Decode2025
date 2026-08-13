package org.firstinspires.ftc.learnbot;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.HeadingInterpolator;
import com.pedropathing.paths.PathChain;

/**** DO NOT EDIT ****
 These paths are specifically for testing the visualizer. If you want to make some
 changes to the 'real' paths, just create a different file...
 **** DO NOT EDIT ****/

@Configurable
public class MoarTestPaths {

    public PathChain Path1;

    public MoarTestPaths(Follower follower) {
        Path1 = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(5, 5), new Pose(5, 75)))
            .setHeadingInterpolation(HeadingInterpolator.reversedLinear(-0.1, 0.1))
            .build();
    }
}
