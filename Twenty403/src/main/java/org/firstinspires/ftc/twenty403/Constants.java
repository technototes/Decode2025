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
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Configurable
public class Constants {

    // note these need to be tuned
    public static double botWeightKg = 0.0;
    public static double xvelocity = 0.0;
    public static double yvelocity = 0.0;
    public static double fowarddeceleration = 0.0;
    public static double lateraldeceleration = 0.0;
    public static double linearscalar = 0.0;
    public static double angularscalar = 0.0;

    public static FollowerConstants followerConstants = new FollowerConstants()
        // tune these
        .mass(botWeightKg)
        .forwardZeroPowerAcceleration(fowarddeceleration)
        .lateralZeroPowerAcceleration(lateraldeceleration);

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);
    public static MecanumConstants driveConstants = new MecanumConstants()
        .maxPower(1)
        .leftFrontMotorName(FLMOTOR)
        .leftRearMotorName(RLMOTOR)
        .rightFrontMotorName(FRMOTOR)
        .rightRearMotorName(RRMOTOR)
        .leftFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
        .leftRearMotorDirection(DcMotorSimple.Direction.FORWARD)
        .rightFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
        .rightRearMotorDirection(DcMotorSimple.Direction.REVERSE)
        .xVelocity(xvelocity)
        .yVelocity(yvelocity);
    // for drive encoder localization

    public static OTOSConstants otosLocalizerConstants = new OTOSConstants()
        .hardwareMapName(OTOS)
        .linearUnit(DistanceUnit.INCH)
        .angleUnit(AngleUnit.RADIANS)
        // need to tune for OTOS localization
        .linearScalar(linearscalar)
        .angularScalar(angularscalar);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
            .pathConstraints(pathConstraints)
            // uncomment for otos localization and vice versa
            .OTOSLocalizer(otosLocalizerConstants)
            .mecanumDrivetrain(driveConstants)
            .build();
    }
}
