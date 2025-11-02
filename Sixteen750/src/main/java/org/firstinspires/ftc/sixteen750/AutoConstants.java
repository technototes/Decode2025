package org.firstinspires.ftc.sixteen750;

import com.acmerobotics.dashboard.config.Config;
import com.bylazar.configurables.annotations.Configurable;
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
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.sixteen750.Setup.HardwareNames;
import org.firstinspires.ftc.sixteen750.helpers.CustomAdafruitIMU;

@Config
@Configurable
public class AutoConstants {

    // note these need to be measured:
    public static double botWeightKg = 9.44;
    public static double robotLength = 17.5;
    public static double robotWidth = 11.5;

    // These come from Tuners:
    public static double xvelocity = 67.3;
    public static double yvelocity = 62.4;
    public static double fwdDeceleration = -42.8;
    public static double latDeceleration = -71.1;
    public static double centripetalScaling = 0.0005;

    // These are hand tuned to work how we want
    public static double brakingStrength = 0.07;
    public static double brakingStart = 0.1;
    public static PIDFCoefficients headingPIDF = new PIDFCoefficients(0.06, 0.0001, 0.004, 0.05);
    public static PIDFCoefficients translationPIDF = new PIDFCoefficients(0.06, 0, 0.007, 0.03);
    // "Kalman filtering": T in this constructor is the % of the previous
    // derivative that should be used to calculate the derivative.
    // (D is "Derivative" in PIDF...)
    public static FilteredPIDFCoefficients drivePIDF = new FilteredPIDFCoefficients(
        0.008,
        0,
        0.000001,
        0.6,
        0.05
    );

    // The percent of a path that must be complete for Pedro to decide it's done
    public static double TValueConstraint = 0.99;
    // Time, in *milliseconds*, to let the follower algorithm correct
    // before the path is considered "complete".
    public static double timeoutConstraint = 100;
    // The maximum velocity (in inches/second) the bot can be moving while still
    // saying the path is complete.
    public static double acceptableVelocity = 1.0;
    // The maximum distance (in inches) the bot can be from the path end
    // while still saying the path is complete.
    public static double acceptableDistance = 2.0;
    // The maximum heading error (in degrees) the bot can be from the path end
    // while still saying the path is complete.
    public static double acceptableHeading = 2.5;

    //public static FilteredPIDFCoefficients drivePIDF = new FilteredPIDFCoefficients(0.1, 0, 0, 0.01);
    //public static PIDFCoefficients centripetalPIDF = new PIDFCoefficients(0.1, 0, 0, 0.01);

    @Configurable
    public static class DriveEncoderConfig {

        public static double fwdTicksToInches = 0.008;
        public static double strafeTicksToInches = -0.009;
        public static double turnTicksToInches = 0.018;
    }

    @Configurable
    public static class OTOSConfig {

        public static SparkFunOTOS.Pose2D offset = new SparkFunOTOS.Pose2D(1.50, 0.0, 180);
        public static double linearscalar = -1.08; //1.9
        public static double angularscalar = 0.9;
    }

    @Configurable
    public static class TwoWheelConfig {

        public static String forwardName = HardwareNames.ODOFB;
        public static String strafeName = HardwareNames.ODORL;
        public static double forwardTicksToInches = ((17.5 / 25.4) * 2 * Math.PI) / 8192; // 5.42, 5.47, 5.49
        public static double strafeTicksToInches = ((17.5 / 25.4) * 2 * Math.PI) / 8192; // 5.37, 5.39, 5.38
        public static double forwardPodYOffset = -3.9; // From Colin's CAD 10/31
        public static double strafePodXOffset = -4.124; // From Colin's CAD 10/31
        public static boolean forwardReversed = true;
        public static boolean strafeReversed = false;
        public static RevHubOrientationOnRobot.LogoFacingDirection logoDir =
            RevHubOrientationOnRobot.LogoFacingDirection.LEFT;
        public static RevHubOrientationOnRobot.UsbFacingDirection usbDir =
            RevHubOrientationOnRobot.UsbFacingDirection.UP;
    }

