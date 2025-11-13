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
public class DrivingConstants {

    @Configurable
    public static class Control {

        // Max power scaling for translational driving:
        public static double SNAIL_SPEED = 0.40;
        public static double NORMAL_SPEED = 0.8;
        public static double TURBO_SPEED = 1.0;
        public static double AUTO_SPEED = 0.95;

        // The 'fastest' the robot can turn (0: not turning, 1.0: Fastest possible)
        public static double SNAIL_TURN = 0.25;
        public static double NORMAL_TURN = 0.5;
        public static double TURBO_TURN = 1.0;

        public static double STICK_DEAD_ZONE = 0.05;

        // The amount to multiply the 'default' rotation by to turn the bot to
        // face the apriltag for the target. This is effectively "P" in a PID,
        // but we don't have I or D implemented
        public static double TAG_ALIGNMENT_GAIN = 2.0;
    }

    /**** Stuff for the PedroPathing follower ****/

    // Measured by hoomans:
    public static double botWeightKg = 4.90;
    public static double botWidth = 10.1;
    public static double botLength = 12.5;

    // Adjusted to be sensible (no good guidance on these :/ )
    public static double brakingStrength = 0.5;
    public static double brakingStart = 0.5;
    // Values from tuners:
    public static double xVelocity = 59.2;
    public static double yVelocity = 51.7;
    public static double forwardDeceleration = -40.0;
    public static double lateralDeceleration = -48.0;
    public static double centripetalScale = 0.0005;
    // PIDs to be tuned:
    public static PIDFCoefficients translationPID = new PIDFCoefficients(
        0.08,
        0.000005,
        0.008,
        0.02
    );
    public static PIDFCoefficients headingPID = new PIDFCoefficients(0.9, 0.005, 0.05, 0.02);
    // "Kalman filtering": T in this constructor is the % of the previous
    // derivative that should be used to calculate the derivative.
    // (D is "Derivative" in PIDF...)
    // Tristan says Kalman Filtering is for curve prediction, so...it helps predict ac/deceleration?
    public static FilteredPIDFCoefficients drivePID = new FilteredPIDFCoefficients(
        0.005,
        00.00001,
        0.0004,
        0.6, // Kalman filter: 60% of D will come from the *previous* derivative
        0.02
    );

    // The percent of a path that must be complete for Pedro to decide it's done
    public static double tValueContraint = 0.99;

    // Time, in *milliseconds*, to let the follower algorithm correct
    // before the path is considered "complete".
    public static double timeoutConstraint = 250;

    // The maximum velocity (in inches/second) the bot can be moving while still
    // saying the path is complete.
    public static double acceptableVelocity = 1.0;
    // The maximum distance (in inches) the bot can be from the path end
    // while still saying the path is complete.
    public static double acceptableDistance = 2.0;
    // The maximum heading error (in degrees) the bot can be from the path end
    // while still saying the path is complete.
    public static double acceptableHeading = 2.5;

    // @Configurable
    public static class OTOSConfig {

        public static double linearScalar = 1.4;
        public static double angularScalar = 1.0;
        public static SparkFunOTOS.Pose2D DEVICE_POSITION = new SparkFunOTOS.Pose2D(
            4.75,
            0,
            -Math.PI / 2
        );
    }

    // @Configurable
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

    public enum LocalizerSelection {
        USE_MOTORS,
        USE_OTOS,
        USE_PINPOINT,
    }

    public static LocalizerSelection WhichLocalizer = LocalizerSelection.USE_PINPOINT;

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
        PathConstraints pc = new PathConstraints(
            tValueContraint,
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
                fb = fb.OTOSLocalizer(getOtosLocalizerConstants());
                break;
            case USE_MOTORS:
                fb = fb.driveEncoderLocalizer(getDriveEncoderConstants());
                break;
            case USE_PINPOINT:
                fb = fb.pinpointLocalizer(getPinpointConstants());
                break;
        }
        Follower f = fb.build();
        f.setMaxPowerScaling(Control.AUTO_SPEED);
        return f;
    }
}
