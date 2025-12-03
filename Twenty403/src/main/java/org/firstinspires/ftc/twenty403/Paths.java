package org.firstinspires.ftc.twenty403;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class Paths {

    public PathChain StartToBlueGoal;
    public PathChain BlueGoalToEscape;
    public PathChain RedStartToRedGoal;
    public PathChain RedGoalToEscape;

    public final Pose start = new Pose(33.037, 134.842);

    public final Pose Rstart = new Pose(111.142, 134.842);
    private final Pose BstartBezierPointToGoal = new Pose(33.037, 123.352);
    private final Pose BGoalBezierPointToEscape = new Pose(25.955, 109.511);
    private final Pose RstartBezierPointToGoal = new Pose(98.753, 125.147);
    private final Pose RGoalBezierPointToEscape = new Pose(101.626, 105.217);


    //blue poses
    private final Pose BlueGoal = new Pose(23.7007, 126.942);
    private final Pose Bescape = new Pose(21.546, 105.755);
    private final Pose intake1end = new Pose(14, 84.233);
    private final Pose intake2 = new Pose(42, 60);
    private final Pose intake2end = new Pose(8, 60);
    private final Pose intake3 = new Pose(41, 35);
    private final Pose intake3end = new Pose(8, 35);
    private final Pose move = new Pose(29.192, 49.617);

    //red poses
    private final Pose Rlaunch = new Pose(105, 102.782);
    private final Pose Rgoal = new Pose(119.401, 128.199);
    private final Pose Rescape = new Pose(119.76, 102.523);
    private final Pose Rintake1 = new Pose(87, 84.233);
    private final Pose Rintake1end = new Pose(125, 84.233);
    private final Pose Rintake2 = new Pose(100, 60);
    private final Pose Rintake2end = new Pose(133, 60);
    private final Pose Rintake3 = new Pose(101, 35);
    private final Pose Rintake3end = new Pose(135, 36);
    private final Pose Rmove = new Pose(115, 49.617);

    public Paths(Follower follower) {
        StartToBlueGoal = follower
                .pathBuilder()
                .addPath(
                        new BezierCurve(
                                start,
                                BstartBezierPointToGoal,
                                BlueGoal
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(145))
                .build();

        BlueGoalToEscape = follower
                .pathBuilder()
                .addPath(
                        new BezierCurve(
                                BlueGoal,
                                BGoalBezierPointToEscape,
                                Bescape
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(145), Math.toRadians(135))
                .build();
        RedStartToRedGoal = follower
                .pathBuilder()
                .addPath(
                        new BezierCurve(
                                Rstart,
                                RstartBezierPointToGoal,
                                Rgoal
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(35))
                .build();

        RedGoalToEscape = follower
                .pathBuilder()
                .addPath(
                        new BezierCurve(
                                Rgoal,
                                RGoalBezierPointToEscape,
                                Rescape
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(35), Math.toRadians(135))
                .build();
    }
}
