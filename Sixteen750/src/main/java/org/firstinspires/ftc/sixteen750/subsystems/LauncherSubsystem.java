package org.firstinspires.ftc.sixteen750.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.hardware.motor.EncodedMotor;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.Subsystem;
import com.technototes.library.util.PIDFController;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.Setup;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;

@Configurable
public class LauncherSubsystem implements Loggable, Subsystem {

    //    @Log.Number(name = "Motor Power")
    //    public static double MOTOR_POWER = 0.65; // 0.5 1.0
    @Log.Number(name = "Target Velocity")
    public static double targetLaunchVelocity = 1150;

    public static double closetargetLaunchVelocity = 1400;
    public static double fartargetLaunchVelocity = 1775;
    public static double fartargetLaunchVelocityforAuto = 2300;
    public static double targetLaunchVelocityforAuto1 = 1950;
    public static double targetLaunchVelocityforAuto2 = 1850;

    @Log.Number(name = "Current Motor Velocity")
    public static double currentLaunchVelocity = 0.0;

    public static double motorVelocity;
    public static double additionAmount;
    public static double additionDelta = 5;

    //@Log(name = "Launcher Power: ")
    public static double power;

    //@Log(name = "Error")
    public static double err;
    public static double launcher1Current;
    public static double launcher2Current;

    @Log(name = "Target Speed: ")
    public static double targetSpeed;

    @Log(name = "Target Power: ")
    public static double targetPower;

    public static PIDFCoefficients launcherPI = new PIDFCoefficients(0.004, 0.0002, 0.0, 0);
    public static PIDFCoefficients launcherPI_ForAuto = new PIDFCoefficients(
        0.0015,
        0.0000,
        0.0,
        0
    ); //p = 0.004, i = 0.00020
    public static double SPIN_F_SCALE = 0.00021;
    public static double SPIN_VOLT_COMP = 0.0216;
    public static double DIFFERENCE = 0.0046;
    public static double PEAK_VOLTAGE = 13;
    private static PIDFController launcherPID;
    public static double lastAutoVelocity = 0;

    boolean hasHardware;
    public Robot robot;
    public PIDFCoefficients launcherPIDF = new PIDFCoefficients(0, 0.0, 0.0, 0);
    public PIDFController launcherPIDFController;
    public static double FEEDFORWARD_COEFFICIENT = 0.0;

    public static double MINIMUM_VELOCITY = 1140;
    public static double RPM_PER_FOOT = 62.3;
    public static double REGRESSION_A = 6.261; // multiplier for x for close zone launch speed formula
    public static double REGRESSION_B = 1550; // minimum velocity for close zone launch speed formula
    public static double REGRESSION_C = 20; // multiplier for x for far zone launch speed formula
    public static double REGRESSION_D = 215; // minimum velocity for far zone launch speed formula - 130
    public static double REGRESSION_C_TELEOP = 20; // multiplier for x for far zone launch speed formula
    public static double REGRESSION_D_TELEOP = 130; // minimum velocity for far zone launch speed formula
    public static double REGRESSION_C_AUTO = 17; // multiplier for x for far zone launch speed formula
    public static double REGRESSION_D_AUTO = 115; // minimum velocity for far zone launch speed formula

    @Log.Number(name = "AutoAim Velocity")
    public static double autoVelocity;

    public double launcherPow;
    // not tested just placeholder but should be used
    EncodedMotor<DcMotorEx> launcher1;
    EncodedMotor<DcMotorEx> launcher2;
    LimelightSubsystem ls;

    //    @Log(name = "Flywheel at Velocity")
    //    public static boolean ready;

    public LauncherSubsystem(Hardware h) {
        hasHardware = Setup.Connected.LAUNCHERSUBSYSTEM;
        // Do stuff in here
        if (hasHardware) {
            launcher1 = h.launcher1;
            launcher2 = h.launcher2;
            launcher1.setDirection(DcMotorSimple.Direction.REVERSE);
            launcher2.setDirection(DcMotorSimple.Direction.FORWARD);
            launcher1.coast();
            launcher2.coast();
            launcher1.setPIDFCoefficients(launcherPIDF);
            launcher2.setPIDFCoefficients(launcherPIDF);
            //ready = false;
            ls = new LimelightSubsystem(h);
            double ADDITION = (PEAK_VOLTAGE - h.voltage());
            if (ADDITION == 0) {
                SPIN_VOLT_COMP = SPIN_VOLT_COMP + 0.001;
            } else {
                SPIN_VOLT_COMP = SPIN_VOLT_COMP + (ADDITION * DIFFERENCE);
            }
            launcherPID = new PIDFController(launcherPI, target ->
                target == 0
                    ? 0
                    : (SPIN_F_SCALE * target) +
                      (SPIN_VOLT_COMP * Math.min(PEAK_VOLTAGE, h.voltage()))
            );
            //            top.setPIDFCoefficients(launcherP);
            setTargetSpeed(0);
        } else {
            launcher1 = null;
            launcher2 = null;
        }
        CommandScheduler.register(this);
    }

    public void Launch() {
        // Spin the motors pid goes here
        if (hasHardware) {
            setTargetSpeed(autoVelocity()); //change to auto aim velocity
            //            launcher1.setVelocity(TargetLaunchVelocity);
            //            launcher2.setVelocity(TargetLaunchVelocity);
        }
    }

    public void CloseShoot() {
        // Spin the motors pid goes here
        if (hasHardware) {
            targetLaunchVelocity = closetargetLaunchVelocity;
        }
    }

    public void FarShoot() {
        // Spin the motors pid goes here
        if (hasHardware) {
            targetLaunchVelocity = fartargetLaunchVelocity;
        }
    }

