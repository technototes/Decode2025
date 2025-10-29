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
import com.technototes.path.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.sixteen750.AutoConstants;
import org.firstinspires.ftc.sixteen750.PathConstants;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;
import org.firstinspires.ftc.sixteen750.subsystems.DrivebaseSubsystem;

public class Paths {

    public static Follower follower;
    public static PathChain pathChain;

    public static Command splineTestCommand(Robot r) {
        return new TrajectorySequenceCommand(r.drivebase, PathConstants.SPLINETEST1_TO_SPLINETEST2);
    }
    public static Command NineArtiRedNear(Robot r) {
        return new TrajectorySequenceCommand(r.drivebase, PathConstants.STARTNEAR_TO_SCORENEAR)
                // need to create a fast and a slow version of the intake
                .andThen(AutoLaunching3Balls(r))
                .andThen(
                        new TrajectorySequenceCommand(r.drivebase, PathConstants.SCORENEAR_TO_INTAKESTART1)
                )
                .andThen(
                        new TrajectorySequenceCommand(
                                r.drivebase,
                                PathConstants.INTAKESTART1_TO_INTAKEDONE1
                        )
                )
                .andThen(
                        new TrajectorySequenceCommand(r.drivebase, PathConstants.INTAKEDONE1_TO_SCORENEAR)
                )
                .andThen(AutoLaunching3Balls(r))
                .andThen(
                        new TrajectorySequenceCommand(r.drivebase, PathConstants.SCORENEAR_TO_INTAKESTART2)
                )
                .andThen(
                        new TrajectorySequenceCommand(
                                r.drivebase,
                                PathConstants.INTAKESTART2_TO_INTAKEDONE2
                        )
                )
                .andThen(
                        new TrajectorySequenceCommand(r.drivebase, PathConstants.INTAKEDONE2_TO_SCORENEAR)
                )
                .andThen(AutoLaunching3Balls(r))
                .andThen(
                        new TrajectorySequenceCommand(r.drivebase, PathConstants.SCORENEAR_TO_PARKNEAR)
                )
                .andThen(TeleCommands.StopLaunch(r));
    }

    public static Command PickupShootCommand(Robot r) {
        DrivebaseSubsystem db = r.drivebase;
        return new TrajectorySequenceCommand(db, PathConstants.START_TO_LAUNCH)
                // need to create a fast and a slow version of the intake
                .alongWith(TeleCommands.GateUp(r))
                .alongWith(TeleCommands.Intake(r))
                .alongWith(TeleCommands.Launch(r))
                .andThen(new TrajectorySequenceCommand(db, PathConstants.LAUNCH_TO_PICKUP1))
                .andThen(new TrajectorySequenceCommand(db, PathConstants.PICKUP1_TO_PICKUP1END))
                .andThen(new TrajectorySequenceCommand(db, PathConstants.PICKUP1END_TO_LAUNCH))
                .alongWith(AutoLaunching3Balls(r))
                .andThen(new TrajectorySequenceCommand(db, PathConstants.LAUNCH_TO_PICKUP2))
                .andThen(new TrajectorySequenceCommand(db, PathConstants.PICKUP2_TO_PICKUP2END))
                .alongWith(TeleCommands.Intake(r))
                .andThen(new TrajectorySequenceCommand(db, PathConstants.PICKUP2END_TO_LAUNCH))
                .alongWith(TeleCommands.Launch(r))
                .andThen(AutoLaunching3Balls(r))
                .andThen(new TrajectorySequenceCommand(db, PathConstants.LAUNCH_TO_PICKUP3))
                .andThen(new TrajectorySequenceCommand(db, PathConstants.PICKUP3_TO_PICKUP3END))
                .alongWith(TeleCommands.Intake(r))
                .andThen(new TrajectorySequenceCommand(db, PathConstants.PICKUP3END_TO_LAUNCH))
                .alongWith(TeleCommands.Launch(r))
                .andThen(AutoLaunching3Balls(r));
    }



