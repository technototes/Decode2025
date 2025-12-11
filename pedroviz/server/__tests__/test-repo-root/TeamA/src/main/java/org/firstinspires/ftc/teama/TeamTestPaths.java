package org.firstinspires.ftc.teama;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

@Configurable
public class TeamTestPaths {

    public static double org = 72.0;
    public static int step = 80;
    public static double one80 = Math.toRadians(180);
    public static double step_mid = 74.0;

    public static Pose start = new Pose(org, org, 0);
    public static Pose step1 = new Pose(step, org, Math.toRadians(90));
    public static Pose step2 = new Pose(step, step, one80);
    public static Pose step23_mid = new Pose(step_mid, step_mid);
    public static Pose step3 = new Pose(org, step, -0.7854);
    public static Pose step4 = new Pose(72.0, 72, Math.toRadians(-30));

    public static BezierLine start_to_step1 = new BezierLine(start, step1);
    public static BezierCurve step2_to_step3 = new BezierCurve(step2, step23_mid, step3);
    public static BezierCurve step4_to_start = new BezierCurve(step4, new Pose(org, 15), start);
    public static BezierLine another_line = new BezierLine(
        new Pose(1.2, step_mid, 0.0),
        new Pose(1, 3.4, Math.toRadians(60))
    );

    public PathChain Path1;
    public PathChain Path2;
    public PathChain Path3;
    public PathChain Path4;
    public PathChain AnotherPath;

    public TeamTestPaths(Follower follower) {
        Path1 = follower
            .pathBuilder()
            .addPath(start_to_step1)
            .setLinearHeadingInterpolation(start.getHeading(), step1.getHeading())
            .build();

        Path2 = follower
            .pathBuilder()
            .addPath(new BezierCurve(step1, step2))
            .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(step_mid))
            .build();

        Path3 = follower
            .pathBuilder()
            .addPath(step2_to_step3)
            .setLinearHeadingInterpolation(step_mid, step3.getHeading())
            .build();

        Path4 = follower
            .pathBuilder()
            .addPath(new BezierLine(step3, step4))
            .setConstantHeadingInterpolation(one80)
            .build();

        AnotherPath = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(0, 0), new Pose(20, 20)))
            .addPath(new BezierCurve(step1, step2, step3, step4))
            .addPath(step4_to_start)
            .setLinearHeadingInterpolation(Math.toRadians(step), step4.getHeading())
            .build();
    }
}
