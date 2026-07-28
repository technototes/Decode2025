package org.firstinspires.ftc.sixteen750.commands.auto;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.paths.HeadingInterpolator;
import com.pedropathing.paths.PathChain;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;

@Configurable
public class RPaths {

    Poses.StartPoses sp = new Poses.StartPoses();
    Poses.RNear18PartnerPoses p = new Poses.RNear18PartnerPoses();

    public PathChain PRStartToRLaunch;
    public PathChain PRLaunchToRInt1;
    public PathChain PRInt1ToRLaunch;
    public static PathChain PRLaunchToRGateInt1;
    public static PathChain PRLaunchToRGateInt2;
    public static PathChain PRLaunchToRGateInt3;
    public static PathChain PRGateInt1ToRLaunch;
    public static PathChain PRGateInt2ToRLaunch;
    public static PathChain PRGateInt3ToRLaunch;
    public PathChain PRLaunchToRInt2;
    public PathChain PRInt2ToRLaunch;
    public PathChain PRLaunchToREnd;
    public PathChain SRStartToRLaunch;
    public PathChain SRLaunchToRInt1;
    public PathChain SRInt1ToRLaunch;
    public static PathChain SRLaunchToRGateInt1;
    public static PathChain SRLaunchToRGateInt2;
    public static PathChain SRGateInt1ToRLaunch;
    public static PathChain SRGateInt2ToRLaunch;
    public PathChain SRLaunchToRInt2;
    public PathChain SRInt2ToRLaunch;
    public PathChain SRLaunchToRInt3;
    public PathChain SRInt3ToRLaunch;
    public PathChain SRLaunchToREnd;
    public PathChain RFStartToRFLaunch;
    public PathChain RFInt1ToRFLaunch;
    public PathChain RFInt2ToRFLaunch;
    public PathChain RFInt3ToRFLaunch;
    public PathChain RFLaunchToRFInt1;
    public PathChain RFLaunchToRFInt2;
    public PathChain RFLaunchToRFInt3;
    public PathChain RFLaunchToRFEnd;
    public static double power085 = 0.85;
    public static double power095 = 0.95;

