package org.firstinspires.ftc.sixteen750.commands.auto;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.HeadingInterpolator;
import com.pedropathing.paths.PathChain;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.technototes.library.command.Command;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.ParallelCommandGroup;
import com.technototes.library.command.ParallelRaceGroup;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import com.technototes.library.util.HeadingHelper;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.Setup;
import org.firstinspires.ftc.sixteen750.commands.AltAutoOrient;
import org.firstinspires.ftc.sixteen750.commands.AltAutoOrientFar;
import org.firstinspires.ftc.sixteen750.commands.AltAutoVelocity;
import org.firstinspires.ftc.sixteen750.commands.PedroPathCommand;
import org.firstinspires.ftc.sixteen750.commands.PedroPathCommand;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;
import org.firstinspires.ftc.sixteen750.commands.auto.Paths;
import org.firstinspires.ftc.sixteen750.commands.auto.Poses;
import org.firstinspires.ftc.sixteen750.controls.DriverController;
import org.firstinspires.ftc.sixteen750.helpers.StartingPosition;
import org.firstinspires.ftc.sixteen750.subsystems.LauncherSubsystem;

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
            TeleCommands.GateUp(r)

            // want to keep launcher running during auto also no need to stop intake
        )
            //.alongWith(new AltAutoOrient(r));
            .raceWith(new AltAutoOrient(r));
    }

    public static Command AutoLaunching3BallsSlowIntake(Robot r) {
        return new SequentialCommandGroup(
            TeleCommands.GateUp(r),
            TeleCommands.HoldIntake(r),
            // no need to wait for spinup as we will leave the flywheel spinning constantly during auto
            //switched to slow intake to remove the up down up down of the gate aswell as drain less power
            TeleCommands.GateDown(r),
            new WaitCommand(1.3),
            TeleCommands.GateUp(r),
            TeleCommands.Intake(r)
        )
            .raceWith(new AltAutoOrient(r));

        // want to keep launcher running during auto also no need to stop intake

        //.raceWith(new AltAutoOrient(r));
    }

    public PathChain RStartToRLaunch;
    public PathChain RLaunchToRInt1;
    public PathChain RInt1ToRLaunch;
    public static PathChain RLaunchToRGateInt;
    public static PathChain RGateIntToRLaunch;
    public static PathChain RLaunchToRGateInt2;
    public static PathChain RLaunchToRGateInt3;
    public static PathChain RGateInt2ToRLaunch;
    public static PathChain RGateInt3ToRLaunch;
    public PathChain RLaunchToRInt2;
    public PathChain RInt2ToRLaunch;
    public PathChain RLaunchToREnd;
    public PathChain BStartToBLaunch;
    public PathChain BLaunchToBInt1;
    public PathChain BInt1ToBLaunch;
    public static PathChain BLaunchToBGateInt1;
    public static PathChain BLaunchToBGateInt2;
    public static PathChain BLaunchToBGateInt3;

    public static PathChain BGateInt1ToBLaunch;
    public static PathChain BGateInt2ToBLaunch;
    public static PathChain BGateInt3ToBLaunch;
    public PathChain BLaunchToBInt2;
    public PathChain BInt2ToBLaunch;
    public PathChain BLaunchToBEnd;
    public PathChain RFStartToRFLaunch;
    public PathChain RFInt1ToRFLaunch;
    public PathChain RFInt2ToRFLaunch;
    public PathChain RFInt3ToRFLaunch;
    public PathChain RFLaunchToRFInt1;
    public PathChain RFLaunchToRFInt2;
    public PathChain RFLaunchToRFInt3;
    public PathChain RFLaunchToRFEnd;
    public PathChain BFStartToBFLaunch;
    public PathChain BFInt1ToBFLaunch;
    public PathChain BFInt2ToBFLaunch;
    public PathChain BFInt3ToBFLaunch;
    public PathChain BFLaunchToBFInt1;
    public PathChain BFLaunchToBFInt2;
    public PathChain BFLaunchToBFInt3;
    public PathChain BFLaunchToBFEnd;
    public static double power085 = 0.85;
    public static double power092 = 0.92;

    public Paths2(Follower follower) {
        follower.setMaxPowerScaling(1);
        RStartToRLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(Poses.RStart, Poses.RLaunch))
            .setConstantHeadingInterpolation(Poses.RLaunchHead)
            .build();
        RLaunchToRInt1 = follower
            .pathBuilder()
            .addPath(
                new BezierCurve(
                    Poses.RLaunch,
                    Poses.RInt1CtrlPoint1,
                    Poses.RInt1CtrlPoint2,
                    Poses.RInt1
                )
            )
            .setConstantHeadingInterpolation(Poses.RInt1Head)
            .build();
        RInt1ToRLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(Poses.RInt1, Poses.RInt1ToLaunchCtrlPoint, Poses.RLaunch))
            .setConstantHeadingInterpolation(Poses.RLaunchHead)
            .build();
        RLaunchToRGateInt = follower
            .pathBuilder()
            .addPath(new BezierCurve(Poses.RLaunch, Poses.RGateCycleCtrlPoint, Poses.RGateInt))
            .setLinearHeadingInterpolation((Poses.RLaunchHead), (Poses.RGateIntHead))
            .build();
        RGateIntToRLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(Poses.RGateInt, Poses.RGateCycleCtrlPoint, Poses.RLaunch))
            .setLinearHeadingInterpolation((Poses.RGateIntHead), (Poses.RLaunchHead))
            .build();
        RLaunchToRGateInt2 = follower
            .pathBuilder()
            .addPath(new BezierCurve(Poses.RLaunch, Poses.RGateCycleCtrlPoint, Poses.RGateInt2))
            .setLinearHeadingInterpolation((Poses.RLaunchHead), (Poses.RGateIntHead))
            .build();
        RGateInt2ToRLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(Poses.RGateInt2, Poses.RGateCycleCtrlPoint, Poses.RLaunch))
            .setLinearHeadingInterpolation((Poses.RGateIntHead), (Poses.RLaunchHead))
            .build();
        RLaunchToRGateInt3 = follower
            .pathBuilder()
            .addPath(new BezierCurve(Poses.RLaunch, Poses.RGateCycleCtrlPoint, Poses.RGateInt3))
            .setLinearHeadingInterpolation(
                Math.toRadians(Poses.RLaunchHead),
                Math.toRadians(Poses.RGateIntHead)
            )
            .build();
        RGateInt3ToRLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(Poses.RGateInt3, Poses.RGateCycleCtrlPoint, Poses.RLaunch))
            .setLinearHeadingInterpolation((Poses.RGateIntHead), (Poses.RLaunchHead))
            .build();
        RLaunchToRInt2 = follower
            .pathBuilder()
            .addPath(new BezierLine(Poses.RLaunch, Poses.RInt2))
            .setConstantHeadingInterpolation((Poses.RInt2Head))
            .build();
        RInt2ToRLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(Poses.RInt2, Poses.RLaunch))
            .setLinearHeadingInterpolation((Poses.RInt2Head), (Poses.RLaunchHead))
            .build();
        RLaunchToREnd = follower
            .pathBuilder()
            .addPath(new BezierLine(Poses.RLaunch, Poses.REnd))
            .setConstantHeadingInterpolation((Poses.REndHead))
            .build();
        BStartToBLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(Poses.BStart, Poses.BLaunch))
            .setConstantHeadingInterpolation(Poses.BLaunchHead)
            .build();
        BLaunchToBInt1 = follower
            .pathBuilder()
            .addPath(
                new BezierCurve(
                    Poses.BLaunch,
                    Poses.BInt1CtrlPoint1,
                    Poses.BInt1CtrlPoint2,
                    Poses.BInt1
                )
            )
            .setConstantHeadingInterpolation(Poses.BInt1Head)
            .build();
        BInt1ToBLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(Poses.BInt1, Poses.BInt1ToLaunchCtrlPoint, Poses.BLaunch))
            .setConstantHeadingInterpolation(Poses.BLaunchHead)
            .build();
        BLaunchToBGateInt1 = follower
            .pathBuilder()
            .addPath(new BezierCurve(Poses.BLaunch, Poses.BGateCycleCtrlPoint, Poses.BGateInt1))
            .setLinearHeadingInterpolation(Poses.BLaunchHead, Poses.BGateIntHead)
            .build();
        BGateInt1ToBLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(Poses.BGateInt1, Poses.BGateCycleCtrlPoint, Poses.BLaunch))
            .setLinearHeadingInterpolation(Poses.BGateIntHead, Poses.BLaunchHead)
            .build();
        BLaunchToBGateInt2 = follower
            .pathBuilder()
            .addPath(new BezierCurve(Poses.BLaunch, Poses.BGateCycleCtrlPoint, Poses.BGateInt2))
            .setLinearHeadingInterpolation(Poses.BLaunchHead, Poses.BGateIntHead)
            .build();
        BGateInt2ToBLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(Poses.BGateInt2, Poses.BGateCycleCtrlPoint, Poses.BLaunch))
            .setLinearHeadingInterpolation(Poses.BGateIntHead, Poses.BLaunchHead)
            .build();
        BLaunchToBGateInt3 = follower
            .pathBuilder()
            .addPath(new BezierCurve(Poses.BLaunch, Poses.BGateCycleCtrlPoint, Poses.BGateInt3))
            .setLinearHeadingInterpolation(Poses.BLaunchHead, Poses.BGateIntHead)
            .build();
        BGateInt3ToBLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(Poses.BGateInt3, Poses.BGateCycleCtrlPoint, Poses.BLaunch))
            .setLinearHeadingInterpolation(Poses.BGateIntHead, Poses.BLaunchHead)
            .build();
        BLaunchToBInt2 = follower
            .pathBuilder()
            .addPath(new BezierLine(Poses.BLaunch, Poses.BInt2))
            .setConstantHeadingInterpolation(Poses.BInt2Head)
            .build();
        BInt2ToBLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(Poses.BInt2, Poses.BLaunch))
            .setLinearHeadingInterpolation(Poses.BInt2Head, Poses.BLaunchHead)
            .build();
        BLaunchToBEnd = follower
            .pathBuilder()
            .addPath(new BezierLine(Poses.BLaunch, Poses.BEnd))
            .setConstantHeadingInterpolation(Poses.BEndHead)
            .build();
        RFStartToRFLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(Poses.RFStart, Poses.RFLaunch))
            .setConstantHeadingInterpolation(Poses.RFLaunchHead)
            .build();
        RFInt1ToRFLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(Poses.RFInt1, Poses.RFLaunch))
            .setConstantHeadingInterpolation(Poses.RFLaunchHead)
            .build();
        RFInt2ToRFLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(Poses.RFInt2, Poses.RFLaunch))
            .setLinearHeadingInterpolation(Poses.RFInt2Head, Poses.RFLaunchHead)
            .build();
        RFInt3ToRFLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(Poses.RFInt3, Poses.RFLaunch))
            .setConstantHeadingInterpolation(Poses.RFLaunchHead)
            .build();
        RFLaunchToRFInt1 = follower
            .pathBuilder()
            .addPath(new BezierCurve(Poses.RFLaunch, Poses.RFInt1CtrlPoint, Poses.RFInt1))
            .setConstantHeadingInterpolation(Poses.RFInt1Head)
            .build();
        RFLaunchToRFInt2 = follower
            .pathBuilder()
            .addPath(
                new BezierCurve(
                    Poses.RFLaunch,
                    Poses.RFInt2CtrlPoint1,
                    Poses.RFInt2CtrlPoint2,
                    Poses.RFInt2
                )
            )
            .setConstantHeadingInterpolation(Poses.RFInt2Head)
            .build();
        RFLaunchToRFInt3 = follower
            .pathBuilder()
            .addPath(
                new BezierCurve(
                    Poses.RFLaunch,
                    Poses.RFInt3CtrlPoint1,
                    Poses.RFInt3CtrlPoint2,
                    Poses.RFInt1
                )
            )
            .setConstantHeadingInterpolation(Poses.RFInt3Head)
            .build();
        RFLaunchToRFEnd = follower
            .pathBuilder()
            .addPath(new BezierLine(Poses.RFLaunch, Poses.RFEnd))
            .setConstantHeadingInterpolation(Poses.RFEndHead)
            .build();
        BFStartToBFLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(Poses.BFStart, Poses.BFLaunch))
            .setConstantHeadingInterpolation(Poses.BFLaunchHead)
            .build();
        BFInt1ToBFLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(Poses.BFInt1, Poses.BFLaunch))
            .setConstantHeadingInterpolation(Poses.BFLaunchHead)
            .build();
        BFInt2ToBFLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(Poses.BFInt2, Poses.BFLaunch))
            .setLinearHeadingInterpolation(Poses.BFInt2Head, Poses.BFLaunchHead)
            .build();
        BFInt3ToBFLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(Poses.BFInt3, Poses.BFLaunch))
            .setConstantHeadingInterpolation(Poses.BFLaunchHead)
            .build();
        BFLaunchToBFInt1 = follower
            .pathBuilder()
            .addPath(new BezierCurve(Poses.BFLaunch, Poses.BFInt1CtrlPoint, Poses.BFInt1))
            .setConstantHeadingInterpolation(Poses.BFInt1Head)
            .build();
        BFLaunchToBFInt2 = follower
            .pathBuilder()
            .addPath(
                new BezierCurve(
                    Poses.BFLaunch,
                    Poses.BFInt2CtrlPoint1,
                    Poses.BFInt2CtrlPoint2,
                    Poses.BFInt2
                )
            )
            .setConstantHeadingInterpolation(Poses.BFInt2Head)
            .build();
        BFLaunchToBFInt3 = follower
            .pathBuilder()
            .addPath(
                new BezierCurve(
                    Poses.BFLaunch,
                    Poses.BFInt3CtrlPoint1,
                    Poses.BFInt3CtrlPoint2,
                    Poses.BFInt1
                )
            )
            .setConstantHeadingInterpolation(Poses.BFInt3Head)
            .build();
        BFLaunchToBFEnd = follower
            .pathBuilder()
            .addPath(new BezierLine(Poses.BFLaunch, Poses.BFEnd))
            .setConstantHeadingInterpolation(Poses.BFEndHead)
            .build();
    }
}
//    public static Command Pedropathcommand(Robot r){
//        return new PPPathCommand()
//    }
