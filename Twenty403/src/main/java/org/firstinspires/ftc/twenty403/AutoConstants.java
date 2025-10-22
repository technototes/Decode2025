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

    // measured 10/14
    public static double botWeightKg20403 = 9.0;
    public static double xvelocity20403 = 77.84;
    public static double yvelocity20403 = 61.23;
    public static double fowarddeceleration20403 = 0.0;
    public static double lateraldeceleration20403 = 0.0;
    public static double linearscalar20403 = 57;
    public static double angularscalar20403 = 0.982;
    public static SparkFunOTOS.Pose2D OTOS_OFFSET_20403 = new SparkFunOTOS.Pose2D(4.75,0,Math.PI/2);
    public static double botWeightKgPtechno;
    public static double xvelocityPtechno;
    public static double yvelocityPtechno;
    public static double fowarddecelerationPtechno = 0.0;
    public static double lateraldecelerationPtechno = 0.0;
    public static double linearscalarPtechno;
    public static double angularscalarPtechno;
    public static SparkFunOTOS.Pose2D OTOS_OFFSET_Ptechno;

    public static FollowerConstants followerConstants = new FollowerConstants()
        // tune these
        .mass(1);

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);
    public static MecanumConstants driveConstants = new MecanumConstants()
        .maxPower(1)
        .leftFrontMotorName(FLMOTOR)
        .leftRearMotorName(RLMOTOR)
        .rightFrontMotorName(FRMOTOR)
        .rightRearMotorName(RRMOTOR)
        .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
        .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
        .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
        .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD);



    public static OTOSConstants otosLocalizerConstants = new OTOSConstants()
        .hardwareMapName(OTOS)
        .linearUnit(DistanceUnit.INCH)
        .angleUnit(AngleUnit.RADIANS);
        // need to tune for OTOS localization


    public static DriveEncoderConstants driveEncoderConstants = new DriveEncoderConstants()
            .leftFrontMotorName(FLMOTOR)
            .leftRearMotorName(RLMOTOR)
            .rightFrontMotorName(FRMOTOR)
            .rightRearMotorName(RRMOTOR)
            .leftFrontEncoderDirection(Encoder.FORWARD)
            .leftRearEncoderDirection(Encoder.FORWARD)
            .rightFrontEncoderDirection(Encoder.FORWARD)
            .rightRearEncoderDirection(Encoder.FORWARD);
    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
            .pathConstraints(pathConstraints)
                .mecanumDrivetrain(driveConstants)
                .OTOSLocalizer(otosLocalizerConstants).build();

    }
}