    public RPaths(Follower follower) {
        follower.setMaxPowerScaling(1);

        PRStartToRLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(sp.RStart, p.RLaunch))
            .setConstantHeadingInterpolation(p.RLaunchHead)
            .build();
        PRLaunchToRInt1 = follower
            .pathBuilder()
            .addPath(new BezierCurve(p.RLaunch, p.RInt1CtrlPoint1, p.RInt1CtrlPoint2, p.RInt1))
            .setConstantHeadingInterpolation(p.RInt1.getHeading())
            .build();
        PRInt1ToRLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(p.RInt1, p.RInt1ToLaunchCtrlPoint, p.RLaunch))
            .setLinearHeadingInterpolation(p.RLaunchHead, p.RInt1.getHeading())
            .build();
        PRLaunchToRGateInt1 = follower
            .pathBuilder()
            .addPath(new BezierCurve(p.RLaunch, p.RGateCycleCtrlPoint, p.RGateInt))
            .setLinearHeadingInterpolation(p.RLaunchHead, p.RGateIntHead)
            .build();
        PRGateInt1ToRLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(p.RGateInt, p.RGateCycleCtrlPoint, p.RLaunch))
            .setLinearHeadingInterpolation(p.RGateIntHead, p.RLaunchHead)
            .build();
        PRLaunchToRGateInt2 = follower
            .pathBuilder()
            .addPath(new BezierCurve(p.RLaunch, p.RGateCycleCtrlPoint, p.RGateInt2))
            .setLinearHeadingInterpolation(p.RLaunchHead, p.RGateIntHead)
            .build();
        PRGateInt2ToRLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(p.RGateInt2, p.RGateCycleCtrlPoint, p.RLaunch))
            .setLinearHeadingInterpolation(p.RGateIntHead, p.RLaunchHead)
            .build();
        PRLaunchToRGateInt3 = follower
            .pathBuilder()
            .addPath(new BezierCurve(p.RLaunch, p.RGateCycleCtrlPoint, p.RGateInt3))
            .setLinearHeadingInterpolation(p.RLaunchHead, p.RGateIntHead)
            .build();
        PRGateInt3ToRLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(p.RGateInt3, p.RGateCycleCtrlPoint, p.RLaunch))
            .setLinearHeadingInterpolation(p.RGateIntHead, p.RLaunchHead)
            .build();
        PRLaunchToRInt2 = follower
            .pathBuilder()
            .addPath(new BezierCurve(p.RLaunch, p.RInt2CtrlPoint, p.RInt2))
            .setConstantHeadingInterpolation(p.RInt2Head)
            .build();
        PRInt2ToRLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(p.RInt2, p.RLaunch))
            .setConstantHeadingInterpolation(p.RLaunchHead)
            .build();
        PRLaunchToREnd = follower
            .pathBuilder()
            .addPath(new BezierLine(p.RLaunch, p.REnd))
            .setConstantHeadingInterpolation(p.REndHead)
            .build();

        Poses.RNear18SafePoses p1 = new Poses.RNear18SafePoses();

        SRStartToRLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(sp.RStart, p1.RLaunch))
            .setConstantHeadingInterpolation(p1.RLaunchHead)
            .build();
        SRLaunchToRInt1 = follower
            .pathBuilder()
            .addPath(new BezierCurve(p1.RLaunch, p1.RInt1CtrlPoint1, p1.RInt1CtrlPoint2, p1.RInt1))
            .setConstantHeadingInterpolation(p1.RInt1.getHeading())
            .build();
        SRInt1ToRLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(p1.RInt1, p1.RInt1ToLaunchCtrlPoint, p1.RLaunch))
            .setConstantHeadingInterpolation(p1.RLaunchHead)
            .build();
        SRLaunchToRGateInt1 = follower
            .pathBuilder()
            .addPath(new BezierCurve(p1.RLaunch, p1.RGateCycleCtrlPoint, p1.RGateInt))
            .setLinearHeadingInterpolation(p1.RLaunchHead, p1.RGateIntHead)
            .build();
        SRGateInt1ToRLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(p1.RGateInt, p1.RGateCycleCtrlPoint, p1.RLaunch))
            .setLinearHeadingInterpolation(p1.RGateIntHead, p1.RLaunchHead)
            .build();
        SRLaunchToRInt2 = follower
            .pathBuilder()
            .addPath(new BezierCurve(p1.RLaunch, p1.RInt2CtrlPoint1, p1.RInt2CtrlPoint2, p1.RInt2))
            .setConstantHeadingInterpolation(p1.RInt2Head)
            .build();
        SRInt2ToRLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(p1.RInt2, p1.RLaunch))
            .setLinearHeadingInterpolation(p1.RInt2Head, p1.RLaunchHead)
            .build();
        SRLaunchToRGateInt2 = follower
            .pathBuilder()
            .addPath(new BezierCurve(p1.RLaunch, p1.RGateCycleCtrlPoint, p1.RGateInt2))
            .setLinearHeadingInterpolation(p1.RLaunchHead, p1.RGateIntHead)
            .build();
        SRGateInt2ToRLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(p1.RGateInt2, p1.RGateCycleCtrlPoint, p1.RLaunch))
            .setLinearHeadingInterpolation(p1.RGateIntHead, p1.RLaunchHead)
            .build();
        SRLaunchToRInt3 = follower
            .pathBuilder()
            .addPath(new BezierCurve(p1.RLaunch, p1.RInt3CtrlPoint, p1.RInt3))
            .setConstantHeadingInterpolation(p1.RInt3Head)
            .build();
        SRInt3ToRLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(p1.RInt3, p1.RLaunch))
            .setConstantHeadingInterpolation(p1.RLaunchHead)
            .build();
        SRLaunchToREnd = follower
            .pathBuilder()
            .addPath(new BezierLine(p1.RLaunch, p1.REnd))
            .setConstantHeadingInterpolation(p1.REndHead)
            .build();

        Poses.RFar15PartnerPoses p2 = new Poses.RFar15PartnerPoses();

        RFStartToRFLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(sp.RFStart, p2.RFLaunch))
            .setConstantHeadingInterpolation(p2.RFLaunchHead)
            .build();
        RFInt1ToRFLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(p2.RFInt1, p2.RFLaunch))
            .setConstantHeadingInterpolation(p2.RFLaunchHead)
            .build();
        RFInt2ToRFLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(p2.RFInt2, p2.RFLaunch))
            .setLinearHeadingInterpolation(p2.RFInt2Head, p2.RFLaunchHead)
            .build();
        RFInt3ToRFLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(p2.RFInt3, p2.RFLaunch))
            .setConstantHeadingInterpolation(p2.RFLaunchHead)
            .build();
        RFLaunchToRFInt1 = follower
            .pathBuilder()
            .addPath(new BezierCurve(p2.RFLaunch, p2.RFInt1CtrlPoint, p2.RFInt1))
            .setConstantHeadingInterpolation(p2.RFInt1.getHeading())
            .build();
        RFLaunchToRFInt2 = follower
            .pathBuilder()
            .addPath(
                new BezierCurve(p2.RFLaunch, p2.RFInt2CtrlPoint1, p2.RFInt2CtrlPoint2, p2.RFInt2)
            )
            .setConstantHeadingInterpolation(p2.RFInt2Head)
            .build();
        RFLaunchToRFInt3 = follower
            .pathBuilder()
            .addPath(
                new BezierCurve(p2.RFLaunch, p2.RFInt3CtrlPoint1, p2.RFInt3CtrlPoint2, p2.RFInt1)
            )
            .setConstantHeadingInterpolation(p2.RFInt3Head)
            .build();
        RFLaunchToRFEnd = follower
            .pathBuilder()
            .addPath(new BezierLine(p2.RFLaunch, p2.RFEnd))
            .setConstantHeadingInterpolation(p2.RFEndHead)
            .build();
    }
}
