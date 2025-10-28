package org.firstinspires.ftc.sixteen750;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.technototes.path.geometry.ConfigurablePoseD;
import com.technototes.path.trajectorysequence.TrajectorySequence;
import com.technototes.path.trajectorysequence.TrajectorySequenceBuilder;
import java.util.function.Function;

public class PathConstants {

    public static ConfigurablePoseD SPLINETEST1 = new ConfigurablePoseD(0, -55, 0);
    public static ConfigurablePoseD SPLINETEST2 = new ConfigurablePoseD(55, 0, 0);
    //blue side pickup and scoring
    public static ConfigurablePoseD START_FAR_LAUNCHZONE = new ConfigurablePoseD(61, -10, 180);
    public static ConfigurablePoseD START_LAUNCHZONE = new ConfigurablePoseD(-63, -34, -90);
    public static ConfigurablePoseD LAUNCHING = new ConfigurablePoseD(-25, -24,-135);
    public static ConfigurablePoseD PICKUP1_START = new ConfigurablePoseD(-11, -25, -90);
    public static ConfigurablePoseD PICKUP1_END = new ConfigurablePoseD(-11, -50, -90);
    public static ConfigurablePoseD PICKUP2_START = new ConfigurablePoseD(11, -24, -90);
    public static ConfigurablePoseD PICKUP2_END = new ConfigurablePoseD(11, -50, -90);
    public static ConfigurablePoseD PICKUP3_START = new ConfigurablePoseD(35, -24, -90);
    public static ConfigurablePoseD PICKUP3_END = new ConfigurablePoseD(35, -50, -90);
    //red side pickup and scoring
    public static ConfigurablePoseD RSTART_FAR_LAUNCHZONE = new ConfigurablePoseD(61, 10, 180);
    public static ConfigurablePoseD RSTART_LAUNCHZONE = new ConfigurablePoseD(-63, 34, 90);
    public static ConfigurablePoseD RLAUNCHING = new ConfigurablePoseD(-25, 24,135);
    public static ConfigurablePoseD RPICKUP1_START = new ConfigurablePoseD(-11, 25, 90);
    public static ConfigurablePoseD RPICKUP1_END = new ConfigurablePoseD(-11, 50, 90);
    public static ConfigurablePoseD RPICKUP2_START = new ConfigurablePoseD(11, 24, 90);
    public static ConfigurablePoseD RPICKUP2_END = new ConfigurablePoseD(11, 50, 90);
    public static ConfigurablePoseD RPICKUP3_START = new ConfigurablePoseD(35, 24, 90);
    public static ConfigurablePoseD RPICKUP3_END = new ConfigurablePoseD(35, 50, 90);

    public static ConfigurablePoseD TELESTART = new ConfigurablePoseD(0, 0, 90);
    public static ConfigurablePoseD FORWARD = new ConfigurablePoseD(48, 0, 0);
    public static ConfigurablePoseD BACKWARD = new ConfigurablePoseD(0, 0, 0);
    public static ConfigurablePoseD SIDE_RIGHT = new ConfigurablePoseD(0, -48, 0);
    public static ConfigurablePoseD SIDE_LEFT = new ConfigurablePoseD(0, 0, 0);
    public static ConfigurablePoseD BLUE_LAUNCH_ZONE = new ConfigurablePoseD(0, 0, 0);

    // These are 'trajectory pieces' which should be named like this:
    // {STARTING_POSITION}_TO_{ENDING_POSITION}

