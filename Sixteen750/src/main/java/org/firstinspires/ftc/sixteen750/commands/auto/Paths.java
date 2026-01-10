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
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;

@Configurable
public class Paths {

    public static Follower follower;

    public static Command Launching3Balls(Robot r) {
        return new SequentialCommandGroup(
            new ParallelCommandGroup(
                TeleCommands.Intake(r),
                TeleCommands.GateUp(r),
                TeleCommands.AutoLaunch1(r)
            ),
            new WaitCommand(3),
            TeleCommands.GateDown(r),
            new WaitCommand(0.1),
            TeleCommands.GateUp(r),
            new WaitCommand(0.5),
            TeleCommands.GateDown(r),
            new WaitCommand(0.1),
            TeleCommands.GateUp(r),
            new WaitCommand(0.5),
            TeleCommands.GateDown(r),
            new WaitCommand(2),
            TeleCommands.GateUp(r)
        );
    }

    public static Command AutoLaunching3Balls(Robot r) {
        return new SequentialCommandGroup(
            TeleCommands.IntakeStop(r),
            TeleCommands.GateUp(r),
            TeleCommands.Intake(r),
            // no need to wait for spinup as we will leave the flywheel spinning constantly during auto
            //switched to slow intake to remove the up down up down of the gate aswell as drain less power
            TeleCommands.GateDown(r),
            new WaitCommand(1),
            TeleCommands.GateUp(r),
            TeleCommands.IntakeStop(r)

            // want to keep launcher running during auto also no need to stop intake
        )
            .raceWith(new AltAutoOrient(r));
    }

    public static Command AutoLaunching3BallsSlowIntake(Robot r) {
        return new SequentialCommandGroup(
            TeleCommands.IntakeStop(r),
            TeleCommands.GateUp(r),
            TeleCommands.HoldIntake(r),
            // no need to wait for spinup as we will leave the flywheel spinning constantly during auto
            //switched to slow intake to remove the up down up down of the gate aswell as drain less power
            TeleCommands.GateDown(r),
            new WaitCommand(1.25),
            TeleCommands.GateUp(r),
            TeleCommands.IntakeStop(r)

            // want to keep launcher running during auto also no need to stop intake
        )
            .raceWith(new AltAutoOrient(r));
    }

    public static Pose Start = new Pose(30.5, 135.152);
    public static Pose Launch = new Pose(54, 93);
    public static Pose Intake1 = new Pose(49, 86);
    public static Pose Intake1ControlPoint = new Pose(61, 89.143);
    public static Pose Intake1end = new Pose(19, 86);
    public static Pose Lever = new Pose(17, 76);
    public static Pose LeverControlPoint = new Pose(24, 74.620);
    public static Pose Intake2 = new Pose(49, 62);
    public static Pose Intake2ControlPoint = new Pose(71, 64.369);
    public static Pose Intake2ControlPoint2 = new Pose(46, 64);
    public static Pose Intake2end = new Pose(16, 62);
    public static Pose Intake2endControlPoint = new Pose(49, 65.696);
    public static Pose Intake3 = new Pose(48, 41);
    public static Pose Intake3ControlPoint = new Pose(74, 41.585);
    public static Pose Intake3end = new Pose(16, 41);
    public static Pose Intake3endControlPoint = new Pose(62.000, 84.000);
    public static Pose Startfar;
    public static Pose End = new Pose(34, 76);
    public static Pose FarStart = new Pose(49, 11);
    public static Pose FarLaunch = new Pose(63, 14);
    public static Pose IntakeCorner = new Pose(10, 12);
    public static Pose IntakeCornerControlPoint = new Pose(8, 73);
    public static Pose IntakeTunnel = new Pose(8, 27);
    public static Pose farStart = new Pose(54.000, 9.000);
    public static Pose farLaunch = new Pose(57.000, 15.000);
    public static Pose farLaunch2 = new Pose(57.000, 15.000);
    public static Pose farLaunch3 = new Pose(57.000, 15.000);
    public static Pose farLaunch4 = new Pose(57.000, 15.000);
    public static Pose intake4 = new Pose(16, 36);
    public static Pose intake4ControlPoint = new Pose(70.000, 40.000);
    public static Pose intakeCorner = new Pose(10, 12);
    public static Pose intakeCornerControlPoint = new Pose(8, 73);
    public static Pose intakeSweep = new Pose(20, 9);
    public static Pose intakeSweepControlPoint = new Pose(10, 76);
    public static Pose intakeEdgeControlPoint = new Pose(10, 76);
    public static Pose intakeNewCorner = new Pose(16.5, 7.7);
    public static Pose intakeNewEdge = new Pose(12.5, 17);
    public static Pose gateIntake = new Pose(6, 41);
    public static Pose gateIntakeControlPoint = new Pose(15, 35);
    public static Pose farPark = new Pose(21.25, 12.735);
    public static Pose testPose = new Pose(72, 72, 145);

