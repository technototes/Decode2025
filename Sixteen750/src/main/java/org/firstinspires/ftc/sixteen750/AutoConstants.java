package org.firstinspires.ftc.sixteen750;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.technototes.path.geometry.ConfigurablePoseD;
import com.technototes.path.trajectorysequence.TrajectorySequence;
import com.technototes.path.trajectorysequence.TrajectorySequenceBuilder;

import java.util.function.Function;

@Config
public class AutoConstants {


    //New testing constants for this year's game
    public static ConfigurablePoseD START = new ConfigurablePoseD(35, 63, 0);
    public static ConfigurablePoseD NETSCORING_TEST = new ConfigurablePoseD(55, 55, 45);
    public static ConfigurablePoseD NETSCORING_CLEAR = new ConfigurablePoseD(45, 35, -45);
    public static ConfigurablePoseD SPLINETEST1 = new ConfigurablePoseD(0, -55, 0);
    public static ConfigurablePoseD SPLINETEST2 = new ConfigurablePoseD(55, 0, 0);

    public static ConfigurablePoseD OBS_START = new ConfigurablePoseD(22, -65, 90);
    public static ConfigurablePoseD OBS_PARK = new ConfigurablePoseD(62, -65, 90);
    public static ConfigurablePoseD OBS_PUSH1 = new ConfigurablePoseD(62, -6, 90);
    public static ConfigurablePoseD OBSERVATION_START = new ConfigurablePoseD(0, 60, -90);
    public static ConfigurablePoseD SUBMARINE = new ConfigurablePoseD(-5, 36, (90));
    public static ConfigurablePoseD OBSERVATION_ZONE = new ConfigurablePoseD(-46, 55, (90));
    public static ConfigurablePoseD SUBMARINE2 = new ConfigurablePoseD(0, 36, (90));
    public static ConfigurablePoseD SUBMARINE3 = new ConfigurablePoseD(5, 36, (-90));
    public static ConfigurablePoseD MINI_LINE = new ConfigurablePoseD(-3, 36, (90));

    public static ConfigurablePoseD PUSH_HALF = new ConfigurablePoseD(-16, 36, (90));

    public static ConfigurablePoseD PUSH_1 = new ConfigurablePoseD(-34, 45, (90));
    public static ConfigurablePoseD PUSH_2 = new ConfigurablePoseD(-34, 10, (90));
    public static ConfigurablePoseD PUSH_3_AND_A_HALF = new ConfigurablePoseD(-46, 11, (90));
    public static ConfigurablePoseD PUSH_3 = new ConfigurablePoseD(-35, 10, (90));
    public static ConfigurablePoseD PUSH_4 = new ConfigurablePoseD(-56, 10, (90));
    public static ConfigurablePoseD OBSERVATION_ZONE_2 = new ConfigurablePoseD(-56, 55, (90));
    public static ConfigurablePoseD PUSH_5 = new ConfigurablePoseD(-61, 10, (90));
    public static ConfigurablePoseD OBSERVATION_ZONE_3 = new ConfigurablePoseD(-61, 55, (90));

    public static ConfigurablePoseD SAMPLE_1 = new ConfigurablePoseD(-48, 26, (90));
    public static ConfigurablePoseD OBSERVATION_PUSH_HALF = new ConfigurablePoseD(-47, 40, (90));


    public static ConfigurablePoseD NETSCORING = new ConfigurablePoseD(62, 51, -90); // 55, 57, -135
    public static ConfigurablePoseD NET_START = new ConfigurablePoseD(35, 63, 180);
    public static ConfigurablePoseD NET_AGAINST_THE_WALL = new ConfigurablePoseD(35, 63, -90);
    public static ConfigurablePoseD NETCLEAR = new ConfigurablePoseD(52, 52, 45);
    public static ConfigurablePoseD INTAKE1 = new ConfigurablePoseD(47, 49, -90);// 59, 50, -115
    public static ConfigurablePoseD INTAKE2 = new ConfigurablePoseD(57, 49, -90);
    public static ConfigurablePoseD INTAKE3 = new ConfigurablePoseD(48, 25, 0); // 48.9, 39, -37 (doesnt quite get it)
    public static ConfigurablePoseD ASCENT = new ConfigurablePoseD(23, 12, 0);
    public static ConfigurablePoseD ASCENT_CLEAR = new ConfigurablePoseD(35, 10, 0);

