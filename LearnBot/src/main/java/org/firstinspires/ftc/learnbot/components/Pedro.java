package org.firstinspires.ftc.learnbot.components;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.Encoder;
import com.pedropathing.ftc.localization.constants.DriveEncoderConstants;
import com.pedropathing.ftc.localization.constants.OTOSConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.ftc.localization.constants.TwoWheelConstants;
import com.pedropathing.geometry.BezierPoint;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.technototes.library.command.Command;
import com.technototes.library.control.Stick;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.Subsystem;
import com.technototes.library.subsystem.TargetAcquisition;
import com.technototes.library.util.Alliance;
import com.technototes.library.util.MathUtils;
import com.technototes.library.util.PIDFController;
import java.util.Locale;
import java.util.function.DoubleSupplier;
import org.firstinspires.ftc.learnbot.Setup;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class PedroDrivebase {

    @Configurable
    public static class Config {

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
        public static com.pedropathing.control.PIDFCoefficients translationPID =
            new com.pedropathing.control.PIDFCoefficients(0.08, 0.000005, 0.008, 0.02);
        public static com.pedropathing.control.PIDFCoefficients headingPID =
            new com.pedropathing.control.PIDFCoefficients(0.9, 0.005, 0.05, 0.02);
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
                .leftFrontMotorName(Setup.HardwareNames.FLMOTOR)
                .leftRearMotorName(Setup.HardwareNames.RLMOTOR)
                .rightFrontMotorName(Setup.HardwareNames.FRMOTOR)
                .rightRearMotorName(Setup.HardwareNames.RRMOTOR)
                .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
                .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
                .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
                .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
                .xVelocity(xVelocity)
                .yVelocity(yVelocity);
        }

        public static class Localizer {

            @Configurable
            public static class OTOSConfig {

                public static double linearScalar = 1.0;
                public static double angularScalar = 1.0;
                public static SparkFunOTOS.Pose2D DEVICE_POSITION = new SparkFunOTOS.Pose2D(
                    -60 / 25.4,
                    35 / 25.4,
                    0
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

            @Configurable
            public static class TwoWheelConfig {

                public static String ForwardPodName = "odofb";
                public static String StrafePodName = "odostrafe";
                public static String IMUName = "imu";
                public static RevHubOrientationOnRobot orientation = new RevHubOrientationOnRobot(
                    RevHubOrientationOnRobot.LogoFacingDirection.FORWARD,
                    RevHubOrientationOnRobot.UsbFacingDirection.UP
                );
                public static double ForwardPodDirection = Encoder.FORWARD;
                public static double StrafePodDirection = Encoder.REVERSE;
                public static double ForwardPodTicksToInches = 2000 / ((Math.PI * 32) / 25.4);
                public static double StrafePodTicksToInches = 2000 / ((Math.PI * 32) / 25.4);
                public static double ForwardPodY = -2.5;
                public static double StrafePodX = 0.25;
            }

            public enum LocalizerSelection {
                USE_MOTORS,
                USE_OTOS,
                USE_PINPOINT,
                USE_TWO_WHEEL,
            }

            public static LocalizerSelection WhichLocalizer = LocalizerSelection.USE_TWO_WHEEL;

            public static OTOSConstants getOtosLocalizerConstants() {
                return new OTOSConstants()
                    .hardwareMapName(Setup.HardwareNames.OTOS)
                    .linearUnit(DistanceUnit.INCH)
                    .angleUnit(AngleUnit.RADIANS)
                    .linearScalar(OTOSConfig.linearScalar)
                    .angularScalar(OTOSConfig.angularScalar)
                    .offset(OTOSConfig.DEVICE_POSITION);
                // need to tune for OTOS localization
            }

            public static DriveEncoderConstants getDriveEncoderConstants() {
                return new DriveEncoderConstants()
                    .leftFrontMotorName(Setup.HardwareNames.FLMOTOR)
                    .leftRearMotorName(Setup.HardwareNames.RLMOTOR)
                    .rightFrontMotorName(Setup.HardwareNames.FRMOTOR)
                    .rightRearMotorName(Setup.HardwareNames.RRMOTOR)
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
                    .hardwareMapName(Setup.HardwareNames.PINPOINT)
                    .distanceUnit(DistanceUnit.INCH)
                    .forwardPodY(PinpointConfig.ForwardPodY)
                    .strafePodX(PinpointConfig.StrafePodX)
                    .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
                    .forwardEncoderDirection(PinpointConfig.ForwardDirection)
                    .strafeEncoderDirection(PinpointConfig.StrafeDirection);
            }

            public static TwoWheelConstants getTwoWheelConstants() {
                TwoWheelConstants twc = new TwoWheelConstants()
                    .forwardEncoder_HardwareMapName(TwoWheelConfig.ForwardPodName)
                    .forwardEncoderDirection(TwoWheelConfig.ForwardPodDirection)
                    .forwardTicksToInches(TwoWheelConfig.ForwardPodTicksToInches)
                    .forwardPodY(TwoWheelConfig.ForwardPodY)
                    .strafeEncoder_HardwareMapName(TwoWheelConfig.StrafePodName)
                    .strafeEncoderDirection(TwoWheelConfig.StrafePodDirection)
                    .strafeTicksToInches(TwoWheelConfig.StrafePodTicksToInches)
                    .strafePodX(TwoWheelConfig.StrafePodX);
                // TODO: Add support for a custom IMU:
                // if (UseCustomIMU) {
                //    return getTwoWheelConstants().customIMU(...);
                // }
                return twc
                    .IMU_HardwareMapName(TwoWheelConfig.IMUName)
                    .IMU_Orientation(TwoWheelConfig.orientation);
            }
        }
    }

    public static class Commands {

        protected static Component self;

        public static Command DrivePower(double p1, double p2, double p3, double p4) {
            return new DrivePowerImpl(p1, p2, p3, p4);
        }

        public static Command DrivePower(double pow) {
            return new DrivePowerImpl(pow, pow, pow, pow);
        }

        public static Command JoystickDrive(Stick xyStick, Stick rotStick) {
            return new JoystickImpl(xyStick, rotStick);
        }

        public static Command FollowPath(PathChain p) {
            return new FollowPathImpl(p);
        }

        public static Command FollowPath(Pose startPose, PathChain p) {
            return new FollowPathImpl(startPose, p);
        }

        public static Command FollowPath(PathChain p, boolean readCurPose) {
            return new FollowPathImpl(p, readCurPose);
        }

        protected static class DrivePowerImpl implements Command {

            double[] p;

            public DrivePowerImpl(double p1, double p2, double p3, double p4) {
                p = new double[4];
                p[0] = p1;
                p[1] = p2;
                p[2] = p3;
                p[3] = p4;
            }

            @Override
            public void execute() {
                self.follower.drivetrain.runDrive(p);
            }
        }

        protected static class JoystickImpl implements Command {

            // The sticks (probably each are CommandAxis suppliers)
            // Note that the stick values returned are oriented like this:
            // Up is a negative value, down is a positive value.
            // Left is a negative value, right is a positive value.
            DoubleSupplier x, y, r;

            public JoystickImpl(Stick xyStick, Stick rotStick) {
                x = xyStick.getXSupplier();
                y = xyStick.getYSupplier();
                r = rotStick.getXSupplier();
                addRequirements(self);
            }

            @Override
            public void initialize() {
                self.StartTele();
            }

            @Override
            public void execute() {
                // Read the stick values, and pass them to the drive base.
                // We invert the signs because both up and left are negative, which is opposite Pedro.
                // The drivebase can do all the filtering & drive mode shenanigans it wants. We're just
                // here to read the joysticks and send the values to the drivebase...
                double fwdVal = -MathUtils.deadZoneScale(y.getAsDouble(), Config.STICK_DEAD_ZONE);
                double strafeVal = -MathUtils.deadZoneScale(
                    x.getAsDouble(),
                    Config.STICK_DEAD_ZONE
                );
                double rotVal = -MathUtils.deadZoneScale(r.getAsDouble(), Config.STICK_DEAD_ZONE);
                self.RegisterJoystickRead(fwdVal, strafeVal, rotVal);
            }

            @Override
            public boolean isFinished() {
                return false;
            }
        }

        protected static class FollowPathImpl implements Command {

            public PathChain pathChain;
            public Pose begin;
            public boolean currentPose;

            public FollowPathImpl(PathChain p) {
                pathChain = p;
            }

            public FollowPathImpl(Pose startPose, PathChain p) {
                pathChain = p;
                currentPose = true;
                begin = startPose;
            }

            public FollowPathImpl(PathChain p, boolean currPose) {
                pathChain = p;
                currentPose = currPose;
                begin = null;
            }

            @Override
            public void initialize() {
                if (currentPose) {
                    self.follower.setStartingPose(begin == null ? self.follower.getPose() : begin);
                }
                self.follower.followPath(pathChain);
            }

            @Override
            public boolean isFinished() {
                return !self.follower.isBusy();
            }

            @Override
            public void execute() {
                self.follower.update();
            }
        }
    }

    public static Follower createFollower(HardwareMap hardwareMap) {
        FollowerBuilder fb = new FollowerBuilder(Config.getFollowerConstants(), hardwareMap)
            .pathConstraints(Config.getPathConstraints())
            .mecanumDrivetrain(Config.getDriveConstants());
        switch (Config.Localizer.WhichLocalizer) {
            case USE_OTOS:
                if (Setup.Connected.OTOS) {
                    fb = fb.OTOSLocalizer(Config.Localizer.getOtosLocalizerConstants());
                }
                break;
            case USE_MOTORS:
                fb = fb.driveEncoderLocalizer(Config.Localizer.getDriveEncoderConstants());
                break;
            case USE_PINPOINT:
                if (Setup.Connected.PINPOINT) {
                    fb = fb.pinpointLocalizer(Config.Localizer.getPinpointConstants());
                }
                break;
            case USE_TWO_WHEEL:
                fb = fb.twoWheelLocalizer(Config.Localizer.getTwoWheelConstants());
                break;
        }
        Follower f = fb.build();
        f.setMaxPowerScaling(Config.AUTO_SPEED);
        return f;
    }

    /* Recall, the Pedro Path coordinate system:
                 [Refs/score table]
+-------------------------------------------------+
|(0,144)             ^ +y                (144,144)|
|                                                 |
|                    90 deg                       |
|                                                 |
|                                                 |
| <== -x 180 deg     (72,72)        0 deg +x ==>  |
|                                                 |
|                                                 |
| Red Drive Team                  Blue Drive Team |
|                    270 deg                      |
|(0,0)                v -y                 (144,0)|
+-------------------------------------------------+
                   [Audience]
 */

    @Configurable
    public static class Component implements Subsystem, Loggable {

        private static final double HalfPi = Math.PI * 0.5;
        private static final double TwoPi = Math.PI * 2;

        // Encapsulated to allow easy "previous driving style" tracking
        private static class DrivingStyle {

            public DrivingStyle() {
                perspective = DrivingPerspective.FieldCentric;
                rotation = RotationMode.Free;
                translation = TranslationMode.Free;
                rotationSpeed = Config.NORMAL_TURN;
                translationSpeed = Config.NORMAL_SPEED;
            }

            public DrivingStyle(DrivingStyle other) {
                perspective = other.perspective;
                rotation = other.rotation;
                translation = other.translation;
                rotationSpeed = other.rotationSpeed;
                translationSpeed = other.translationSpeed;
            }

            public DrivingPerspective perspective;
            public RotationMode rotation;
            public TranslationMode translation;
            public double rotationSpeed;
            public double translationSpeed;
        }

        // This concerns how the bot is rotating (or not)
        public enum RotationMode {
            Free,
            Snap,
            Hold, // Hold the current heading
            Tangential, // Aim toward the translational direction
            Bidirectional, // Like tangential, but will turn to the closest tangential direction
            Vision, // Use Vision to find the target & aim toward it
            Target_NYI, // The controller is used to specify a desired heading
            None,
        }

        // This concerns how the bot is moving around the field
        public enum TranslationMode {
            Free,
            Square,
            Hold,
            Vision, // Drive toward the target using vision
            Target_NYI, // Drive toward an external target (that is movable using the controller)
            None,
        }

        public enum DrivingPerspective {
            RobotCentric,
            FieldCentric,
        }

        // The PedroPath follower, to let us actually make the bot move:
        public Follower follower;
        // The vision subsystem, for vision-based driving stuff
        public TargetAcquisition targetAcquisition;

        // The direction of the 3 axes for manual control: Should be updated by a joystick command
        public double strafe, forward, rotation;

        // These numbers seem a little weird, but the target value is *radians* and the output is
        // 'stick distance'.
        public static PIDFCoefficients pidf = new PIDFCoefficients(0.9, 0.3, 0.05, 0);
        // This is the limit above which we don't consider "error" for the turning PID controller.
        // 4.5 degree 'range' for the error to start accumulating
        public static double WIND_UP_LIMIT = HalfPi * 0.05;

        // These are here to let me change the PIDF on they fly, rather than forcing me to constantly
        // restart the opmode.
        private double P = 0;
        private double I = 0;
        private double D = 0;
        private double F = 0;

        private PIDFController turningPIDF = new PIDFController(pidf);
        private boolean turnPidStarted = false;

        public double rotationTransform(double r) {
            // This gives you more sensitivity at the low end:
            // .25^2 = .0625, .5^2 = .25, .75^2 = .5625, etc...
            return Math.copySign(r * r * driveStyle.rotationSpeed, r);
        }

        // The offset heading for field-relative controls
        double headingOffsetRadians;
        double targetHeading;
        double directedHeading; // used for Target-based rotation mode
        ElapsedTime lastReading; // also used for Target-based rotation mode
        double curHeading;

        // used to keep the directions straight
        Alliance alliance;

        // Used to keep track of the previous drive style when using the "StayPut" operation
        private DrivingStyle prevDriveStyle;

        public double[] snapRadians;

        DrivingStyle driveStyle;

        Pose holdPose;
        boolean started;

        public RotationMode getRotationMode() {
            return driveStyle.rotation;
        }

        public void setRotationalMode(RotationMode r) {
            driveStyle.rotation = r;
        }

        public TranslationMode getTranslationMode() {
            return driveStyle.translation;
        }

        public void setTranslationMode(TranslationMode t) {
            driveStyle.translation = t;
        }

        public DrivingPerspective getPerspective() {
            return driveStyle.perspective;
        }

        private double baseHeadingOffset() {
            return alliance == Alliance.BLUE ? Math.PI : 0;
        }

        private double getHeadingOffsetRadians() {
            return driveStyle.perspective == DrivingPerspective.FieldCentric
                ? headingOffsetRadians
                : 0;
        }

        public Component(Follower f, TargetAcquisition viz, Alliance all) {
            Commands.self = this;
            started = false;
            follower = f;
            targetAcquisition = viz;
            alliance = all;
            headingOffsetRadians = baseHeadingOffset();
            holdPose = null;
            forward = 0;
            strafe = 0;
            rotation = 0;
            targetHeading = 0;
            driveStyle = new DrivingStyle();
            directedHeading = Double.NaN;
            lastReading = new ElapsedTime();
            // Snap to these angles
            // You need the final point there for "wrap-around" to work properly
            snapRadians = new double[] { -Math.PI, -HalfPi, 0, HalfPi, Math.PI };
            SetFieldCentricMode();
            SetFreeDriving();
            SetNormalSpeed();
        }

        public Component(Follower f, Alliance all) {
            this(f, null, all);
        }

        public Component(Follower f, TargetAcquisition viz) {
            this(f, viz, Alliance.NONE);
        }

        public Component(Follower f) {
            this(f, null, Alliance.NONE);
        }

        // Command to start autonomous driving)
        // Command to start teleop driving
        public void StartTele() {
            started = true;
            follower.startTeleOpDrive();
        }

        // Methods to bind to buttons (Commands)
        public void ResetGyro() {
            headingOffsetRadians = MathUtils.normalizeRadians(
                follower.getHeading() + baseHeadingOffset()
            );
        }

        public void SetSnailSpeed() {
            follower.setMaxPowerScaling(Config.SNAIL_SPEED);
            driveStyle.translationSpeed = Config.SNAIL_SPEED;
            driveStyle.rotationSpeed = Config.SNAIL_TURN;
        }

        public void SetNormalSpeed() {
            follower.setMaxPowerScaling(Config.NORMAL_SPEED);
            driveStyle.translationSpeed = Config.NORMAL_SPEED;
            driveStyle.rotationSpeed = Config.NORMAL_TURN;
        }

        public void SetTurboSpeed() {
            follower.setMaxPowerScaling(Config.TURBO_SPEED);
            driveStyle.translationSpeed = Config.TURBO_SPEED;
            driveStyle.rotationSpeed = Config.TURBO_TURN;
        }

        private void switchDrivingMode(TranslationMode x, RotationMode r) {
            TranslationMode px = driveStyle.translation;
            RotationMode pr = driveStyle.rotation;
            if (px == x && pr == r) {
                return;
            }
            driveStyle.translation = x;
            driveStyle.rotation = r;
            if (px == TranslationMode.Hold && x != TranslationMode.Hold) {
                // If we're going to stop holding a position, start Tele back up
                StartTele();
            } else if (px != TranslationMode.Hold && x == TranslationMode.Hold) {
                // If we've switched *to* holding a pose, start the follower
                holdPose = follower.getPose();
                follower.holdPoint(new BezierPoint(holdPose), holdPose.getHeading(), false);
            } else if (pr != RotationMode.Hold && driveStyle.rotation == RotationMode.Hold) {
                // If we're transitioning to a Rotational hold, just set the pos in the holdPose
                holdPose = follower.getPose();
            }
            if (r == RotationMode.Target_NYI) {
                directedHeading = Double.NaN;
            }
            turnPidStarted = false;
        }

        public void SetFreeDriving() {
            switchDrivingMode(TranslationMode.Free, RotationMode.Free);
        }

        public void SetFreeRotation() {
            switchDrivingMode(getTranslationMode(), RotationMode.Free);
        }

        // The list of rotation points (in degrees) you want to snap to
        public void SetSnapRotation(double... degrees) {
            snapRadians = new double[degrees.length + 1];
            double lowest = TwoPi;
            for (int i = 0; i < degrees.length; i++) {
                snapRadians[i] = MathUtils.posNegRadians(Math.toRadians(degrees[i]));
                if (snapRadians[i] < lowest) {
                    lowest = snapRadians[i];
                }
            }
            // For wrap-around to work properly, we'll add the first location, but past the wrap-around
            // point of the circle. So, if you want to snap to 15 and -170, we'll append 190 so that 175
            // will wind up going to 190 (which then normalizes back to -170). Neat, huh?
            snapRadians[degrees.length] = lowest + TwoPi;
            switchDrivingMode(getTranslationMode(), RotationMode.Snap);
        }

        public void SetSnapRotation() {
            switchDrivingMode(getTranslationMode(), RotationMode.Snap);
        }

        public void SetTangentRotation() {
            switchDrivingMode(getTranslationMode(), RotationMode.Tangential);
        }

        public void SetBidirectionalRotation() {
            switchDrivingMode(getTranslationMode(), RotationMode.Bidirectional);
        }

        public void SetVisionRotation() {
            switchDrivingMode(getTranslationMode(), RotationMode.Vision);
        }

        public void SetHoldRotation() {
            switchDrivingMode(getTranslationMode(), RotationMode.Hold);
        }

        public void HoldCurrentPosition() {
            switchDrivingMode(TranslationMode.Hold, RotationMode.Hold);
        }

        public void SetFreeMotion() {
            switchDrivingMode(TranslationMode.Free, getRotationMode());
        }

        public void SetSquareMotion() {
            switchDrivingMode(TranslationMode.Square, getRotationMode());
        }

        public void SetVisionDriving() {
            prevDriveStyle = new DrivingStyle(driveStyle);
            switchDrivingMode(TranslationMode.Vision, RotationMode.Vision);
        }

        public void SetRobotCentricMode() {
            driveStyle.perspective = DrivingPerspective.RobotCentric;
        }

        public void SetFieldCentricMode() {
            driveStyle.perspective = DrivingPerspective.FieldCentric;
        }

        // This, when well tuned, should allow the driver to ignore minor bot-bumps and the like.
        public void SetTargetBasedRotation() {
            driveStyle.rotation = RotationMode.Target_NYI;
        }

        // This, when well tuned, should allow the programmers to ignore frictional differences between
        // wheels on the drivebase...
        public void SetTargetBasedMotion() {
            driveStyle.translation = TranslationMode.Target_NYI;
        }

        // Some just slightly more complex commands:
        public void StayPut() {
            if (prevDriveStyle == null) {
                prevDriveStyle = driveStyle;
            }
            driveStyle = new DrivingStyle(prevDriveStyle);
            HoldCurrentPosition();
            SetTurboSpeed();
        }

        public void ResumeDriving() {
            if (prevDriveStyle == null) {
                SetFreeDriving();
                SetNormalSpeed();
            } else {
                driveStyle = prevDriveStyle;
                prevDriveStyle = null;
                StartTele();
            }
        }

        public void RegisterJoystickRead(double f, double s, double r) {
            this.strafe = s;
            this.forward = f;
            this.rotation = r;
        }

        @Override
        public void periodic() {
            if (pidf.p != P || pidf.i != I || pidf.d != D || pidf.f != F) {
                // Someone changed the PIDF on the panels: Updated it in realtime...
                turningPIDF = new PIDFController(pidf);
                P = pidf.p;
                I = pidf.i;
                D = pidf.d;
                F = pidf.f;
            }
            // If subsystem is busy it is running a path, just ignore the stick.
            ShowDriveInfo();
            if (follower == null) {
                return;
            }
            if (follower.isBusy()) {
                follower.update();
                drvVec = "busy";
                return;
            }
            if (!started) {
                return;
            }

            if (driveStyle.translation == TranslationMode.Square) {
                // for square driving, we only use one of the two translation directions:
                if (Math.abs(forward) > Math.abs(strafe)) {
                    strafe = 0;
                } else {
                    forward = 0;
                }
            }
            double rot = getRotation();
            boolean botCentric = driveStyle.perspective == DrivingPerspective.RobotCentric;
            if (driveStyle.translation == TranslationMode.Vision) {
                // For vision *translation* we will move forward or backward to maintain a 'size' of the
                // target. The 'a' part of the result is the percentage of the total image that the
                // target is filling, so the closer you are, the larger the area of the image. It's
                // kinda dopey, but works just fine...
                double distance = targetAcquisition.getDistance();
                if (distance >= 0) {
                    forward = distance;
                    botCentric = true;
                }
            }
            ShowDriveVectors(forward, strafe, rot, getHeadingOffsetRadians());
            follower.setTeleOpDrive(forward, strafe, rot, botCentric, getHeadingOffsetRadians());
            follower.update();
        }

        double getRotation() {
            // Negative, because pushing left is negative, but that is a positive change in Pedro's
            // coordinate system.
            curHeading = MathUtils.posNegRadians(follower.getHeading() - getHeadingOffsetRadians());
            switch (driveStyle.rotation) {
                case Target_NYI:
                // The controller is used to specify a desired heading: NYI
                // To implement this, we need to know a "degrees/sec" change for each speed.
                // From basic observations: slow: 90 deg/sec; normal: 180 deg/sec, fast: 300 deg/sec
                // Once we have that, we need to have a timer between updates, as well as a decent
                // PIDF for the heading, so that we can get to the desired location quickly
                // and accurately. If the rotation stick goes to zero, we probably just hold the
                // current location.
                case Free:
                    return rotationTransform(rotation);
                case Snap:
                    // Angle-focused driving styles override target-based driving mode
                    targetHeading = MathUtils.closestTo(curHeading, snapRadians);
                    break;
                case Hold:
                    // Hold the current heading
                    if (driveStyle.translation != TranslationMode.Hold && holdPose != null) {
                        targetHeading = MathUtils.posNegRadians(
                            holdPose.getHeading() - getHeadingOffsetRadians()
                        );
                    } else {
                        // This is weird: We have a rotational hold, but not a translational hold, and
                        // no holdPose. This is probably an error
                        targetHeading = 0;
                    }
                    break;
                case Tangential: // Aim toward the translational direction
                    // Tangential is an angle-focused driving style, but the heading
                    // is strictly in the direction of the stick. Logically, this is an attempt to
                    // eliminate "actual" strafing: The robot should be oriented to drive forward in the
                    // direction of the stick
                    if (Math.abs(strafe) > 0 || Math.abs(forward) > 0) {
                        targetHeading = MathUtils.posNegRadians(Math.atan2(strafe, forward));
                    } else {
                        return 0;
                    }
                    break;
                case Bidirectional: // Align the bot to the translational direction
                    // Bidirectional is an angle-focused driving style, but the heading is strictly in
                    // the direction of the stick, but with a twist: If you flip the direction you're
                    // driving, the bot will drive in reverse, rather than fully rotate.
                    if (Math.abs(strafe) > 0 || Math.abs(forward) > 0) {
                        targetHeading = MathUtils.posNegRadians(Math.atan2(strafe, forward));
                        // targetHeading should be within 90 degrees of curHeading, if it's not, flip it
                        if (
                            Math.abs(MathUtils.posNegRadians(targetHeading - curHeading)) > HalfPi
                        ) {
                            targetHeading = MathUtils.posNegRadians(targetHeading + Math.PI);
                        }
                    } else {
                        return 0;
                    }
                    break;
                case Vision: // Use Vision to find the target & aim toward it
                    double x = (targetAcquisition == null)
                        ? Double.NaN
                        : targetAcquisition.getHorizontalPosition();
                    if (!Double.isNaN((x))) {
                        targetHeading = MathUtils.posNegRadians(curHeading + Math.toRadians(x));
                    } else {
                        return rotationTransform(rotation);
                    }
                    break;
                default:
                    return 0;
            }
            // Use the turn PID controller to get to the target heading
            if (!turnPidStarted) {
                turningPIDF.reset();
                turnPidStarted = true;
            }
            if (Math.abs(MathUtils.posNegRadians(targetHeading - curHeading)) > WIND_UP_LIMIT) {
                // The goal is to prevent *massive* early error from making the I value useless.
                turningPIDF.reset();
            }
            turningPIDF.setTarget(targetHeading);
            // We need to handle a weird case: When the current/target heading values are close
            // to the angle wrap-around point, we should switch one of them from -180 to 180 to 0-360
            // or -360 - 0 instead. This minimizes the error. Without this, when your target heading is
            // near that wrap-around location, you'll get some serious bot jiggling, without an obvious
            // reason.
            if (Math.abs(curHeading - targetHeading) > Math.PI) {
                curHeading += Math.signum(targetHeading) * TwoPi;
            }
            return turningPIDF.update(curHeading);
        }

        // Everything below here is just for displaying/diagnostics

        @Log(name = "DrvMode")
        public static String drvMode = "";

        @Log(name = "DrvVec")
        public static String drvVec = "";

        @Log(name = "Pose")
        public static String drvLoc = "";

        private void ShowDriveInfo() {
            switch (driveStyle.rotation) {
                case Target_NYI:
                    drvMode = "rTgt";
                    break;
                case Free:
                    drvMode = "rFre";
                    break;
                case Snap:
                    drvMode = "rSnp";
                    break;
                case Hold:
                    drvMode = "rHld";
                    break;
                case Tangential:
                    drvMode = "rTan";
                    break;
                case Bidirectional:
                    drvMode = "rBid";
                    break;
                case Vision:
                    drvMode = "rViz";
                    break;
                default:
                    drvMode = "rUnk";
                    break;
            }
            switch (driveStyle.translation) {
                case Free:
                    drvMode += "xFre";
                    break;
                case Square:
                    drvMode += "xSqu";
                    break;
                case Hold:
                    drvMode += "xHld";
                    break;
                case Vision:
                    drvMode += "xViz";
                    break;
                default:
                    drvMode += "xUnk";
                    break;
            }
            switch (driveStyle.perspective) {
                case RobotCentric:
                    drvMode += ":Bot";
                    break;
                case FieldCentric:
                    drvMode += ":Fld";
                    break;
                default:
                    drvMode += ":[?]";
                    break;
            }
            drvMode += String.format(Locale.ENGLISH, " Max:%.2f", driveStyle.translationSpeed);
            Pose curPose = follower.getPose();
            drvLoc = String.format(
                Locale.ENGLISH,
                "X:%.2f Y:%.2f H:%.1f° T:%.1f°",
                curPose.getX(),
                curPose.getY(),
                Math.toDegrees(curPose.getHeading()),
                Math.toDegrees(targetHeading)
            );
        }

        private void ShowDriveVectors(double fwd, double strafe, double rot, double offset) {
            drvVec = String.format(
                Locale.ENGLISH,
                "f %.2f s %.2f r %.2f@%.1f° [%.1f°]",
                fwd,
                strafe,
                rot,
                Math.toDegrees(curHeading),
                Math.toDegrees(MathUtils.posNegRadians(offset))
            );
        }
    }
}
