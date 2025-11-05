package org.firstinspires.ftc.learnbot.opmodes;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import org.firstinspires.ftc.learnbot.Setup;
import org.firstinspires.ftc.learnbot.subsystems.TestSubsystem;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@Configurable
@TeleOp(name = "Test Bed", group = "--Testing--")
public class TestBedTele extends LinearOpMode {

    public static double motorPower = 0.2;
    public static double motorVelocity = Math.PI;
    public static double triggerThreshold = 0.1;
    public TestSubsystem ts;

    public TelemetryManager ptel;

    @Override
    public void runOpMode() throws InterruptedException {
        // First, get the hardware
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

        ptel = PanelsTelemetry.INSTANCE.getTelemetry();
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
                telemetry.addData("leftX", gamepad1.left_stick_x);
                telemetry.addData("leftY", gamepad1.left_stick_y);
                telemetry.addData("rightX", gamepad1.right_stick_x);
                telemetry.addData("rightY", gamepad1.right_stick_y);
                ptel.addData("FL", fl.getCurrentPosition());
                ptel.addData("FR", fr.getCurrentPosition());
                ptel.addData("RL", rl.getCurrentPosition());
                ptel.addData("RR", rr.getCurrentPosition());
                ptel.addData("leftX", gamepad1.left_stick_x);
                ptel.addData("leftY", gamepad1.left_stick_y);
                ptel.addData("rightX", gamepad1.right_stick_x);
                ptel.addData("rightY", gamepad1.right_stick_y);
            }
            ptel.update();
            telemetry.update();
        }
    }
}
