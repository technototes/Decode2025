package org.firstinspires.ftc.learnbot;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class TestPaths {

    public static double org = 72.0;
    public static double dist = 2.0;
    public PathChain Path1;
    public PathChain Path2;
    public PathChain Path3;
    public PathChain Path4;

    public Pose getStart() {
        return new Pose(org, org, 0);
    }

    private static final double halfPi = Math.PI / 6.0;

    public TestPaths(Follower follower) {
        Path1 = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(org, org), new Pose(org + dist, org)))
            .setLinearHeadingInterpolation(0, halfPi)
            .build();

        Path2 = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(org + dist, org), new Pose(org, org)))
            .setLinearHeadingInterpolation(halfPi, 0)
            .build();

        Path3 = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(org, org), new Pose(org + dist, org)))
            .setLinearHeadingInterpolation(0, halfPi - 0.2)
            .build();

        Path4 = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(org + dist, org), new Pose(org, org)))
            .setLinearHeadingInterpolation(halfPi - .2, halfPi)
            .build();
    }
}
