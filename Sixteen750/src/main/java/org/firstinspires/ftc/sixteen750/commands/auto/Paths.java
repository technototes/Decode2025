package org.firstinspires.ftc.sixteen750.commands.auto;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.technototes.library.command.Command;
import com.technototes.library.command.WaitCommand;
import com.technototes.path.command.TrajectorySequenceCommand;
import org.firstinspires.ftc.sixteen750.AutoConstants;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;

public class Paths {
    public static Follower follower;
    public static PathChain pathChain;
    public static Command splineTestCommand(Robot r) {
        return new TrajectorySequenceCommand(r.drivebase, AutoConstants.SPLINETEST1_TO_SPLINETEST2);
    }
    public void PedroPathTestCommand(Robot r){
        pathChain = follower.pathBuilder()
                .addPath(new BezierLine(AutoConstants.scorePose, AutoConstants.pickup1Pose))
                .setLinearHeadingInterpolation(AutoConstants.scorePose.getHeading(), AutoConstants.pickup1Pose.getHeading())
                .addPath(new BezierLine(AutoConstants.pickup1Pose, AutoConstants.scorePose))
                .setLinearHeadingInterpolation(AutoConstants.pickup1Pose.getHeading(), AutoConstants.scorePose.getHeading())
                .build();
        follower.followPath(pathChain);
    }
    public static Command JustShootCommand(Robot r) {
    return new TrajectorySequenceCommand(r.drivebase, AutoConstants.BLUE_SCORING)
    .alongWith(TeleCommands.Launch(r))
            .andThen(new WaitCommand(3))
            //.andThen(TeleCommands.)
            ;
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
                    .addPath(
                            new BezierLine(Start, Score)
                    )
                    .setTangentHeadingInterpolation()
                    .setReversed()
                    .build();

            Score_To_Pickup1 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierCurve(
                                    Score,
                                    new Pose(106.194, 69.665),
                                    Pickup1
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(140), Math.toRadians(180))
                    .build();

            Pickup1_To_Score = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(Pickup1, Score)
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(140))
                    .build();

            Score_To_Pickup2 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierCurve(
                                    Score,
                                    new Pose(135.839, 36.480),
                                    Pickup2
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(140), Math.toRadians(180))
                    .build();

            Pickup2_To_Score = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(Pickup2, Score)
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(140))
                    .build();

            Score_To_Pickup3 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierCurve(
                                    Score,
                                    new Pose(139.379, 8.383),
                                    Pickup3
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(140), Math.toRadians(180))
                    .build();

            Pickup3_To_Score = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(Pickup3, Score)
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(140))
                    .build();
        }
    }


//    public static Command Pedropathcommand(Robot r){
//        return new PPPathCommand()
//    }

