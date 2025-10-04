package org.firstinspires.ftc.sixteen750;

import static org.firstinspires.ftc.sixteen750.Setup.HardwareNames.FL_DRIVE_MOTOR;
import static org.firstinspires.ftc.sixteen750.Setup.HardwareNames.FR_DRIVE_MOTOR;
import static org.firstinspires.ftc.sixteen750.Setup.HardwareNames.OTOS;
import static org.firstinspires.ftc.sixteen750.Setup.HardwareNames.RL_DRIVE_MOTOR;
import static org.firstinspires.ftc.sixteen750.Setup.HardwareNames.RR_DRIVE_MOTOR;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
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
import com.technototes.path.geometry.ConfigurablePoseD;
import com.technototes.path.trajectorysequence.TrajectorySequence;
import com.technototes.path.trajectorysequence.TrajectorySequenceBuilder;
import java.util.function.Function;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Config
public class AutoConstants {

    // note these need to be tuned
    public static double botWeightKg = 9.33;
    public static double xvelocity = 0.0;
    public static double yvelocity = 0.0;
    public static double fowarddeceleration = 0.0;
    public static double lateraldeceleration = 0.0;
    public static double linearscalar = 0.0;
    public static double angularscalar = 0.0;
    public static Pose2d offset = new Pose2d(3,3,90);

    public static FollowerConstants followerConstants = new FollowerConstants()
            // tune these
            .mass(botWeightKg)
            .forwardZeroPowerAcceleration(fowarddeceleration)
            .lateralZeroPowerAcceleration(lateraldeceleration);

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);
    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)
            .leftFrontMotorName(FL_DRIVE_MOTOR)
            .leftRearMotorName(RL_DRIVE_MOTOR)
            .rightFrontMotorName(FR_DRIVE_MOTOR)
            .rightRearMotorName(RR_DRIVE_MOTOR)
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