    public static double launchHeading0 = 133; //120
    public static double launchHeading1 = 152; //145
    public static double launchHeading2 = 140; //145
    public static double launchHeading3 = 135; //130
    public static double launchfarheading = 108;
    public static double intakeHeading = 180;
    public static double SweepIntakeHeading = 237;
    public static double NewCornerIntakeHeading = -147;
    public static double farlaunchHeading1 = 117;
    public static double farlaunchHeading2 = 117;
    public static double farlaunchHeading3 = 115;
    public static double farlaunchHeading4 = 115;
    public static double cornerIntakeHeading = 250;
    public static double cornerIntakeHeading2 = 0;
    public static double tunnelIntakeHeading = 90;
    public static double power = 0.38;
    public static double powerforintake4 = 0.5;
    public static double power2 = 0.75;

    //Red poses reconfigure these
    public static Pose RStart = new Pose(113.5, 135.152);
    public static Pose RLaunch = new Pose(90, 93, 0);
    public static Pose RLaunchStart = new Pose(85, 88);
    public static Pose RGoal = new Pose(144, 144);
    public static Pose RLaunchend = new Pose(90, 90);
    public static Pose RIntake1 = new Pose(95, 88);
    public static Pose RIntake1ControlPoint = new Pose(83, 89.143);
    public static Pose RIntake1end = new Pose(125, 88);
    public static Pose RIntake2 = new Pose(95, 62);
    public static Pose RIntake2ControlPoint = new Pose(73, 64.369);
    public static Pose RIntake2end = new Pose(128, 62);
    public static Pose RIntake2endControlPoint = new Pose(95, 65.696);
    public static Pose RIntake3 = new Pose(95, 41);
    public static Pose RIntake3ControlPoint = new Pose(70, 41.585);
    public static Pose RIntake3end = new Pose(128, 41);
    public static Pose RIntake3endControlPoint = new Pose(82, 84.000);
    public static Pose Rlever = new Pose(125, 77);
    public static Pose RleverControlPoint = new Pose(120, 74.620);
    public static Pose REnd = new Pose(95, 84);
    public static Pose RfarStart = new Pose(90.000, 9.000);
    public static Pose RfarLaunch = new Pose(87.000, 17);
    public static Pose RfarLaunch2 = new Pose(87.000, 19);
    public static Pose RfarLaunch3 = new Pose(87.000, 19);
    public static Pose RfarLaunch4 = new Pose(87.000, 24);
    public static Pose Rintake4 = new Pose(128, 36.000);
    public static Pose Rintake4ControlPoint = new Pose(78.000, 40.000);
    public static Pose RintakeCorner = new Pose(132, 11.229);
    public static Pose RintakeCornerControlPoint = new Pose(134, 76);
    public static Pose RintakeSweep = new Pose(132, 17);
    public static Pose RintakeSweepControlPoint = new Pose(134, 76);
    public static Pose RintakeEdgeControlPoint = new Pose(134, 76);
    public static Pose RintakeNewCorner = new Pose(127.5, 7.7); //new way to intake corner balls
    public static Pose RintakeNewEdge = new Pose(129, 19); //new way to intake corner balls
    public static Pose RintakeNewCornerControlPoint = new Pose(131, 56); //pull the control point in more (decrease y)
    public static Pose RgateIntake = new Pose(132, 45.000);
    public static Pose RgateIntakeControlPoint = new Pose(129, 35);
    public static Pose RfarPark = new Pose(120, 12.735);
    public static double RlaunchHeading1 = 42;
    public static double RlaunchHeading2 = 34;
    public static double RlaunchHeading3 = 41;
    public static double RlaunchHeading4 = 48;

    public static double RintakeHeading = 0;
    public static double RfarlaunchHeading = 65;
    //58
    public static double RfarlaunchHeading2 = 66;
    // 66;
    public static double RfarlaunchHeading3 = 67;
    //53;
    public static double RfarlaunchHeading4 = 67;
    //56;

    public static double RcornerIntakeHeading = 250;
    public static double RcornerIntakeHeading2 = 0;
    public static double RtunnelIntakeHeading = 50;

    public static double RNewCornerIntakeHeading = -33;
    public static double RSweepIntakeHeading = -57;

    //public static double RNewCornerIntakeHeading2 = 0;

