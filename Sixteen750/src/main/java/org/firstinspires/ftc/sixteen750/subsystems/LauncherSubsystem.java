package org.firstinspires.ftc.sixteen750.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.control.PIDFController;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.technototes.library.command.Command;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.hardware.motor.EncodedMotor;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.Setup;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;

@Configurable
public class LauncherSubsystem implements Loggable {

    @Log.Number(name = "Motor Power")
    public static double MOTOR_POWER = 0.65; // 0.5 1.0

    @Log.Number(name = "Target Velocity")
    public static double TARGET_LAUNCH_VELOCITY = 2250
            ;

    @Log.Number(name = "Motor Velocity")
    public static double CURRENT_LAUNCH_VELOCITY = 0.0;

    boolean hasHardware;
    public Robot robot;
    public PIDFCoefficients launcherPIDF = new PIDFCoefficients(1.0, 0.0, 0.0, 10.0);
    public PIDFController launcherPIDFController;
    public static double FEEDFORWARD_COEFFICIENT = 0.0;
    public double launcherPow;
    // not tested just placeholder but should be used
    EncodedMotor<DcMotorEx> launcher1;
    EncodedMotor<DcMotorEx> launcher2;
    @Log (name = "Flywheel at Velocity")
    public static boolean ready;

    @Log.Number (name = "Current Launcher Velocity")
    public static double CURRENT_LAUNCHER_VELOCITY;

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
        } else {
            launcher1 = null;
            launcher2 = null;
        }
    }

    public void Launch() {
        // Spin the motors pid goes here
        if (hasHardware) {
            launcher1.setVelocity(TARGET_LAUNCH_VELOCITY);
            launcher2.setVelocity(TARGET_LAUNCH_VELOCITY);
            readVelocity();
        }
    }

    public double readVelocity(){
//        if(launcher1.getVelocity() == TARGET_LAUNCH_VELOCITY && launcher2.getVelocity() == TARGET_LAUNCH_VELOCITY) {
//            return ready = true;
//        }
//
//        else {
//            return ready = false;
//        }
        CURRENT_LAUNCH_VELOCITY = launcher1.getVelocity();
        return CURRENT_LAUNCH_VELOCITY;

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
            TARGET_LAUNCH_VELOCITY += 50;
        }
    }

    public void DecreaseMotorVelocity() {
        // Spin the motors pid goes here
        if (hasHardware) {
            TARGET_LAUNCH_VELOCITY -= 50;
        }
    }

    public void setMotorVelocityTest() {
        launcher1.setVelocity(TARGET_LAUNCH_VELOCITY);
    }

    public void setMotorPowerTest() {
        launcher1.setPower(MOTOR_POWER);
        CURRENT_LAUNCH_VELOCITY = getMotor1Velocity();
    }

    public double getMotor1Velocity() {
        return launcher1.getVelocity();
    }

    public double getMotor2Velocity() {
        return launcher2.getVelocity();
    }

    public void VelocityShoot() {
        if (
            getMotor1Velocity() == TARGET_LAUNCH_VELOCITY &&
            getMotor2Velocity() == TARGET_LAUNCH_VELOCITY
        ) {
            TeleCommands.GateDown(robot);
        }
    }

}
