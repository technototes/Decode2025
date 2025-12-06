package org.firstinspires.ftc.sixteen750.commands.auto;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.technototes.library.command.Command;
import com.technototes.library.command.ParallelCommandGroup;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import org.firstinspires.ftc.sixteen750.R;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;
import org.firstinspires.ftc.sixteen750.commands.driving.DrivingCommands;

@Configurable
public class Paths {

    public static Follower follower;

    public static Command Launching3Balls(Robot r) {
        return new SequentialCommandGroup(
            new ParallelCommandGroup(
                TeleCommands.Intake(r),
                TeleCommands.GateUp(r),
                TeleCommands.AutoLaunch(r)
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
            new WaitCommand(1.15),
            TeleCommands.GateUp(r),
            TeleCommands.Intake(r)
            // want to keep launcher running during auto also no need to stop intake
        );
    }

    public static Pose Start = new Pose(30.748, 135.152);
    public static Pose Launch = new Pose(45, 110);
    public static Pose Intake1 = new Pose(41.808, 85);
    public static Pose Intake1ControlPoint = new Pose(61.273, 89.143);
    public static Pose Intake1end = new Pose(19, 85);
    public static Pose Lever = new Pose(12.25, 75.5);
    public static Pose Intake2 = new Pose(40.038, 63);
    public static Pose Intake2ControlPoint = new Pose(72, 61);
    public static Pose Intake2ControlPoint2 = new Pose(46, 64);
    public static Pose Intake2end = new Pose(15, 63);
    public static Pose Intake2endControlPoint = new Pose(49.328, 65.696);
    public static Pose Intake3 = new Pose(38.923, 38);
    public static Pose Intake3ControlPoint = new Pose(76.536, 41.585);
    public static Pose Intake3end = new Pose(15, 38);
    public static Pose Intake3endControlPoint = new Pose(62.000, 84.000);
    public static Pose Startfar;
    public static Pose End = new Pose(19, 105);
    public static Pose FarStart = new Pose(49, 11);
    public static Pose FarLaunch = new Pose(63, 14);
    public static Pose IntakeCorner = new Pose(11, 12);
    public static Pose IntakeCornerControlPoint = new Pose(42, 29);
    public static Pose IntakeTunnel = new Pose(8, 27);

    public static double launchHeading = 130;
    public static double launchHeading1 = 140;
    public static double launchHeading2 = 150;
    public static double launchHeading3 = 140;
    public static double launchfarheading = 108;
    public static double intakeHeading = 180;
    public static double farlaunchHeading = 115;
    public static double cornerIntakeHeading = 195;
    public static double tunnelIntakeHeading = 90;

    //Red poses reconfigure these
    public static Pose RStart = new Pose(114, 135.152);
    public static Pose RLaunch = new Pose(90, 90);
    public static Pose RLaunchend = new Pose(90, 90);

    public static Pose RIntake1 = new Pose(103, 88);
    public static Pose RIntake1ControlPoint = new Pose(83, 89.143);
    public static Pose RIntake1end = new Pose(123, 88);
    public static Pose RIntake2 = new Pose(104, 65);
    public static Pose RIntake2ControlPoint = new Pose(73, 64.369);
    public static Pose RIntake2end = new Pose(131, 65);
    public static Pose RIntake2endControlPoint = new Pose(95, 65.696);
    public static Pose RIntake3 = new Pose(104, 40);
    public static Pose RIntake3ControlPoint = new Pose(68, 41.585);
    public static Pose RIntake3end = new Pose(131, 40);
    public static Pose RIntake3endControlPoint = new Pose(82, 84.000);
    public static Pose Rlever = new Pose(131.75, 75.5);
    public static Pose REnd = new Pose(128.5, 105);
    public static double RlaunchHeading = 50.5;
    public static double RlaunchHeading2 = 45;
    public static double RlaunchHeading3 = 45;
    public static double RintakeHeading = 0;
    public static double Rlaunchfarheading = 282;

    public PathChain launch;
    public PathChain launchtointake1;
    public PathChain intake1tolaunch;
    public PathChain launchtointake2;
    public PathChain intake2tolaunch;
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
    public PathChain Rintake5tolaunchfar;
    public PathChain intake5tolaunchfar;
    public PathChain Rlaunchfartointake5;
    public PathChain launchfartointake5;
    public PathChain launchfartogateintake;
    public PathChain gateintaketolaunchfar;
    public PathChain Rlaunchfartopark;
    public PathChain launchfartopark;
    public PathChain Rlaunch;
    public PathChain Rlaunchtointake1;
    public PathChain Rintake1tolaunch;
    public PathChain Rlaunchtointake2;
    public PathChain Rintake2tolaunch;
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
            .addPath(new BezierLine(new Pose(32.671, 135.916), new Pose(51.249, 101.338)))
            .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(135))
            .build();
        follower.setMaxPowerScaling(1);
        launchtointake1 = follower
            .pathBuilder()
            .addPath(
                new BezierCurve(
                    new Pose(51.249, 101.338),
                    new Pose(73.411, 86.685),
                    new Pose(18.135, 87.177)
                )
            )
            .setLinearHeadingInterpolation(Math.toRadians(135), Math.toRadians(180))
            .build();

