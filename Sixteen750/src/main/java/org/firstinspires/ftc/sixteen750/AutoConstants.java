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
}
