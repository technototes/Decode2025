package org.firstinspires.ftc.sixteen750;

import static org.firstinspires.ftc.sixteen750.Setup.HardwareNames.FL_DRIVE_MOTOR;
import static org.firstinspires.ftc.sixteen750.Setup.HardwareNames.FR_DRIVE_MOTOR;
import static org.firstinspires.ftc.sixteen750.Setup.HardwareNames.OTOS;
import static org.firstinspires.ftc.sixteen750.Setup.HardwareNames.RL_DRIVE_MOTOR;
import static org.firstinspires.ftc.sixteen750.Setup.HardwareNames.RR_DRIVE_MOTOR;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.Encoder;
import com.pedropathing.ftc.localization.constants.DriveEncoderConstants;
import com.pedropathing.ftc.localization.constants.OTOSConstants;
import com.pedropathing.ftc.localization.constants.TwoWheelConstants;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.technototes.path.geometry.ConfigurablePoseD;
import com.technototes.path.trajectorysequence.TrajectorySequence;
import com.technototes.path.trajectorysequence.TrajectorySequenceBuilder;
import java.util.function.Function;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Config
public class AutoConstants {

    // note these need to be tuned
    public static double botWeightKg = 9.44;
    public static double xvelocity = 10.0;
    public static double yvelocity = 10.0;
    // Need to talk about naming constants with students:
    public static double fwdDeceleration = 0.0;
    public static double latDeceleration = 0.0;
    public static double fwdTicksToInches = 0.008;
    public static double strafeTicksToInches = -0.009;
    public static double turnTicksToInches = 0.018;
    public static double robotLength = 10.28;
    public static double robotWidth = 7.625;
    public static SparkFunOTOS.Pose2D offset = new SparkFunOTOS.Pose2D(1.50, 0.0, 180);
    public static double linearscalar = -1.08; //1.9
    public static double angularscalar = 0.9;
    public static PIDFCoefficients headingPIDF = new PIDFCoefficients(0.0003, 0, 0, 0);
    public static PIDFCoefficients translationPIDF = new PIDFCoefficients(0.0003, 0, 0, 0);
    public static PIDFCoefficients secondaryheadingPIDF = new PIDFCoefficients(0.0003, 0, 0, 0);
    public static PIDFCoefficients secondarytranslationPIDF = new PIDFCoefficients(0.0003, 0, 0, 0);
    public static FilteredPIDFCoefficients drivePIDF = new FilteredPIDFCoefficients(
        0.025,
        0,
        0.00001,
        0.6,
        0.01
    );

    //public static FilteredPIDFCoefficients drivePIDF = new FilteredPIDFCoefficients(0.1, 0, 0, 0.01);
    //public static PIDFCoefficients centripetalPIDF = new PIDFCoefficients(0.1, 0, 0, 0.01);

    public static double TValueConstraint = 0.99;
    public static double timeoutConstraint = 100;
    public static double brakingStrength = 1.0;
    public static double brakingStart = 1.0;

    // Need to explain SIOF to students.
    // (Hurray for programming languages never quite doing what you expect)
    public static DriveEncoderConstants getEncoderConstants() {
        return new DriveEncoderConstants()
            .forwardTicksToInches(fwdTicksToInches)
            .strafeTicksToInches(strafeTicksToInches)
            .turnTicksToInches(turnTicksToInches)
            .robotLength(robotLength)
            .robotWidth(robotWidth)
            .rightFrontMotorName(FR_DRIVE_MOTOR)
            .rightRearMotorName(RR_DRIVE_MOTOR)
            .leftRearMotorName(RL_DRIVE_MOTOR)
            .leftFrontMotorName(FL_DRIVE_MOTOR)
            .leftFrontEncoderDirection(Encoder.FORWARD)
            .leftRearEncoderDirection(Encoder.REVERSE)
            .rightFrontEncoderDirection(Encoder.REVERSE)
            .rightRearEncoderDirection(Encoder.FORWARD);
    }

    public static TwoWheelConstants localizerConstants = new TwoWheelConstants()
        .forwardEncoder_HardwareMapName(Setup.HardwareNames.ODOF) //odo name
        .strafeEncoder_HardwareMapName(Setup.HardwareNames.ODOR) //odo name
        .IMU_HardwareMapName(Setup.HardwareNames.EXTERNAL_IMU)
        .forwardPodY(4.27) //offset
        .strafePodX(-4.006) //offset
        .forwardTicksToInches(5.47) //5.42, 5.47, 5.49
        .strafeTicksToInches(5.38) //5.37, 5.39, 5.38
        .forwardEncoderDirection(Encoder.REVERSE)
        .strafeEncoderDirection(Encoder.FORWARD)
        .IMU_Orientation(
            new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                RevHubOrientationOnRobot.UsbFacingDirection.DOWN
            )
        );

    public static FollowerConstants getFollowerConstants() {
        // tune these
        return new FollowerConstants()
            .mass(botWeightKg)
            .forwardZeroPowerAcceleration(fwdDeceleration)
            .lateralZeroPowerAcceleration(latDeceleration)
            .holdPointTranslationalScaling(1)
            .headingPIDFCoefficients(headingPIDF)
            .drivePIDFCoefficients(drivePIDF)
            .secondaryHeadingPIDFCoefficients(secondaryheadingPIDF)
            .secondaryTranslationalPIDFCoefficients(secondarytranslationPIDF)
            .translationalPIDFCoefficients(translationPIDF);
    }

    public static PathConstraints getPathConstraints() {
        return new PathConstraints(
            TValueConstraint,
            timeoutConstraint,
            brakingStrength,
            brakingStart
        );
    }

    public static MecanumConstants getDriveConstants() {
        return new MecanumConstants()
            .maxPower(1)
            .leftFrontMotorName(FL_DRIVE_MOTOR)
            .leftRearMotorName(RL_DRIVE_MOTOR)
            .rightFrontMotorName(FR_DRIVE_MOTOR)
            .rightRearMotorName(RR_DRIVE_MOTOR)
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .xVelocity(xvelocity)
            .yVelocity(yvelocity);
    }

    public static OTOSConstants getOTOSConstants() {
        return new OTOSConstants()
            .hardwareMapName(OTOS)
            .linearUnit(DistanceUnit.INCH)
            .angleUnit(AngleUnit.RADIANS)
            // need to tune for OTOS localization
            .linearScalar(linearscalar)
            .angularScalar(angularscalar);
    }

    public static Follower createFollower(HardwareMap hardwareMap) {
        SparkFunOTOS otos = hardwareMap.get(SparkFunOTOS.class, OTOS);
        otos.calibrateImu();
        return new FollowerBuilder(getFollowerConstants(), hardwareMap)
            .pathConstraints(getPathConstraints())
            //.OTOSLocalizer(getOTOSConstants())
            .mecanumDrivetrain(getDriveConstants())
            .twoWheelLocalizer(localizerConstants)
            //.driveEncoderLocalizer(getEncoderConstants())
            .build();
    }

    //New testing constants for this year's game
    public static Pose scorePose = new Pose(0.0, 0.0, 0.0);
    public static Pose pickup1Pose = new Pose(0.0, 0.0, 0.0);

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
