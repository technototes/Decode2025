package org.firstinspires.ftc.twenty403.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.technototes.library.hardware.motor.EncodedMotor;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.Subsystem;
import com.technototes.library.util.PIDFController;
import org.firstinspires.ftc.twenty403.Hardware;
import org.firstinspires.ftc.twenty403.Setup;

@Configurable
public class LauncherSubsystem implements Loggable, Subsystem {

    public static double TARGET_MOTOR_VELOCITY = 1300 ; //.58; // 0.5 // /1.0

    boolean hasHardware;
    public static EncodedMotor<DcMotorEx> top;
    public static PIDFCoefficients launcherP = new PIDFCoefficients(1.2, 0.0, 0.0, 0);
    public static double SPIN_F_SCALE = 1.0 / 6000;
    public static double SPIN_VOLT_COMP = 0.1;
    public static double PEAK_VOLTAGE = 13.0;
    private static PIDFController launcherPID;

    @Log(name = "Launcher Velo: ")
    public static double MOTOR_VELOCITY;
    @Log(name = "Target Speed: ")
    public static double target;
    @Log(name = "Launcher Power: ")
    public static double power;
    @Log(name = "Error")
    public static double err;

    private boolean launching = false;

    public LauncherSubsystem(Hardware h) {
        hasHardware = Setup.Connected.LAUNCHER;
        // Do stuff in here
        if (hasHardware) {
            top = h.top;
            top.coast();
            launcherPID = new PIDFController(launcherP, target -> SPIN_F_SCALE * target + SPIN_VOLT_COMP * Math.min(PEAK_VOLTAGE, h.voltage()));
//            top.setPIDFCoefficients(launcherP);
            setTargetSpeed(0);
        } else {
            top = null;
        }
    }

    public void Launch() {
        // Spin the motors
        // TODO: make the motors spit the thing at the right angle
        if (hasHardware) {
            setTargetSpeed(TARGET_MOTOR_VELOCITY);
        }
        launching = true;
    }

    public void IncreaseVelocity() {
        if (hasHardware && launching) {
            target += 100;
            setTargetSpeed(target);
        }
    }

    public void DecreaseVelocity() {
        if (hasHardware && launching) {
            target -= 100;
            setTargetSpeed(target);
        }
    }


    public boolean GetCurrentTargetVelocity() {
        return top.getVelocity() >= 1300;
    }

    public void Stop() {
        if (hasHardware) {
            setTargetSpeed(0);
        }
        launching = false;
    }

    public void setTargetSpeed(double speed) {
        target = speed;
//        top.setVelocity(speed);
        launcherPID.setTarget(speed);
    }
    public double getTargetSpeed() {
        return target;
    }
    private void setMotorPower(double pow) {
        double power = Math.clamp(pow, -1, 1);
        if (top != null) {
            top.setPower(power);
        }
    }
    public double getMotorSpeed() {
        if (top != null) {
            return launcherPID.getTarget();
        }
        return -1;
    }
    @Override
    public void periodic() {
        setMotorPower(launcherPID.update(getMotorSpeed()));
        err = launcherPID.getLastError();
//        launcherP.f =  SPIN_F_SCALE * target + SPIN_VOLT_COMP * Math.min(PEAK_VOLTAGE, Hardware.voltage());
    }
}
