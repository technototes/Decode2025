package org.firstinspires.ftc.learnbot.opmodes;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.technototes.library.hardware.motor.CRServo;
import org.firstinspires.ftc.learnbot.Setup;
import org.firstinspires.ftc.learnbot.subsystems.TestSubsystem;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@Configurable
@TeleOp(name = "Drivebase Testbed")
public class DriveBaseValidation extends LinearOpMode {

    public static double motorPower = 0.2;
    public static double motorVelocity = Math.PI;
    public static double triggerThreshold = 0.1;
    public TestSubsystem ts;

    @Override
    public void runOpMode() throws InterruptedException {
        // First, get the hardward
        DcMotorEx fr, fl, rr, rl, launch;
        com.qualcomm.robotcore.hardware.CRServo bl, br;
        if (Setup.Connected.DRIVEBASE) {
            fr = this.hardwareMap.get(DcMotorEx.class, Setup.HardwareNames.FRMOTOR);
            fl = this.hardwareMap.get(DcMotorEx.class, Setup.HardwareNames.FLMOTOR);
            rr = this.hardwareMap.get(DcMotorEx.class, Setup.HardwareNames.RRMOTOR);
            rl = this.hardwareMap.get(DcMotorEx.class, Setup.HardwareNames.RLMOTOR);
            fl.setDirection(DcMotorSimple.Direction.REVERSE);
            rl.setDirection(DcMotorSimple.Direction.REVERSE);
            rr.setDirection(DcMotorSimple.Direction.FORWARD);
            fr.setDirection(DcMotorSimple.Direction.FORWARD);
        } else {
            fl = null;
            fr = null;
            rl = null;
            rr = null;
        }
        if (Setup.Connected.LAUNCHER) {
            launch = this.hardwareMap.get(DcMotorEx.class, Setup.HardwareNames.TOP);
        } else {
            launch = null;
        }
        if (Setup.Connected.FEED) {
            bl = this.hardwareMap.get(
                com.qualcomm.robotcore.hardware.CRServo.class,
                Setup.HardwareNames.BOTTOML
            );
            br = this.hardwareMap.get(
                com.qualcomm.robotcore.hardware.CRServo.class,
                Setup.HardwareNames.BOTTOMR
            );
        } else {
            bl = null;
            br = null;
        }

        waitForStart();
        while (opModeIsActive()) {
            sleep(5);
            if (Setup.Connected.DRIVEBASE) {
                if (gamepad1.left_trigger > triggerThreshold) {
                    fl.setVelocity(motorVelocity, AngleUnit.RADIANS);
                } else {
                    fl.setVelocity(0);
                }
                if (gamepad1.right_trigger > triggerThreshold) {
                    fr.setVelocity(motorVelocity, AngleUnit.RADIANS);
                } else {
                    fr.setVelocity(0);
                }
                if (gamepad1.left_bumper) {
                    rl.setVelocity(motorVelocity, AngleUnit.RADIANS);
                } else {
                    rl.setVelocity(0);
                }
                if (gamepad1.right_bumper) {
                    rr.setVelocity(motorVelocity, AngleUnit.RADIANS);
                } else {
                    rr.setVelocity(0);
                }
                telemetry.addData("FL", fl.getCurrentPosition());
                telemetry.addData("FR", fr.getCurrentPosition());
                telemetry.addData("RL", rl.getCurrentPosition());
                telemetry.addData("RR", rr.getCurrentPosition());
            }
            if (Setup.Connected.LAUNCHER) {
                if (gamepad1.cross) {
                    launch.setPower(5);
                } else {
                    launch.setPower(0);
                }
                telemetry.addData("launch", gamepad1.cross);
            }
            if (Setup.Connected.FEED) {
                if (gamepad1.square) {
                    br.setPower(-1);
                } else {
                    br.setPower(0);
                }
                if (gamepad1.triangle) {
                    bl.setPower(1);
                } else {
                    bl.setPower(0);
                }
                telemetry.addData("br", gamepad1.square);
                telemetry.addData("bl", gamepad1.triangle);
            }
            telemetry.update();
        }
    }
}