    //blue side scoring trajectories
    public static final Function<
            Function<Pose2d, TrajectorySequenceBuilder>,
            TrajectorySequence
            > FARSTART_TO_LAUNCH = b ->
            b.apply(START_FAR_LAUNCHZONE.toPose()).lineToLinearHeading(LAUNCHING.toPose()).build();
    public static final Function<
        Function<Pose2d, TrajectorySequenceBuilder>,
        TrajectorySequence
    > START_TO_LAUNCH = b ->
        b.apply(START_LAUNCHZONE.toPose()).lineToLinearHeading(LAUNCHING.toPose()).build();
    public static final Function<
        Function<Pose2d, TrajectorySequenceBuilder>,
        TrajectorySequence
    > LAUNCH_TO_PICKUP1 = b ->
        b.apply(LAUNCHING.toPose()).lineToLinearHeading(PICKUP1_START.toPose()).build();
    public static final Function<
        Function<Pose2d, TrajectorySequenceBuilder>,
        TrajectorySequence
    > PICKUP1_TO_PICKUP1END = b ->
        b.apply(PICKUP1_START.toPose()).lineToLinearHeading(PICKUP1_END.toPose()).build();
    public static final Function<
        Function<Pose2d, TrajectorySequenceBuilder>,
        TrajectorySequence
    > PICKUP1END_TO_LAUNCH = b ->
        b.apply(PICKUP1_END.toPose()).lineToLinearHeading(LAUNCHING.toPose()).build();
    public static final Function<
        Function<Pose2d, TrajectorySequenceBuilder>,
        TrajectorySequence
    > LAUNCH_TO_PICKUP2 = b ->
        b.apply(LAUNCHING.toPose()).lineToLinearHeading(PICKUP2_START.toPose()).build();
    public static final Function<
        Function<Pose2d, TrajectorySequenceBuilder>,
        TrajectorySequence
    > PICKUP2_TO_PICKUP2END = b ->
        b.apply(PICKUP2_START.toPose()).lineToLinearHeading(PICKUP2_END.toPose()).build();
    public static final Function<
        Function<Pose2d, TrajectorySequenceBuilder>,
        TrajectorySequence
    > PICKUP2END_TO_LAUNCH = b ->
        b.apply(PICKUP2_END.toPose()).lineToLinearHeading(LAUNCHING.toPose()).build();
    public static final Function<
        Function<Pose2d, TrajectorySequenceBuilder>,
        TrajectorySequence
    > LAUNCH_TO_PICKUP3 = b ->
        b.apply(LAUNCHING.toPose()).lineToLinearHeading(PICKUP3_START.toPose()).build();
    public static final Function<
        Function<Pose2d, TrajectorySequenceBuilder>,
        TrajectorySequence
    > PICKUP3_TO_PICKUP3END = b ->
        b.apply(PICKUP3_START.toPose()).lineToLinearHeading(PICKUP3_END.toPose()).build();
    public static final Function<
        Function<Pose2d, TrajectorySequenceBuilder>,
        TrajectorySequence
    > PICKUP3END_TO_LAUNCH = b ->
        b.apply(PICKUP3_END.toPose()).lineToLinearHeading(LAUNCHING.toPose()).build();

