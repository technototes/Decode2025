package org.firstinspires.ftc.twenty403.opmodes;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.twenty403.Setup;

@Configurable
@TeleOp(name="Drivebase Testbed")
public class DriveBaseValidation extends LinearOpMode {

    public static double motorPower = 0.2;
    public static double triggerThreshold = 0.1;
    @Override
    public void runOpMode() throws InterruptedException {
        // First, get the hardward
        DcMotorEx fr = this.hardwareMap.get(DcMotorEx.class, Setup.HardwareNames.FRMOTOR);
        DcMotorEx fl = this.hardwareMap.get(DcMotorEx.class, Setup.HardwareNames.FLMOTOR);
        DcMotorEx rr = this.hardwareMap.get(DcMotorEx.class, Setup.HardwareNames.RRMOTOR);
        DcMotorEx rl = this.hardwareMap.get(DcMotorEx.class, Setup.HardwareNames.RLMOTOR);
        fl.setDirection(DcMotorSimple.Direction.FORWARD);
        rl.setDirection(DcMotorSimple.Direction.FORWARD);
        rr.setDirection(DcMotorSimple.Direction.REVERSE);
        fr.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();
        while (opModeIsActive()) {
            sleep(5);
            if (gamepad1.left_trigger > triggerThreshold){
                fl.setPower(motorPower);
            } else {
                fl.setPower(0);
            }
            if (gamepad1.right_trigger > triggerThreshold){
                fr.setPower(motorPower);
            } else {
                fr.setPower(0);
            }
            if (gamepad1.left_bumper){
                rl.setPower(motorPower);
            } else {
                rl.setPower(0);
            }
            if (gamepad1.right_bumper) {
                rr.setPower(motorPower);
            } else {
                rr.setPower(0);
            }
        }


    }
}
