package org.firstinspires.ftc.sixteen750.commands.auto;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.technototes.library.command.Command;
import com.technototes.library.command.ParallelCommandGroup;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.path.command.TrajectorySequenceCommand;
import org.firstinspires.ftc.sixteen750.AutoConstants;
import org.firstinspires.ftc.sixteen750.PathConstants;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;
import org.firstinspires.ftc.sixteen750.subsystems.DrivebaseSubsystem;

public class Paths {

    public static Follower follower;
    public static Command Launching3Balls(Robot r) {
        return new SequentialCommandGroup(
            new ParallelCommandGroup(
                TeleCommands.Intake(r),
                TeleCommands.GateUp(r),
                TeleCommands.Launch(r)
            ),
            new WaitCommand(2),
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
            TeleCommands.Intake(r),
            TeleCommands.GateUp(r),
            // no need to wait for spinup as we will leave the flywheel spinning constantly during auto
            new WaitCommand(0.2),
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
            // want to keep launcher running during auto also no need to stop intake
        );
    }

    public static Command SidetoSideCommand(Robot r) {
        return new SequentialCommandGroup(
            new TrajectorySequenceCommand(r.drivebase, PathConstants.SIDE_LEFT_TO_SIDE_RIGHT),
            new TrajectorySequenceCommand(r.drivebase, PathConstants.SIDE_RIGHT_TO_SIDE_LEFT)
        );
    }
    public Pose Start = new Pose(30.748, 135.152);
    public Pose Launch = new Pose(38.490, 105.512);
    public Pose Intake1 = new Pose(41.808, 91.276);
    public Pose Intake1ControlPoint = new Pose(61.273, 89.143);
    public Pose Intake1end = new Pose(15.043, 90.276);
    public Pose Intake2 = new Pose(40.038, 69.502);
    public Pose Intake2ControlPoint = new Pose(71.891, 64.369);
    public Pose Intake2end = new Pose(9.513, 69.281);
    public Pose Intake2endControlPoint = new Pose(49.328, 65.696);
    public Pose Intake3 = new Pose(38.923, 47.834);
    public Pose Intake3ControlPoint = new Pose(76.536, 41.585);
    public Pose Intake3end = new Pose(7.407, 47.834);
    public Pose Intake3endControlPoint = new Pose(62.000, 84.000);
    //Red poses reconfigure these
    public Pose RStart = new Pose(114, 135.152);
    public Pose RLaunch = new Pose(106, 105.512);
    public Pose RIntake1 = new Pose(103, 84.276);
    public Pose RIntake1ControlPoint = new Pose(83, 89.143);
    public Pose RIntake1end = new Pose(129, 84.276);
    public Pose RIntake2 = new Pose(104, 59.502);
    public Pose RIntake2ControlPoint = new Pose(73, 64.369);
    public Pose RIntake2end = new Pose(134, 59.281);
    public Pose RIntake2endControlPoint = new Pose(95, 65.696);
    public Pose RIntake3 = new Pose(104, 35.834);
    public Pose RIntake3ControlPoint = new Pose(68, 41.585);
    public Pose RIntake3end = new Pose(136, 35.613);
    public Pose RIntake3endControlPoint = new Pose(82, 84.000);

    public PathChain launch;
    public PathChain launchtointake1;
    public PathChain intake1tolaunch;
    public PathChain launchtointake2;
    public PathChain intake2tolaunch;
    public PathChain launchtopark;
    public PathChain intake3tolaunch;
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