    public void AutoLaunch1() {
        // Spin the motors pid goes here
        if (hasHardware) {
            setTargetSpeed(targetLaunchVelocityforAuto1); //change to auto aim velocity
            //launcher1.setVelocity(targetLaunchVelocityforAuto1);
            //launcher2.setVelocity(targetLaunchVelocityforAuto1);
        }
    }

    public void AutoLaunch2() {
        // Spin the motors pid goes here
        if (hasHardware) {
            setTargetSpeed(targetLaunchVelocityforAuto2); //change to auto aim velocity
            //            launcher1.setVelocity(TargetLaunchVelocity);
            //            launcher2.setVelocity(TargetLaunchVelocity);
        }
    }

    public void FarAutoLaunch() {
        // Spin the motors pid goes here
        if (hasHardware) {
            setTargetSpeed(fartargetLaunchVelocityforAuto); //change to auto aim velocity
            //            launcher1.setVelocity(TargetLaunchVelocity);
            //            launcher2.setVelocity(TargetLaunchVelocity);
        }
    }

    public double readVelocity() {
        // if(launcher1.getVelocity() == TARGET_LAUNCH_VELOCITY && launcher2.getVelocity() == TARGET_LAUNCH_VELOCITY) {
        //     return ready = true;
        // } else {
        //     return ready = false;
        // }
        return launcher1.getVelocity();

        // 12.25 stationary voltage - had to decrease velocity by 150 (trial one: true, trial two: true)
        // 11.84 stationary voltage - had to decrease velocity by 100? (trial one:
    }

    public void setTargetSpeed(double speed) {
        targetSpeed = speed;
        //        top.setVelocity(speed);
        launcherPID.setTarget(speed);
    }

    public double getTargetSpeed() {
        return targetSpeed;
    }

    private void setMotorPower(double pow) {
        double power = Math.clamp(pow, -1, 1);
        targetPower = power;
        if (launcher1 != null && launcher2 != null) {
            launcher1.setPower(power);
            launcher2.setPower(power);
        }
    }

    public double getMotorSpeed() {
        if (launcher1 != null) {
            return launcher1.getVelocity();
        }
        return -1;
    }

    public double getMotor1Current() {
        if (launcher1 != null) {
            return launcher1.getAmperage(CurrentUnit.AMPS);
        }
        return -1;
    }

    public double getMotor2Current() {
        if (launcher2 != null) {
            return launcher2.getAmperage(CurrentUnit.AMPS);
        }
        return -1;
    }

    public void Stop() {
        if (hasHardware) {
            //            launcher1.setVelocity(0);
            //            launcher2.setVelocity(0);
            //launcher1.setPower(0);
            //launcher2.setPower(0);
            launcherPID.setTarget(0);
        }
    }

    public void IncreaseMotorVelocity() {
        // Spin the motors pid goes here
        if (hasHardware) {
            additionAmount += additionDelta;
        }
    }

    public void DecreaseMotorVelocity() {
        // Spin the motors pid goes here
        if (hasHardware) {
            additionAmount -= additionDelta;
        }
    }

    public void setMotorVelocityTest() {
        launcher1.setVelocity(targetLaunchVelocity);
    }

    //    public void setMotorPowerTest() {
    //        launcher1.setPower(MOTOR_POWER);
    //        CurrentLaunchVelocity = getMotor1Velocity();
    //    }

    public double getMotor1Velocity() {
        return launcher1.getVelocity();
    }

    public double getMotor2Velocity() {
        return launcher2.getVelocity();
    }

    public void VelocityShoot() {
        if (
            getMotor1Velocity() == targetLaunchVelocity &&
            getMotor2Velocity() == targetLaunchVelocity
        ) {
            TeleCommands.GateDown(robot);
        }
    }

    public double autoVelocity() {
        // x = distance in feet
        double x = ls.getDistance();

        if (x < 100 && x > 0) {
            //launcherPI.p = 0.0015;
            //launcherPI.i = 0;
            lastAutoVelocity = REGRESSION_A * x + REGRESSION_B;
            return lastAutoVelocity;
        }
        if (x < 0) {
            return lastAutoVelocity;
        } else {
            launcherPI.p = 0.004;
            launcherPI.i = 0.0002;
            return REGRESSION_C * x + REGRESSION_D;
        }

        //return ((RPM_PER_FOOT * ls.getDistance()) / 12 + MINIMUM_VELOCITY) + addtionamount;
    }

    public void setRegressionCAuto() {
        // Spin the motors pid goes here
        REGRESSION_C = REGRESSION_C_AUTO;
    }

    public void setRegressionDAuto() {
        // Spin the motors pid goes here
        REGRESSION_D = REGRESSION_D_AUTO;
    }

    public void setRegressionCTeleop() {
        // Spin the motors pid goes here
        REGRESSION_C = REGRESSION_C_TELEOP;
    }

    public void setRegressionDTeleop() {
        // Spin the motors pid goes here
        REGRESSION_D = REGRESSION_D_TELEOP;
    }

    @Override
    public void periodic() {
        autoVelocity = autoVelocity();
        currentLaunchVelocity = readVelocity();
        if (launcherPID.getTarget() != 0) {
            setMotorPower(launcherPID.update(getMotorSpeed()));
        } else {
            setMotorPower(0);
            launcherPID.update(getMotorSpeed());
            launcherPID.reset();
        }

        err = launcherPID.getLastError();
        motorVelocity = getMotorSpeed();
        power = launcher1.getPower();
        launcher1Current = getMotor1Current();
        launcher2Current = getMotor2Current();
    }
}