    public PathChain launch;
    public PathChain launchtointake1;
    public PathChain intake1tolaunch;
    public PathChain launchtointake2;
    public PathChain intake2tolaunch;
    public PathChain launchtointake3;
    public PathChain launchtopark;
    public PathChain intake3tolaunch;
    public PathChain launchfartointake4;
    public PathChain Rlaunchfartointake4;
    public PathChain intake4tolaunchfar;
    public PathChain Rintake4tolaunchfar;
    public PathChain StartFartolaunchfar;
    public PathChain launchfar;
    public PathChain FarStarttoFarLaunch;
    public PathChain FarLaunchtoIntakeCorner;
    public PathChain IntakeCornertoLaunch;
    public PathChain LaunchtoIntakeTunnel;
    public PathChain IntakeTunneltoLaunch;
    public PathChain Rlaunchfar;
    public PathChain RStartFartolaunchfar;
    public PathChain RintakeCornertolaunchfar;
    public PathChain RintakeCornerNewtolaunchfar;
    public PathChain intakeCornertolaunchfar;
    public PathChain RlaunchfartointakeCorner;
    public PathChain RlaunchfartointakeCornerNew;
    public PathChain launchfartointakeCorner;
    public PathChain launchfartogateintake;
    public PathChain gateintaketolaunchfar;
    public PathChain launchfartointakeSweep;
    public PathChain intakeSweeptolaunchfar;
    public PathChain launchfartointakeEdgeNew;
    public PathChain intakeEdgeNewtolaunchfar;

    public PathChain Rlaunchfartopark;
    public PathChain launchfartopark;
    public PathChain Rstarttolaunch;
    public PathChain Rlaunchtointake1;
    public PathChain Rintake1tolaunch;
    public PathChain Rlaunchtointake2;
    public PathChain Rintake2tolaunch;
    public PathChain Rlaunchtointake3;
    public PathChain Rlaunchtopark;
    public PathChain Rintake3tolaunch;
    public PathChain StarttoLaunch;
    public PathChain LaunchtoIntake1;
    public PathChain Intake1toIntake1end;
    public PathChain Intake1endtoLaunch;
    public PathChain LaunchtoIntake2;
    public PathChain Intake2toIntake2end;
    public PathChain Intake2endtoLaunch;
    public PathChain LaunchtoIntake3;
    public PathChain Intake3toIntake3end;
    public PathChain Intake3endtoLaunch;
    public PathChain RStarttoLaunchH;
    public PathChain RLaunchtoIntake1H;
    public PathChain RIntake1toIntake1endH;
    public PathChain RIntake1endtoLeverH;
    public PathChain RLevertoLaunchH;
    public PathChain StartToTestPose;
    public PathChain RStarttoLaunch;
    public PathChain RLaunchtoIntake1;
    public PathChain RIntake1toIntake1end;
    public PathChain RIntake1endtoLaunch;
    public PathChain RLaunchtoIntake2;
    public PathChain RIntake2toIntake2end;
    public PathChain RIntake2endtoLaunch;
    public PathChain RLaunchtoIntake3;
    public PathChain RIntake3toIntake3end;
    public PathChain RIntake3endtoLaunch;
    public PathChain Forward48;
    public PathChain Backward48;
    public PathChain SideLeft48;
    public PathChain SideRight48;
    public PathChain Intake1endtoLever;
    public PathChain RIntake1endtoLever;
    public PathChain LevertoLaunch;
    public PathChain RLevertoLaunch;
    public PathChain LaunchtoEnd;
    public PathChain RLaunchtoEnd;
    public PathChain Rlaunchfartogateintake;
    public PathChain Rgateintaketolaunchfar;
    public PathChain RintakeEdgeNewtolaunchfar;
    public PathChain RlaunchfartointakeEdgeNew;
    public PathChain RlaunchfartointakeSweep;
    public PathChain RintakeSweeptolaunchfar;

    public static Pose getStart() {
        return new Pose(32.671, 135.916, Math.toRadians(90));
    }

    public static Pose getRStart() {
        return new Pose(112, 135.916, Math.toRadians(90));
    }

    public static Pose getBSegmentedCurveStart() {
        return new Pose(30.748, 135.152, Math.toRadians(90));
    }

    public static Pose getForward48Start() {
        return new Pose(56.000, 8.000, Math.toRadians(90));
    }

    public static Pose getRSegmentedCurveStart() {
        return new Pose(114, 135.152, Math.toRadians(90));
    }

    public static Pose getRFar9BallStart() {
        return new Pose(90, 9, Math.toRadians(90));
    }

    public static Pose getFar9BallStart() {
        return new Pose(54, 9, Math.toRadians(90));
    }

    public static Pose getBFarZoneStart() {
        return new Pose(58, 11, Math.toRadians(115));
    }