    public Pose getStart() {
        return new Pose(32.671, 135.916, Math.toRadians(90));
    }
    public Pose getRStart() {
        return new Pose(112, 135.916, Math.toRadians(90));
    }
    public Pose getBSegmentedCurveStart() {
        return new Pose(30.748, 135.152, Math.toRadians(90));
    }
    public Pose getRSegmentedCurveStart() {
        return new Pose(114, 135.152, Math.toRadians(90));
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
                        new BezierCurve(
                                new Pose(93, 101.338),
                                new Pose(71, 86.685),
                                new Pose(126, 95)
                        )
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
                .addPath(
                        new BezierCurve(
                                new Pose(93, 101.123),
                                new Pose(64, 61),
                                new Pose(126, 70)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(0))
                .build();
        Rintake2tolaunch = follower
                .pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Pose(126, 70),
                                new Pose(82, 76),
                                new Pose(93, 101.595)
                        )
                )
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
                .addPath(
                        new BezierLine(Start, Launch)
                )
                .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(125))
                .build();

        LaunchtoIntake1 = follower
                .pathBuilder()
                .addPath(
                        new BezierCurve(
                                Launch,
                                Intake1ControlPoint,
                                Intake1
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(125), Math.toRadians(180))
                .build();

        Intake1toIntake1end = follower
                .pathBuilder()
                .addPath(
                        new BezierLine(Intake1, Intake1end)
                )
                .setTangentHeadingInterpolation()
                .build();

        Intake1endtoLaunch = follower
                .pathBuilder()
                .addPath(
                        new BezierLine(Intake1end, Launch)
                )
                .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(125))
                .build();

        LaunchtoIntake2 = follower
                .pathBuilder()
                .addPath(
                        new BezierCurve(
                                Launch,
                                Intake2ControlPoint,
                                Intake2
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(125), Math.toRadians(180))
                .build();

        Intake2toIntake2end = follower
                .pathBuilder()
                .addPath(
                        new BezierLine(Intake2, Intake2end)
                )
                .setTangentHeadingInterpolation()
                .build();

        Intake2endtoLaunch = follower
                .pathBuilder()
                .addPath(
                        new BezierCurve(
                                Intake2end,
                                Intake2endControlPoint,
                                Launch
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(125))
                .build();

        LaunchtoIntake3 = follower
                .pathBuilder()
                .addPath(
                        new BezierCurve(
                                Launch,
                                Intake3ControlPoint,
                                Intake3
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(125), Math.toRadians(180))
                .build();

        Intake3toIntake3end = follower
                .pathBuilder()
                .addPath(
                        new BezierLine(Intake3, Intake3end)
                )
                .setTangentHeadingInterpolation()
                .build();

        Intake3endtoLaunch = follower
                .pathBuilder()
                .addPath(
                        new BezierCurve(
                                Intake3end,
                                Intake3endControlPoint,
                                Launch
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(125))
                .build();
        RStarttoLaunch = follower
                .pathBuilder()
                .addPath(
                        new BezierLine(RStart, RLaunch)
                )
                .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(45))
                .build();

        RLaunchtoIntake1 = follower
                .pathBuilder()
                .addPath(
                        new BezierCurve(
                                RLaunch,
                                RIntake1ControlPoint,
                                RIntake1
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(0))
                .build();

        RIntake1toIntake1end = follower
                .pathBuilder()
                .addPath(
                        new BezierLine(RIntake1, RIntake1end)
                )
                .setTangentHeadingInterpolation()
                .build();

        RIntake1endtoLaunch = follower
                .pathBuilder()
                .addPath(
                        new BezierLine(RIntake1end, RLaunch)
                )
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(45))
                .build();

        RLaunchtoIntake2 = follower
                .pathBuilder()
                .addPath(
                        new BezierCurve(
                                RLaunch,
                                RIntake2ControlPoint,
                                RIntake2
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(0))
                .build();

        RIntake2toIntake2end = follower
                .pathBuilder()
                .addPath(
                        new BezierLine(RIntake2, RIntake2end)
                )
                .setTangentHeadingInterpolation()
                .build();

        RIntake2endtoLaunch = follower
                .pathBuilder()
                .addPath(
                        new BezierCurve(
                                RIntake2end,
                                RIntake2endControlPoint,
                                RLaunch
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(45))
                .build();

        RLaunchtoIntake3 = follower
                .pathBuilder()
                .addPath(
                        new BezierCurve(
                                RLaunch,
                                RIntake3ControlPoint,
                                RIntake3
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(0))
                .build();

        RIntake3toIntake3end = follower
                .pathBuilder()
                .addPath(
                        new BezierLine(RIntake3, RIntake3end)
                )
                .setTangentHeadingInterpolation()
                .build();

        RIntake3endtoLaunch = follower
                .pathBuilder()
                .addPath(
                        new BezierCurve(
                                RIntake3end,
                                RIntake3endControlPoint,
                                RLaunch
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(45))
                .build();
    }
}

//    public static Command Pedropathcommand(Robot r){
//        return new PPPathCommand()
//    }
