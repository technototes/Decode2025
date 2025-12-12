package org.firstinspires.ftc.sixteen750.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.hardware.motor.EncodedMotor;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.Subsystem;
import java.lang.reflect.Array;
import java.util.ArrayList;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Setup;

@Configurable
public class IntakeSubsystem implements Loggable, Subsystem {

    Gamepad gamepad;
    public static double MOTOR_VELOCITY = 1; // 0.5 1.0
    boolean hasHardware;
    Array pastValuesArray;

    @Log.Number(name = "artifacts")
    public static double artifacts = 0;

    @Log.Number(name = "intakeCurrent")
    public static double intakecurrent = 0;

    @Log.Number(name = "intakespike")
    public static double intakespike = 0; //the current it goes to when a ball is intake - will test and see

    DcMotorEx intake;

    public IntakeSubsystem(Hardware h) {
        // intake.getRawMotor(DcMotorEx.class).getCurrent(CurrentUnit.AMPS)
        hasHardware = Setup.Connected.INTAKESUBSYSTEM;
        // Do stuff in here
        if (hasHardware) {
            intake = h.intake;
            intakecurrent = getCurrent();
            CommandScheduler.register(this);
            gamepad = null;
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

    public void setGamepad(Gamepad g) {
        gamepad = g;
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
            intake.setPower(MOTOR_VELOCITY / 1.1); // needed to make hold a little bit faster to keep spinning at atleast a slow speed
        }
    }

    public void StopIntake() {
        if (hasHardware) {
            intake.setPower(0);
        }
    }

    public double getCurrent() {
        return intake.getCurrent(CurrentUnit.AMPS);
    }

    public void detectBall() {
        if (getCurrent() < 1.1) {
            artifacts = 0;
        } else if (getCurrent() < 2) {
            artifacts = 1;
        } else if (getCurrent() < 3) {
            artifacts = 2;
        } else if (getCurrent() < 4) {
            artifacts = 3;
            if (gamepad != null) {
                gamepad.rumble(20);
            }
        }
    }

    @Override
    public void periodic() {
        intakecurrent = getCurrent();
        detectBall();
    }
}
