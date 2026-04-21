package org.firstinspires.ftc.learnbot.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;

@SuppressWarnings("unused")
@TeleOp(name = "SimpleDriving")
public class SimpleDrivingTele extends LinearOpMode {

    public DcMotorEx fl, fr, rl, rr;

    @Override
    public void runOpMode() throws InterruptedException {
        // First, get the hardware
        fl = hardwareMap.get(DcMotorEx.class, "fl");
        fr = hardwareMap.get(DcMotorEx.class, "fr");
        rl = hardwareMap.get(DcMotorEx.class, "rl");
        rr = hardwareMap.get(DcMotorEx.class, "rr");
        // Motors on opposite sides of the bot need to spin opposite directions
        fl.setDirection(Direction.REVERSE);
        rl.setDirection(Direction.REVERSE);
        fr.setDirection(Direction.FORWARD);
        rr.setDirection(Direction.FORWARD);
        waitForStart();
        while (opModeIsActive()) {
            double fwdBack = gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double rotate = gamepad1.right_stick_x;
            double fbMagnitude = Math.abs(fwdBack);
            double sMagnitude = Math.abs(strafe);
            double rMagnitude = Math.abs(rotate);
            if (fbMagnitude >= sMagnitude && fbMagnitude >= rMagnitude) {
                // We're driving forward/backward: Move all wheels in the same direction
                fl.setPower(fwdBack);
                fr.setPower(fwdBack);
                rl.setPower(fwdBack);
                rr.setPower(fwdBack);
            } else if (sMagnitude >= fbMagnitude && sMagnitude >= rMagnitude) {
                // We're strafing left/right: Move opposite corners in the same direction
                fl.setPower(strafe);
                rr.setPower(strafe);
                fr.setPower(-strafe);
                rl.setPower(-strafe);
            } else {
                // We're rotating:: Opposite sides move in opposite directions
                fl.setPower(rotate);
                rl.setPower(rotate);
                fr.setPower(-rotate);
                rr.setPower(-rotate);
            }
            telemetry.addData("forward/backward", fwdBack);
            telemetry.addData("strafe", strafe);
            telemetry.addData("rotate", rotate);
            telemetry.update();
        }
    }
}
