package org.firstinspires.ftc.sixteen750.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.Subsystem;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Setup;

@Configurable
public class IntakeSubsystem implements Loggable, Subsystem {

    Gamepad gamepad;
    public static double one_threshold = 1.65;
    public static double two_threshold = 3;
    public static double theree_threshold = 4.5;
    public static double MOTOR_VELOCITY = 1; // 0.5 1.0
    public static double SLOW_MOTOR_VELOCITY = 0.67; // 0.5 1.0
    public static int duration = 80;
    boolean hasHardware;
    int currentIndex = 0;
    double[] pastValuesArray;

    @Log.Number(name = "artifacts")
    public static double artifacts = 0;

    @Log.Number(name = "intakeCurrent")
    public static double intakecurrent = 0;

    @Log.Number(name = "intakespike")
    public static double intakespike = 0; //the current it goes to when a ball is intake - will test and see

    DcMotorEx intake;
    DcMotorEx intake2;

    public IntakeSubsystem(Hardware h) {
        // intake.getRawMotor(DcMotorEx.class).getCurrent(CurrentUnit.AMPS)
        hasHardware = Setup.Connected.INTAKESUBSYSTEM;
        // Do stuff in here
        if (hasHardware) {
            intake = h.intake;
            intake2 = h.intake2;
            intakecurrent = getCurrent();
            CommandScheduler.register(this);
            gamepad = null;
            intake.setDirection(DcMotorSimple.Direction.REVERSE);
            intake2.setDirection(DcMotorSimple.Direction.REVERSE);
        } else {
            intake = null;
        }
        // Create the array to hold past current values
        pastValuesArray = new double[10];
    }

    public void Intake() {
        // Spin the motors
        if (hasHardware) {
            intake.setPower(MOTOR_VELOCITY);
            intake2.setPower(MOTOR_VELOCITY);
        }
    }

    public void setGamepad(Gamepad g) {
        gamepad = g;
    }

    public void Spit() {
        // Spin the motors
        if (hasHardware) {
            //intake.setDirection(DcMotorSimple.Direction.FORWARD);
            intake.setPower(-MOTOR_VELOCITY);
            intake2.setPower(-MOTOR_VELOCITY);
        }
    }

    public void Hold() {
        if (hasHardware) {
            intake.setDirection(DcMotorSimple.Direction.REVERSE);
            intake.setPower(SLOW_MOTOR_VELOCITY);
            intake2.setDirection(DcMotorSimple.Direction.REVERSE);
            intake2.setPower(SLOW_MOTOR_VELOCITY); // needed to make hold a little bit faster to keep spinning at atleast a slow speed
        }
    }

    public void StopIntake() {
        if (hasHardware) {
            intake.setPower(0);
            intake2.setPower(0);
        }
    }

    public void setRumble() {
        duration = 80;
    }

    public void setRumbleOff() {
        duration = 0;
    }

    public double getCurrent() {
        return intake.getCurrent(CurrentUnit.AMPS);
    }

    public void detectBall(double averageCurrent) {
        if (averageCurrent < one_threshold) {
            artifacts = 0;
        } else if (averageCurrent < two_threshold) {
            artifacts = 1;
        } else if (averageCurrent < theree_threshold) {
            artifacts = 2;
        } else {
            artifacts = 3;
            if (gamepad != null) {
                gamepad.rumble(duration);
            }
        }
    }

    public double getAverageCurrent() {
        // Calculate the average of the values in the array
        double valuesTotal = 0;
        for (int i = 0; i < pastValuesArray.length; i++) {
            valuesTotal += pastValuesArray[i];
        }
        return valuesTotal / 10;
    }

    @Override
    public void periodic() {
        // Add an item to the array and update the index for the next update to the 'circular' array
        pastValuesArray[currentIndex] = getCurrent();
        currentIndex = (currentIndex + 1) % pastValuesArray.length;
        detectBall(getAverageCurrent());
    }
}
