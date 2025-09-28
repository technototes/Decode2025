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

    public static double INTAK_VELO = 0.5; // 0.5 1.0
    public static double SPIT_VELO =0.3;
    public static double HOLD_VELO = 0.25;
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
            intake.setVelocity(INTAK_VELO);
        }
    }
    public void Spit() {
        // Spin the motors
        if (hasHardware) {
            intake.setDirection(DcMotorSimple.Direction.REVERSE);
            intake.setVelocity(SPIT_VELO);
        }
    }
public void Hold() {
    if (hasHardware) {
        intake.setDirection(DcMotorSimple.Direction.FORWARD);
        intake.setVelocity(HOLD_VELO);
    }
}

    public void Stop() {
        if (hasHardware) {
            intake.setVelocity(0);
        }
    }
}
