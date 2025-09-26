package org.firstinspires.ftc.sixteen750.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.technototes.library.hardware.motor.EncodedMotor;

import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Setup;

@Configurable
public class LauncherSubsystem {

    public static double MOTOR_VELOCITY = 0.25; // 0.5 1.0
    boolean hasHardware;
    EncodedMotor<DcMotorEx> launcher1;
    EncodedMotor<DcMotorEx> launcher2;


    public LauncherSubsystem(Hardware h) {
        hasHardware = Setup.Connected.LAUNCHERSUBSYSTEM;
        // Do stuff in here
        if (hasHardware) {
            launcher1 = h.launcher1;
            launcher2 = h.launcher2;
            launcher1.setDirection(DcMotorSimple.Direction.FORWARD);
            launcher2.setDirection(DcMotorSimple.Direction.REVERSE);
            launcher1.coast();
            launcher2.coast();
        } else {
            launcher1 = null;
            launcher2 = null;
        }
    }

    public void Launch() {
        // Spin the motors
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