    public Paths(Follower follower) {
        follower.setMaxPowerScaling(0.3);
        launch = follower
            .pathBuilder()
            .addPath(new BezierLine(Start, Launch))
            .setConstantHeadingInterpolation(Math.toRadians(launchHeading0))
            //.setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(135))
            .build();
        follower.setMaxPowerScaling(1);
        launchtointake1 = follower
            .pathBuilder()
            .addPath(new BezierCurve(Launch, new Pose(73.411, 86.685), Intake1end))
            .setConstantHeadingInterpolation(Math.toRadians(intakeHeading))
            //.setLinearHeadingInterpolation(Math.toRadians(135), Math.toRadians(180))
            .build();

        intake1tolaunch = follower
            .pathBuilder()
            .addPath(
                //changing all return-to-launch coordinate points except for the very first one cause its
                //not touching the white line when shooting (x increases by 10, y decreases by 10)
                new BezierLine(Intake1end, Launch)
            )
            .setConstantHeadingInterpolation(Math.toRadians(launchHeading1))
            //.setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(135))
            .build();

        launchtointake2 = follower
            .pathBuilder()
            .addPath(new BezierCurve(Launch, new Pose(80, 61), Intake2end))
            .setConstantHeadingInterpolation(Math.toRadians(intakeHeading))
            //.setLinearHeadingInterpolation(Math.toRadians(135), Math.toRadians(180))
            .build();

        intake2tolaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(Intake2end, new Pose(62, 76), Launch))
            .setConstantHeadingInterpolation(Math.toRadians(launchHeading2))
            //.setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(135))
            .build();

        launchtointake3 = follower
            .pathBuilder()
            .addPath(new BezierCurve(Launch, new Pose(46.407, 96.652), Intake3end))
            .setConstantHeadingInterpolation(Math.toRadians(intakeHeading))
            //.setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(135))
            .build();

