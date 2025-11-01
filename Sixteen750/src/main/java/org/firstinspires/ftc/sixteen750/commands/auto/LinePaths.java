package org.firstinspires.ftc.sixteen750.commands.auto;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class LinePaths {

    public PathChain Path2;
    public PathChain Path3;
    public PathChain Path4;
    public PathChain Path5;

    public Pose getStart() {
        return new Pose(32.671, 135.916, Math.toRadians(90));
    }

    public LinePaths(Follower follower) {
        Path2 = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(31.941, 135.281), new Pose(39.339, 102.782)))
            .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(125))
            .build();

        Path3 = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(39.339, 102.782), new Pose(57.768, 84.233)))
            .setLinearHeadingInterpolation(Math.toRadians(125), Math.toRadians(180))
            .build();

        Path4 = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(57.768, 84.233), new Pose(19.192, 84.233)))
            .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
            .build();

        Path5 = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(19.192, 84.233), new Pose(39.273, 103.257)))
            .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(125))
            .build();
    }
}
