package org.firstinspires.ftc.twenty403.opmodes;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.technototes.library.hardware.motor.CRServo;

import org.firstinspires.ftc.twenty403.Setup;
import org.firstinspires.ftc.twenty403.subsystems.TestSubsystem;

import java.util.Set;

@Configurable
@TeleOp(name = "Drivebase Testbed")
public class DriveBaseValidation extends LinearOpMode {

    public static double motorPower = 0.2;
    public static double triggerThreshold = 0.1;
    public TestSubsystem ts;

    @Override
    public void runOpMode() throws InterruptedException {
        // First, get the hardward
        DcMotorEx fr = this.hardwareMap.get(DcMotorEx.class, Setup.HardwareNames.FRMOTOR);
        DcMotorEx fl = this.hardwareMap.get(DcMotorEx.class, Setup.HardwareNames.FLMOTOR);
        DcMotorEx rr = this.hardwareMap.get(DcMotorEx.class, Setup.HardwareNames.RRMOTOR);
        DcMotorEx rl = this.hardwareMap.get(DcMotorEx.class, Setup.HardwareNames.RLMOTOR);
        DcMotorEx launch = this.hardwareMap.get(DcMotorEx.class, Setup.HardwareNames.TOP);
        com.qualcomm.robotcore.hardware.CRServo bl = this.hardwareMap.get(com.qualcomm.robotcore.hardware.CRServo.class, Setup.HardwareNames.BOTTOML);
        com.qualcomm.robotcore.hardware.CRServo br = this.hardwareMap.get(com.qualcomm.robotcore.hardware.CRServo.class, Setup.HardwareNames.BOTTOMR);
        fl.setDirection(DcMotorSimple.Direction.FORWARD);
        rl.setDirection(DcMotorSimple.Direction.FORWARD);
        rr.setDirection(DcMotorSimple.Direction.REVERSE);
        fr.setDirection(DcMotorSimple.Direction.REVERSE);


        waitForStart();
        while (opModeIsActive()) {
            sleep(5);
            if (gamepad1.left_trigger > triggerThreshold) {
                fl.setPower(motorPower);
            } else {
                fl.setPower(0);
            }
            if (gamepad1.right_trigger > triggerThreshold) {
                fr.setPower(motorPower);
            } else {
                fr.setPower(0);
            }
            if (gamepad1.left_bumper) {
                rl.setPower(motorPower);
            } else {
                rl.setPower(0);
            }
            if (gamepad1.right_bumper) {
                rr.setPower(motorPower);
            } else {
                rr.setPower(0);
            }
            if (gamepad1.cross) {
                launch.setPower(5);
            } else {
                launch.setPower(0);
            }
            if(gamepad1.square){
                br.setPower(-1);
            } else {
                br.setPower(0);
            }
            if (gamepad1.triangle){
                bl.setPower(1);
            } else {
                bl.setPower(0);
            }
        }
    }
}
