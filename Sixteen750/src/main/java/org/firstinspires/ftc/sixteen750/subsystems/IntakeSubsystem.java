package org.firstinspires.ftc.sixteen750.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.technototes.library.hardware.motor.CRServo;
import com.technototes.library.hardware.motor.EncodedMotor;

import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Setup;

@Configurable
public class IntakeSubsystem {

    public static double MOTOR_VELOCITY = 0.25; // 0.5 1.0
    boolean hasHardware;
    EncodedMotor<DcMotorEx> intake;

    public IntakeSubsystem(Hardware h) {
        hasHardware = Setup.Connected.INTAKESUBSYSTEM;
        // Do stuff in here
        if (hasHardware) {
            intake = h.intake;
            intake.coast();
        } else {
            intake = null;
        }
    }

    public void Intake() {
        // Spin the motors
        if (hasHardware) {
            intake.setVelocity(MOTOR_VELOCITY);
        }
    }
    public void Spit() {
        // Spin the motors
        if (hasHardware) {
            intake.setDirection(DcMotorSimple.Direction.REVERSE);
            intake.setVelocity(MOTOR_VELOCITY);
        }
    }



    public void Stop() {
        if (hasHardware) {
            intake.setVelocity(0);
        }
    }
}
