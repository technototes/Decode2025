package org.firstinspires.ftc.twenty403;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class Paths {

    public PathChain starttobluegoal;

    public Pose getStart() {
        return new Pose(32.671, 135.916, Math.toRadians(90));
    }

    public Pose getRStart() {
        return new Pose(113, 135.916, Math.toRadians(90));
    }

    //blue poses
    public Pose BlueGoal = new Pose(23.104, 126.537);
    public Pose intake1end = new Pose(14, 84.233);
    public Pose intake2 = new Pose(42, 60);
    public Pose intake2end = new Pose(8, 60);
    public Pose intake3 = new Pose(41, 35);
    public Pose intake3end = new Pose(8, 35);
    public Pose move = new Pose(29.192, 49.617);

    //red poses
    public Pose Rstart = new Pose(113, 135.916);
    public Pose Rlaunch = new Pose(105, 102.782);
    public Pose Rintake1 = new Pose(87, 84.233);
    public Pose Rintake1end = new Pose(125, 84.233);
    public Pose Rintake2 = new Pose(100, 60);
    public Pose Rintake2end = new Pose(133, 60);
    public Pose Rintake3 = new Pose(101, 35);
    public Pose Rintake3end = new Pose(135, 36);
    public Pose Rmove = new Pose(115, 49.617);

    public Paths(Follower follower) {

    }
}
