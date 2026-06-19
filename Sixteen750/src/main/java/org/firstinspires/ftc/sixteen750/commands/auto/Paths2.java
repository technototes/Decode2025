package org.firstinspires.ftc.sixteen750.commands.auto;


import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.HeadingInterpolator;
import com.pedropathing.paths.PathChain;
import com.technototes.library.command.Command;
import com.technototes.library.command.ParallelCommandGroup;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.commands.AltAutoOrient;
import org.firstinspires.ftc.sixteen750.commands.AltAutoOrientFar;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;
import org.firstinspires.ftc.sixteen750.commands.auto.Poses;

@Configurable
public class Paths2 {

    public static Follower follower;

    public static Command AutoLaunching3Balls(Robot r) {
        return new SequentialCommandGroup(
            //TeleCommands.IntakeStop(r),
            TeleCommands.GateUp(r),
            TeleCommands.Intake(r),
            // no need to wait for spinup as we will leave the flywheel spinning constantly during auto
            //switched to slow intake to remove the up down up down of the gate aswell as drain less power
            TeleCommands.GateDown(r),
            new WaitCommand(0.55),
            TeleCommands.GateUp(r),
            TeleCommands.IntakeStop(r)

            // want to keep launcher running during auto also no need to stop intake
        )
            //.alongWith(new AltAutoOrient(r));
            .raceWith(new AltAutoOrient(r));
    }
    public static Command AutoLaunching3BallsSlowIntake(Robot r) {
        return new SequentialCommandGroup(
            new ParallelCommandGroup(
                //TeleCommands.IntakeStop(r),
                TeleCommands.GateUp(r),
                new WaitCommand(0.7)
            )
                .raceWith(new AltAutoOrient(r)),
            TeleCommands.HoldIntake(r),
            // no need to wait for spinup as we will leave the flywheel spinning constantly during auto
            //switched to slow intake to remove the up down up down of the gate aswell as drain less power
            TeleCommands.GateDown(r),
            new WaitCommand(1.5),
            TeleCommands.GateUp(r),
            TeleCommands.IntakeStop(r)

            // want to keep launcher running during auto also no need to stop intake
        );
        //.raceWith(new AltAutoOrient(r));
    }

    public PathChain RStartToRLaunch;
    public PathChain RLaunchToRInt1;
    public PathChain RInt1ToRLaunch;
    public PathChain RLaunchToRGateInt;
    public PathChain RGateIntToRLaunch;
    public PathChain RLaunchToRInt2;
    public PathChain RInt2ToRLaunch;
    public PathChain RLaunchToREnd;
    public PathChain BStartToBLaunch;
    public PathChain BLaunchToBInt1;
    public PathChain BInt1ToBLaunch;
    public PathChain BLaunchToBGateInt;
    public PathChain BGateIntToBLaunch;
    public PathChain BLaunchToBInt2;
    public PathChain BInt2ToBLaunch;
    public PathChain BLaunchToBEnd;

    public Paths2(Follower follower) {
        RStartToRLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(Poses.RStart,Poses.RLaunch))
            .setConstantHeadingInterpolation(Poses.RLaunchHead)
            .build();
        RLaunchToRInt1 = follower
            .pathBuilder()
            .addPath(new BezierCurve(Poses.RLaunch,Poses.RInt1CtrlPoint1,Poses.RInt1CtrlPoint2,Poses.RInt1))
            .setConstantHeadingInterpolation(Poses.RInt1Head)
            .build();
        RInt1ToRLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(Poses.RInt1,Poses.RInt1ToLaunchCtrlPoint,Poses.RLaunch))
            .setConstantHeadingInterpolation(Poses.RLaunchHead)
            .build();
        RLaunchToRGateInt = follower
            .pathBuilder()
            .addPath(new BezierCurve(Poses.RLaunch,Poses.RGateCycleCtrlPoint,Poses.RGateInt))
            .setLinearHeadingInterpolation(Poses.RLaunchHead,Poses.RGateIntHead)
            .build();
        RGateIntToRLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(Poses.RGateInt,Poses.RGateCycleCtrlPoint,Poses.RLaunch))
            .setLinearHeadingInterpolation(Poses.RGateIntHead,Poses.RLaunchHead)
            .build();
        RLaunchToRInt2 = follower
            .pathBuilder()
            .addPath(new BezierLine(Poses.RLaunch,Poses.RInt2))
            .setConstantHeadingInterpolation(Poses.RInt2Head)
            .build();
        RInt2ToRLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(Poses.RInt2,Poses.RLaunch))
            .setLinearHeadingInterpolation(Poses.RInt2Head,Poses.RLaunchHead)
            .build();
        RLaunchToREnd = follower
            .pathBuilder()
            .addPath(new BezierLine(Poses.RLaunch,Poses.REnd))
            .setConstantHeadingInterpolation(Poses.REndHead)
            .build();
        BStartToBLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(Poses.BStart,Poses.BLaunch))
            .setConstantHeadingInterpolation(Poses.BLaunchHead)
            .build();
        BLaunchToBInt1 = follower
            .pathBuilder()
            .addPath(new BezierCurve(Poses.BLaunch,Poses.BInt1CtrlPoint1,Poses.BInt1CtrlPoint2,Poses.BInt1))
            .setConstantHeadingInterpolation(Poses.BInt1Head)
            .build();
        BInt1ToBLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(Poses.BInt1,Poses.BInt1ToLaunchCtrlPoint,Poses.BLaunch))
            .setConstantHeadingInterpolation(Poses.BLaunchHead)
            .build();
        BLaunchToBGateInt = follower
            .pathBuilder()
            .addPath(new BezierCurve(Poses.BLaunch,Poses.BGateCycleCtrlPoint,Poses.BGateInt))
            .setLinearHeadingInterpolation(Poses.BLaunchHead,Poses.BGateIntHead)
            .build();
        BGateIntToBLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(Poses.BGateInt,Poses.BGateCycleCtrlPoint,Poses.BLaunch))
            .setLinearHeadingInterpolation(Poses.BGateIntHead,Poses.BLaunchHead)
            .build();
        BLaunchToBInt2 = follower
            .pathBuilder()
            .addPath(new BezierLine(Poses.BLaunch,Poses.BInt2))
            .setConstantHeadingInterpolation(Poses.BInt2Head)
            .build();
        BInt2ToBLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(Poses.BInt2,Poses.BLaunch))
            .setLinearHeadingInterpolation(Poses.BInt2Head,Poses.BLaunchHead)
            .build();
        BLaunchToBEnd = follower
            .pathBuilder()
            .addPath(new BezierLine(Poses.BLaunch,Poses.BEnd))
            .setConstantHeadingInterpolation(Poses.BEndHead)
            .build();




    }
}

//    public static Command Pedropathcommand(Robot r){
//        return new PPPathCommand()
//    }
