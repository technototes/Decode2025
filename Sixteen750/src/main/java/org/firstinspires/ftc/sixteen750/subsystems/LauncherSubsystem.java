package org.firstinspires.ftc.sixteen750.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.control.PIDFController;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.hardware.motor.EncodedMotor;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.Subsystem;

import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.Setup;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;

@Configurable
public class LauncherSubsystem implements Loggable, Subsystem {

    @Log.Number(name = "Motor Power")
    public static double MOTOR_POWER = 0.65; // 0.5 1.0

    @Log.Number(name = "Target Velocity")
    public static double TargetLaunchVelocity = 2250;

    @Log.Number(name = "Current Motor Velocity")
    public static double CurrentLaunchVelocity = 0.0;

    boolean hasHardware;
    public Robot robot;
    public PIDFCoefficients launcherPIDF = new PIDFCoefficients(1.0, 0.0, 0.0, 10.0);
    public PIDFController launcherPIDFController;
    public static double FEEDFORWARD_COEFFICIENT = 0.0;
    @Log.Number (name = "AutoAim Velocity")
    public static double auto_velocity;
    public double launcherPow;
    // not tested just placeholder but should be used
    EncodedMotor<DcMotorEx> launcher1;
    EncodedMotor<DcMotorEx> launcher2;
    LimelightSubsystem ls;

    @Log(name = "Flywheel at Velocity")
    public static boolean ready;

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
            ready = false;
            ls = new LimelightSubsystem(h);
        } else {
            launcher1 = null;
            launcher2 = null;
        }
        CommandScheduler.register(this);
    }

    public void Launch() {
        // Spin the motors pid goes here
        if (hasHardware) {
            launcher1.setVelocity(TargetLaunchVelocity);
            launcher2.setVelocity(TargetLaunchVelocity);
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

    public void Stop() {
        if (hasHardware) {
            launcher1.setVelocity(0);
            launcher2.setVelocity(0);
        }
    }

    public void IncreaseMotorVelocity() {
        // Spin the motors pid goes here
        if (hasHardware) {
            TargetLaunchVelocity += 50;
        }
    }

    public void DecreaseMotorVelocity() {
        // Spin the motors pid goes here
        if (hasHardware) {
            TargetLaunchVelocity -= 50;
        }
    }

    public void setMotorVelocityTest() {
        launcher1.setVelocity(TargetLaunchVelocity);
    }

    public void setMotorPowerTest() {
        launcher1.setPower(MOTOR_POWER);
        CurrentLaunchVelocity = getMotor1Velocity();
    }

    public double getMotor1Velocity() {
        return launcher1.getVelocity();
    }

    public double getMotor2Velocity() {
        return launcher2.getVelocity();
    }

    public void VelocityShoot() {
        if (
            getMotor1Velocity() == TargetLaunchVelocity &&
            getMotor2Velocity() == TargetLaunchVelocity
        ) {
            TeleCommands.GateDown(robot);
        }
    }

    public double autoVelocity() {
        double auto_velocity = 100 * (0.982 * ((ls.getDistance() / 12) + 1.25) + 16.2);
        // x = distance in feet
        return auto_velocity;
    }

    @Override
    public void periodic() {
        auto_velocity = autoVelocity();
        CurrentLaunchVelocity = readVelocity();
    }
}
