package org.firstinspires.ftc.twenty403.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.technototes.library.hardware.motor.CRServo;
import com.technototes.library.hardware.motor.EncodedMotor;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.twenty403.Hardware;
import org.firstinspires.ftc.twenty403.Setup;

@Configurable
public class LauncherSubsystem implements Loggable {

    public static double TARGET_MOTOR_VELOCITY = .59; //.58; // 0.5 // /1.0

    boolean hasHardware;
    public static EncodedMotor<DcMotorEx> top;
    private double currentTargetVelocity = TARGET_MOTOR_VELOCITY;

    @Log(name = "Launcher Velo: ")
    public static double READ_MOTOR_VELOCITY;

    private boolean launching = false;

    public LauncherSubsystem(Hardware h) {
        hasHardware = Setup.Connected.LAUNCHER;
        // Do stuff in here
        if (hasHardware) {
            top = h.top;
            top.coast();
        } else {
            top = null;
        }
    }

    public void Launch() {
        // Spin the motors
        // TODO: make the motors spit the thing at the right angle
        if (hasHardware) {
            top.setPower(currentTargetVelocity);
        }
        launching = true;
    }

    public void IncreaseVelocity() {
        currentTargetVelocity += 0.01;
        if (hasHardware && launching) {
            top.setPower(currentTargetVelocity);
        }
    }

    public void DecreaseVelocity() {
        currentTargetVelocity -= 0.01;
        if (hasHardware && launching) {
            top.setPower(currentTargetVelocity);
        }
    }

    public void RunLoop(Telemetry telemetry) {
        telemetry.addData("Launcher", launching ? currentTargetVelocity : "0.0");
    }

    public void readMotorVelocity() {
        READ_MOTOR_VELOCITY = top.getPower();
    }

    public double GetCurrentTargetVelocity() {
        return currentTargetVelocity;
    }

    public void Stop() {
        if (hasHardware) {
            top.setVelocity(0);
        }
        launching = false;
    }
}
