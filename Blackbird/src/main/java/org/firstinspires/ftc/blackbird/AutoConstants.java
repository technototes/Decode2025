package org.firstinspires.ftc.blackbird;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.Encoder;
import com.pedropathing.ftc.localization.constants.DriveEncoderConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.ftc.localization.constants.TwoWheelConstants;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.blackbird.Setup.HardwareNames;
import org.firstinspires.ftc.blackbird.helpers.CustomAdafruitIMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Configurable
public class AutoConstants {

    // note these need to be measured:
    public static double botWeightKg = 10.1;
    public static double robotLength = 17.5;
    public static double robotWidth = 11.5;

    // These come from Tuners:
    public static double xvelocity = 67.3;
    public static double yvelocity = 62.4;
    public static double fwdDeceleration = -42.8;
    public static double latDeceleration = -71.1;
    public static double centripetalScaling = 0.0005;

    // These are hand tuned to work how we want
    public static double brakingStrength = 1;
    public static double brakingStart = 1;
    public static PIDFCoefficients headingPIDF = new PIDFCoefficients(0.5, 0, 0.03, 0.03); //11-7 tuning i = 0.00055
    public static PIDFCoefficients second_headingPIDF = new PIDFCoefficients(0.5, 0.05, 0.03, 0);
    public static PIDFCoefficients translationPIDF = new PIDFCoefficients(0.07, 0, 0.009, 0.02); //11-7 tuning i = 0.00015

    // "Kalman filtering": T in this constructor is the % of the previous
    // derivative that should be used to calculate the derivative.
    // (D is "Derivative" in PIDF...)
    public static FilteredPIDFCoefficients drivePIDF = new FilteredPIDFCoefficients(
        0.04,
        0,
        0.003,
        0.6,
        0.03
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

    // @Configurable
    public static class DriveEncoderLocalizer {

        public static double fwdTicksToInches = 0.008;
        public static double strafeTicksToInches = -0.009;
        public static double turnTicksToInches = 0.018;

        public static DriveEncoderConstants get() {
            return new DriveEncoderConstants()
                .forwardTicksToInches(fwdTicksToInches)
                .strafeTicksToInches(strafeTicksToInches)
                .turnTicksToInches(turnTicksToInches)
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
    }

    // @Configurable
    public static class TwoWheelLocalizer {

        public static String forwardName = HardwareNames.ODO_FWDBACK;
        public static String strafeName = HardwareNames.ODO_STRAFE;
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

        public static TwoWheelConstants get() {
            TwoWheelConstants tc = new TwoWheelConstants()
                .forwardEncoder_HardwareMapName(forwardName)
                .strafeEncoder_HardwareMapName(strafeName)
                .forwardPodY(forwardPodYOffset)
                .strafePodX(strafePodXOffset)
                .forwardTicksToInches(forwardTicksToInches)
                .strafeTicksToInches(strafeTicksToInches)
                .forwardEncoderDirection(forwardReversed ? Encoder.REVERSE : Encoder.FORWARD)
                .strafeEncoderDirection(strafeReversed ? Encoder.REVERSE : Encoder.FORWARD);
            if (Setup.Connected.EXTERNAL_IMU) {
                tc = tc.customIMU(new CustomAdafruitIMU());
            } else {
                tc = tc
                    .IMU_HardwareMapName(Setup.HardwareNames.IMU)
                    .IMU_Orientation(new RevHubOrientationOnRobot(logoDir, usbDir));
            }
            return tc;
        }
    }

    @Configurable
    public static class PinpointLocalizer {

        public static double FORWARD_POD_Y_OFFSET = -1.5;
        public static double STRAFE_POD_X_OFFSET = 2.3;
        public static GoBildaPinpointDriver.EncoderDirection FORWARD_DIR =
            GoBildaPinpointDriver.EncoderDirection.FORWARD;
        public static GoBildaPinpointDriver.EncoderDirection STRAFE_DIR =
            GoBildaPinpointDriver.EncoderDirection.REVERSED;

        public static PinpointConstants get() {
            return new PinpointConstants()
                .hardwareMapName(HardwareNames.PINPOINT)
                .distanceUnit(DistanceUnit.INCH)
                .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
                .forwardEncoderDirection(FORWARD_DIR)
                .strafeEncoderDirection(STRAFE_DIR)
                .forwardPodY(FORWARD_POD_Y_OFFSET)
                .strafePodX(STRAFE_POD_X_OFFSET);
        }
    }

    public static FollowerConstants getFollowerConstants() {
        // tune these
        return new FollowerConstants()
            .mass(botWeightKg)
            .forwardZeroPowerAcceleration(fwdDeceleration)
            .lateralZeroPowerAcceleration(latDeceleration)
            .headingPIDFCoefficients(headingPIDF)
            .useSecondaryHeadingPIDF(true)
            .secondaryHeadingPIDFCoefficients(second_headingPIDF)
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

    public static Follower createFollower(HardwareMap hardwareMap) {
        Follower fol = new FollowerBuilder(getFollowerConstants(), hardwareMap)
            .pathConstraints(getPathConstraints())
            .mecanumDrivetrain(getDriveConstants())
            .pinpointLocalizer(PinpointLocalizer.get())
            .build();
        //        fol.setMaxPowerScaling(0.5);
        return fol;
    }

    //New testing constants for this year's game
    public static Pose scorePose = new Pose(0.0, 0.0, 0.0);
    public static Pose pickup1Pose = new Pose(0.0, 0.0, 0.0);
}
