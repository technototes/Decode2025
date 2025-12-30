package org.firstinspires.ftc.teama.subdir2;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
// import com.pedropathing.geometry.BezierCurve; Commented out: should fail to register!
// import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

@Configurable
public class NotPathFile {

    public static double org = 72.0;
    public static double dist = 8.0;

    public static Pose start = new Pose(org, org, Math.toRadians(0));
    public static Pose step1 = new Pose(org + dist, org, Math.toRadians(90));
    public static Pose step2 = new Pose(org + dist, org + dist, Math.toRadians(0));
    public static Pose step3 = new Pose(org, org + dist, Math.toRadians(-45));
    public static Pose step4 = new Pose(org, org, Math.toRadians(30));

    public PathChain Path1;
    public PathChain Path2;
    public PathChain Path3;
    public PathChain Path4;

    public TestPaths(Follower follower) {
        Path1 = follower
            .pathBuilder()
            .setLinearHeadingInterpolation(start.getHeading(), step1.getHeading())
            .build();

        Path2 = follower
            .pathBuilder()
            .setLinearHeadingInterpolation(step1.getHeading(), step2.getHeading())
            .build();

        Path3 = follower
            .pathBuilder()
            .setLinearHeadingInterpolation(step2.getHeading(), step3.getHeading())
            .build();

        Path4 = follower
            .pathBuilder()
            .setLinearHeadingInterpolation(step3.getHeading(), step4.getHeading())
            .build();
    }
}