        intake1tolaunch = follower
            .pathBuilder()
            .addPath(
                //changing all return-to-launch coordinate points except for the very first one cause its
                //not touching the white line when shooting (x increases by 10, y decreases by 10)
                new BezierLine(new Pose(18.135, 87.177), new Pose(51.464, 101.123))
            )
            .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(135))
            .build();

        launchtointake2 = follower
            .pathBuilder()
            .addPath(
                new BezierCurve(
                    new Pose(51.464, 101.123),
                    new Pose(80, 61),
                    new Pose(18.400, 63.132)
                )
            )
            .setLinearHeadingInterpolation(Math.toRadians(135), Math.toRadians(180))
            .build();

        intake2tolaunch = follower
            .pathBuilder()
            .addPath(
                new BezierCurve(
                    new Pose(18.400, 63.132),
                    new Pose(62, 76),
                    new Pose(51.407, 101.595)
                )
            )
            .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(135))
            .build();

        launchtopark = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(51.407, 101.595), new Pose(29.192, 49.617)))
            .setLinearHeadingInterpolation(Math.toRadians(135), Math.toRadians(180))
            .build();

        intake3tolaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(19.192, 39.617), new Pose(46.407, 96.652)))
            .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(135))
            .build();
        //red zone autos
        Rlaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(112, 135.916), new Pose(93, 101.338)))
            .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(45))
            .build();
        follower.setMaxPowerScaling(1);
        Rlaunchtointake1 = follower
            .pathBuilder()
            .addPath(
                new BezierCurve(new Pose(93, 101.338), new Pose(71, 86.685), new Pose(126, 95))
            )
            .setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(0))
            .build();

        Rintake1tolaunch = follower
            .pathBuilder()
            .addPath(
                //changing all return-to-launch coordinate points except for the very first one cause its
                //not touching the white line when shooting (x increases by 10, y decreases by 10)
                new BezierLine(new Pose(126, 95), new Pose(93, 101.123))
            )
            .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(45))
            .build();

        Rlaunchtointake2 = follower
            .pathBuilder()
            .addPath(new BezierCurve(new Pose(93, 101.123), new Pose(64, 61), new Pose(126, 70)))
            .setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(0))
            .build();
        Rintake2tolaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(new Pose(126, 70), new Pose(82, 76), new Pose(93, 101.595)))
            .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(45))
            .build();
        Rlaunchtopark = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(93, 101.595), new Pose(115, 49.617)))
            .setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(0))
            .build();

        Rintake3tolaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(125, 50), new Pose(118, 96.652)))
            .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(45))
            .build();

        StarttoLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(Start, Launch))
            .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(launchHeading))
            .build();

        LaunchtoIntake1 = follower
            .pathBuilder()
            .addPath(new BezierCurve(Launch, Intake1ControlPoint, Intake1))
            .setLinearHeadingInterpolation(
                Math.toRadians(launchHeading),
                Math.toRadians(intakeHeading)
            )
            .build();

        Intake1toIntake1end = follower
            .pathBuilder()
            .addPath(new BezierLine(Intake1, Intake1end))
            .setTangentHeadingInterpolation()
            .build();
        Intake1endtoLever = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(20.000, 85.000), new Pose(12.250, 75.500)))
            .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(90))
            .build();
        LevertoLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(Lever, Launch))
            .setLinearHeadingInterpolation(
                Math.toRadians(intakeHeading),
                Math.toRadians(launchHeading)
            )
            .build();
        Intake1endtoLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(Intake1end, Launch))
            .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(launchHeading1))
            .build();

        LaunchtoIntake2 = follower
            .pathBuilder()
            .addPath(new BezierCurve(Launch, Intake2ControlPoint, Intake2ControlPoint2, Intake2))
            .setLinearHeadingInterpolation(
                Math.toRadians(launchHeading1),
                Math.toRadians(intakeHeading)
            )
            .build();

        Intake2toIntake2end = follower
            .pathBuilder()
            .addPath(new BezierLine(Intake2, Intake2end))
            .setTangentHeadingInterpolation()
            .build();

        Intake2endtoLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(Intake2end, Intake2endControlPoint, Launch))
            .setLinearHeadingInterpolation(
                Math.toRadians(intakeHeading),
                Math.toRadians(launchHeading2)
            )
            .build();

        LaunchtoIntake3 = follower
            .pathBuilder()
            .addPath(new BezierCurve(Launch, Intake3ControlPoint, Intake3))
            .setLinearHeadingInterpolation(
                Math.toRadians(launchHeading2),
                Math.toRadians(intakeHeading)
            )
            .build();

        Intake3toIntake3end = follower
            .pathBuilder()
            .addPath(new BezierLine(Intake3, Intake3end))
            .setTangentHeadingInterpolation()
            .build();

        Intake3endtoLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(Intake3end, Intake3endControlPoint, Launch))
            .setLinearHeadingInterpolation(
                Math.toRadians(intakeHeading),
                Math.toRadians(launchHeading3)
            )
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
                Math.toRadians(farlaunchHeading)
            )
            .build();
        FarLaunchtoIntakeCorner = follower
            .pathBuilder()
            .addPath(new BezierCurve(FarLaunch, IntakeCornerControlPoint, IntakeCorner))
            .setLinearHeadingInterpolation(farlaunchHeading, cornerIntakeHeading)
            .build();

        IntakeCornertoLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(IntakeCorner, FarLaunch))
            .setLinearHeadingInterpolation(cornerIntakeHeading, farlaunchHeading)
            .build();

        LaunchtoIntakeTunnel = follower
            .pathBuilder()
            .addPath(new BezierLine(FarLaunch, IntakeTunnel))
            .setLinearHeadingInterpolation(farlaunchHeading, tunnelIntakeHeading)
            .build();

        IntakeTunneltoLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(IntakeTunnel, FarLaunch))
            .setLinearHeadingInterpolation(tunnelIntakeHeading, farlaunchHeading)
            .build();

        RStarttoLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(RStart, RLaunch))
            .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(RlaunchHeading))
            .build();

        RLaunchtoIntake1 = follower
            .pathBuilder()
            .addPath(new BezierCurve(RLaunch, RIntake1ControlPoint, RIntake1))
            .setLinearHeadingInterpolation(
                Math.toRadians(RlaunchHeading),
                Math.toRadians(RintakeHeading)
            )
            .build();

        RIntake1toIntake1end = follower
            .pathBuilder()
            .addPath(new BezierLine(RIntake1, RIntake1end))
            .setTangentHeadingInterpolation()
            .build();
        RIntake1endtoLever = follower
            .pathBuilder()
            .addPath(new BezierLine(RIntake1end, Rlever))
            .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(90))
            .build();

        RLevertoLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(Rlever, RLaunch))
            .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(RlaunchHeading))
            .build();
        RIntake1endtoLaunch = follower
            .pathBuilder()
            .addPath(new BezierLine(RIntake1end, RLaunch))
            .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(RlaunchHeading))
            .build();

        RLaunchtoIntake2 = follower
            .pathBuilder()
            .addPath(new BezierCurve(RLaunch, RIntake2ControlPoint, RIntake2))
            .setLinearHeadingInterpolation(
                Math.toRadians(RlaunchHeading),
                Math.toRadians(RintakeHeading)
            )
            .build();

        RIntake2toIntake2end = follower
            .pathBuilder()
            .addPath(new BezierLine(RIntake2, RIntake2end))
            .setTangentHeadingInterpolation()
            .build();

        RIntake2endtoLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(RIntake2end, RIntake2endControlPoint, RLaunch))
            .setLinearHeadingInterpolation(
                Math.toRadians(RintakeHeading),
                Math.toRadians(RlaunchHeading)
            )
            .build();

        RLaunchtoIntake3 = follower
            .pathBuilder()
            .addPath(new BezierCurve(RLaunch, RIntake3ControlPoint, RIntake3))
            .setLinearHeadingInterpolation(
                Math.toRadians(RlaunchHeading),
                Math.toRadians(RintakeHeading)
            )
            .build();

        RIntake3toIntake3end = follower
            .pathBuilder()
            .addPath(new BezierLine(RIntake3, RIntake3end))
            .setTangentHeadingInterpolation()
            .build();

        RIntake3endtoLaunch = follower
            .pathBuilder()
            .addPath(new BezierCurve(RIntake3end, RIntake3endControlPoint, RLaunchend))
            .setLinearHeadingInterpolation(
                Math.toRadians(RintakeHeading),
                Math.toRadians(RlaunchHeading)
            )
            .build();
        RLaunchtoEnd = follower
            .pathBuilder()
            .addPath(new BezierLine(RLaunch, REnd))
            .setLinearHeadingInterpolation(Math.toRadians(launchHeading3), Math.toRadians(0))
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
            .addPath(new BezierLine(new Pose(90.000, 9.000), new Pose(85.000, 12.000)))
            .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(65))
            .setVelocityConstraint(0.3)
            .build();

        Rlaunchfartointake4 = follower
            .pathBuilder()
            .addPath(
                new BezierCurve(
                    new Pose(85.000, 12.000),
                    new Pose(78.000, 40.000),
                    new Pose(135.000, 36.000)
                )
            )
            .setLinearHeadingInterpolation(Math.toRadians(65), Math.toRadians(0))
            .setVelocityConstraint(0.3)
            .build();

        Rintake4tolaunchfar = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(135.000, 36.000), new Pose(85.000, 12.000)))
            .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(45))
            .setVelocityConstraint(0.3)
            .build();

        Rlaunchfartointake5 = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(85.000, 12.000), new Pose(138.286, 13.061)))
            .setLinearHeadingInterpolation(Math.toRadians(65), Math.toRadians(0))
            .setVelocityConstraint(0.3)
            .build();

        Rintake5tolaunchfar = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(138.286, 13.061), new Pose(85.000, 12.000)))
            .setVelocityConstraint(0.3)
            .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(65))
            .build();

        Rlaunchfartopark = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(85.000, 12.000), new Pose(122.776, 12.735)))
            .setLinearHeadingInterpolation(Math.toRadians(65), Math.toRadians(0))
            .setVelocityConstraint(0.3)
            .build();

        StartFartolaunchfar = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(54.000, 9.000), new Pose(59.000, 12.000)))
            .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(125))
            .setVelocityConstraint(0.3)
            .build();

        launchfartointake4 = follower
            .pathBuilder()
            .addPath(
                new BezierCurve(
                    new Pose(35.000, 12.000),
                    new Pose(66.000, 40.000),
                    new Pose(7.000, 36.000)
                )
            )
            .setLinearHeadingInterpolation(Math.toRadians(115), Math.toRadians(180))
            .setVelocityConstraint(0.3)
            .build();

        intake4tolaunchfar = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(7.000, 36.000), new Pose(59.000, 12.000)))
            .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(125))
            .setVelocityConstraint(0.3)
            .build();

        //        launchfartointake5 = follower
        //                .pathBuilder()
        //                .addPath(new BezierLine(new Pose(59.000, 12.000), new Pose(10.75, 8.061)))
        //                .setLinearHeadingInterpolation(Math.toRadians(115), Math.toRadians(180))
        //                .setVelocityConstraint(0.3)
        //                .build();

        launchfartointake5 = follower
            .pathBuilder()
            .addPath(
                new BezierCurve(
                    new Pose(59.000, 12.000),
                    new Pose(2.220, 30.666),
                    new Pose(11.480, 12.229)
                )
            )
            .setLinearHeadingInterpolation(Math.toRadians(115), Math.toRadians(290))
            .build();

        intake5tolaunchfar = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(11.48, 12.229), new Pose(59.000, 12.000)))
            .setVelocityConstraint(0.3)
            .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(125))
            .build();

        launchfartogateintake = follower
            .pathBuilder()
            .addPath(
                new BezierCurve(
                    new Pose(59.000, 12.000),
                    new Pose(0.000, 0.000),
                    new Pose(6.000, 43.000)
                )
            )
            .setLinearHeadingInterpolation(Math.toRadians(115), Math.toRadians(80))
            .build();

        gateintaketolaunchfar = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(6, 43), new Pose(59.000, 12.000)))
            .setVelocityConstraint(0.3)
            .setLinearHeadingInterpolation(Math.toRadians(80), Math.toRadians(125))
            .build();

        launchfartopark = follower
            .pathBuilder()
            .addPath(new BezierLine(new Pose(59.000, 12.000), new Pose(21.25, 12.735)))
            .setLinearHeadingInterpolation(Math.toRadians(115), Math.toRadians(180))
            .setVelocityConstraint(0.3)
            .build();
    }
    // move the park position lower to scoop more balls and shoot again
}

//    public static Command Pedropathcommand(Robot r){
//        return new PPPathCommand()
//    }
