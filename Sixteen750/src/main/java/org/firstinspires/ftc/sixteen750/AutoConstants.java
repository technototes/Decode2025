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
import com.pedropathing.paths.PathConstraints;
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

    public static double botWeightKg = 9.44;
    public static double xvelocity = 0.0;
    public static double yvelocity = 0.0;
    // Need to talk about naming constants with students:
    public static double fwdDeceleration = 0.0;
    public static double latDeceleration = 0.0;
    public static double fwdTicksToInches = 0.6;
    public static double strafeTicksToInches = -0.19;
    public static double turnTicksToInches = -0.018;
    public static double robotLength = 10.28;
    public static double robotWidth = 7.625;
    public static SparkFunOTOS.Pose2D offset = new SparkFunOTOS.Pose2D(1.50, 0.0,180);
    public static PIDFCoefficients headingPIDF = new PIDFCoefficients(1, 0, 0, 0.01);
    public static PIDFCoefficients translationPIDF = new PIDFCoefficients(0.1, 0, 0, 0.01);
    //public static FilteredPIDFCoefficients drivePIDF = new FilteredPIDFCoefficients(0.1, 0, 0, 0.01);
    //public static PIDFCoefficients centripetalPIDF = new PIDFCoefficients(0.1, 0, 0, 0.01);

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
            .leftFrontEncoderDirection(Encoder.REVERSE)
            .leftRearEncoderDirection(Encoder.REVERSE)
            .rightFrontEncoderDirection(Encoder.FORWARD)
            .rightRearEncoderDirection(Encoder.FORWARD);
    }

    public static FollowerConstants getFollowerConstants() {
            // tune these
        return new FollowerConstants()
            .mass(botWeightKg)
            .forwardZeroPowerAcceleration(fwdDeceleration)
            .lateralZeroPowerAcceleration(latDeceleration)
            .headingPIDFCoefficients(headingPIDF)
            .translationalPIDFCoefficients(translationPIDF);
    }

    public static PathConstraints getPathConstraints() {
        return new PathConstraints(0.99, 100, 1, 1);
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


    public static Follower createFollower(HardwareMap hardwareMap) {
        SparkFunOTOS otos = hardwareMap.get(SparkFunOTOS.class, OTOS);
        otos.calibrateImu();
        return new FollowerBuilder(getFollowerConstants(), hardwareMap)
            .pathConstraints(getPathConstraints())
            .OTOSLocalizer(getOTOSConstants())
            .mecanumDrivetrain(getDriveConstants())
            //.driveEncoderLocalizer(getEncoderConstants())
            .build();
    }

    public static OTOSConstants getOTOSConstants() {
        return new OTOSConstants()
            .hardwareMapName(OTOS)
            .linearUnit(DistanceUnit.INCH)
            .linearScalar(-1.08)
            .angularScalar(0.9) // 0.9 1.08
            .offset(offset)
            .angleUnit(AngleUnit.RADIANS);
    }

    //New testing constants for this year's game
    public static ConfigurablePoseD SPLINETEST1 = new ConfigurablePoseD(0, -55, 0);
    public static ConfigurablePoseD SPLINETEST2 = new ConfigurablePoseD(55, 0, 0);

    public static ConfigurablePoseD TELESTART = new ConfigurablePoseD(0, 0, 90);
    public static ConfigurablePoseD FORWARD = new ConfigurablePoseD(48, 0, 0);
    public static ConfigurablePoseD BACKWARD = new ConfigurablePoseD(0, 0, 0);
    public static ConfigurablePoseD SIDE_RIGHT = new ConfigurablePoseD(0, -48, 0);
    public static ConfigurablePoseD SIDE_LEFT = new ConfigurablePoseD(0, 0, 0);

    // These are 'trajectory pieces' which should be named like this:
    // {STARTING_POSITION}_TO_{ENDING_POSITION}

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
}
