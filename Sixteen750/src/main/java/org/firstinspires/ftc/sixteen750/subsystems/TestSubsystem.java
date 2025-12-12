package org.firstinspires.ftc.sixteen750.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.technototes.library.hardware.motor.CRServo;
import com.technototes.library.hardware.motor.EncodedMotor;
import com.technototes.library.hardware.servo.Servo;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.Setup;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;

@Configurable
public class TestSubsystem implements Loggable {

    @Log.Number(name = "Motor Power")
    public static double MOTOR_POWER = 0.65; // 0.5 1.0

    public static double TARGET_LAUNCH_VELOCITY = 6000;

    @Log.Number(name = "Motor Velocity")
    public static double CURRENT_LAUNCH_VELOCITY = 0.0;

    public static double TESTMOTOR_VELOCITY = 0.25; // 0.5 1.0

    public static double TESTCRSERVO_SPEED = 1; // 0.15 0.25
    public static double SERVO_POS = 0.1;

    boolean hasHardware;
    public Robot robot;
    EncodedMotor<DcMotorEx> testmotor;
    CRServo testcrservo;

    Servo testservo;

    public TestSubsystem(Hardware h) {
        hasHardware = Setup.Connected.TESTSUBSYSTEM;
        // Do stuff in here
        if (hasHardware) {
            testmotor = h.testMotor;
            testmotor.coast();
            testcrservo = h.testCRServo;
        } else {
            testmotor = null;
        }
    }

    public void spinMotor() {
        // Spin the motors
        // TODO: make the motors spit the thing at the right angle
        if (hasHardware) {
            testmotor.setVelocity(TESTMOTOR_VELOCITY);
        }
    }

    public void setServo() {
        if (hasHardware) {
            testservo.setPosition(SERVO_POS);
            //Decide how the servo works later xD
        }
    }

    public void spinCRServo() {
        if (hasHardware) {
            testcrservo.setPower(TESTCRSERVO_SPEED);
        }
    }

    public void Stop() {
        if (hasHardware) {
            testmotor.setVelocity(0);
            testcrservo.setPower(0);
        }
    }

    public void setMotorVelocityTest() {
        testmotor.setVelocity(TARGET_LAUNCH_VELOCITY);
    }

    public void setMotorPowerTest() {
        testmotor.setPower(MOTOR_POWER);
        CURRENT_LAUNCH_VELOCITY = getMotor1Velocity();
    }

    public double getMotor1Velocity() {
        return testmotor.getVelocity();
    }

    public void VelocityShoot() {
        if (getMotor1Velocity() == TARGET_LAUNCH_VELOCITY) {
            TeleCommands.GateDown(robot);
        }
    }
}
