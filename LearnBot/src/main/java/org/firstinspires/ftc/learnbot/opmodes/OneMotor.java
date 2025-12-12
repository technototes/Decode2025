package org.firstinspires.ftc.learnbot.opmodes;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.technototes.library.control.CommandAxis;
import com.technototes.library.control.CommandButton;
import org.firstinspires.ftc.learnbot.Setup;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

@SuppressWarnings("unused")
@Configurable
@TeleOp(name = "SingleMotor")
public class OneMotor extends LinearOpMode {

    public static double motorPower = 0.2;
    // Radians/second?
    public static double motorVelocity = Math.PI / 2;
    public static double triggerThreshold = 0.2;
    public static int duration = 25;
    public TelemetryManager ptel;

    public CommandAxis trigger;
    public CommandButton button;

    @Override
    public void runOpMode() throws InterruptedException {
        // First, get the hardware
        DcMotorEx fr;
        trigger = new CommandAxis(() -> gamepad1.left_trigger);
        button = trigger.getAsButton(triggerThreshold);
        fr = this.hardwareMap.get(DcMotorEx.class, "m");
        fr.setDirection(DcMotorSimple.Direction.FORWARD);

        ptel = PanelsTelemetry.INSTANCE.getTelemetry();
        waitForStart();
        while (opModeIsActive()) {
            sleep(5);
            fr.setPower(triggered(gamepad1.right_trigger) ? motorVelocity : 0);
            String frd = motorData(fr);
            telemetry.addData("FR", frd);
            telemetry.addData("leftX", gamepad1.left_stick_x);
            telemetry.addData("leftY", gamepad1.left_stick_y);
            telemetry.addData("rightX", gamepad1.right_stick_x);
            telemetry.addData("rightY", gamepad1.right_stick_y);
            telemetry.addData("ltrig", gamepad1.left_trigger);
            telemetry.addData("cmd trigger", trigger.getAsDouble());
            telemetry.addData("cmd button", button.getAsBoolean() ? "true" : "false");
            ptel.addData("m", frd);
            ptel.addData("leftX", gamepad1.left_stick_x);
            ptel.addData("leftY", gamepad1.left_stick_y);
            ptel.addData("rightX", gamepad1.right_stick_x);
            ptel.addData("rightY", gamepad1.right_stick_y);
            ptel.addData("ltrig", gamepad1.left_trigger);
            ptel.addData("cmd trigger", trigger.getAsDouble());
            ptel.addData("cmd button", button.getAsBoolean() ? "true" : "false");
            ptel.update();
            telemetry.update();
            if (triggered(gamepad1.left_trigger)) {
                gamepad1.rumble(duration);
            }
        }
    }

    public static boolean triggered(double d) {
        return d > triggerThreshold;
    }

    public static String motorData(DcMotorEx motor) {
        double pos = motor.getCurrentPosition();
        double vel = motor.getVelocity();
        double amps = motor.getCurrent(CurrentUnit.AMPS);
        return String.format("%.2f %.2f/s %.3fA", pos, vel, amps);
    }
}
