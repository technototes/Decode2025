package org.firstinspires.ftc.learnbot;

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
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.learnbot.Setup.HardwareNames;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Configurable
public class AutoConstants {

    public static double botWeightKg = 4.90;
    public static double botWidth = 10.1;
    public static double botLength = 12.5;
    public static double xVelocity = 77.84;
    public static double yVelocity = 61.23;
    public static double forwardDeceleration = -25.0;
    public static double lateralDeceleration = -30.0;
    public static double brakingStrength = 0.005;
    public static double brakingStart = 0.1;
    public static double centripetalScale = 0.0005;
    public static PIDFCoefficients translationPID = new PIDFCoefficients(0.06, 0.00001, 0, 0.015);
    public static PIDFCoefficients headingPID = new PIDFCoefficients(0.4, 0.0008, 0, 0.01);

    // "Kalman filtering": T in this constructor is the % of the previous
    // derivative that should be used to calculate the derivative.
    // (D is "Derivative" in PIDF...)
    public static FilteredPIDFCoefficients drivePID = new FilteredPIDFCoefficients(
        0.005,
        0,
        0.00035,
        0.6,
        0.015
    );

    @Configurable
    public static class OTOSConfig {

        public static double linearScalar = 1.4;
        public static double angularScalar = 1.0;
        public static SparkFunOTOS.Pose2D DEVICE_POSITION = new SparkFunOTOS.Pose2D(
            4.75,
            0,
            -Math.PI / 2
        );
    }

    @Configurable
    public static class MotorLocConfig {

        public static double fwdTicksToInches = 135;
        public static double latTicksToInches = 150;
        public static double turnTicksToInches = 100;
    }

    @Configurable
    public static class PinpointConfig {

        public static double ForwardPodY = -2.5;
        public static double StrafePodX = 0.25;
        public static GoBildaPinpointDriver.EncoderDirection ForwardDirection =
            GoBildaPinpointDriver.EncoderDirection.REVERSED;
        public static GoBildaPinpointDriver.EncoderDirection StrafeDirection =
            GoBildaPinpointDriver.EncoderDirection.REVERSED;
    }

    // 0 = Motor Encoders, 1 = OTOS, 2 = PinPoint
    public static final int USE_MOTORS = 0,
        USE_OTOS = 1,
        USE_PINPOINT = 2;
    public static int WhichLocalizer = USE_PINPOINT;

    public static FollowerConstants getFollowerConstants() {
        return new FollowerConstants()
            // tune these
            .mass(botWeightKg)
            .forwardZeroPowerAcceleration(forwardDeceleration)
            .lateralZeroPowerAcceleration(lateralDeceleration)
            .useSecondaryDrivePIDF(false)
            .useSecondaryHeadingPIDF(false)
            .useSecondaryTranslationalPIDF(false)
            .translationalPIDFCoefficients(translationPID)
            .headingPIDFCoefficients(headingPID)
            .drivePIDFCoefficients(drivePID)
            .centripetalScaling(centripetalScale);
    }

    public static PathConstraints getPathConstraints() {
        return new PathConstraints(0.99, 0.1, brakingStrength, brakingStart);
    }

    public static MecanumConstants getDriveConstants() {
        return new MecanumConstants()
            .maxPower(1)
            .leftFrontMotorName(HardwareNames.FLMOTOR)
            .leftRearMotorName(HardwareNames.RLMOTOR)
            .rightFrontMotorName(HardwareNames.FRMOTOR)
            .rightRearMotorName(HardwareNames.RRMOTOR)
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .xVelocity(xVelocity)
            .yVelocity(yVelocity);
    }

    public static OTOSConstants getOtosLocalizerConstants() {
        return new OTOSConstants()
            .hardwareMapName(HardwareNames.OTOS)
            .linearUnit(DistanceUnit.INCH)
            .angleUnit(AngleUnit.RADIANS)
            .linearScalar(OTOSConfig.linearScalar)
            .angularScalar(OTOSConfig.angularScalar)
            .offset(OTOSConfig.DEVICE_POSITION);
        // need to tune for OTOS localization
    }

    public static DriveEncoderConstants getDriveEncoderConstants() {
        return new DriveEncoderConstants()
            .leftFrontMotorName(HardwareNames.FLMOTOR)
            .leftRearMotorName(HardwareNames.RLMOTOR)
            .rightFrontMotorName(HardwareNames.FRMOTOR)
            .rightRearMotorName(HardwareNames.RRMOTOR)
            .leftFrontEncoderDirection(Encoder.FORWARD)
            .leftRearEncoderDirection(Encoder.FORWARD)
            .rightFrontEncoderDirection(Encoder.FORWARD)
            .rightRearEncoderDirection(Encoder.FORWARD)
            .forwardTicksToInches(MotorLocConfig.fwdTicksToInches)
            .strafeTicksToInches(MotorLocConfig.latTicksToInches)
            .turnTicksToInches(MotorLocConfig.turnTicksToInches)
            .robotLength(botLength)
            .robotWidth(botWidth);
    }

    public static PinpointConstants getPinpointConstants() {
        return new PinpointConstants()
            .hardwareMapName(HardwareNames.PINPOINT)
            .distanceUnit(DistanceUnit.INCH)
            .forwardPodY(PinpointConfig.ForwardPodY)
            .strafePodX(PinpointConfig.StrafePodX)
            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
            .forwardEncoderDirection(PinpointConfig.ForwardDirection)
            .strafeEncoderDirection(PinpointConfig.StrafeDirection);
    }

    public static Follower createFollower(HardwareMap hardwareMap) {
        FollowerBuilder fb = new FollowerBuilder(getFollowerConstants(), hardwareMap)
            .pathConstraints(getPathConstraints())
            .mecanumDrivetrain(getDriveConstants());
        switch (WhichLocalizer) {
            case USE_OTOS:
                return fb.OTOSLocalizer(getOtosLocalizerConstants()).build();
            case USE_MOTORS:
                return fb.driveEncoderLocalizer(getDriveEncoderConstants()).build();
            case USE_PINPOINT:
                return fb.pinpointLocalizer(getPinpointConstants()).build();
            default:
                return fb.build();
        }
    }
}
