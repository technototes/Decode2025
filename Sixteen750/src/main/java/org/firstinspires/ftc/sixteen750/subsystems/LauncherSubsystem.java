package org.firstinspires.ftc.sixteen750.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.control.PIDFController;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.technototes.library.hardware.motor.EncodedMotor;
import com.technototes.library.hardware.motor.Motor;
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

    public static double TARGET_LAUNCH_VELOCITY = 6000;
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
        } else {
            launcher1 = null;
            launcher2 = null;
        }
    }

    public void Launch() {
        // Spin the motors pid goes here
        if (hasHardware) {
            launcher1.setPower(MOTOR_POWER);
            launcher2.setPower(MOTOR_POWER);
        }
    }

    public void Stop() {
        if (hasHardware) {
            launcher1.setPower(0);
            launcher2.setPower(0);
        }
    }
    public void IncreaseMotorSpeed() {
        // Spin the motors pid goes here
        if (hasHardware) {
            MOTOR_POWER += 0.05;
        }
    }
    public void DecreaseMotorSpeed() {
        // Spin the motors pid goes here
        if (hasHardware) {
            MOTOR_POWER -= 0.05;
        }
    }
    public void setMotorVelocityTest(){
        launcher1.setVelocity(TARGET_LAUNCH_VELOCITY);
    }
    public void setMotorPowerTest(){
        launcher1.setPower(MOTOR_POWER);
        CURRENT_LAUNCH_VELOCITY = getMotor1Velocity();
    }
    public double getMotor1Velocity(){
        return launcher1.getVelocity();
    }

    public double getMotor2Velocity(){
        return launcher2.getVelocity();
    }

    public void VelocityShoot(){
        if (getMotor1Velocity() == TARGET_LAUNCH_VELOCITY && getMotor2Velocity() == TARGET_LAUNCH_VELOCITY){
            TeleCommands.GateDown(robot);
        }
    }
}
