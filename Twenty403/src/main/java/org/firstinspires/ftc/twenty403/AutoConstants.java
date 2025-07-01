package org.firstinspires.ftc.twenty403;

import static java.lang.Math.toRadians;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.technototes.path.geometry.ConfigurablePoseD;
import com.technototes.path.trajectorysequence.TrajectorySequence;
import com.technototes.path.trajectorysequence.TrajectorySequenceBuilder;


import java.util.function.Function;
import java.util.function.Supplier;

@Config
public class AutoConstants {
    //Park positions
    public static ConfigurablePoseD PARK_START = new ConfigurablePoseD(-40, -52, 90);
    public static ConfigurablePoseD PARK_END = new ConfigurablePoseD(-70, -34, 90);
    // Stuff for testing:
    public static ConfigurablePoseD TEST_START = new ConfigurablePoseD(-50, -50, 0);
    public static ConfigurablePoseD TEST_LEFT = new ConfigurablePoseD(-2, -50, 0);
    public static ConfigurablePoseD TEST_FORWARD = new ConfigurablePoseD(-50, -2, 0);
    public static ConfigurablePoseD TEST_SPLINE_1 = new ConfigurablePoseD(-38, -2, 0);
    public static ConfigurablePoseD TEST_SPLINE_2 = new ConfigurablePoseD(-38, -2, -90);

