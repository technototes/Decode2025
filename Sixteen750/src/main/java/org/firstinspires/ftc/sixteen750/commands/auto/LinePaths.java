package org.firstinspires.ftc.sixteen750.commands.auto;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class LinePaths {

    public PathChain Start_to_Launch;
    public PathChain Launch_to_Intake1;
    public PathChain Intake1_to_Intake1end;
    public PathChain Intake1end_to_Launch;
    public PathChain Launch_to_Intake2;
    public PathChain Intake2_to_Intake2end;
    public PathChain Intake2end_to_Launch;
    public PathChain Launch_to_Intake3;
    public PathChain Intake3_to_Intake3end;
    public PathChain Intake3end_to_Launch;
    public PathChain Launch_to_Move;

    public PathChain RStart_to_Launch;
    public PathChain RLaunch_to_Intake1;
    public PathChain RIntake1_to_Intake1end;
    public PathChain RIntake1end_to_Launch;
    public PathChain RLaunch_to_Intake2;
    public PathChain RIntake2_to_Intake2end;
    public PathChain RIntake2end_to_Launch;
    public PathChain RLaunch_to_Intake3;
    public PathChain RIntake3_to_Intake3end;
    public PathChain RIntake3end_to_Launch;
    public PathChain RLaunch_to_Move;

    public Pose getStart() {
        return new Pose(32.671, 135.916, Math.toRadians(90));
    }
    public Pose getRStart() {
        return new Pose(113, 135.916, Math.toRadians(90));
    }
    //blue poses
    public Pose start = new Pose(32.671, 135.916);
    public Pose launch = new Pose(39.339, 102.782);
    public Pose intake1 = new Pose(57.768, 84.233);
    public Pose intake1end = new Pose(14, 84.233);
    public Pose intake2 = new Pose(42, 60);
    public Pose intake2end = new Pose(8, 60);
    public Pose intake3 = new Pose(41, 35);
    public Pose intake3end = new Pose(8, 35);
    public Pose move = new Pose(29.192, 49.617);

    //red poses
    public Pose Rstart = new Pose(113, 135.916);
    public Pose Rlaunch = new Pose(105, 102.782);
    public Pose Rintake1 = new Pose(87, 84.233);
    public Pose Rintake1end = new Pose(125, 84.233);
    public Pose Rintake2 = new Pose(100, 60);
    public Pose Rintake2end = new Pose(133, 60);
    public Pose Rintake3 = new Pose(101, 35);
    public Pose Rintake3end = new Pose(135, 36);
    public Pose Rmove = new Pose(115, 49.617);

    public LinePaths(Follower follower) {
        Start_to_Launch = follower
                .pathBuilder()
                .addPath(new BezierLine(start, launch))
                .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(125))
                .build();

        Launch_to_Intake1 = follower
                .pathBuilder()
                .addPath(new BezierLine(launch, intake1))
                .setLinearHeadingInterpolation(Math.toRadians(125), Math.toRadians(180))
                .build();

        Intake1_to_Intake1end = follower
                .pathBuilder()
                .addPath(new BezierLine(intake1, intake1end))
                .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                .build();

        Intake1end_to_Launch = follower
                .pathBuilder()
                .addPath(new BezierLine(intake1end, launch))
                .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(125))
                .build();
        Launch_to_Intake2 = follower
                .pathBuilder()
                .addPath(new BezierLine(launch, intake2))
                .setLinearHeadingInterpolation(Math.toRadians(125), Math.toRadians(180))
                .build();
        Intake2_to_Intake2end = follower
                .pathBuilder()
                .addPath(new BezierLine(intake2, intake2end))
                .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                .build();
        Intake2end_to_Launch = follower
                .pathBuilder()
                .addPath(new BezierLine(intake2end, launch))
                .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(125))
                .build();
        Launch_to_Intake3 = follower
                .pathBuilder()
                .addPath(new BezierLine(launch, intake3))
                .setLinearHeadingInterpolation(Math.toRadians(125), Math.toRadians(180))
                .build();
        Intake3_to_Intake3end = follower
                .pathBuilder()
                .addPath(new BezierLine(intake3, intake3end))
                .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                .build();
        Intake3end_to_Launch = follower
                .pathBuilder()
                .addPath(new BezierLine(intake3end, launch))
                .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(125))
                .build();
        Launch_to_Move = follower
                .pathBuilder()
                .addPath(new BezierLine(launch, move))
                .setLinearHeadingInterpolation(Math.toRadians(125), Math.toRadians(125))
                .build();
        //red auto paths

        RStart_to_Launch = follower
                .pathBuilder()
                .addPath(new BezierLine(Rstart, Rlaunch))
                .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(45))
                .build();

        RLaunch_to_Intake1 = follower
                .pathBuilder()
                .addPath(new BezierLine(Rlaunch, Rintake1))
                .setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(0))
                .build();

        RIntake1_to_Intake1end = follower
                .pathBuilder()
                .addPath(new BezierLine(Rintake1, Rintake1end))
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                .build();

        RIntake1end_to_Launch = follower
                .pathBuilder()
                .addPath(new BezierLine(Rintake1end, Rlaunch))
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(45))
                .build();

        RLaunch_to_Intake2 = follower
                .pathBuilder()
                .addPath(new BezierLine(Rlaunch, Rintake2))
                .setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(0))
                .build();

        RIntake2_to_Intake2end = follower
                .pathBuilder()
                .addPath(new BezierLine(Rintake2, Rintake2end))
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                .build();

        RIntake2end_to_Launch = follower
                .pathBuilder()
                .addPath(new BezierLine(Rintake2end, Rlaunch))
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(45))
                .build();

        RLaunch_to_Intake3 = follower
                .pathBuilder()
                .addPath(new BezierLine(Rlaunch, Rintake3))
                .setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(0))
                .build();

        RIntake3_to_Intake3end = follower
                .pathBuilder()
                .addPath(new BezierLine(Rintake3, Rintake3end))
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                .build();

        RIntake3end_to_Launch = follower
                .pathBuilder()
                .addPath(new BezierLine(Rintake3, Rlaunch))
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(45))
                .build();
        RLaunch_to_Move = follower
                .pathBuilder()
                .addPath(new BezierLine(Rlaunch, Rmove))
                .setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(45))
                .build();
    }
}