    //These are testing constants for last year's game
    public static ConfigurablePoseD START1 = new ConfigurablePoseD(35, 60, -90);
    public static ConfigurablePoseD START_STAGE = new ConfigurablePoseD(35, 58, 0);
    public static ConfigurablePoseD RIGHT_SPIKE = new ConfigurablePoseD(31, 32, -180); // near the metal,  fine tuned
    public static ConfigurablePoseD MID_SPLINE_CLEAR = new ConfigurablePoseD(35, 34, -180); // fine tuned OKAYY
    public static ConfigurablePoseD HEAD_TO_STAGE = new ConfigurablePoseD(0, 58, 0);
    public static ConfigurablePoseD PLACE_RIGHT = new ConfigurablePoseD(-46, 40, 180);// not fine tuned
    //testing constants
    public static ConfigurablePoseD TELESTART = new ConfigurablePoseD(0, 0, 90);
    public static ConfigurablePoseD FORWARD = new ConfigurablePoseD(48, 0, 0);
    public static ConfigurablePoseD BACKWARD = new ConfigurablePoseD(0, 0, 0);
    public static ConfigurablePoseD SIDE_RIGHT = new ConfigurablePoseD(0, -48, 0);
    public static ConfigurablePoseD SIDE_LEFT = new ConfigurablePoseD(0, 0, 0);

