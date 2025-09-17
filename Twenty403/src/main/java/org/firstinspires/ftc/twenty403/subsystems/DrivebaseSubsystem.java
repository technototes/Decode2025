package org.firstinspires.ftc.twenty403.subsystems;


import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import com.technototes.library.hardware.motor.EncodedMotor;
import com.technototes.library.hardware.sensor.IGyro;

import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.drivebase.SimpleMecanumDrivebaseSubsystem;


public class DrivebaseSubsystem
    extends SimpleMecanumDrivebaseSubsystem<DcMotorEx>
    implements Loggable {

    // Notes from Kevin:
    // The 5203 motors when direct driven
    // move about 63 inches forward and is measured as roughly 3000 ticks on the encoders

    @Configurable
    public abstract static class DriveConstants {

        public static double SLOW_MOTOR_SPEED = 0.4;
        public static double NORMAL_MOTOR_SPEED = 0.9;
        public static double TURBO_MOTOR_SPEED = 1.0;
        public static double SLOW_ROTATION_SCALE = 0.2;
        public static double NORMAL_ROTATION_SCALE = 0.3; // too big, make it smaller to slow down rotation
        public static double TRIGGER_THRESHOLD = 0.6;

        public static double AFR_SCALE = 1;
        public static double AFL_SCALE = 1;
        public static double ARR_SCALE = 1;
        public static double ARL_SCALE = 1;
    }

    // @Log.Number(name = "FL")
    public EncodedMotor<DcMotorEx> fl2;

    // @Log.Number(name = "FR")
    public EncodedMotor<DcMotorEx> fr2;

    // @Log.Number(name = "RL")
    public EncodedMotor<DcMotorEx> rl2;

    // @Log.Number(name = "RR")
    public EncodedMotor<DcMotorEx> rr2;

    // @Log(name = "Speed")
    public double speed = DriveConstants.NORMAL_MOTOR_SPEED;

    @Log(name = "")
    public String driveStr;

    public DrivebaseSubsystem(
        EncodedMotor<DcMotorEx> fl,
        EncodedMotor<DcMotorEx> fr,
        EncodedMotor<DcMotorEx> rl,
        EncodedMotor<DcMotorEx> rr,
        IGyro i
    ) {
        // The localizer is not quite working. Bot drives a little crazy
        super(() -> i.getHeading(), fl, fr, rl, rr);
        fl2 = fl;
        fr2 = fr;
        rl2 = rl;
        rr2 = rr;
        setNormalMode();

        fl.getRawMotor(DcMotorEx.class).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rl.getRawMotor(DcMotorEx.class).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rr.getRawMotor(DcMotorEx.class).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        fr.getRawMotor(DcMotorEx.class).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        fl.setDirection(DcMotorSimple.Direction.FORWARD);
        rl.setDirection(DcMotorSimple.Direction.FORWARD);
        rr.setDirection(DcMotorSimple.Direction.FORWARD);
        fr.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    @Override
    public double getSpeed() {
        if (isSnailMode()) {
            return DriveConstants.SLOW_MOTOR_SPEED;
        }
        if (isTurboMode()) {
            return DriveConstants.TURBO_MOTOR_SPEED;
        }
        return DriveConstants.NORMAL_MOTOR_SPEED;
    }

    public void setSnailMode() {
        speed = DriveConstants.SLOW_MOTOR_SPEED;
    }

    public boolean isSnailMode() {
        return Math.abs(speed - DriveConstants.SLOW_MOTOR_SPEED) < 0.01;
    }

    public void setTurboMode() {
        speed = DriveConstants.TURBO_MOTOR_SPEED;
    }

    public boolean isTurboMode() {
        return Math.abs(speed - DriveConstants.TURBO_MOTOR_SPEED) < 0.01;
    }

    public void setNormalMode() {
        speed = DriveConstants.NORMAL_MOTOR_SPEED;
    }

    public boolean isNormalMode() {
        return Math.abs(speed - DriveConstants.NORMAL_MOTOR_SPEED) < 0.01;
    }

    @Override
    public void drive(double flSpeed, double frSpeed, double rlSpeed, double rrSpeed) {
        motors[0].setPower(flSpeed * speed * DriveConstants.AFL_SCALE);
        motors[1].setPower(frSpeed * speed * DriveConstants.AFR_SCALE);
        motors[2].setPower(rlSpeed * speed * DriveConstants.ARL_SCALE);
        motors[3].setPower(rrSpeed * speed * DriveConstants.ARR_SCALE);
    }

    @Override
    public void drive(double speed, double angle, double rotation) {
        driveStr = String.format("s: %f a: %f r: %f", speed, angle, rotation);
        super.drive(speed, angle, rotation);
    }
}