        public static Command BluePickupShootCommand(Robot r) {
        DrivebaseSubsystem db = r.drivebase;
        return new TrajectorySequenceCommand(db, PathConstants.START_TO_LAUNCH)
            // need to create a fast and a slow version of the intake
            .alongWith(TeleCommands.GateUp(r))
            .alongWith(TeleCommands.Intake(r))
            .alongWith(TeleCommands.Launch(r))
            .andThen(new TrajectorySequenceCommand(db, PathConstants.LAUNCH_TO_PICKUP1))
            .andThen(new TrajectorySequenceCommand(db, PathConstants.PICKUP1_TO_PICKUP1END))
            .andThen(new TrajectorySequenceCommand(db, PathConstants.PICKUP1END_TO_LAUNCH))
            .alongWith(Launching3Balls(r))
            .andThen(new TrajectorySequenceCommand(db, PathConstants.LAUNCH_TO_PICKUP2))
            .andThen(new TrajectorySequenceCommand(db, PathConstants.PICKUP2_TO_PICKUP2END))
            .alongWith(TeleCommands.Intake(r))
            .andThen(new TrajectorySequenceCommand(db, PathConstants.PICKUP2END_TO_LAUNCH))
            .alongWith(TeleCommands.Launch(r))
            .andThen(Launching3Balls(r))
            .andThen(new TrajectorySequenceCommand(db, PathConstants.LAUNCH_TO_PICKUP3))
            .andThen(new TrajectorySequenceCommand(db, PathConstants.PICKUP3_TO_PICKUP3END))
            .alongWith(TeleCommands.Intake(r))
            .andThen(new TrajectorySequenceCommand(db, PathConstants.PICKUP3END_TO_LAUNCH))
            .alongWith(TeleCommands.Launch(r))
            .andThen(AutoLaunching3Balls(r));
    }

    public static Command BlueFarPickupShootCommand(Robot r) {
        return new TrajectorySequenceCommand(r.drivebase, PathConstants.FARSTART_TO_LAUNCH)
            // need to create a fast and a slow version of the intake
            .alongWith(TeleCommands.GateUp(r))
            .alongWith(TeleCommands.Intake(r))
            .alongWith(TeleCommands.Launch(r))
            .andThen(new WaitCommand(3))
            .andThen(new TrajectorySequenceCommand(r.drivebase, PathConstants.LAUNCH_TO_PICKUP1))
            .andThen(
                new TrajectorySequenceCommand(r.drivebase, PathConstants.PICKUP1_TO_PICKUP1END)
            )
            .andThen(new TrajectorySequenceCommand(r.drivebase, PathConstants.PICKUP1END_TO_LAUNCH))
            .alongWith(Launching3Balls(r))
            .andThen(new TrajectorySequenceCommand(r.drivebase, PathConstants.LAUNCH_TO_PICKUP2))
            .andThen(
                new TrajectorySequenceCommand(r.drivebase, PathConstants.PICKUP2_TO_PICKUP2END)
            )
            .alongWith(TeleCommands.Intake(r))
            .andThen(new TrajectorySequenceCommand(r.drivebase, PathConstants.PICKUP2END_TO_LAUNCH))
            .alongWith(TeleCommands.Launch(r))
            .andThen(Launching3Balls(r))
            .andThen(new TrajectorySequenceCommand(r.drivebase, PathConstants.LAUNCH_TO_PICKUP3))
            .andThen(
                new TrajectorySequenceCommand(r.drivebase, PathConstants.PICKUP3_TO_PICKUP3END)
            )
            .alongWith(TeleCommands.Intake(r))
            .andThen(new TrajectorySequenceCommand(r.drivebase, PathConstants.PICKUP3END_TO_LAUNCH))
            .alongWith(TeleCommands.Launch(r))
            .andThen(Launching3Balls(r));
    }

