package org.firstinspires.ftc.sixteen750.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.control.PIDFController;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.technototes.library.hardware.motor.EncodedMotor;
import com.technototes.library.logger.Loggable;

import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Setup;

@Configurable
public class LauncherSubsystem implements Loggable {

    public static double MOTOR_VELOCITY = 0.85; // 0.5 1.0
    boolean hasHardware;
    public com.qualcomm.robotcore.hardware.PIDFCoefficients launcherPIDF = new PIDFCoefficients(1.0, 0.0, 0.0, 10.0);
    public PIDFController launcherPIDFController;
    public static double FEEDFORWARD_COEFFICIENT = 0.0;
    public double launcherPow;
    EncodedMotor<DcMotorEx> launcher1;
    EncodedMotor<DcMotorEx> launcher2;


    public LauncherSubsystem(Hardware h) {
        hasHardware = Setup.Connected.LAUNCHERSUBSYSTEM;
        // Do stuff in here
        if (hasHardware) {
            launcher1 = h.launcher1;
            launcher2 = h.launcher2;
            launcher1.setDirection(DcMotorSimple.Direction.REVERSE);
            launcher2.setDirection(DcMotorSimple.Direction.FORWARD);
            launcher1.coast();
            launcher2.coast();
            launcher1.setPIDFCoefficients(launcherPIDF);
            launcher2.setPIDFCoefficients(launcherPIDF);
        } else {
            launcher1 = null;
            launcher2 = null;
        }
    }

    public void Launch() {
        // Spin the motors pid goes here
        if (hasHardware) {
            launcher1.setVelocity(MOTOR_VELOCITY);
            launcher2.setVelocity(MOTOR_VELOCITY);
        }
    }



    public void Stop() {
        if (hasHardware) {
            launcher1.setVelocity(0);
            launcher2.setVelocity(0);
        }
    }
}
