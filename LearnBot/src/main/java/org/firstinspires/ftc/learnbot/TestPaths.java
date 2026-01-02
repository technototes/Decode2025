package org.firstinspires.ftc.learnbot;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

/**** DO NOT EDIT ****
 These paths are specifically for testing the visualizer. If you want to make some
 changes to the 'real' paths, just create a different file...
 **** DO NOT EDIT ****/

@Configurable
public class TestPaths {

    public static double org = 15.0;
    public static double edge = 50.0;
    public static double extra = 25.0;
    public static double extra2 = 27.0;
    public static double one80 = Math.toRadians(180);
    public static double refVal = edge;
    public static int sixty = 60;
    public static int ninetyD = 90;
    public static double ninety = Math.toRadians(ninetyD);

    public static Pose start = new Pose(org, org, Math.toRadians(0));
    public static Pose step1 = new Pose(edge, org, ninety);
    public static Pose step2 = new Pose(edge, refVal, 35);
    public static Pose step3 = new Pose(extra, extra2, Math.toRadians(sixty));
    public static Pose step4 = new Pose(org, org, one80);
    public static Pose stepb = new Pose(extra, extra, Math.toRadians(sixty));
    public static Pose stepc = new Pose(15, 20);
    public static Pose stepd = new Pose(18, 55, Math.toRadians(135));

    public static BezierLine start_to_step1 = new BezierLine(start, step1);
    public static BezierCurve unused1 = new BezierCurve(step1, step2, step4, step1);
    public static BezierLine u1_u2 = new BezierLine(step1, new Pose(org, edge));
    public static BezierCurve unused2 = new BezierLine(new Pose(org, edge), start);
    public static BezierLine u2_u3 = new BezierLine(start, new Pose(edge, 5, 15));
    public static BezierCurve unused3 = new BezierCurve(
        new Pose(edge, 5, 15),
        start,
        new Pose(5, 5)
    );
    public static BezierLine u3_u4 = new BezierLine(new Pose(5, 5), start);
    public static BezierCurve unused4 = new BezierCurve(
        start,
        new Pose(15, 25),
        new Pose(55, 44),
        new Pose(10, org),
        new Pose(edge, 10, Math.toRadians(sixty)),
        step1
    );
    public static BezierLine u4_ol = new BezierLine(step1, stepc);
    public static BezierLine otherLine = new BezierLine(stepc, stepd);

    public Pose getStart() {
        return start;
    }

    public PathChain Path1;
    public PathChain Path2;
    public PathChain Path3;
    public PathChain Path4;
    public PathChain Path5;

    public TestPaths(Follower follower) {
        Path1 = follower
            .pathBuilder()
            .addPath(start_to_step1)
            .addPath(unused1)
            .addPath(new BezierCurve(step1, new Pose(10, extra), step4, new Pose(edge, 10), step1))
            .setLinearHeadingInterpolation(0, ninety)
            .build();

        Path2 = follower
            .pathBuilder()
            .addPath(new BezierCurve(step1, stepb, step2))
            .setConstantHeadingInterpolation(ninety)
            .build();

        Path3 = follower
            .pathBuilder()
            .addPath(new BezierLine(step2, step3))
            .setLinearHeadingInterpolation(ninety, step3.getHeading())
            .build();

        Path4 = follower
            .pathBuilder()
            .addPath(new BezierLine(step3, step4))
            .setTangentHeadingInterpolation()
            .build();

        Path5 = follower
            .pathBuilder()
            .addPath(unused1)
            .addPath(u1_u2)
            .addPath(unused2)
            .addPath(u2_u3)
            .addPath(unused3)
            .addPath(u3_u4)
            .addPath(unused4)
            .addPath(u4_ol)
            .addPath(otherLine)
            .setTangentHeadingInterpolation()
            .build();
    }
}
