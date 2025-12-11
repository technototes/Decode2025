package org.firstinspires.ftc.sixteen750.commands.auto;

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
    public PathChain Path5;

    public TestPaths(Follower follower) {
        Path1 = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(95.670, 7.725), new Pose(95.868, 35.257)))
            .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(180))
            .build();

        Path2 = follower
            .pathBuilder()
            .addPath(
                new BezierCurve(
                    new Pose(95.868, 35.257),
                    new Pose(81.607, 37.040),
                    new Pose(82.399, 27.730),
                    new Pose(90.916, 28.721)
                )
            )
            .setConstantHeadingInterpolation(Math.PI)
            .build();

        Path3 = follower
            .pathBuilder()
            .addPath(
                new BezierCurve(
                    new Pose(90.916, 28.721),
                    new Pose(105.970, 26.542),
                    new Pose(105.970, 34.663),
                    new Pose(95.670, 35.257)
                )
            )
            .setConstantHeadingInterpolation(Math.PI)
            .build();

        Path4 = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(95.670, 35.257), new Pose(62.195, 35.257)))
            .setConstantHeadingInterpolation(Math.PI)
            .build();
        Path5 = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(62.195, 35.257), new Pose(95.670, 7.725)))
            .setLinearHeadingInterpolation(Math.PI, Math.PI / 2)
            .build();
    }
}
