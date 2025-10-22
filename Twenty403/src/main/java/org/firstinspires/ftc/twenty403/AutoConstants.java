package org.firstinspires.ftc.twenty403;

import static org.firstinspires.ftc.twenty403.Setup.HardwareNames.FLMOTOR;
import static org.firstinspires.ftc.twenty403.Setup.HardwareNames.FRMOTOR;
import static org.firstinspires.ftc.twenty403.Setup.HardwareNames.OTOS;
import static org.firstinspires.ftc.twenty403.Setup.HardwareNames.RLMOTOR;
import static org.firstinspires.ftc.twenty403.Setup.HardwareNames.RRMOTOR;

import com.bylazar.configurables.annotations.Configurable;
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
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Configurable
public class AutoConstants {

    public static boolean ptechno = true;
    // measured 10/14
    public static double botWeightKg20403 = 9.0;
    public static double xvelocity20403 = 77.84;
    public static double yvelocity20403 = 61.23;
    public static double fowarddeceleration20403 = 0.0;
    public static double lateraldeceleration20403 = 0.0;
    public static double linearscalar20403 = 57;
    public static double angularscalar20403 = 0.982;
    public static SparkFunOTOS.Pose2D OTOS_OFFSET_20403 = new SparkFunOTOS.Pose2D(
        4.75,
        0,
        Math.PI / 2
    );
    public static double botWeightKgPtechno = 4.85;
    public static double xvelocityPtechno;
    public static double yvelocityPtechno;
    public static double fowarddecelerationPtechno = 0.0;
    public static double lateraldecelerationPtechno = 0.0;
    public static double linearscalarPtechno;
    public static double angularscalarPtechno;
    public static SparkFunOTOS.Pose2D OTOS_OFFSET_Ptechno;

    public static FollowerConstants getFollowerConstants() {
        return new FollowerConstants()
            // tune these
            .mass(ptechno ? botWeightKgPtechno : botWeightKg20403)
            .forwardZeroPowerAcceleration(
                ptechno ? fowarddecelerationPtechno : fowarddeceleration20403
            )
            .lateralZeroPowerAcceleration(
                ptechno ? lateraldecelerationPtechno : lateraldeceleration20403
            );
        /*
            .translationalPIDFCoefficients(
                new PIDFCoefficients(
                    0.03,
                    0,
                    0,
                    0.015
                )
            )
            .translationalPIDFSwitch(4)
            .secondaryTranslationalPIDFCoefficients(
                new PIDFCoefficients(
                    0.4,
                    0,
                    0.005,
                    0.0006
                )
            )
            .headingPIDFCoefficients(
                new PIDFCoefficients(
                    0.8,
                    0,
                    0,
                    0.01
                )
            )
            .secondaryHeadingPIDFCoefficients(
                new PIDFCoefficients(
                    2.5,
                    0,
                    0.1,
                    0.0005
                )
            )
            .drivePIDFCoefficients(
                new FilteredPIDFCoefficients(
                    0.1,
                    0,
                    0.00035,
                    0.6,
                    0.015
                )
            )
            .secondaryDrivePIDFCoefficients(
                new FilteredPIDFCoefficients(
                    0.02,
                    0,
                    0.000005,
                    0.6,
                    0.01
                )
            )
            .drivePIDFSwitch(15)
            .centripetalScaling(0.0005);
        */
    }

    public static PathConstraints getPathConstraints() {
        return new PathConstraints(0.99, 100, 1, 1);
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
            .xVelocity(ptechno ? xvelocityPtechno : xvelocity20403)
            .yVelocity(ptechno ? yvelocityPtechno : yvelocity20403);
    }

    public static OTOSConstants getOtosLocalizerConstants() {
        return new OTOSConstants()
            .hardwareMapName(OTOS)
            .linearUnit(DistanceUnit.INCH)
            .angleUnit(AngleUnit.RADIANS)
            .linearScalar(ptechno ? linearscalarPtechno : linearscalar20403)
            .angularScalar(ptechno ? angularscalarPtechno : angularscalar20403)
            .offset(ptechno ? OTOS_OFFSET_Ptechno : OTOS_OFFSET_20403);
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
            .rightRearEncoderDirection(Encoder.FORWARD);
    }

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(getFollowerConstants(), hardwareMap)
            .pathConstraints(getPathConstraints())
            .mecanumDrivetrain(getDriveConstants())
            .OTOSLocalizer(getOtosLocalizerConstants())
            .build();
    }
}