    //preston positions
    public static ConfigurablePoseD FIRST = new ConfigurablePoseD(0, 61, 90);
    public static ConfigurablePoseD SECOND = new ConfigurablePoseD(-32, 35, 75);
    public static ConfigurablePoseD THIRD = new ConfigurablePoseD(-42, 10, 55);
    public static ConfigurablePoseD FOURTH = new ConfigurablePoseD(-45, 10, 90);
    public static ConfigurablePoseD FIFTH = new ConfigurablePoseD(-45, 55, 110);
    public static ConfigurablePoseD SIXTH = new ConfigurablePoseD(-45, 55.1, 90);
    public static ConfigurablePoseD SEVENTH = new ConfigurablePoseD(-45.1, 55, 90);
    public static ConfigurablePoseD SEVENTH1 = new ConfigurablePoseD(0, 35, -90);
    public static ConfigurablePoseD SEVENTH2 = new ConfigurablePoseD(0, 35.1, 180);
    public static ConfigurablePoseD SEVENTH3 = new ConfigurablePoseD(-32, 35, 180);
    public static ConfigurablePoseD SEVENTH4 = new ConfigurablePoseD(-45, 10, 180);
    public static ConfigurablePoseD EIGHTH = new ConfigurablePoseD(-52, 47, 90);
    public static ConfigurablePoseD EIGHTH1 = new ConfigurablePoseD(0, 35, -90);
    public static ConfigurablePoseD EIGHTH2 = new ConfigurablePoseD(0, 35.1, 180);
    public static ConfigurablePoseD EIGHTH3 = new ConfigurablePoseD(-32, 35, 180);
    public static ConfigurablePoseD EIGHTH4 = new ConfigurablePoseD(-42, 10, 180);
    public static ConfigurablePoseD NINTH = new ConfigurablePoseD(-56, 13, 90);
    public static ConfigurablePoseD TENTH = new ConfigurablePoseD(-56, 48, 90);
    public static ConfigurablePoseD ELEVENTH = new ConfigurablePoseD(-56, 13, 90);
    public static ConfigurablePoseD TWELFTH = new ConfigurablePoseD(-61, 13, 90);
    public static ConfigurablePoseD THIRTEENTH = new ConfigurablePoseD(-61, 48, 90);
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> FIRST_TO_SECOND = func -> func.apply(FIRST.toPose()).lineToLinearHeading(SECOND.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> SECOND_TO_THIRD = func -> func.apply(SECOND.toPose()).lineToLinearHeading(THIRD.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> THIRD_TO_FOURTH = func -> func.apply(THIRD.toPose()).lineToLinearHeading(FOURTH.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> FOURTH_TO_FIFTH = func -> func.apply(FOURTH.toPose()).lineToLinearHeading(FIFTH.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> FIFTH_TO_SIXTH = func -> func.apply(FIFTH.toPose()).lineToLinearHeading(SIXTH.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> SIXTH_TO_SEVENTH = func -> func.apply(SIXTH.toPose()).lineToLinearHeading(SEVENTH.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> SEVENTH_TO_SEVENTH1 = func -> func.apply(SEVENTH.toPose()).lineToLinearHeading(SEVENTH1.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> SEVENTH1_TO_SEVENTH2 = func -> func.apply(SEVENTH1.toPose()).lineToLinearHeading(SEVENTH2.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> SEVENTH2_TO_SEVENTH3 = func -> func.apply(SEVENTH2.toPose()).lineToLinearHeading(SEVENTH3.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> SEVENTH3_TO_SEVENTH4 = func -> func.apply(SEVENTH3.toPose()).lineToLinearHeading(SEVENTH4.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> SEVENTH4_TO_EIGHTH = func -> func.apply(SEVENTH4.toPose()).lineToLinearHeading(EIGHTH.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> EIGHTH_TO_EIGHTH1 = func -> func.apply(EIGHTH.toPose()).lineToLinearHeading(EIGHTH1.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> EIGHTH1_TO_EIGHTH2 = func -> func.apply(EIGHTH1.toPose()).lineToLinearHeading(EIGHTH2.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> EIGHTH2_TO_EIGHTH3 = func -> func.apply(EIGHTH2.toPose()).lineToLinearHeading(EIGHTH3.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> EIGHTH3_TO_EIGHTH4 = func -> func.apply(EIGHTH3.toPose()).lineToLinearHeading(EIGHTH4.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> EIGHTH4_TO_NINTH = func -> func.apply(EIGHTH4.toPose()).lineToLinearHeading(NINTH.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> NINTH_TO_TENTH = func -> func.apply(NINTH.toPose()).lineToLinearHeading(TENTH.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> TENTH_TO_ELEVENTH = func -> func.apply(TENTH.toPose()).lineToLinearHeading(ELEVENTH.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> ELEVENTH_TO_TWELFTH = func -> func.apply(ELEVENTH.toPose()).lineToLinearHeading(TWELFTH.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> TWELFTH_TO_THIRTEENTH = func -> func.apply(TWELFTH.toPose()).lineToLinearHeading(THIRTEENTH.toPose()).build();
    //Park trajectory
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> PARK_START_TO_END = func -> func.apply(PARK_START.toPose()).lineToLinearHeading(PARK_END.toPose()).build();

    public static ConfigurablePoseD OBS_START = new ConfigurablePoseD(22, -65, 90);
    public static ConfigurablePoseD OBS_PARK = new ConfigurablePoseD(62, -6, 90);
    public static ConfigurablePoseD OBS_PUSH1 = new ConfigurablePoseD(62, -6, 90);
    public static ConfigurablePoseD OBSERVATION_START = new ConfigurablePoseD(0, 60, -90);
    public static ConfigurablePoseD SUBMARINE = new ConfigurablePoseD(-5, 36, (90));
    public static ConfigurablePoseD OBSERVATION_ZONE = new ConfigurablePoseD(-46, 55, (90));


    //stolen constants from 16750 heehee
    public static ConfigurablePoseD NETSCORING = new ConfigurablePoseD(54, 50, -135);
    public static ConfigurablePoseD NET_START = new ConfigurablePoseD(35, 63, 180);
    public static ConfigurablePoseD NET_AGAINST_THE_WALL = new ConfigurablePoseD(35, 63, -90);
    public static ConfigurablePoseD NETCLEAR = new ConfigurablePoseD(52, 52, 45);
    public static ConfigurablePoseD INTAKE1 = new ConfigurablePoseD(53, 49, -90);
    public static ConfigurablePoseD INTAKE2 = new ConfigurablePoseD(57, 50, -90);
    public static ConfigurablePoseD INTAKE3 = new ConfigurablePoseD(53, 40, -30);
    public static ConfigurablePoseD ASCENT = new ConfigurablePoseD(23, 12, 0);
    public static ConfigurablePoseD ASCENT_CLEAR = new ConfigurablePoseD(35, 10, 0);
    public static ConfigurablePoseD PUSHY_1 = new ConfigurablePoseD(45, 45, -90);
    public static ConfigurablePoseD PUSHY_2 = new ConfigurablePoseD(45, 2, -90);
    public static ConfigurablePoseD PUSHY_3 = new ConfigurablePoseD(34, 2, -90);
    public static ConfigurablePoseD OBS_STARTY = new ConfigurablePoseD(-20, 50, -90);
    public static ConfigurablePoseD OBS_PARKY = new ConfigurablePoseD(-60, 50, -90);


    //push1 34, 59
    //push1 34, 59d
    //push2 34, 11
    //push3 23, 11
    //end of the crimes

    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence>
            OBS_START_TO_OBS_PARK = b ->
            b.apply(OBS_START.toPose()).lineToLinearHeading(OBS_PARK.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> TEST_RIGHT_TO_LEFT =
            func -> func.apply(TEST_START.toPose())
                    .lineToLinearHeading(TEST_LEFT.toPose())
                    .build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> TEST_LEFT_TO_RIGHT =
            func -> func.apply(TEST_LEFT.toPose())
                    .lineToLinearHeading(TEST_START.toPose())
                    .build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> TEST_FORWARD_PATH =
            func -> func.apply(TEST_START.toPose())
                    .lineToLinearHeading(TEST_FORWARD.toPose())
                    .build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> TEST_BACKWARD_PATH =
            func -> func.apply(TEST_FORWARD.toPose())
                    .lineToLinearHeading(TEST_START.toPose())
                    .build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> TEST_SPLINE_PATH_1 =
            func -> func.apply(TEST_START.toPose())
                    .splineToLinearHeading(TEST_SPLINE_1.toPose(), TEST_START.getHeading())
                    .splineToLinearHeading(TEST_START.toPose(), TEST_START.getHeading())
                    .build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> TEST_SPLINE_PATH_2 =
            func -> func.apply(TEST_START.toPose())
                    .splineToSplineHeading(TEST_SPLINE_2.toPose(), Math.toRadians(90))
                    .splineToSplineHeading(TEST_START.toPose(), Math.toRadians(-45))
                    .build();

    //    public static ConfigurablePoseD OBSERVATION_START = new ConfigurablePoseD(0, 60, -90);
//    public static ConfigurablePoseD SUBMARINE = new ConfigurablePoseD(-5, 36, (90));
//    public static ConfigurablePoseD OBSERVATION_ZONE = new ConfigurablePoseD(-46, 55, (90));
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

    //forward backward constants

    public static ConfigurablePoseD FORWARD = new ConfigurablePoseD(0, 18, 90);
    public static ConfigurablePoseD REST = new ConfigurablePoseD(0, 0, 90);
    public static ConfigurablePoseD SIDE = new ConfigurablePoseD(18, 0, 90);

    //public static ConfigurablePoseD OBSERVATION_ZONE = new ConfigurablePoseD(-60, 55, (135));

    //Lines for Into the Deeeeeeeep
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence>
            OBSERVATION_SIDE_CYCLE = func ->
            func
                    .apply(OBSERVATION_START.toPose())
                    .lineToLinearHeading(SUBMARINE.toPose())
                    .lineToLinearHeading(OBSERVATION_ZONE.toPose())
                    .build();

    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> OBSERVATION_TEST1 = func ->
            func
                    .apply(OBSERVATION_START.toPose())
                    .lineToLinearHeading(SUBMARINE.toPose())
                    .build();

    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> OBSERVATION_TEST1QUARTER = func ->
            func
                    .apply(SUBMARINE.toPose())
                    .lineToLinearHeading(PUSH_1.toPose())
                    .build();

    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> OBSERVATION_TEST2 = func ->
            func
                    .apply(PUSH_HALF.toPose())
                    //.splineToLinearHeading(PUSH_HALF, Math.PI - PUSH_1.getHeading())
                    .splineToLinearHeading(PUSH_1.toPose(), Math.PI - PUSH_1.getHeading())
                    .build();

    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> OBSERVATION_TEST3 = func ->
            func
                    .apply(SUBMARINE.toPose())
                    .lineToLinearHeading(MINI_LINE.toPose())
                    .splineToConstantHeading(PUSH_HALF.toVec(), Math.PI - PUSH_HALF.getHeading())
                    .splineToConstantHeading(PUSH_1.toVec(), Math.PI - PUSH_1.getHeading())
                    .splineToConstantHeading(PUSH_2.toVec(), Math.PI - PUSH_2.getHeading())
                    .splineToConstantHeading(PUSH_3.toVec(), Math.PI - PUSH_3.getHeading())
                    .splineToConstantHeading(PUSH_3_AND_A_HALF.toVec(), PUSH_3_AND_A_HALF.getHeading())
                    .splineToConstantHeading(SAMPLE_1.toVec(), Math.PI - SAMPLE_1.getHeading())
                    .splineToConstantHeading(OBSERVATION_PUSH_HALF.toVec(), Math.PI - OBSERVATION_PUSH_HALF.getHeading())
                    .splineToConstantHeading(OBSERVATION_ZONE.toVec(), Math.PI - OBSERVATION_ZONE.getHeading())
                    .build();

    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> OBSERVATION_TEST4 = func ->
            func
                    .apply(OBSERVATION_ZONE.toPose())
                    .lineToLinearHeading(SUBMARINE2.toPose())
                    .build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> OBSERVATION_TEST5 = func ->
            func
                    .apply(SUBMARINE2.toPose())
                    .lineToLinearHeading(OBSERVATION_ZONE.toPose())
                    .build();

    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> OBSERVATION_TEST6 = func ->
            func
                    .apply(OBSERVATION_ZONE.toPose())
                    .lineToLinearHeading(SUBMARINE3.toPose())
                    .build();


    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> TANGENT_TEST = func ->
            func
                    .apply(OBSERVATION_ZONE.toPose())
                    //.splineToConstantHeading(OBSERVATION_ZONE.vec(), (130))
                    .splineToLinearHeading(SUBMARINE3.toPose(), (0))
                    .build();


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

    //forward backward yippee

    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> FORWARD_BACKWARD1 = func ->
            func
                    .apply(REST.toPose())
                    .lineToLinearHeading(FORWARD.toPose())
                    .build();

    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> FORWARD_BACKWARD2 = func ->
            func
                    .apply(FORWARD.toPose())
                    .lineToLinearHeading(REST.toPose())
                    .build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> FORWARD_BACKWARD3 = func ->
            func
                    .apply(REST.toPose())
                    .lineToLinearHeading(SIDE.toPose())
                    .build();

    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> FORWARD_BACKWARD4 = func ->
            func
                    .apply(SIDE.toPose())
                    .lineToLinearHeading(REST.toPose())
                    .build();

    // "borrowed" from 16750 sample side auto
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence>
            START_TO_NETSCORING = b ->
            b.apply(NET_START.toPose()).lineToLinearHeading(NETSCORING.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence>
            NETSCORING_TO_INTAKE1 = b ->
            b.apply(NETSCORING.toPose()).lineToLinearHeading(INTAKE1.toPose()).build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence>
            INTAKE1_TO_NETSCORING = b ->
            b.apply(INTAKE1.toPose()).lineToLinearHeading(NETSCORING.toPose()).build();

    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> PUSHY_BOT_1 = func ->
            func
                    .apply(NETSCORING.toPose())
                    .lineToLinearHeading(PUSHY_1.toPose())
                    .build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> PUSHY_BOT_2 = func ->
            func
                    .apply(PUSHY_1.toPose())
                    .lineToLinearHeading(PUSHY_2.toPose())
                    .build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> PUSHY_BOT_3 = func ->
            func
                    .apply(PUSHY_2.toPose())
                    .lineToLinearHeading(PUSHY_3.toPose())
                    .build();
    public static final Function<Function<Pose2d, TrajectorySequenceBuilder>, TrajectorySequence> OBSERVATION_PARKY_WARKY = func ->
            func
                    .apply(OBS_STARTY.toPose())
                    .lineToLinearHeading(OBS_PARKY.toPose())
                    .build();
    //end of borrowing

    //New testing constants for this year's game
    public static ConfigurablePoseD START = new ConfigurablePoseD(35, 63, 0);
    //public static ConfigurablePoseD OBS_START = new ConfigurablePoseD(22, -65, 90);
    public static ConfigurablePoseD NETSCORING_TEST = new ConfigurablePoseD(55, 55, 45);
    public static ConfigurablePoseD NETSCORING_CLEAR = new ConfigurablePoseD(45, 35, -45);
    public static ConfigurablePoseD SPLINETEST1 = new ConfigurablePoseD(0, -55, 0);
    public static ConfigurablePoseD SPLINETEST2 = new ConfigurablePoseD(55, 0, 0);
    //public static ConfigurablePoseD OBS_PARK = new ConfigurablePoseD(62, -6, 90);
}





    //end of forward backward yippee


//Do i need the meep-meep stuff down below?

    //gonna comment this stuff out below but idk if i still need it here or somewhere else :DDD

        /*
    .trajectorySequenceBuilder(AutoConstants.OBSERVATION_START)
            .addTrajectory(AutoConstants.PUSH_BOT_OBSERVATION_SIDE_AUTO1.get())
                .addTrajectory(AutoConstants.PUSH_BOT_OBSERVATION_SIDE_AUTO2.get())
                .addTrajectory(AutoConstants.PUSH_BOT_OBSERVATION_SIDE_AUTO4.get())
                .addTrajectory(AutoConstants.PUSH_BOT_OBSERVATION_SIDE_AUTO4HALF.get())
                .addTrajectory(AutoConstants.PUSH_BOT_OBSERVATION_SIDE_AUTO5.get())
                .addTrajectory(AutoConstants.PUSH_BOT_OBSERVATION_SIDE_AUTO6.get())
                .addTrajectory(AutoConstants.PUSH_BOT_OBSERVATION_SIDE_AUTO7.get())
                .addTrajectory(AutoConstants.PUSH_BOT_OBSERVATION_SIDE_AUTO8.get())
                .addTrajectory(AutoConstants.PUSH_BOT_OBSERVATION_SIDE_AUTO9.get())
                .addTrajectory(AutoConstants.PUSH_BOT_OBSERVATION_SIDE_AUTO10.get())
                .build();

*/ 
 
