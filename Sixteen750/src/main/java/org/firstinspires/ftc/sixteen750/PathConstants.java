package org.firstinspires.ftc.sixteen750;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.technototes.path.geometry.ConfigurablePoseD;
import com.technototes.path.trajectorysequence.TrajectorySequence;
import com.technototes.path.trajectorysequence.TrajectorySequenceBuilder;
import java.util.function.Function;

public class PathConstants {

    public static ConfigurablePoseD SPLINETEST1 = new ConfigurablePoseD(0, -55, 0);
    public static ConfigurablePoseD SPLINETEST2 = new ConfigurablePoseD(55, 0, 0);
    public static ConfigurablePoseD START_LAUNCHZONE = new ConfigurablePoseD(21.613, 121.866, 140);
    public static ConfigurablePoseD LAUNCHING = new ConfigurablePoseD(57.743, 86.258, 140);
    public static ConfigurablePoseD PICKUP1_START = new ConfigurablePoseD(57.964, 72.541, 180);
    public static ConfigurablePoseD PICKUP1_END = new ConfigurablePoseD(22.346, 72.763, 180);
    public static ConfigurablePoseD PICKUP2_START = new ConfigurablePoseD(56.637, 44.887, 180);
    public static ConfigurablePoseD PICKUP2_END = new ConfigurablePoseD(19.912, 44.223, 180);
    public static ConfigurablePoseD PICKUP3_START = new ConfigurablePoseD(56.195, 15.463, 180);
    public static ConfigurablePoseD PICKUP3_END = new ConfigurablePoseD(18.585, 15.020, 180);

    public static ConfigurablePoseD TELESTART = new ConfigurablePoseD(0, 0, 90);
    public static ConfigurablePoseD FORWARD = new ConfigurablePoseD(48, 0, 0);
    public static ConfigurablePoseD BACKWARD = new ConfigurablePoseD(0, 0, 0);
    public static ConfigurablePoseD SIDE_RIGHT = new ConfigurablePoseD(0, -48, 0);
    public static ConfigurablePoseD SIDE_LEFT = new ConfigurablePoseD(0, 0, 0);
    public static ConfigurablePoseD BLUE_LAUNCH_ZONE = new ConfigurablePoseD(0, 0, 0);

    // These are 'trajectory pieces' which should be named like this:
    // {STARTING_POSITION}_TO_{ENDING_POSITION}

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
