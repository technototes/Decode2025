package org.firstinspires.ftc.sixteen750.opmodes;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import org.firstinspires.ftc.sixteen750.Setup;
import org.firstinspires.ftc.sixteen750.subsystems.TestSubsystem;

@Configurable
@TeleOp(name = "Drivebase Testbed")
public class DriveBaseValidation extends LinearOpMode {

    public static double motorPower = 0.2;
    public static double triggerThreshold = 0.1;
    public TestSubsystem ts;

    @Override
    public void runOpMode() throws InterruptedException {
        // First, get the hardward
        DcMotorEx fr = this.hardwareMap.get(DcMotorEx.class, Setup.HardwareNames.FR_DRIVE_MOTOR);
        DcMotorEx fl = this.hardwareMap.get(DcMotorEx.class, Setup.HardwareNames.FL_DRIVE_MOTOR);
        DcMotorEx rr = this.hardwareMap.get(DcMotorEx.class, Setup.HardwareNames.RR_DRIVE_MOTOR);
        DcMotorEx rl = this.hardwareMap.get(DcMotorEx.class, Setup.HardwareNames.RL_DRIVE_MOTOR);
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
            if (gamepad1.circle) {
                ts.setServo();
            }
            if (gamepad1.square) {
                ts.spinCRServo();
            }
            if (gamepad1.triangle) {
                ts.spinMotor();
            }
            if (gamepad1.cross) {
                ts.Stop();
            }
        }
    }
}
