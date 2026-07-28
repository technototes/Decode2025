package org.firstinspires.ftc.sixteen750.commands.auto;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.paths.PathChain;

@Configurable
public class BPaths {

    Poses.StartPoses sp = new Poses.StartPoses();

    public static Follower follower;

    public PathChain PBStartToBLaunch;
    public PathChain PBLaunchToBInt1;
    public PathChain PBInt1ToBLaunch;
    public static PathChain PBLaunchToBGateInt1;
    public static PathChain PBLaunchToBGateInt2;
    public static PathChain PBLaunchToBGateInt3;
    public static PathChain PBGateInt1ToBLaunch;
    public static PathChain PBGateInt2ToBLaunch;
    public static PathChain PBGateInt3ToBLaunch;
    public PathChain PBLaunchToBInt2;
    public PathChain PBInt2ToBLaunch;
    public PathChain PBLaunchToBEnd;
    public PathChain SBStartToBLaunch;
    public PathChain SBLaunchToBInt1;
    public PathChain SBInt1ToBLaunch;
    public static PathChain SBLaunchToBGateInt1;
    public static PathChain SBLaunchToBGateInt2;
    public static PathChain SBGateInt1ToBLaunch;
    public static PathChain SBGateInt2ToBLaunch;
    public PathChain SBLaunchToBInt2;
    public PathChain SBInt2ToBLaunch;
    public PathChain SBLaunchToBInt3;
    public PathChain SBInt3ToBLaunch;
    public PathChain SBLaunchToBEnd;
    public PathChain BFStartToBFLaunch;
    public PathChain BFInt1ToBFLaunch;
    public PathChain BFInt2ToBFLaunch;
    public PathChain BFInt3ToBFLaunch;
    public PathChain BFLaunchToBFInt1;
    public PathChain BFLaunchToBFInt2;
    public PathChain BFLaunchToBFInt3;
    public PathChain BFLaunchToBFEnd;
    public static double power085 = 0.85;
    public static double power095 = 0.95;

