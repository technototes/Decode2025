package org.firstinspires.ftc.learnbot;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.HeadingInterpolator;
import com.pedropathing.paths.PathChain;

/**** DO NOT EDIT ****
 These paths are specifically for testing the visualizer. If you want to make some
 changes to the 'real' paths, just create a different file...
 **** DO NOT EDIT ****/

@Configurable
public class TestPaths {

    public static double org = 15.0;
    public static double edge = 50.0;
    public static double orgu = 130.0;
    public static double edgeu = 90.0;
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
    public static Pose step4 = new Pose(orgu, orgu, one80);
    public static Pose startu = new Pose(org, org, Math.toRadians(0));
    public static Pose step1u = new Pose(edge, org, ninety);
    public static Pose step2u = new Pose(edge, refVal, 35);
    public static Pose step3u = new Pose(extra, extra2, Math.toRadians(sixty));
    public static Pose step4u = new Pose(org, org, one80);
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

    public static BezierLine start_to_step1_5 = new BezierLine(startu, step1u);
    public static BezierCurve unused1_5 = new BezierCurve(step1u, step2u, step4u, step1u);
    public static BezierLine u1_u2_5 = new BezierLine(step1u, new Pose(orgu, edgeu));
    public static BezierCurve unused2_5 = new BezierLine(new Pose(orgu, edgeu), startu);
    public static BezierLine u2_u3_5 = new BezierLine(startu, new Pose(edgeu, 95, 15));
    public static BezierCurve unused3_5 = new BezierCurve(
        new Pose(edgeu, 95, 15),
        startu,
        new Pose(95, 95)
    );
    public static BezierLine u3_u4_5 = new BezierLine(new Pose(5, 5), startu);
    public static BezierCurve unused4_5 = new BezierCurve(
        startu,
        new Pose(95, 125),
        new Pose(85, 133),
        new Pose(130, orgu),
        new Pose(edgeu, 10, Math.toRadians(sixty)),
        step1u
    );
    public static BezierLine u4_ol_5 = new BezierLine(step1, stepc);
    public static BezierLine otherLine_5 = new BezierLine(stepc, stepd);

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
            .setConstantHeadingInterpolation(step3.getHeading())
            .build();

        Path3 = follower
            .pathBuilder()
            .addPath(new BezierLine(step2, step3))
            .setTangentHeadingInterpolation()
            .build();

        Path4 = follower
            .pathBuilder()
            .addPath(new BezierCurve(step3, step1u, step4))
            .setHeadingInterpolation(
                HeadingInterpolator.piecewise(
                    new HeadingInterpolator.PiecewiseNode(0, .2, HeadingInterpolator.tangent),
                    new HeadingInterpolator.PiecewiseNode(
                        .2,
                        .4,
                        HeadingInterpolator.facingPoint(5, 5)
                    ),
                    new HeadingInterpolator.PiecewiseNode(
                        .4,
                        .6,
                        HeadingInterpolator.constant(Math.toRadians(90))
                    ),
                    new HeadingInterpolator.PiecewiseNode(
                        .6,
                        .8,
                        HeadingInterpolator.linear(Math.toRadians(90), Math.PI)
                    ),
                    new HeadingInterpolator.PiecewiseNode(
                        .8,
                        1,
                        HeadingInterpolator.reversedLinear(Math.PI, Math.toRadians(90))
                    )
                )
            )
            .build();

        Path5 = follower
            .pathBuilder()
            .addPath(unused1_5)
            .addPath(u1_u2_5)
            .addPath(unused2_5)
            .addPath(u2_u3_5)
            .addPath(unused3_5)
            .addPath(u3_u4_5)
            .addPath(unused4_5)
            .addPath(u4_ol_5)
            .addPath(otherLine_5)
            .setHeadingInterpolation(HeadingInterpolator.facingPoint(1, 1))
            .build();
    }
}