    public static Command RedPickupShootCommand(Robot r) {
        return new TrajectorySequenceCommand(r.drivebase, PathConstants.RSTART_TO_LAUNCH)
            // need to create a fast and a slow version of the intake
            .alongWith(TeleCommands.GateUp(r))
            .alongWith(TeleCommands.Intake(r))
            .alongWith(TeleCommands.Launch(r))
            .andThen(new TrajectorySequenceCommand(r.drivebase, PathConstants.RLAUNCH_TO_PICKUP1))
            .andThen(
                new TrajectorySequenceCommand(r.drivebase, PathConstants.RPICKUP1_TO_PICKUP1END)
            )
            .andThen(
                new TrajectorySequenceCommand(r.drivebase, PathConstants.RPICKUP1END_TO_LAUNCH)
            )
            .alongWith(Launching3Balls(r))
            .andThen(new TrajectorySequenceCommand(r.drivebase, PathConstants.RLAUNCH_TO_PICKUP2))
            .andThen(
                new TrajectorySequenceCommand(r.drivebase, PathConstants.RPICKUP2_TO_PICKUP2END)
            )
            .alongWith(TeleCommands.Intake(r))
            .andThen(
                new TrajectorySequenceCommand(r.drivebase, PathConstants.RPICKUP2END_TO_LAUNCH)
            )
            .alongWith(TeleCommands.Launch(r))
            .andThen(Launching3Balls(r))
            .andThen(new TrajectorySequenceCommand(r.drivebase, PathConstants.RLAUNCH_TO_PICKUP3))
            .andThen(
                new TrajectorySequenceCommand(r.drivebase, PathConstants.RPICKUP3_TO_PICKUP3END)
            )
            .alongWith(TeleCommands.Intake(r))
            .andThen(
                new TrajectorySequenceCommand(r.drivebase, PathConstants.RPICKUP3END_TO_LAUNCH)
            )
            .alongWith(TeleCommands.Launch(r))
            .andThen(Launching3Balls(r));
    }

    public static Command RedFarPickupShootCommand(Robot r) {
        return new TrajectorySequenceCommand(r.drivebase, PathConstants.RFARSTART_TO_LAUNCH)
            // need to create a fast and a slow version of the intake
            .alongWith(TeleCommands.GateUp(r))
            .alongWith(TeleCommands.Intake(r))
            .alongWith(TeleCommands.Launch(r))
            .andThen(new WaitCommand(3))
            .andThen(new TrajectorySequenceCommand(r.drivebase, PathConstants.RLAUNCH_TO_PICKUP1))
            .andThen(
                new TrajectorySequenceCommand(r.drivebase, PathConstants.RPICKUP1_TO_PICKUP1END)
            )
            .andThen(
                new TrajectorySequenceCommand(r.drivebase, PathConstants.RPICKUP1END_TO_LAUNCH)
            )
            .alongWith(Launching3Balls(r))
            .andThen(new TrajectorySequenceCommand(r.drivebase, PathConstants.RLAUNCH_TO_PICKUP2))
            .andThen(
                new TrajectorySequenceCommand(r.drivebase, PathConstants.RPICKUP2_TO_PICKUP2END)
            )
            .alongWith(TeleCommands.Intake(r))
            .andThen(
                new TrajectorySequenceCommand(r.drivebase, PathConstants.RPICKUP2END_TO_LAUNCH)
            )
            .alongWith(TeleCommands.Launch(r))
            .andThen(Launching3Balls(r))
            .andThen(new TrajectorySequenceCommand(r.drivebase, PathConstants.RLAUNCH_TO_PICKUP3))
            .andThen(
                new TrajectorySequenceCommand(r.drivebase, PathConstants.RPICKUP3_TO_PICKUP3END)
            )
            .alongWith(TeleCommands.Intake(r))
            .andThen(
                new TrajectorySequenceCommand(r.drivebase, PathConstants.RPICKUP3END_TO_LAUNCH)
            )
            .alongWith(TeleCommands.Launch(r))
            .andThen(Launching3Balls(r));
    }

    public static Command Launching3Balls(Robot r) {
        return new SequentialCommandGroup(
            new ParallelCommandGroup(
                TeleCommands.Intake(r),
                TeleCommands.GateUp(r),
                TeleCommands.Launch(r)
            ),
            new WaitCommand(4),
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
            TeleCommands.GateUp(r),
            TeleCommands.StopLaunch(r),
            TeleCommands.IntakeStop(r)
        );
    }

