package org.firstinspires.ftc.twenty403;

import static org.firstinspires.ftc.twenty403.Setup.HardwareNames.FLMOTOR;
import static org.firstinspires.ftc.twenty403.Setup.HardwareNames.FRMOTOR;
import static org.firstinspires.ftc.twenty403.Setup.HardwareNames.OTOS;
import static org.firstinspires.ftc.twenty403.Setup.HardwareNames.RLMOTOR;
import static org.firstinspires.ftc.twenty403.Setup.HardwareNames.RRMOTOR;

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
import com.pedropathing.paths.PathBuilder;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Configurable
public class AutoConstants {

    // measured 10/14
    public static double botWeightKg = 8.50;
    public static double botWidth = 16.75;
    public static double botLength = 11.75;
    public static double xVelocity = 77.84;
    public static double yVelocity = 61.23;
    public static double forwardDeceleration = -25.0;
    public static double lateralDeceleration = -30.0;
    public static double tValueConstraint = 0.98;
    public static double timeoutConstraint = 250;
    public static double linearScalar = 1.0;
    public static double angularScalar = 1.0;
    public static double brakingStrength = 0.09;
    public static double brakingStart = 0.1;
    public static double centripetalScale = 0.0005;
    // Only used for motor encoders:
    public static double fwdTicksToInches = 1.0;
    public static double latTicksToInches = 1.0;
    public static double turnTicksToInches = 1.0;
    // Only used for OTOS:
    public static SparkFunOTOS.Pose2D OTOS_OFFSET = new SparkFunOTOS.Pose2D(4.75, 0, -Math.PI / 2);
    public static PIDFCoefficients translationPID = new PIDFCoefficients(0.2, 0, 0.02, 0.02);
    public static PIDFCoefficients headingPID = new PIDFCoefficients(0.75, 0.0, 0.075, 0.01);

    // "Kalman filtering": T in this constructor is the % of the previous
    // derivative that should be used to calculate the derivative.
    // (D is "Derivative" in PIDF...)
    public static FilteredPIDFCoefficients drivePID = new FilteredPIDFCoefficients(
        0.03,
        0,
        5E-5,
        0.6,
        1E-4
    );

    public static boolean USE_OTOS = true;

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
        return new PathConstraints(
            tValueConstraint,
            timeoutConstraint,
            brakingStrength,
            brakingStart
        );
    }

    public static MecanumConstants getDriveConstants() {
        return new MecanumConstants()
            .maxPower(1)
            .leftFrontMotorName(FLMOTOR)
            .leftRearMotorName(RLMOTOR)
            .rightFrontMotorName(FRMOTOR)
            .rightRearMotorName(RRMOTOR)
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .xVelocity(xVelocity)
            .yVelocity(yVelocity);
    }

    public static OTOSConstants getOtosLocalizerConstants() {
        return new OTOSConstants()
            .hardwareMapName(OTOS)
            .linearUnit(DistanceUnit.INCH)
            .angleUnit(AngleUnit.RADIANS)
            .linearScalar(linearScalar)
            .angularScalar(angularScalar)
            .offset(OTOS_OFFSET);
        // need to tune for OTOS localization
    }

    public static DriveEncoderConstants getDriveEncoderConstants() {
        return new DriveEncoderConstants()
            .leftFrontMotorName(FLMOTOR)
            .leftRearMotorName(RLMOTOR)
            .rightFrontMotorName(FRMOTOR)
            .rightRearMotorName(RRMOTOR)
            .leftFrontEncoderDirection(Encoder.FORWARD)
            .leftRearEncoderDirection(Encoder.FORWARD)
            .rightFrontEncoderDirection(Encoder.FORWARD)
            .rightRearEncoderDirection(Encoder.FORWARD)
            .robotLength(botLength)
            .robotWidth(botWidth)
            .forwardTicksToInches(fwdTicksToInches)
            .strafeTicksToInches(latTicksToInches)
            .turnTicksToInches(turnTicksToInches);
    }

    public static Follower createFollower(HardwareMap hardwareMap) {
        FollowerBuilder fb = new FollowerBuilder(getFollowerConstants(), hardwareMap)
            .pathConstraints(getPathConstraints())
            .mecanumDrivetrain(getDriveConstants());
        if (USE_OTOS) {
            return fb.OTOSLocalizer(getOtosLocalizerConstants()).build();
        } else {
            return fb.driveEncoderLocalizer(getDriveEncoderConstants()).build();
        }
    }
}
