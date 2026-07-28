package org.firstinspires.ftc.sixteen750.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.hardware.motor.CRServo;
import com.technototes.library.hardware.motor.MotorPlus;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.Subsystem;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Setup;

@Configurable
public class IntakeSubsystem implements Loggable, Subsystem {

    Gamepad gamepad;

    //@Log.Number(name = "intakePow")
    public static double SMART_INTAKE_VELOCITY = 1;

    // @Log.Number(name = "transferPow")
    public static double SMART_TRANSFER_VELOCITY = 1;

    @Log.Number(name = "IntSpeed")
    public static double IntakeSpeed = 0;

    @Log.Number(name = "TransferSpeed")
    public static double TransferSpeed = 0;

    public static double TRANSFER_ONE_THRESHOLD = 0.9;
    public static double TRANSFER_TWO_THRESHOLD = 1.7;
    public static double INTAKE_THRESHOLD = 3;
    public static double ONE_THRESHOLD = 1.65;
    public static double TWO_THRESHOLD = 3;
    public static double THREE_THRESHOLD = 10;
    public static double INTAKE_VELOCITY = 1;
    public static double SLOW_MOTOR_VELOCITY = 0.6; // 0.5 1.0
    public static double SLOW_INTAKE_VELOCITY = 0.4; // velocity to set intake motor too once we have 3 balls
    public static double SLOW_TRANSFER_VELOCITY = 0.55;
    public static int duration = 80;
    public static double GATE_INTAKE_HEADING_BLUE = 155;
    public static double GATE_INTAKE_HEADING_RED = 25;

    boolean hasHardware;
    int currentIndex = 0;
    double[] pastIntakeValuesArray;
    double[] pastTransferValuesArray;
    public static double SIGN = 1;

    @Log.Number(name = "artifacts")
    public static double artifacts = 0;

    @Log.Number(name = "transferFull")
    public boolean transferFull = false;

    public static boolean robotFull = false;

    @Log.Number(name = "intakeFull")
    public boolean intakeFull = false;

    @Log.Number(name = "IntakeCur")
    public static double intakecurrent = 0.0;

    @Log.Number(name = "TransferCur")
    public static double transfercurrent = 0.0;

    public static double intakespike = 0; //the current it goes to when a ball is intake - will test and see

    MotorPlus intake;
    MotorPlus transfer;
    CRServo gobbleServo;
    CRServo gulpServo;

    public IntakeSubsystem(Hardware h) {
        // intake.getRawMotor(DcMotorEx.class).getCurrent(CurrentUnit.AMPS)
        hasHardware = Setup.Connected.INTAKESUBSYSTEM;
        // Do stuff in here
        if (hasHardware) {
            intake = h.intake;
            transfer = h.intake2;
            gobbleServo = h.gobbleServo;
            gulpServo = h.gulpServo;
            intakecurrent = getIntakeCurrent();
            transfercurrent = getTransferCurrent();
            CommandScheduler.register(this);
            gamepad = null;
            intake.setDirection(DcMotorSimple.Direction.REVERSE);
            transfer.setDirection(DcMotorSimple.Direction.REVERSE);
        } else {
            intake = null;
        }
        // Create the array to hold past current values
        pastIntakeValuesArray = new double[4];
        pastTransferValuesArray = new double[8];
    }

    public void Intake() {
        // Spin the motors
        if (hasHardware) {
            intake.setPower(INTAKE_VELOCITY);
            transfer.setPower(SLOW_TRANSFER_VELOCITY);
            gobbleServo.setPower(1);
            gulpServo.setPower(-1);
        }
    }

    public void Feed() {
        if (hasHardware) {
            intake.setPower(INTAKE_VELOCITY);
            transfer.setPower(INTAKE_VELOCITY);
        }
    }

    public void setGamepad(Gamepad g) {
        gamepad = g;
    }

    public void Spit() {
        // Spin the motors
        if (hasHardware) {
            //intake.setDirection(DcMotorSimple.Direction.FORWARD);
            intake.setPower(-INTAKE_VELOCITY);
            transfer.setPower(-INTAKE_VELOCITY);
        }
    }

    public void Hold() {
        if (hasHardware) {
            intake.setDirection(DcMotorSimple.Direction.REVERSE);
            intake.setPower(SLOW_MOTOR_VELOCITY);
            transfer.setDirection(DcMotorSimple.Direction.REVERSE);
            transfer.setPower(SLOW_MOTOR_VELOCITY); // needed to make hold a little bit faster to keep spinning at atleast a slow speed
        }
    }

    public void StopIntake() {
        if (hasHardware) {
            intake.setPower(0);
            transfer.setPower(0);
            gobbleServo.setPower(0);
            gulpServo.setPower(0);
        }
    }

    public void GobbleGulp() {
        if (hasHardware) {
            gobbleServo.setPower(1 * SIGN); // they are inverted from each other cause mirrored i added sign in there so its easy to invert both of them
            gulpServo.setPower(-1 * SIGN);
        }
    }

