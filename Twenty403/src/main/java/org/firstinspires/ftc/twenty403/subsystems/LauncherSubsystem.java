package org.firstinspires.ftc.twenty403.subsystems;
import com.acmerobotics.dashboard.config.Config;
import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.technototes.library.hardware.motor.EncodedMotor;
import org.firstinspires.ftc.twenty403.Hardware;
import org.firstinspires.ftc.twenty403.Setup;

@Configurable
public class LauncherSubsystem {

    public static double MAX_MOTOR_VELOCITY = 0.25; // 0.5 1.0

    public static double MIN_MOTOR_VELOCITY = 0.075; // 0.15 0.25

    boolean hasHardware;
    EncodedMotor<DcMotorEx> top, bottoml, bottomr;
    public LauncherSubsystem(Hardware h) {
        // Do stuff in here
        if (Setup.Connected.LAUNCHER) {
            hasHardware = true;
            top = h.top;
            bottoml = h.bottoml;
            bottomr = h.bottomr;
            bottoml.setDirection(DcMotorSimple.Direction.FORWARD);
            bottomr.setDirection(DcMotorSimple.Direction.REVERSE);
            bottoml.coast();
            top.coast();
        } else {
            hasHardware = false;
            top = null;
            bottoml = null;
            bottomr = null;
        }
    }

    public void Launch() {
        // Spin the motors
        // TODO: make the motors spit the thing at the right angle
        if (hasHardware) {
            top.setVelocity(MAX_MOTOR_VELOCITY);
        }
    }
    public void moveball(){
        if(hasHardware) {
            bottoml.setVelocity(MIN_MOTOR_VELOCITY);
            bottomr.setVelocity(MIN_MOTOR_VELOCITY);
        }
    }

    public void everything(){
        if(hasHardware) {
            Launch();
            moveball();
        }
    }

    public void Stop() {
        if (hasHardware) {
            top.setVelocity(0);
            bottoml.setVelocity(0);
            bottomr.setVelocity(0);
        }
    }
}