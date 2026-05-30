package org.firstinspires.ftc.learnbot.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Maggie's Opmode :(")
public class MotorOpmode extends LinearOpMode {

    DcMotorEx motor;
    Servo servo;

    @Override
    public void runOpMode() throws InterruptedException {
        motor = hardwareMap.get(DcMotorEx.class, "motor");
        servo = hardwareMap.get(Servo.class, "servo");

        waitForStart();

        while (opModeIsActive()) {
            servo.setPosition(gamepad1.left_trigger);
            motor.setPower(gamepad1.right_stick_y);
        }
    }
}