    public static Command AutoLaunching3Balls(Robot r) {
        return new SequentialCommandGroup(
            TeleCommands.Intake(r),
            TeleCommands.GateUp(r),
            TeleCommands.Launch(r),
            // no need to wait for spinup as we will leave the flywheel spinning constantly during auto
            new WaitCommand(0.2),
            TeleCommands.GateDown(r),
            new WaitCommand(0.5),
            TeleCommands.GateUp(r),
            new WaitCommand(1),
            TeleCommands.GateDown(r),
            new WaitCommand(0.5),
            TeleCommands.GateUp(r),
            new WaitCommand(0.5),
            TeleCommands.GateDown(r),
            new WaitCommand(0.5),
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

    public void PedroPathTestCommand(Robot r) {
        pathChain = follower
            .pathBuilder()
            .addPath(new BezierLine(AutoConstants.scorePose, AutoConstants.pickup1Pose))
            .setLinearHeadingInterpolation(
                AutoConstants.scorePose.getHeading(),
                AutoConstants.pickup1Pose.getHeading()
            )
            .addPath(new BezierLine(AutoConstants.pickup1Pose, AutoConstants.scorePose))
            .setLinearHeadingInterpolation(
                AutoConstants.pickup1Pose.getHeading(),
                AutoConstants.scorePose.getHeading()
            )
            .build();
        follower.followPath(pathChain);
    }

    public static Command JustShootCommand(Robot r) {
        return new TrajectorySequenceCommand(r.drivebase, PathConstants.BLUE_SCORING)
            .alongWith(TeleCommands.Launch(r))
            .andThen(new WaitCommand(3));
        //.andThen(TeleCommands.)
        // return new SequentialCommandGroup(TeleCommands.Launch(new LauncherSubsystem(Hardware h)));
    }

    public Pose Start = new Pose(21.613, 121.866);
    public Pose Score = new Pose(57.743, 86.258);
    public Pose Pickup1 = new Pose(20.355, 73.205);
    public Pose Pickup2 = new Pose(23.010, 45.108);
    public Pose Pickup3 = new Pose(22.567, 16.127);
    public PathChain Start_To_Score;
    public PathChain Score_To_Pickup1;
    public PathChain Pickup1_To_Score;
    public PathChain Score_To_Pickup2;
    public PathChain Pickup2_To_Score;
    public PathChain Score_To_Pickup3;
    public PathChain Pickup3_To_Score;

    public Paths(Follower follower) {
        Start_To_Score = follower
            .pathBuilder()
            .addPath(new BezierLine(Start, Score))
            .setTangentHeadingInterpolation()
            .setReversed()
            .build();

        Score_To_Pickup1 = follower
            .pathBuilder()
            .addPath(new BezierCurve(Score, new Pose(106.194, 69.665), Pickup1))
            .setLinearHeadingInterpolation(Math.toRadians(140), Math.toRadians(180))
            .build();

        Pickup1_To_Score = follower
            .pathBuilder()
            .addPath(new BezierLine(Pickup1, Score))
            .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(140))
            .build();

        Score_To_Pickup2 = follower
            .pathBuilder()
            .addPath(new BezierCurve(Score, new Pose(135.839, 36.480), Pickup2))
            .setLinearHeadingInterpolation(Math.toRadians(140), Math.toRadians(180))
            .build();

        Pickup2_To_Score = follower
            .pathBuilder()
            .addPath(new BezierLine(Pickup2, Score))
            .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(140))
            .build();

        Score_To_Pickup3 = follower
            .pathBuilder()
            .addPath(new BezierCurve(Score, new Pose(139.379, 8.383), Pickup3))
            .setLinearHeadingInterpolation(Math.toRadians(140), Math.toRadians(180))
            .build();

        Pickup3_To_Score = follower
            .pathBuilder()
            .addPath(new BezierLine(Pickup3, Score))
            .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(140))
            .build();
    }
}

//    public static Command Pedropathcommand(Robot r){
//        return new PPPathCommand()
//    }
