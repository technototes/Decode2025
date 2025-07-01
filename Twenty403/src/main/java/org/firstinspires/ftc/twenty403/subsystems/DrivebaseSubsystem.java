package org.firstinspires.ftc.twenty403.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.localization.TwoTrackingWheelLocalizer;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.technototes.library.hardware.motor.EncodedMotor;
import com.technototes.library.hardware.sensor.IGyro;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.LogConfig;
import com.technototes.library.logger.Loggable;
import com.technototes.path.subsystem.MecanumConstants;
import com.technototes.path.subsystem.PathingMecanumDrivebaseSubsystem;
import java.util.function.Supplier;
import org.firstinspires.ftc.twenty403.helpers.HeadingHelper;

public class DrivebaseSubsystem
    extends PathingMecanumDrivebaseSubsystem
    implements Supplier<Pose2d>, Loggable {

    // Notes from Kevin:
    // The 5203 motors when direct driven
    // move about 63 inches forward and is measured as roughly 3000 ticks on the encoders

    @Config
    public abstract static class DriveConstants implements MecanumConstants {

        public static double SLOW_MOTOR_SPEED = 0.4;
        public static double SLOW_ROTATION_SCALE = 0.2;
        public static double NORMAL_MOTOR_SPEED = 1.0;
        public static double NORMAL_ROTATION_SCALE = 0.3; //too big, make it smaller to slow down rotation
        public static double TRIGGER_THRESHOLD = 0.6;

        @TicksPerRev
        public static final double TICKS_PER_REV = 384.5; // From Gobilda Specs

        @MaxRPM
        public static final double MAX_RPM = 435; // From Gobilda Specs

        public static double MAX_TICKS_PER_SEC = (TICKS_PER_REV * MAX_RPM) / 60.0;

        @UseDriveEncoder
        public static final boolean RUN_USING_ENCODER = true;

        @MotorVeloPID
        public static PIDFCoefficients MOTOR_VELO_PID = new PIDFCoefficients(
            35,
            0,
            12,
            0
            // 0   MecanumConstants.getMotorVelocityF((MAX_RPM / 60) * TICKS_PER_REV)
        );

        @WheelRadius
        public static double WHEEL_RADIUS = 1.875; // in

        @GearRatio
        public static double GEAR_RATIO = 1.0; // 2021: / 19.2; // output (wheel) speed / input (motor) speed

        @TrackWidth
        public static double TRACK_WIDTH = 15; // 2021: 10; // in (side to side)

        @WheelBase
        public static double WHEEL_BASE = 14; // in (front to back)

        @KV
        public static double kV =
            1.0 / MecanumConstants.rpmToVelocity(MAX_RPM, WHEEL_RADIUS, GEAR_RATIO);

        @KA
        public static double kA = 0;

        @KStatic
        public static double kStatic = 0;

        // This was 60, which was too fast. Things slid around a lot.
        @MaxVelo
        public static double MAX_VEL = 64;

        // This was 35, which also felt a bit too fast. The bot controls more smoothly now
        @MaxAccel
        public static double MAX_ACCEL = 20;

        // This was 180 degrees
        @MaxAngleVelo
        public static double MAX_ANG_VEL = Math.toRadians(90); // was 180, LRR says 299.4658071428571

        // This was 90 degrees
        @MaxAngleAccel
        public static double MAX_ANG_ACCEL = Math.toRadians(30); // wa 90, LRR says 299.4658071428571

        @TransPID
        public static PIDCoefficients TRANSLATIONAL_PID = new PIDCoefficients(8, 0, 0);

        @HeadPID
        public static PIDCoefficients HEADING_PID = new PIDCoefficients(8, 0, 0);

        @LateralMult
        public static double LATERAL_MULTIPLIER = 1.00; // Lateral position is off by 14%

        @VXWeight
        public static double VX_WEIGHT = 1;

        @VYWeight
        public static double VY_WEIGHT = 1;

        @OmegaWeight
        public static double OMEGA_WEIGHT = 1;

        @PoseLimit
        public static int POSE_HISTORY_LIMIT = 100;

        public static double AFR_SCALE = 1;
        public static double AFL_SCALE = 1;
        public static double ARR_SCALE = 1;
        public static double ARL_SCALE = 1;

        public static double encoderTicksToInches(double ticks) {
            return (WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks) / TICKS_PER_REV;
        }

        public static double rpmToVelocity(double rpm) {
            return (rpm * GEAR_RATIO * 2 * Math.PI * WHEEL_RADIUS) / 60.0;
        }

        public static double getMotorVelocityF(double ticksPerSecond) {
            // see https://docs.google.com/document/d/1tyWrXDfMidwYyP_5H4mZyVgaEswhOC35gvdmP-V-5hA/edit#heading=h.61g9ixenznbx
            return 32767 / ticksPerSecond;
        }
    }

    private static final boolean ENABLE_POSE_DIAGNOSTICS = true;

    @Log(name = "Pose2d: ")
    public String poseDisplay = ENABLE_POSE_DIAGNOSTICS ? "" : null;

    //@Log.Number(name = "FL")
    public EncodedMotor<DcMotorEx> fl2;

    //@Log.Number(name = "FR")
    public EncodedMotor<DcMotorEx> fr2;

    //@Log.Number(name = "RL")
    public EncodedMotor<DcMotorEx> rl2;

    //@Log.Number(name = "RR")
    public EncodedMotor<DcMotorEx> rr2;

    //    @Log(name = "Turbo")
    public boolean Turbo = false;

    //    @Log(name = "Snail")
    public boolean Snail = false;

    @LogConfig.Run(duringRun = true, duringInit = true)
    @Log(name = "heading")
    public double heading;

    public DrivebaseSubsystem(
        EncodedMotor<DcMotorEx> fl,
        EncodedMotor<DcMotorEx> fr,
        EncodedMotor<DcMotorEx> rl,
        EncodedMotor<DcMotorEx> rr,
        IGyro i,
        TwoTrackingWheelLocalizer l
    ) {
        // The localizer is not quite working. Bot drives a little crazy
        super(fl, fr, rl, rr, i, () -> DriveConstants.class, l);
        fl2 = fl;
        fr2 = fr;
        rl2 = rl;
        rr2 = rr;
        speed = DriveConstants.SLOW_MOTOR_SPEED;
        // This is already handled in the parent class constructor (super)
        // setLocalizer(l);
        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        fl.setDirection(DcMotorSimple.Direction.FORWARD);
        rl.setDirection(DcMotorSimple.Direction.FORWARD);
        rr.setDirection(DcMotorSimple.Direction.REVERSE);
        fr.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    @Override
    public Pose2d get() {
        return getPoseEstimate();
    }

    public void saveHeading() {
        HeadingHelper.saveHeading(get().getX(), get().getY(), gyro.getHeading());
    }

    @Override
    public void periodic() {
        if (ENABLE_POSE_DIAGNOSTICS) {
            updatePoseEstimate();
            Pose2d pose = getPoseEstimate();
            Pose2d poseVelocity = getPoseVelocity();
            poseDisplay =
                pose.toString() +
                " : " +
                (poseVelocity != null ? poseVelocity.toString() : "<null>");
        }
        heading = gyro.getHeading();
    }

    // Velocity driving, in the hopes that the bot with drive straight ;)
    @Override
    public void setMotorPowers(double lfv, double lrv, double rrv, double rfv) {
        // TODO: Use the stick position to determine how to scale these values
        // in Turbo mode (If the robot is driving in a straight line, the values are
        // going to max out at sqrt(2)/2, rather than: We can go faster, but we don't
        // *always* want to scale faster, only when we're it turbo mode, and when one (or more)
        // of the control sticks are at their limit
        double maxlfvlrv = Math.max(Math.abs(lfv), Math.abs(lrv));
        double maxrfvrrv = Math.max(Math.abs(rfv), Math.abs(rrv));
        double maxall = Math.max(maxlfvlrv, maxrfvrrv);
        if (isSnailMode()) {
            maxall = 1.0 / DriveConstants.SLOW_MOTOR_SPEED;
        } else if (isNormalMode()) {
            maxall = 1.0 / DriveConstants.NORMAL_MOTOR_SPEED;
        }
        leftFront.setVelocity(
            (lfv * DriveConstants.MAX_TICKS_PER_SEC * DriveConstants.AFL_SCALE) / maxall
        );
        leftRear.setVelocity(
            (lrv * DriveConstants.MAX_TICKS_PER_SEC * DriveConstants.ARL_SCALE) / maxall
        );
        rightRear.setVelocity(
            (rrv * DriveConstants.MAX_TICKS_PER_SEC * DriveConstants.ARR_SCALE) / maxall
        );
        rightFront.setVelocity(
            (rfv * DriveConstants.MAX_TICKS_PER_SEC * DriveConstants.AFR_SCALE) / maxall
        );
    }

    public void setMotorPark(double lfv, double lrv, double rrv, double rfv) {
        // TODO: Use the stick position to determine how to scale these values
        // in Turbo mode (If the robot is driving in a straight line, the values are
        // going to max out at sqrt(2)/2, rather than: We can go faster, but we don't
        // *always* want to scale faster, only when we're it turbo mode, and when one (or more)
        // of the control sticks are at their limit
        lfv = 1;
        lrv = 1;
        rrv = 1;
        rfv = 1;
        double maxlfvlrv = Math.max(Math.abs(lfv), Math.abs(lrv));
        double maxrfvrrv = Math.max(Math.abs(rfv), Math.abs(rrv));
        double maxall = Math.max(maxlfvlrv, maxrfvrrv);

        leftFront.setVelocity(
            (lfv * DriveConstants.MAX_TICKS_PER_SEC * DriveConstants.AFL_SCALE) / maxall
        );
        leftRear.setVelocity(
            (lrv * DriveConstants.MAX_TICKS_PER_SEC * DriveConstants.ARL_SCALE) / maxall
        );
        rightRear.setVelocity(
            (rrv * DriveConstants.MAX_TICKS_PER_SEC * DriveConstants.ARR_SCALE) / maxall
        );
        rightFront.setVelocity(
            (rfv * DriveConstants.MAX_TICKS_PER_SEC * DriveConstants.AFR_SCALE) / maxall
        );
    }

    public void setSnailMode() {
        Snail = true;
        Turbo = false;
    }

    public boolean isSnailMode() {
        return Snail && !Turbo;
    }

    public void setTurboMode() {
        Turbo = true;
        Snail = false;
    }

    public boolean isTurboMode() {
        return Turbo && !Snail;
    }

    public void setNormalMode() {
        Snail = false;
        Turbo = false;
    }

    public boolean isNormalMode() {
        return !Snail && !Turbo;
    }
}
