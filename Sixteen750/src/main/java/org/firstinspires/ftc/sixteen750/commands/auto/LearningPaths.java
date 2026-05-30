package org.firstinspires.ftc.sixteen750.commands.auto;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class LearningPaths {

    //public Follower follower;
    public PathChain FirstPathitsgoinginanS;
    public PathChain DownPath;

    public LearningPaths(Follower follower) {
        FirstPathitsgoinginanS = follower
            .pathBuilder()
            .addPath(
                new BezierCurve(
                    new Pose(43.771, 19.979),
                    new Pose(13.076, 65.047),
                    new Pose(107.411, 121.601)
                )
            )
            .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(180))
            .build();

        DownPath = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(107.411, 121.601), new Pose(82.208, 43.029)))
            .setTangentHeadingInterpolation()
            .build();
    }
}
