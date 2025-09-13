package org.firstinspires.ftc.twenty403;

import static org.firstinspires.ftc.twenty403.Setup.HardwareNames.FLMOTOR;
import static org.firstinspires.ftc.twenty403.Setup.HardwareNames.FRMOTOR;
import static org.firstinspires.ftc.twenty403.Setup.HardwareNames.RLMOTOR;
import static org.firstinspires.ftc.twenty403.Setup.HardwareNames.RRMOTOR;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;

import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.Encoder;
import com.pedropathing.ftc.localization.constants.DriveEncoderConstants;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants();

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
            .rightRearMotorDirection(DcMotorSimple.Direction.REVERSE);
    public static DriveEncoderConstants localizerConstants = new DriveEncoderConstants()
            .leftFrontMotorName(FLMOTOR)
            .leftRearMotorName(RLMOTOR)
            .rightFrontMotorName(FRMOTOR)
            .rightRearMotorName(RRMOTOR)
            .leftFrontEncoderDirection(Encoder.FORWARD)
            .leftRearEncoderDirection(Encoder.FORWARD)
            .rightFrontEncoderDirection(Encoder.REVERSE)
            .rightRearEncoderDirection(Encoder.REVERSE);

    public PathChain toPathChain(Path path){
        return new PathChain(path);
    }
    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .pathConstraints(pathConstraints)
                .driveEncoderLocalizer(localizerConstants)
                .mecanumDrivetrain(driveConstants)
                .build();
    }
}
