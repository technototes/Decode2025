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

    public static double TARGET_MOTOR_VELOCITY = 1375; //.58; // 0.5 // /1.0

    boolean hasHardware;
    public static EncodedMotor<DcMotorEx> top;
    public static PIDFCoefficients launcherP = new PIDFCoefficients(0.002, 0.0, 0.0, 0);
    public static double SPIN_F_SCALE = 1.0 / 6000;
    public static double SPIN_VOLT_COMP = 0.0216;
    public static double DIFFERENCE = 0.0046;
    public static double PEAK_VOLTAGE = 13;
    private static PIDFController launcherPID;
    private double voltage;

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
            double ADDITION = (PEAK_VOLTAGE - h.voltage());
            if (ADDITION == 0) {
                SPIN_VOLT_COMP = SPIN_VOLT_COMP + 0.001;
            } else {
                SPIN_VOLT_COMP = SPIN_VOLT_COMP + (ADDITION * DIFFERENCE);
            }
            launcherPID = new PIDFController(launcherP, target ->
                target == 0
                    ? 0
                    : (SPIN_F_SCALE * target) +
                      (SPIN_VOLT_COMP * Math.min(PEAK_VOLTAGE, h.voltage()))
            );
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
            if (Hardware.voltage() < 13.0 && Hardware.voltage() > 12.8) {
                setTargetSpeed(1475);
            } else {
                setTargetSpeed(TARGET_MOTOR_VELOCITY);
            }
        }
        launching = true;
    }

    public void AutoLaunch() {
        // Spin the motors
        // TODO: make the motors spit the thing at the right angle
        if (hasHardware) {
            setTargetSpeed(1370);
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
            return top.getVelocity();
        }
        return -1;
    }

    @Override
    public void periodic() {
        setMotorPower(launcherPID.update(getMotorSpeed()));
        err = launcherPID.getLastError();
        MOTOR_VELOCITY = getMotorSpeed();
        power = top.getPower();
        // launcherP.f =  SPIN_F_SCALE * target + SPIN_VOLT_COMP * Math.min(PEAK_VOLTAGE, Hardware.voltage());
    }
}
