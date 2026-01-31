package org.firstinspires.ftc.crossbones.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.technototes.library.hardware.motor.CRServo;
import com.technototes.library.hardware.motor.EncodedMotor;
import com.technototes.library.hardware.servo.Servo;
import org.firstinspires.ftc.crossbones.Hardware;
import org.firstinspires.ftc.crossbones.Setup;

@Configurable
public class TestSubsystem {

    public static double TESTMOTOR_VELOCITY = 0.25; // 0.5 1.0

    public static double TESTCRSERVO_SPEED = 1; // 0.15 0.25
    public static double SERVO_POS = 0.1;

    boolean hasHardware;
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
}
