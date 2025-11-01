package org.firstinspires.ftc.learnbot.commands.auto;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class TestPaths {

    public PathChain Path1;
    public PathChain Path2;
    public PathChain Path3;
    public PathChain Path4;

    public Pose getStart() {
        return new Pose(72.000, 72.000, 90);
    }

    public TestPaths(Follower follower) {
        Path1 = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(72.000, 72.000), new Pose(72.000, 106.000)))
            .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(180))
            .build();

        Path2 = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(72.000, 106.000), new Pose(72.000, 72.000)))
            .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(270))
            .build();

        Path3 = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(72.000, 72.000), new Pose(72.000, 106.000)))
            .setLinearHeadingInterpolation(Math.toRadians(270), Math.toRadians(0))
            .build();

        Path4 = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(72.000, 106.000), new Pose(72.000, 72.000)))
            .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(90))
            .build();
    }
}
