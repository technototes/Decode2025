package org.firstinspires.ftc.learnbot.opmodes;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.hardware.ServoControllerEx;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.util.ElapsedTime;

@Configurable
@TeleOp(name = "Gimbal")
public class GimbalTesting extends OpMode {

    public static String YAW_SERVO = "yaw";
    public static String PITCH_SERVO = "pitch";
    public static double YAW_INIT = 0.5;
    public static double PITCH_INIT = 0.0;
    Servo yaw, pitch;
    double yawPos = YAW_INIT;
    double pitchPos = PITCH_INIT;
    public static double yawChange = 0.01;
    public static double pitchChange = 0.01;

    @Override
    public void init() {
        yaw = hardwareMap.get(Servo.class, YAW_SERVO);
        pitch = hardwareMap.get(Servo.class, PITCH_SERVO);
        if (yaw instanceof ServoImplEx) {
            ((ServoImplEx) yaw).setPwmRange(new PwmControl.PwmRange(500, 2500));
        }
        yaw.setPosition(yawPos);
        if (pitch instanceof ServoImplEx) {
            ((ServoImplEx) pitch).setPwmRange(new PwmControl.PwmRange(500, 2500));
        }
        pitch.setDirection(Servo.Direction.REVERSE);
        pitch.setPosition(pitchPos);
    }

    @Override
    public void loop() {
        if (gamepad1.dpadUpWasPressed()) {
            pitchPos += pitchChange;
        } else if (gamepad1.dpadDownWasPressed()) {
            pitchPos -= pitchChange;
        } else if (gamepad1.dpadLeftWasPressed()) {
            yawPos += yawChange;
        } else if (gamepad1.dpadRightWasPressed()) {
            yawPos -= yawChange;
        } else if (
            gamepad1.aWasPressed() ||
            gamepad1.bWasPressed() ||
            gamepad1.xWasPressed() ||
            gamepad1.yWasPressed()
        ) {
            yawPos = YAW_INIT;
            pitchPos = PITCH_INIT;
        }
        yaw.setPosition(yawPos);
        pitch.setPosition(pitchPos);
        telemetry.addLine("dpad up/dn: Pitch, dpad lt/rt: Yaw");
        telemetry.addLine("Press A/B/X/Y or PS4/5 shapes to reset to start");
        telemetry.addData("Yaw", yawPos);
        telemetry.addData("Pitch", pitchPos);
        telemetry.update();
    }
}