    public static TwoWheelConstants getTwoWheelLocalizerConstants() {
        TwoWheelConstants tc = new TwoWheelConstants()
            .forwardEncoder_HardwareMapName(TwoWheelConfig.forwardName)
            .strafeEncoder_HardwareMapName(TwoWheelConfig.strafeName)
            .forwardPodY(TwoWheelConfig.forwardPodYOffset)
            .strafePodX(TwoWheelConfig.strafePodXOffset)
            .forwardTicksToInches(TwoWheelConfig.forwardTicksToInches)
            .strafeTicksToInches(TwoWheelConfig.strafeTicksToInches)
            .forwardEncoderDirection(
                TwoWheelConfig.forwardReversed ? Encoder.REVERSE : Encoder.FORWARD
            )
            .strafeEncoderDirection(
                TwoWheelConfig.strafeReversed ? Encoder.REVERSE : Encoder.FORWARD
            );
        if (Setup.Connected.EXTERNAL_IMU) {
            tc = tc.customIMU(new CustomAdafruitIMU());
        } else {
            tc = tc
                .IMU_HardwareMapName(Setup.HardwareNames.IMU)
                .IMU_Orientation(
                    new RevHubOrientationOnRobot(TwoWheelConfig.logoDir, TwoWheelConfig.usbDir)
                );
        }
        return tc;
    }

    public static FollowerConstants getFollowerConstants() {
        // tune these
        return new FollowerConstants()
            .mass(botWeightKg)
            .forwardZeroPowerAcceleration(fwdDeceleration)
            .lateralZeroPowerAcceleration(latDeceleration)
            // .holdPointTranslationalScaling(1)
            .headingPIDFCoefficients(headingPIDF)
            .drivePIDFCoefficients(drivePIDF)
            .translationalPIDFCoefficients(translationPIDF)
            .centripetalScaling(centripetalScaling);
    }

    public static PathConstraints getPathConstraints() {
        PathConstraints pc = new PathConstraints(
            TValueConstraint,
            timeoutConstraint,
            brakingStrength,
            brakingStart
        );
        pc.setVelocityConstraint(acceptableVelocity);
        pc.setTranslationalConstraint(acceptableDistance);
        pc.setHeadingConstraint(Math.toRadians(acceptableHeading));
        return pc;
    }

    public static MecanumConstants getDriveConstants() {
        return new MecanumConstants()
            .maxPower(1)
            .leftFrontMotorName(HardwareNames.FL_DRIVE_MOTOR)
            .leftRearMotorName(HardwareNames.RL_DRIVE_MOTOR)
            .rightFrontMotorName(HardwareNames.FR_DRIVE_MOTOR)
            .rightRearMotorName(HardwareNames.RR_DRIVE_MOTOR)
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .xVelocity(xvelocity)
            .yVelocity(yvelocity);
    }

    /*
    public static DriveEncoderConstants getEncoderConstants() {
        return new DriveEncoderConstants()
                .forwardTicksToInches(DriveEncoderConfig.fwdTicksToInches)
                .strafeTicksToInches(DriveEncoderConfig.strafeTicksToInches)
                .turnTicksToInches(DriveEncoderConfig.turnTicksToInches)
                .robotLength(robotLength)
                .robotWidth(robotWidth)
                .rightFrontMotorName(HardwareNames.FR_DRIVE_MOTOR)
                .rightRearMotorName(HardwareNames.RR_DRIVE_MOTOR)
                .leftRearMotorName(HardwareNames.RL_DRIVE_MOTOR)
                .leftFrontMotorName(HardwareNames.FL_DRIVE_MOTOR)
                .leftFrontEncoderDirection(Encoder.FORWARD)
                .leftRearEncoderDirection(Encoder.REVERSE)
                .rightFrontEncoderDirection(Encoder.REVERSE)
                .rightRearEncoderDirection(Encoder.FORWARD);
    }

    public static OTOSConstants getOTOSConstants() {
        return new OTOSConstants()
            .hardwareMapName(HardwareNames.OTOS)
            .linearUnit(DistanceUnit.INCH)
            .angleUnit(AngleUnit.RADIANS)
            .linearScalar(OTOSConfig.linearscalar)
            .angularScalar(OTOSConfig.angularscalar);
    }
    */

    public static Follower createFollower(HardwareMap hardwareMap) {
        if (Setup.Connected.OTOS) {
            SparkFunOTOS otos = hardwareMap.get(SparkFunOTOS.class, HardwareNames.OTOS);
            otos.calibrateImu();
        }
        return new FollowerBuilder(getFollowerConstants(), hardwareMap)
            .pathConstraints(getPathConstraints())
            //.driveEncoderLocalizer(getEncoderConstants())
            //.OTOSLocalizer(getOTOSConstants())
            .mecanumDrivetrain(getDriveConstants())
            .twoWheelLocalizer(getTwoWheelLocalizerConstants())
            .build();
    }

    //New testing constants for this year's game
    public static Pose scorePose = new Pose(0.0, 0.0, 0.0);
    public static Pose pickup1Pose = new Pose(0.0, 0.0, 0.0);
}