    public void IThinkIAteTooMuch() {
        if (hasHardware) {
            gobbleServo.setPower(0);
            gulpServo.setPower(0);
        }
    }

    public void setRumble() {
        duration = 80;
    }

    public void setRumbleOff() {
        duration = 0;
    }

    public double getIntakeCurrent() {
        return intake.getAmperage(CurrentUnit.AMPS);
    }

    public double getTransferCurrent() {
        return transfer.getAmperage(CurrentUnit.AMPS);
    }

    /* public void detectBall(double averageCurrent) {
        if (averageCurrent < ONE_THRESHOLD) {
            artifacts = 0;
        } else if (averageCurrent < TWO_THRESHOLD) {
            artifacts = 1;
        } else if (averageCurrent < THREE_THRESHOLD) {
            artifacts = 2;
        } else {
            artifacts = 3;
            if (gamepad != null) {
                gamepad.rumble(duration);
            }
        }
    }*/

    public void SmartIntake(double averageIntakeCurrent, double averageTransferCurrent) {
        if (Setup.Connected.SMARTINTAKE) {
            if (averageTransferCurrent < TRANSFER_ONE_THRESHOLD) {
                artifacts = 0;
                transferFull = false;
                if (averageIntakeCurrent < INTAKE_THRESHOLD) {
                    intakeFull = false;
                }
            } else if (averageTransferCurrent < TRANSFER_TWO_THRESHOLD) {
                transferFull = false;
                artifacts = 1;
                if (averageIntakeCurrent < INTAKE_THRESHOLD) {
                    intakeFull = false;
                }
            } else if (averageTransferCurrent >= TRANSFER_TWO_THRESHOLD) {
                transferFull = true;
                artifacts = 2;
                if (averageIntakeCurrent < INTAKE_THRESHOLD) {
                    intakeFull = false;
                } else if (averageIntakeCurrent >= INTAKE_THRESHOLD) {
                    intakeFull = true;
                    artifacts = 3;
                } else robotFull = intakeFull;
            }
        }
    }

    public void SmartVelocity() {
        if (AimingSubsystem.GateDown) {
            SMART_INTAKE_VELOCITY = 1;
            SMART_TRANSFER_VELOCITY = 1;
        } else if (intakeFull) {
            SMART_INTAKE_VELOCITY = 0;
            SMART_TRANSFER_VELOCITY = 0;
        } else if (transferFull) {
            SMART_INTAKE_VELOCITY = 1;
            SMART_TRANSFER_VELOCITY = 0;
        } else {
            SMART_INTAKE_VELOCITY = 1;
            SMART_TRANSFER_VELOCITY = 1;
        }
    }

    public void SmartTransferVelocity() {
        if (AimingSubsystem.GateDown) {
            SMART_TRANSFER_VELOCITY = 1;
        } else {
            SMART_TRANSFER_VELOCITY = 0.38;
        }
    }

    public double getAverageIntakeCurrent() {
        // Calculate the average of the values in the array
        double valuesTotal = 0;
        for (int i = 0; i < pastIntakeValuesArray.length; i++) {
            valuesTotal += pastIntakeValuesArray[i];
        }
        return valuesTotal / 4;
    }

    public double getAverageTransferCurrent() {
        // Calculate the average of the values in the array
        double valuesTotal = 0;
        for (int i = 0; i < pastTransferValuesArray.length; i++) {
            valuesTotal += pastTransferValuesArray[i];
        }
        return valuesTotal / 8;
    }

    public double getIntakeSpeed() {
        // funny math chatgpt gave me it looks right i plug in current draw and it gives me velocity in rpm (for bare motor)
        IntakeSpeed = (5900 * (9.3 - getIntakeCurrent())) / 9;
        return IntakeSpeed;
    }

    public double getTransferSpeed() {
        TransferSpeed = (5900 * (9.3 - getTransferCurrent())) / 9;
        return TransferSpeed;
    }

    //public double getIntakeSpeed() {
    // return intake.getSpeed();
    //}
    //public double getTransferSpeed() {
    //  return transfer.getSpeed();
    // }

    @Override
    public void periodic() {
        // Add an item to the array and update the index for the next update to the 'circular' array
        pastIntakeValuesArray[currentIndex] = getIntakeCurrent();
        pastTransferValuesArray[currentIndex] = getTransferCurrent();
        currentIndex = (currentIndex + 1) % pastIntakeValuesArray.length;
        // detectBall(getAverageIntakeCurrent());
        SmartIntake(getAverageIntakeCurrent(), getAverageTransferCurrent());
        SmartTransferVelocity();
        intakecurrent = getAverageIntakeCurrent();
        transfercurrent = getAverageTransferCurrent();
        IntakeSpeed = getIntakeSpeed();
        TransferSpeed = getTransferSpeed();
    }
}