        launchtopark = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(51.407, 101.595), new Pose(29.192, 49.617)))
            .setLinearHeadingInterpolation(Math.toRadians(135), Math.toRadians(180))
            .build();

        intake3tolaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(Intake3end, Launch))
            .setConstantHeadingInterpolation(Math.toRadians(launchHeading3))
            //.setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(135))
            .build();
        //red zone autos
        Rstarttolaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(RStart, RLaunch))
            .setConstantHeadingInterpolation(Math.toRadians(RlaunchHeading1))
            //.setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(45))
            .build();
        follower.setMaxPowerScaling(1);
        Rlaunchtointake1 = follower
            .pathBuilder()
            .addPath(new BezierCurve(RLaunch, new Pose(71, 86.685), RIntake1))
            .setConstantHeadingInterpolation(Math.toRadians(RintakeHeading))
            //.setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(0))
            .build();

        Rintake1tolaunch = follower
            .pathBuilder()
            .addPath(
                //changing all return-to-launch coordinate points except for the very first one cause its
                //not touching the white line when shooting (x increases by 10, y decreases by 10)
                new BezierLine(RIntake1, RLaunch)
            )
            .setConstantHeadingInterpolation(Math.toRadians(RlaunchHeading2))
            //.setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(45))
            .build();

        Rlaunchtointake2 = follower
            .pathBuilder()
            .addPath(new BezierCurve(RLaunch, new Pose(64, 61), RIntake2end))
            .setConstantHeadingInterpolation(Math.toRadians(RintakeHeading))
            //.setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(RintakeHeading))
            .build();
        Rintake2tolaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(RIntake2end, new Pose(82, 76), RLaunch))
            .setConstantHeadingInterpolation(Math.toRadians(RlaunchHeading3))
            //.setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(RlaunchHeading3))
            .build();
        Rlaunchtointake3 = follower
            .pathBuilder()
            .addPath(new BezierLine(RLaunch, RIntake3end))
            .setConstantHeadingInterpolation(Math.toRadians(RintakeHeading))
            //.setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(135))
            .build();
        Rlaunchtopark = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(93, 101.595), new Pose(115, 49.617)))
            .setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(0))
            .build();

        Rintake3tolaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(RIntake3end, RLaunch))
            .setConstantHeadingInterpolation(Math.toRadians(RlaunchHeading4))
            //.setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(45))
            .build();

        StarttoLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(Start, Launch))
            .setConstantHeadingInterpolation(Math.toRadians(launchHeading0))
            .build();

        LaunchtoIntake1 = follower
            .pathBuilder()
            .addPath(new BezierCurve(Launch, Intake1ControlPoint, Intake1))
            .setConstantHeadingInterpolation(Math.toRadians(intakeHeading))
            .build();

        Intake1toIntake1end = follower
            .pathBuilder()
            .addPath(new BezierLine(Intake1, Intake1end))
            .setTangentHeadingInterpolation()
            .build();
        Intake1endtoLever = follower
            .pathBuilder()
            .addPath(new BezierCurve(Intake1end, LeverControlPoint, Lever))
            .setConstantHeadingInterpolation(Math.toRadians(180))
            //.setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(90))
            .build();
        LevertoLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(Lever, Launch))
            //.setLinearHeadingInterpolation(Math.toRadians(intakeHeading),Math.toRadians(launchHeading1))
            .setConstantHeadingInterpolation(Math.toRadians(launchHeading0))
            .build();
        Intake1endtoLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(Intake1end, Launch))
            //.setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(launchHeading2))
            .setConstantHeadingInterpolation(Math.toRadians(launchHeading1))
            .build();

        LaunchtoIntake2 = follower
            .pathBuilder()
            .addPath(new BezierCurve(Launch, Intake2ControlPoint, Intake2))
            .setConstantHeadingInterpolation(Math.toRadians(intakeHeading))
            .build();

        Intake2toIntake2end = follower
            .pathBuilder()
            .addPath(new BezierLine(Intake2, Intake2end))
            .setTangentHeadingInterpolation()
            .build();

        Intake2endtoLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(Intake2end, Intake2endControlPoint, Launch))
            //.setLinearHeadingInterpolation(Math.toRadians(intakeHeading), Math.toRadians(launchHeading3))
            .setConstantHeadingInterpolation(Math.toRadians(launchHeading2))
            .build();

        LaunchtoIntake3 = follower
            .pathBuilder()
            .addPath(new BezierCurve(Launch, Intake3ControlPoint, Intake3))
            .setConstantHeadingInterpolation(Math.toRadians(intakeHeading))
            .build();

        Intake3toIntake3end = follower
            .pathBuilder()
            .addPath(new BezierLine(Intake3, Intake3end))
            .setTangentHeadingInterpolation()
            .build();

        Intake3endtoLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(Intake3end, Intake3endControlPoint, Launch))
            //.setLinearHeadingInterpolation(Math.toRadians(intakeHeading), Math.toRadians(launchHeading4))
            .setConstantHeadingInterpolation(Math.toRadians(launchHeading3))
            .build();
        LaunchtoEnd = follower
            .pathBuilder()
            .addPath(new BezierLine(Launch, End))
            .setLinearHeadingInterpolation(
                Math.toRadians(launchHeading3),
                Math.toRadians(intakeHeading)
            )
            .build();
        FarStarttoFarLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(FarStart, FarLaunch))
            .setLinearHeadingInterpolation(
                Math.toRadians(tunnelIntakeHeading),
                Math.toRadians(farlaunchHeading1)
            )
            .build();
        FarLaunchtoIntakeCorner = follower
            .pathBuilder()
            .addPath(new BezierCurve(FarLaunch, IntakeCornerControlPoint, IntakeCorner))
            .setLinearHeadingInterpolation(farlaunchHeading1, cornerIntakeHeading)
            .build();

        IntakeCornertoLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(IntakeCorner, FarLaunch))
            .setLinearHeadingInterpolation(cornerIntakeHeading, farlaunchHeading1)
            .build();

        LaunchtoIntakeTunnel = follower
            .pathBuilder()
            .addPath(new BezierLine(FarLaunch, IntakeTunnel))
            .setLinearHeadingInterpolation(farlaunchHeading1, tunnelIntakeHeading)
            .build();

        IntakeTunneltoLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(IntakeTunnel, FarLaunch))
            .setLinearHeadingInterpolation(tunnelIntakeHeading, farlaunchHeading1)
            .build();
        RStarttoLaunchH = follower
            .pathBuilder()
            .addPath(new BezierLine(RStart, RLaunch))
            .setHeadingInterpolation(HeadingInterpolator.facingPoint(RGoal)) //            .setHeadingInterpolation(HeadingInterpolator.facingPoint(0, RADIUS))
            .build();

        RLaunchtoIntake1H = follower
            .pathBuilder()
            .addPath(new BezierCurve(RLaunch, RIntake1ControlPoint, RIntake1))
            .setLinearHeadingInterpolation(
                Math.toRadians(RlaunchHeading1),
                Math.toRadians(RintakeHeading)
            )
            .build();

        RIntake1toIntake1endH = follower
            .pathBuilder()
            .addPath(new BezierLine(RIntake1, RIntake1end))
            .setTangentHeadingInterpolation()
            .build();
        RIntake1endtoLeverH = follower
            .pathBuilder()
            .addPath(new BezierLine(RIntake1end, Rlever))
            .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(90))
            .build();

        RLevertoLaunchH = follower
            .pathBuilder()
            .addPath(new BezierLine(Rlever, RLaunch))
            .setHeadingInterpolation(HeadingInterpolator.facingPoint(RGoal))
            .build();

        RStarttoLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(RStart, RLaunch))
            .setConstantHeadingInterpolation(Math.toRadians(RlaunchHeading1))
            .build();

        RLaunchtoIntake1 = follower
            .pathBuilder()
            .addPath(new BezierCurve(RLaunch, RIntake1ControlPoint, RIntake1))
            .setConstantHeadingInterpolation(Math.toRadians(RintakeHeading))
            .build();

        RIntake1toIntake1end = follower
            .pathBuilder()
            .addPath(new BezierLine(RIntake1, RIntake1end))
            .setTangentHeadingInterpolation()
            .build();
        RIntake1endtoLever = follower
            .pathBuilder()
            .addPath(new BezierCurve(RIntake1end, RleverControlPoint, Rlever))
            .setConstantHeadingInterpolation(Math.toRadians(0))
            //.setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(90))
            .build();

        RLevertoLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(Rlever, RLaunch))
            .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(RlaunchHeading1))
            .build();
        RIntake1endtoLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(RIntake1end, RLaunch))
            .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(RlaunchHeading2))
            .build();

        RLaunchtoIntake2 = follower
            .pathBuilder()
            .addPath(new BezierCurve(RLaunch, RIntake2ControlPoint, RIntake2))
            .setConstantHeadingInterpolation(Math.toRadians(RintakeHeading))
            .build();

        RIntake2toIntake2end = follower
            .pathBuilder()
            .addPath(new BezierLine(RIntake2, RIntake2end))
            .setTangentHeadingInterpolation()
            .build();

        RIntake2endtoLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(RIntake2end, RIntake2endControlPoint, RLaunch))
            .setConstantHeadingInterpolation(Math.toRadians(RlaunchHeading3))
            .build();

        RLaunchtoIntake3 = follower
            .pathBuilder()
            .addPath(new BezierCurve(RLaunch, RIntake3ControlPoint, RIntake3))
            .setConstantHeadingInterpolation(Math.toRadians(RintakeHeading))
            .build();

        RIntake3toIntake3end = follower
            .pathBuilder()
            .addPath(new BezierLine(RIntake3, RIntake3end))
            .setTangentHeadingInterpolation()
            .build();

        RIntake3endtoLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(RIntake3end, RIntake3endControlPoint, RLaunchend))
            .setConstantHeadingInterpolation(Math.toRadians(RlaunchHeading4))
            .build();
        RLaunchtoEnd = follower
            .pathBuilder()
            .addPath(new BezierLine(RLaunch, REnd))
            .setLinearHeadingInterpolation(Math.toRadians(RlaunchHeading4), Math.toRadians(0))
            .build();

        Forward48 = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(56.000, 8.000), new Pose(56.000, 56.000)))
            .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(90))
            .setVelocityConstraint(0.5)
            .build();
        Backward48 = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(56.000, 56.000), new Pose(56.000, 8.000)))
            .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(90))
            .build();

        SideLeft48 = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(56.000, 8.000), new Pose(8, 8.000)))
            .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(90))
            .build();

        SideRight48 = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(8, 8.000), new Pose(56.000, 8.000)))
            .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(90))
            .build();
        RStartFartolaunchfar = follower
            .pathBuilder()
            .addPath(new BezierLine(RfarStart, RfarLaunch))
            .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(RfarlaunchHeading))
            .setVelocityConstraint(0.3)
            .build();

        Rlaunchfartointake4 = follower
            .pathBuilder()
            .addPath(new BezierCurve(RfarLaunch, Rintake4ControlPoint, Rintake4))
            .setConstantHeadingInterpolation(Math.toRadians(RintakeHeading))
            .setVelocityConstraint(0.3)
            .build();

        Rintake4tolaunchfar = follower
            .pathBuilder()
            .addPath(new BezierLine(Rintake4, RfarLaunch2))
            .setConstantHeadingInterpolation(Math.toRadians(RfarlaunchHeading2))
            //.setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(RfarlaunchHeading))
            .setVelocityConstraint(0.3)
            .build();

        RlaunchfartointakeCorner = follower
            .pathBuilder()
            .addPath(new BezierCurve(RfarLaunch2, RintakeCornerControlPoint, RintakeCorner))
            //            .setLinearHeadingInterpolation(
            //                Math.toRadians(RlaunchHeading1),
            //                Math.toRadians(RcornerIntakeHeading)
            //            )
            //.setTangentHeadingInterpolation() //test this
            .setConstantHeadingInterpolation(RcornerIntakeHeading)
            .setVelocityConstraint(0.3)
            .build();

        RintakeCornertolaunchfar = follower
            .pathBuilder()
            .addPath(new BezierLine(RintakeCorner, RfarLaunch3))
            .setVelocityConstraint(0.3)
            .setLinearHeadingInterpolation(
                Math.toRadians(RcornerIntakeHeading2),
                Math.toRadians(RfarlaunchHeading3)
            )
            .build();

        RlaunchfartointakeCornerNew = follower
            //new way to intake corner balls
            .pathBuilder()
            .addPath(new BezierCurve(RfarLaunch3, RintakeCornerControlPoint, RintakeNewCorner))
            //            .setLinearHeadingInterpolation(
            //                Math.toRadians(RlaunchHeading1),
            //                Math.toRadians(RcornerIntakeHeading)
            //            )
            //.setTangentHeadingInterpolation() //test this
            .setConstantHeadingInterpolation(Math.toRadians(RNewCornerIntakeHeading))
            .setVelocityConstraint(0.3)
            .build();

        RintakeCornerNewtolaunchfar = follower
            //new way to intake corner balls
            .pathBuilder()
            .addPath(new BezierLine(RintakeNewCorner, RfarLaunch4))
            .setVelocityConstraint(0.3)
            .setLinearHeadingInterpolation(
                Math.toRadians(RNewCornerIntakeHeading),
                Math.toRadians(RfarlaunchHeading3)
            )
            .build();
        RlaunchfartointakeSweep = follower
            .pathBuilder()
            .addPath(new BezierLine(RfarLaunch3, RintakeSweep))
            //            .setLinearHeadingInterpolation(
            //                Math.toRadians(RlaunchHeading1),
            //                Math.toRadians(RcornerIntakeHeading)
            //            )
            //.setTangentHeadingInterpolation() //test this
            .setConstantHeadingInterpolation(Math.toRadians(RSweepIntakeHeading))
            .setVelocityConstraint(0.3)
            .build();

        RintakeSweeptolaunchfar = follower
            //new way to intake corner balls
            .pathBuilder()
            .addPath(new BezierLine(RintakeSweep, RfarLaunch4))
            .setVelocityConstraint(0.3)
            .setLinearHeadingInterpolation(
                Math.toRadians(RSweepIntakeHeading),
                Math.toRadians(RfarlaunchHeading4)
            )
            .build();

        RlaunchfartointakeEdgeNew = follower
            //new way to intake corner balls
            .pathBuilder()
            .addPath(new BezierCurve(RfarLaunch3, RintakeEdgeControlPoint, RintakeNewEdge))
            //            .setLinearHeadingInterpolation(
            //                Math.toRadians(RlaunchHeading1),
            //                Math.toRadians(RcornerIntakeHeading)
            //            )
            //.setTangentHeadingInterpolation() //test this
            .setConstantHeadingInterpolation(Math.toRadians(RNewCornerIntakeHeading))
            .setVelocityConstraint(0.3)
            .build();

        RintakeEdgeNewtolaunchfar = follower
            //new way to intake corner balls
            .pathBuilder()
            .addPath(new BezierLine(RintakeNewEdge, RfarLaunch4))
            .setVelocityConstraint(0.3)
            .setLinearHeadingInterpolation(
                Math.toRadians(RNewCornerIntakeHeading),
                Math.toRadians(RfarlaunchHeading3)
            )
            .build();

        Rlaunchfartopark = follower
            .pathBuilder()
            .addPath(new BezierLine(RfarLaunch4, RfarPark))
            .setLinearHeadingInterpolation(Math.toRadians(RfarlaunchHeading), Math.toRadians(0))
            .setVelocityConstraint(0.3)
            .build();

        StartFartolaunchfar = follower
            .pathBuilder()
            .addPath(new BezierLine(farStart, farLaunch))
            .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(farlaunchHeading1))
            .setVelocityConstraint(0.3)
            .build();

        launchfartointake4 = follower
            .pathBuilder()
            .addPath(new BezierCurve(farLaunch, intake4ControlPoint, intake4))
            .setConstantHeadingInterpolation(Math.toRadians(intakeHeading))
            //.setLinearHeadingInterpolation(Math.toRadians(115), Math.toRadians(180))
            .setVelocityConstraint(0.3)
            .build();

        intake4tolaunchfar = follower
            .pathBuilder()
            .addPath(new BezierLine(intake4, farLaunch))
            .setLinearHeadingInterpolation(
                Math.toRadians(intakeHeading),
                Math.toRadians(farlaunchHeading2)
            )
            .setVelocityConstraint(0.3)
            .build();

        //        launchfartointake5 = follower
        //                .pathBuilder()
        //                .addPath(new BezierLine(new Pose(59.000, 12.000), new Pose(10.75, 8.061)))
        //                .setLinearHeadingInterpolation(Math.toRadians(115), Math.toRadians(180))
        //                .setVelocityConstraint(0.3)
        //                .build();

        launchfartointakeSweep = follower
            .pathBuilder()
            .addPath(new BezierLine(farLaunch3, intakeSweep))
            //            .setLinearHeadingInterpolation(
            //                Math.toRadians(RlaunchHeading1),
            //                Math.toRadians(RcornerIntakeHeading)
            //            )
            //.setTangentHeadingInterpolation() //test this
            .setConstantHeadingInterpolation(Math.toRadians(SweepIntakeHeading))
            .setVelocityConstraint(0.3)
            .build();

        intakeSweeptolaunchfar = follower
            //new way to intake corner balls
            .pathBuilder()
            .addPath(new BezierLine(intakeSweep, farLaunch4))
            .setVelocityConstraint(0.3)
            .setLinearHeadingInterpolation(
                Math.toRadians(SweepIntakeHeading),
                Math.toRadians(farlaunchHeading4)
            )
            .build();

        launchfartointakeEdgeNew = follower
            //new way to intake corner balls
            .pathBuilder()
            .addPath(new BezierCurve(farLaunch3, intakeEdgeControlPoint, intakeNewEdge))
            //            .setLinearHeadingInterpolation(
            //                Math.toRadians(RlaunchHeading1),
            //                Math.toRadians(RcornerIntakeHeading)
            //            )
            //.setTangentHeadingInterpolation() //test this
            .setConstantHeadingInterpolation(Math.toRadians(NewCornerIntakeHeading))
            .setVelocityConstraint(0.3)
            .build();

        intakeEdgeNewtolaunchfar = follower
            //new way to intake corner balls
            .pathBuilder()
            .addPath(new BezierLine(intakeNewEdge, farLaunch4))
            .setVelocityConstraint(0.3)
            .setLinearHeadingInterpolation(
                Math.toRadians(NewCornerIntakeHeading),
                Math.toRadians(farlaunchHeading3)
            )
            .build();
        launchfartointakeCorner = follower
            .pathBuilder()
            .addPath(new BezierCurve(farLaunch, intakeCornerControlPoint, intakeCorner))
            //            .setLinearHeadingInterpolation(
            //                Math.toRadians(farlaunchHeading),
            //                Math.toRadians(cornerIntakeHeading)
            //            )
            .setConstantHeadingInterpolation(cornerIntakeHeading)
            .build();

        intakeCornertolaunchfar = follower
            .pathBuilder()
            .addPath(new BezierLine(intakeCorner, farLaunch))
            .setVelocityConstraint(0.3)
            .setLinearHeadingInterpolation(
                Math.toRadians(cornerIntakeHeading2),
                Math.toRadians(farlaunchHeading3)
            )
            .build();

        launchfartogateintake = follower
            .pathBuilder()
            .addPath(new BezierCurve(farLaunch, gateIntakeControlPoint, gateIntake))
            .setLinearHeadingInterpolation(
                Math.toRadians(farlaunchHeading3),
                Math.toRadians(tunnelIntakeHeading)
            )
            .build();

        gateintaketolaunchfar = follower
            .pathBuilder()
            .addPath(new BezierLine(gateIntake, farLaunch))
            .setVelocityConstraint(0.3)
            .setLinearHeadingInterpolation(
                Math.toRadians(tunnelIntakeHeading),
                Math.toRadians(farlaunchHeading4)
            )
            .build();

        launchfartopark = follower
            .pathBuilder()
            .addPath(new BezierLine(farLaunch, farPark))
            .setLinearHeadingInterpolation(Math.toRadians(farlaunchHeading4), Math.toRadians(180))
            .setVelocityConstraint(0.3)
            .build();
        Rlaunchfartogateintake = follower
            .pathBuilder()
            .addPath(new BezierCurve(RfarLaunch, RgateIntakeControlPoint, RgateIntake))
            .setLinearHeadingInterpolation(
                Math.toRadians(RfarlaunchHeading),
                Math.toRadians(RtunnelIntakeHeading)
            )
            .build();

        Rgateintaketolaunchfar = follower
            .pathBuilder()
            .addPath(new BezierLine(RgateIntake, RfarLaunch))
            .setVelocityConstraint(0.3)
            .setLinearHeadingInterpolation(
                Math.toRadians(RtunnelIntakeHeading),
                Math.toRadians(RfarlaunchHeading4)
            )
            .build();
        StartToTestPose = follower
            .pathBuilder()
            .addPath(new BezierLine(Start, testPose))
            .setVelocityConstraint(0.3)
            .setConstantHeadingInterpolation(Math.toRadians(testPose.getHeading()))
            .build();
    }

    // move the park position lower to scoop more balls and shoot again
}

//    public static Command Pedropathcommand(Robot r){
//        return new PPPathCommand()
//    }