    //red side scoring trajectories
    public static final Function<
            Function<Pose2d, TrajectorySequenceBuilder>,
            TrajectorySequence
            > RFARSTART_TO_LAUNCH = b ->
            b.apply(RSTART_FAR_LAUNCHZONE.toPose()).lineToLinearHeading(RLAUNCHING.toPose()).build();
    public static final Function<
            Function<Pose2d, TrajectorySequenceBuilder>,
            TrajectorySequence
            > RSTART_TO_LAUNCH = b ->
            b.apply(RSTART_LAUNCHZONE.toPose()).lineToLinearHeading(RLAUNCHING.toPose()).build();
    public static final Function<
            Function<Pose2d, TrajectorySequenceBuilder>,
            TrajectorySequence
            > RLAUNCH_TO_PICKUP1 = b ->
            b.apply(RLAUNCHING.toPose()).lineToLinearHeading(RPICKUP1_START.toPose()).build();
    public static final Function<
            Function<Pose2d, TrajectorySequenceBuilder>,
            TrajectorySequence
            > RPICKUP1_TO_PICKUP1END = b ->
            b.apply(RPICKUP1_START.toPose()).lineToLinearHeading(RPICKUP1_END.toPose()).build();
    public static final Function<
            Function<Pose2d, TrajectorySequenceBuilder>,
            TrajectorySequence
            > RPICKUP1END_TO_LAUNCH = b ->
            b.apply(RPICKUP1_END.toPose()).lineToLinearHeading(RLAUNCHING.toPose()).build();
    public static final Function<
            Function<Pose2d, TrajectorySequenceBuilder>,
            TrajectorySequence
            > RLAUNCH_TO_PICKUP2 = b ->
            b.apply(RLAUNCHING.toPose()).lineToLinearHeading(RPICKUP2_START.toPose()).build();
    public static final Function<
            Function<Pose2d, TrajectorySequenceBuilder>,
            TrajectorySequence
            > RPICKUP2_TO_PICKUP2END = b ->
            b.apply(RPICKUP2_START.toPose()).lineToLinearHeading(RPICKUP2_END.toPose()).build();
    public static final Function<
            Function<Pose2d, TrajectorySequenceBuilder>,
            TrajectorySequence
            > RPICKUP2END_TO_LAUNCH = b ->
            b.apply(RPICKUP2_END.toPose()).lineToLinearHeading(RLAUNCHING.toPose()).build();
    public static final Function<
            Function<Pose2d, TrajectorySequenceBuilder>,
            TrajectorySequence
            > RLAUNCH_TO_PICKUP3 = b ->
            b.apply(RLAUNCHING.toPose()).lineToLinearHeading(RPICKUP3_START.toPose()).build();
    public static final Function<
            Function<Pose2d, TrajectorySequenceBuilder>,
            TrajectorySequence
            > RPICKUP3_TO_PICKUP3END = b ->
            b.apply(RPICKUP3_START.toPose()).lineToLinearHeading(RPICKUP3_END.toPose()).build();
    public static final Function<
            Function<Pose2d, TrajectorySequenceBuilder>,
            TrajectorySequence
            > RPICKUP3END_TO_LAUNCH = b ->
            b.apply(RPICKUP3_END.toPose()).lineToLinearHeading(RLAUNCHING.toPose()).build();
    public static final Function<
        Function<Pose2d, TrajectorySequenceBuilder>,
        TrajectorySequence
    > SPLINETEST1_TO_SPLINETEST2 = b ->
        b
            .apply(SPLINETEST1.toPose())
            .splineToConstantHeading(SPLINETEST2.toPose().vec(), SPLINETEST2.getHeading())
            .build();

    //testing trajectories from last year

    // testing trajectories
    public static final Function<
        Function<Pose2d, TrajectorySequenceBuilder>,
        TrajectorySequence
    > BACKWARD_TO_FORWARD = b ->
        b.apply(BACKWARD.toPose()).lineToLinearHeading(FORWARD.toPose()).build();
    public static final Function<
        Function<Pose2d, TrajectorySequenceBuilder>,
        TrajectorySequence
    > FORWARD_TO_BACKWARD = b ->
        b.apply(FORWARD.toPose()).lineToLinearHeading(BACKWARD.toPose()).build();
    public static final Function<
        Function<Pose2d, TrajectorySequenceBuilder>,
        TrajectorySequence
    > SIDE_LEFT_TO_SIDE_RIGHT = b ->
        b.apply(SIDE_LEFT.toPose()).lineToLinearHeading(SIDE_RIGHT.toPose()).build();
    public static final Function<
        Function<Pose2d, TrajectorySequenceBuilder>,
        TrajectorySequence
    > SIDE_RIGHT_TO_SIDE_LEFT = b ->
        b.apply(SIDE_RIGHT.toPose()).lineToLinearHeading(SIDE_LEFT.toPose()).build();
    public static final Function<
        Function<Pose2d, TrajectorySequenceBuilder>,
        TrajectorySequence
    > BLUE_SCORING = b ->
        b.apply(BLUE_LAUNCH_ZONE.toPose()).lineToLinearHeading(BLUE_LAUNCH_ZONE.toPose()).build();
}
