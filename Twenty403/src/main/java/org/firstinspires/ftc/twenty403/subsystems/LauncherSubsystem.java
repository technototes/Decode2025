package org.firstinspires.ftc.twenty403.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.technototes.library.hardware.motor.CRServo;
import com.technototes.library.hardware.motor.EncodedMotor;
import org.firstinspires.ftc.twenty403.Hardware;
import org.firstinspires.ftc.twenty403.Setup;

@Configurable
public class LauncherSubsystem {

    public static double TARGET_MOTOR_VELOCITY = .58; // 0.5 1.0

    boolean hasHardware;
    public static EncodedMotor<DcMotorEx> top;
    CRServo bottomLeft, bottomRight;

    public LauncherSubsystem(Hardware h) {
        hasHardware = Setup.Connected.LAUNCHER;
        // Do stuff in here
        if (hasHardware) {
            top = h.top;
            top.coast();
        } else {
            top = null;
        }
    }

    public void Launch() {
        // Spin the motors
        // TODO: make the motors spit the thing at the right angle
        if (hasHardware) {
            top.setVelocity(TARGET_MOTOR_VELOCITY);
        }
    }



    public void Stop() {
        if (hasHardware) {
            top.setVelocity(0);
        }
    }
}
