package org.firstinspires.ftc.twenty403;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class Paths {

    public PathChain BlueGoalToEscape;
    public PathChain RedGoalToEscape;
    //orientation 143
    public Pose start = new Pose(26.507, 131.641);
    //orientation 37
    public Pose Rstart = new Pose(117.850, 131.462);
    public Pose BGoalBezierPointToEscape = new Pose(31.164, 120.358);
    public Pose RGoalBezierPointToEscape = new Pose(117.313, 117.313);

    //blue poses
    public Pose Bescape = new Pose(16.298, 105.492);
    public Pose intake1end = new Pose(14, 84.233);
    public Pose intake2 = new Pose(42, 60);
    public Pose intake2end = new Pose(8, 60);
    public Pose intake3 = new Pose(41, 35);
    public Pose intake3end = new Pose(8, 35);
    public Pose move = new Pose(29.192, 49.617);

    //red poses
    public Pose Rlaunch = new Pose(105, 102.782);
    public Pose Rgoal = new Pose(119.401, 128.199);
    public Pose Rescape = new Pose(127.522, 105.134);
    public Pose Rintake1 = new Pose(87, 84.233);
    public Pose Rintake1end = new Pose(125, 84.233);
    public Pose Rintake2 = new Pose(100, 60);
    public Pose Rintake2end = new Pose(133, 60);
    public Pose Rintake3 = new Pose(101, 35);
    public Pose Rintake3end = new Pose(135, 36);
    public Pose Rmove = new Pose(115, 49.617);

    public Paths(Follower follower) {
        BlueGoalToEscape = follower
            .pathBuilder()
            .addPath(new BezierCurve(start, BGoalBezierPointToEscape, Bescape))
            .setLinearHeadingInterpolation(Math.toRadians(145), Math.toRadians(180))
            .build();

        RedGoalToEscape = follower
            .pathBuilder()
            .addPath(new BezierCurve(Rstart, RGoalBezierPointToEscape, Rescape))
            .setLinearHeadingInterpolation(Math.toRadians(37), Math.toRadians(0))
            .build();
    }
}