    // These are 'trajectory pieces' which should be named like this:
    // {STARTING_POSITION}_TO_{ENDING_POSITION}
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> PUSH_BOT_OBSERVATION_SIDE_AUTO1 = func ->
            func
                    .apply(OBSERVATION_START.toPose())
                    .lineToLinearHeading(PUSH_1.toPose())
                    .build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> PUSH_BOT_OBSERVATION_SIDE_AUTO2 = func ->
            func
                    .apply(PUSH_1.toPose())
                    .lineToLinearHeading(PUSH_2.toPose())
                    .build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> PUSH_BOT_OBSERVATION_SIDE_AUTO4 = func ->
            func
                    .apply(PUSH_2.toPose())
                    .lineToLinearHeading(PUSH_3_AND_A_HALF.toPose())
                    .build();

    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> PUSH_BOT_OBSERVATION_SIDE_AUTO4HALF = func ->
            func
                    .apply(PUSH_3_AND_A_HALF.toPose())
                    .lineToLinearHeading(OBSERVATION_ZONE.toPose())
                    .build();


    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> PUSH_BOT_OBSERVATION_SIDE_AUTO5 = func ->
            func
                    .apply(OBSERVATION_ZONE.toPose())
                    .lineToLinearHeading(PUSH_3_AND_A_HALF.toPose())
                    .build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> PUSH_BOT_OBSERVATION_SIDE_AUTO6 = func ->
            func
                    .apply(PUSH_3_AND_A_HALF.toPose())
                    .lineToLinearHeading(PUSH_4.toPose())
                    .build();

    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> PUSH_BOT_OBSERVATION_SIDE_AUTO7 = func ->
            func
                    .apply(PUSH_4.toPose())
                    .lineToLinearHeading(OBSERVATION_ZONE_2.toPose())
                    .build();

    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> PUSH_BOT_OBSERVATION_SIDE_AUTO8 = func ->
            func
                    .apply(OBSERVATION_ZONE_2.toPose())
                    .lineToLinearHeading(PUSH_4.toPose())
                    .build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> PUSH_BOT_OBSERVATION_SIDE_AUTO9 = func ->
            func
                    .apply(PUSH_4.toPose())
                    .lineToLinearHeading(PUSH_5.toPose())
                    .build();

    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> PUSH_BOT_OBSERVATION_SIDE_AUTO10 = func ->
            func
                    .apply(PUSH_5.toPose())
                    .lineToLinearHeading(OBSERVATION_ZONE_3.toPose())
                    .build();

    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence>
            OBS_START_TO_OBS_PARK = b ->
            b.apply(OBS_START.toPose()).lineToLinearHeading(OBS_PARK.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence>
            START_TO_NETSCORING = b ->
            b.apply(NET_START.toPose()).lineToLinearHeading(NETSCORING.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence>
            NETSCORING_TO_INTAKE1 = b ->
            b.apply(NETSCORING.toPose()).lineToLinearHeading(INTAKE1.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence>
            INTAKE1_TO_NETSCORING = b ->
            b.apply(INTAKE1.toPose()).lineToLinearHeading(NETSCORING.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence>
            NETSCORING_TO_INTAKE2 = b ->
            b.apply(NETSCORING.toPose()).lineToLinearHeading(INTAKE2.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence>
            INTAKE2_TO_NETSCORING = b ->
            b.apply(INTAKE2.toPose()).lineToLinearHeading(NETSCORING.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence>
            NETSCORING_TO_INTAKE3 = b ->
            b.apply(NETSCORING.toPose()).lineToLinearHeading(INTAKE3.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence>
            INTAKE3_TO_NETSCORING = b ->
            b.apply(INTAKE3.toPose()).lineToLinearHeading(NETSCORING.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence>
            NETSCORING_TO_ASCENT_CLEAR = b ->
            b.apply(NETSCORING.toPose()).lineToLinearHeading(ASCENT_CLEAR.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence>
            NETSCORING_TO_AGAINST_THE_WALL = b ->
            b.apply(NETSCORING.toPose()).lineToLinearHeading(NET_AGAINST_THE_WALL.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence>
            ASCENT_CLEAR_TO_ASCENT = b ->
            b.apply(ASCENT_CLEAR.toPose()).lineToLinearHeading(ASCENT.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence>
            NET_START_TO_ASCENT_CLEAR = b ->
            b.apply(NET_START.toPose()).lineToLinearHeading(ASCENT_CLEAR.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence>
            NETSCORING_TO_ASCENT = b ->
            b.apply(NETSCORING_TEST.toPose())
//                        .splineToLinearHeading(NETCLEAR.toPose(), NETCLEAR.getHeading())
                    // .splineToSplineHeading(NETSCORING_CLEAR.toPose(), NETSCORING_CLEAR.getHeading())
                    .splineToLinearHeading(ASCENT.toPose(), ASCENT.getHeading())
                    //.setReversed(true)
                    .build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence>
            NETSCORING_TO_NETSCORING_CLEAR = b ->
            b.apply(NETSCORING_TEST.toPose())
                    .splineToLinearHeading(NETSCORING_CLEAR.toPose(), NETSCORING_CLEAR.getHeading())
                    .build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence>
            NETSCORING_CLEAR_TO_ASCENT = b ->
            b.apply(NETSCORING_CLEAR.toPose())
                    .splineToLinearHeading(ASCENT.toPose(), ASCENT.getHeading())
                    .build();

    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence>
            SPLINETEST1_TO_SPLINETEST2 = b ->
            b.apply(SPLINETEST1.toPose())
                    .splineToConstantHeading(SPLINETEST2.toPose().vec(), SPLINETEST2.getHeading())
                    .build();


    //testing trajectories from last year
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence>
            START_TO_RIGHT_SPIKE = b ->
            b.apply(START1.toPose())
                    .splineTo(MID_SPLINE_CLEAR.toPose().vec(), MID_SPLINE_CLEAR.getHeading())
                    .splineToLinearHeading(RIGHT_SPIKE.toPose(), RIGHT_SPIKE.getHeading())
                    .build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence>
            RIGHT_SPIKE_TO_STAGE = b ->
            b.apply(RIGHT_SPIKE.toPose())
                    .splineToLinearHeading(MID_SPLINE_CLEAR.toPose(), Math.PI - MID_SPLINE_CLEAR.getHeading())
                    .splineToConstantHeading(START_STAGE.toPose().vec(), Math.PI - START_STAGE.getHeading())
                    .splineToConstantHeading(HEAD_TO_STAGE.toPose().vec(), Math.PI - START_STAGE.getHeading())
                    .splineToConstantHeading(PLACE_RIGHT.toPose().vec(), Math.PI - START_STAGE.getHeading())
                    .build();

    // testing trajectories
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence>
            BACKWARD_TO_FORWARD = b ->
            b.apply(BACKWARD.toPose()).lineToLinearHeading(FORWARD.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence>
            FORWARD_TO_BACKWARD = b ->
            b.apply(FORWARD.toPose()).lineToLinearHeading(BACKWARD.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence>
            SIDE_LEFT_TO_SIDE_RIGHT = b ->
            b.apply(SIDE_LEFT.toPose()).lineToLinearHeading(SIDE_RIGHT.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence>
            SIDE_RIGHT_TO_SIDE_LEFT = b ->
            b.apply(SIDE_RIGHT.toPose()).lineToLinearHeading(SIDE_LEFT.toPose()).build();
}
 
 