    public BPaths(Follower follower) {
        follower.setMaxPowerScaling(1);

        Poses.BNear18PartnerPoses p = new Poses.BNear18PartnerPoses();

        PBStartToBLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(sp.BStart, p.BLaunch))
            .setConstantHeadingInterpolation(p.BLaunchHead)
            .build();
        PBLaunchToBInt1 = follower
            .pathBuilder()
            .addPath(new BezierCurve(p.BLaunch, p.BInt1CtrlPoint1, p.BInt1CtrlPoint2, p.BInt1))
            .setConstantHeadingInterpolation(p.BInt1Head)
            .build();
        PBInt1ToBLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(p.BInt1, p.BInt1ToLaunchCtrlPoint, p.BLaunch))
            .setConstantHeadingInterpolation(p.BLaunchHead)
            .build();
        PBLaunchToBGateInt1 = follower
            .pathBuilder()
            .addPath(new BezierCurve(p.BLaunch, p.BGateCycleCtrlPoint, p.BGateInt1))
            .setLinearHeadingInterpolation(p.BLaunchHead, p.BGateIntHead)
            .build();
        PBGateInt1ToBLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(p.BGateInt1, p.BGateCycleCtrlPoint, p.BLaunch))
            .setLinearHeadingInterpolation(p.BGateIntHead, p.BLaunchHead)
            .build();
        PBLaunchToBGateInt2 = follower
            .pathBuilder()
            .addPath(new BezierCurve(p.BLaunch, p.BGateCycleCtrlPoint, p.BGateInt2))
            .setLinearHeadingInterpolation(p.BLaunchHead, p.BGateIntHead)
            .build();
        PBGateInt2ToBLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(p.BGateInt2, p.BGateCycleCtrlPoint, p.BLaunch))
            .setLinearHeadingInterpolation(p.BGateIntHead, p.BLaunchHead)
            .build();
        PBLaunchToBGateInt3 = follower
            .pathBuilder()
            .addPath(new BezierCurve(p.BLaunch, p.BGateCycleCtrlPoint, p.BGateInt3))
            .setLinearHeadingInterpolation(p.BLaunchHead, p.BGateIntHead)
            .build();
        PBGateInt3ToBLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(p.BGateInt3, p.BGateCycleCtrlPoint, p.BLaunch))
            .setLinearHeadingInterpolation(p.BGateIntHead, p.BLaunchHead)
            .build();
        PBLaunchToBInt2 = follower
            .pathBuilder()
            .addPath(new BezierCurve(p.BLaunch, p.BInt2CtrlPoint, p.BInt2))
            .setConstantHeadingInterpolation(p.BInt2Head)
            .build();
        PBInt2ToBLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(p.BInt2, p.BLaunch))
            .setConstantHeadingInterpolation(p.BLaunchHead)
            .build();
        PBLaunchToBEnd = follower
            .pathBuilder()
            .addPath(new BezierLine(p.BLaunch, p.BEnd))
            .setConstantHeadingInterpolation(p.BEndHead)
            .build();

        Poses.BNear18SafePoses p1 = new Poses.BNear18SafePoses();

        SBStartToBLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(sp.BStart, p1.BLaunch))
            .setConstantHeadingInterpolation(p1.BLaunchHead)
            .build();
        SBLaunchToBInt1 = follower
            .pathBuilder()
            .addPath(new BezierCurve(p1.BLaunch, p1.BInt1CtrlPoint1, p1.BInt1CtrlPoint2, p1.BInt1))
            .setConstantHeadingInterpolation(p1.BInt1Head)
            .build();
        SBInt1ToBLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(p1.BInt1, p1.BInt1ToLaunchCtrlPoint, p1.BLaunch))
            .setConstantHeadingInterpolation(p1.BLaunchHead)
            .build();
        SBLaunchToBGateInt1 = follower
            .pathBuilder()
            .addPath(new BezierCurve(p1.BLaunch, p1.BGateCycleCtrlPoint, p1.BGateInt1))
            .setLinearHeadingInterpolation(p1.BLaunchHead, p1.BGateIntHead)
            .build();
        SBGateInt1ToBLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(p1.BGateInt1, p1.BGateCycleCtrlPoint, p1.BLaunch))
            .setLinearHeadingInterpolation(p1.BGateIntHead, p1.BLaunchHead)
            .build();
        SBLaunchToBInt2 = follower
            .pathBuilder()
            .addPath(new BezierCurve(p1.BLaunch, p1.BInt2CtrlPoint1, p1.BInt2CtrlPoint2, p1.BInt2))
            .setConstantHeadingInterpolation(p1.BInt2Head)
            .build();
        SBInt2ToBLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(p1.BInt2, p1.BLaunch))
            .setLinearHeadingInterpolation((p1.BInt2Head), (p1.BLaunchHead))
            .build();
        SBLaunchToBGateInt2 = follower
            .pathBuilder()
            .addPath(new BezierCurve(p1.BLaunch, p1.BGateCycleCtrlPoint, p1.BGateInt2))
            .setLinearHeadingInterpolation(p1.BLaunchHead, p1.BGateIntHead)
            .build();
        SBGateInt2ToBLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(p1.BGateInt2, p1.BGateCycleCtrlPoint, p1.BLaunch))
            .setLinearHeadingInterpolation(p1.BGateIntHead, p1.BLaunchHead)
            .build();
        SBLaunchToBInt3 = follower
            .pathBuilder()
            .addPath(new BezierCurve(p1.BLaunch, p1.BInt3CtrlPoint, p1.BInt3))
            .setConstantHeadingInterpolation(p1.BInt3Head)
            .build();
        SBInt3ToBLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(p1.BInt3, p1.BLaunch))
            .setConstantHeadingInterpolation(p1.BLaunchHead)
            .build();
        SBLaunchToBEnd = follower
            .pathBuilder()
            .addPath(new BezierLine(p1.BLaunch, p1.BEnd))
            .setConstantHeadingInterpolation(p1.BEndHead)
            .build();

        Poses.BFar15PartnerPoses p2 = new Poses.BFar15PartnerPoses();

        BFStartToBFLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(sp.BFStart, p2.BFLaunch))
            .setConstantHeadingInterpolation(p2.BFLaunchHead)
            .build();
        BFInt1ToBFLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(p2.BFInt1, p2.BFLaunch))
            .setConstantHeadingInterpolation(p2.BFLaunchHead)
            .build();
        BFInt2ToBFLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(p2.BFInt2, p2.BFLaunch))
            .setLinearHeadingInterpolation(p2.BFInt2Head, p2.BFLaunchHead)
            .build();
        BFInt3ToBFLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(p2.BFInt3, p2.BFLaunch))
            .setConstantHeadingInterpolation(p2.BFLaunchHead)
            .build();
        BFLaunchToBFInt1 = follower
            .pathBuilder()
            .addPath(new BezierCurve(p2.BFLaunch, p2.BFInt1CtrlPoint, p2.BFInt1))
            .setConstantHeadingInterpolation(p2.BFInt1Head)
            .build();
        BFLaunchToBFInt2 = follower
            .pathBuilder()
            .addPath(
                new BezierCurve(p2.BFLaunch, p2.BFInt2CtrlPoint1, p2.BFInt2CtrlPoint2, p2.BFInt2)
            )
            .setConstantHeadingInterpolation(p2.BFInt2Head)
            .build();
        BFLaunchToBFInt3 = follower
            .pathBuilder()
            .addPath(
                new BezierCurve(p2.BFLaunch, p2.BFInt3CtrlPoint1, p2.BFInt3CtrlPoint2, p2.BFInt1)
            )
            .setConstantHeadingInterpolation(p2.BFInt3Head)
            .build();
        BFLaunchToBFEnd = follower
            .pathBuilder()
            .addPath(new BezierLine(p2.BFLaunch, p2.BFEnd))
            .setConstantHeadingInterpolation(p2.BFEndHead)
            .build();
    }
}

//    public static Command Pedropathcommand(Robot r){
//        return new PPPathCommand()
//    }
