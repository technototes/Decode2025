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
import org.firstinspires.ftc.learnbot.subsystems.AllianceDetection;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

@SuppressWarnings("unused")
@Configurable
@TeleOp(name = "Test Bed", group = "--Testing--")
public class TestBedTele extends LinearOpMode {

    public static double motorPower = 0.2;
    // Radians/second?
    public static double motorVelocity = Math.PI / 2;
    public static double triggerThreshold = 0.2;
    public TelemetryManager ptel;
    public AllianceDetection allianceDetector;
    public CommandAxis trigger;
    public CommandButton button;

    public String getAlliance() {
        return allianceDetector.get().toString();
    }

    @Override
    public void runOpMode() throws InterruptedException {
        // First, get the hardware
        DcMotorEx fr, fl, rr, rl;
        trigger = new CommandAxis(() -> gamepad1.left_trigger);
        button = trigger.getAsButton(triggerThreshold);
        allianceDetector = new AllianceDetection(
            this.hardwareMap,
            Setup.HardwareNames.RED_SWITCH,
            Setup.HardwareNames.BLUE_SWITCH
        );
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
                fl.setVelocity(
                    triggered(gamepad1.left_trigger) ? motorVelocity : 0,
                    AngleUnit.RADIANS
                );
                fr.setVelocity(
                    triggered(gamepad1.right_trigger) ? motorVelocity : 0,
                    AngleUnit.RADIANS
                );
                rl.setVelocity(gamepad1.left_bumper ? motorVelocity : 0, AngleUnit.RADIANS);
                rr.setVelocity(gamepad1.right_bumper ? motorVelocity : 0, AngleUnit.RADIANS);
                String fld = motorData(fl);
                String frd = motorData(fr);
                String rld = motorData(rl);
                String rrd = motorData(rr);
                String redOrBlue = getAlliance();
                telemetry.addData("Alliance", redOrBlue);
                telemetry.addData("FL", fld);
                telemetry.addData("FR", frd);
                telemetry.addData("RL", rld);
                telemetry.addData("RR", rrd);
                telemetry.addData("leftX", gamepad1.left_stick_x);
                telemetry.addData("leftY", gamepad1.left_stick_y);
                telemetry.addData("rightX", gamepad1.right_stick_x);
                telemetry.addData("rightY", gamepad1.right_stick_y);
                telemetry.addData("ltrig", gamepad1.left_trigger);
                telemetry.addData("cmd trigger", trigger.getAsDouble());
                telemetry.addData("cmd button", button.getAsBoolean() ? "true" : "false");

                ptel.addData("Alliance", redOrBlue);
                ptel.addData("FL", fld);
                ptel.addData("FR", frd);
                ptel.addData("RL", rld);
                ptel.addData("RR", rrd);
                ptel.addData("leftX", gamepad1.left_stick_x);
                ptel.addData("leftY", gamepad1.left_stick_y);
                ptel.addData("rightX", gamepad1.right_stick_x);
                ptel.addData("rightY", gamepad1.right_stick_y);
                ptel.addData("ltrig", gamepad1.left_trigger);
                ptel.addData("cmd trigger", trigger.getAsDouble());
                ptel.addData("cmd button", button.getAsBoolean() ? "true" : "false");
            }
            ptel.update();
            telemetry.update();
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
