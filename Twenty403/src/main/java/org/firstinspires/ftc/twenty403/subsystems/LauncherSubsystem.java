package org.firstinspires.ftc.twenty403.subsystems;
import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.technototes.library.hardware.motor.CRServo;
import com.technototes.library.hardware.motor.EncodedMotor;
import org.firstinspires.ftc.twenty403.Hardware;
import org.firstinspires.ftc.twenty403.Setup;

@Configurable
public class LauncherSubsystem {


    public static double MOTOR_VELOCITY = 0.25; // 0.5 1.0

    public static double CRSERVO_SPEED = 1; // 0.15 0.25

    boolean hasHardware;
    EncodedMotor<DcMotorEx> top;
    CRServo bottoml, bottomr;

    public LauncherSubsystem(Hardware h) {
        // Do stuff in here
        if (Setup.Connected.LAUNCHER) {
            hasHardware = true;
            top = h.top;

            top.coast();
            bottoml = h.bottoml;
            bottomr = h.bottomr;
      
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
            top.setVelocity(MOTOR_VELOCITY);

        }
    }
    public void moveball(){
        if(hasHardware) {
            bottomr.setPower(CRSERVO_SPEED);
            bottoml.setPower(CRSERVO_SPEED);

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
            bottoml.setPower(0);
            bottomr.setPower(0);

        }
    }
}