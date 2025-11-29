package org.firstinspires.ftc.sixteen750.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.technototes.library.hardware.motor.CRServo;
import com.technototes.library.hardware.motor.EncodedMotor;
import com.technototes.library.logger.Loggable;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Setup;

@Configurable
public class IntakeSubsystem implements Loggable {

    public static double MOTOR_VELOCITY = 1; // 0.5 1.0
    boolean hasHardware;
    EncodedMotor<DcMotorEx> intake;

    public IntakeSubsystem(Hardware h) {
        // intake.getRawMotor(DcMotorEx.class).getCurrent(CurrentUnit.AMPS)
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
            intake.setDirection(DcMotorSimple.Direction.REVERSE);
            intake.setPower(MOTOR_VELOCITY);
        }
    }

    public void Spit() {
        // Spin the motors
        if (hasHardware) {
            intake.setDirection(DcMotorSimple.Direction.FORWARD);
            intake.setPower(MOTOR_VELOCITY);
        }
    }

    public void Hold() {
        if (hasHardware) {
            intake.setDirection(DcMotorSimple.Direction.REVERSE);
            intake.setPower(MOTOR_VELOCITY / 1.2); // needed to make hold a little bit faster to keep spinning at atleast a slow speed
        }
    }

    public void StopIntake() {
        if (hasHardware) {
            intake.setPower(0);
        }
    }
}
