package org.firstinspires.ftc.learnbot.opmodes;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.MovingStatistics;
import com.qualcomm.robotcore.util.RollingAverage;
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
    public DcMotorEx[] motors;
    public RollingAverage[] motorAmps;

    public String getAlliance() {
        return allianceDetector.get().toString();
    }

    @Override
    public void runOpMode() throws InterruptedException {
        // First, get the hardware
        motors = new DcMotorEx[4];
        motorAmps = new RollingAverage[4];
        DcMotorEx fr, fl, rr, rl;
        trigger = new CommandAxis(() -> gamepad1.left_trigger);
        button = trigger.getAsButton(triggerThreshold);
        allianceDetector = new AllianceDetection(
            this.hardwareMap,
            Setup.HardwareNames.ALLIANCE_SWITCH_RED,
            Setup.HardwareNames.ALLIANCE_SWITCH_BLUE
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
            motors[0] = fl;
            motors[1] = fr;
            motors[2] = rl;
            motors[3] = rr;
            for (int i = 0; i < 4; i++) {
                motorAmps[i] = new RollingAverage(50);
            }
        } else {
            fl = null;
            fr = null;
            rl = null;
            rr = null;
        }

        ptel = PanelsTelemetry.INSTANCE.getTelemetry();
        waitForStart();
        com.qualcomm.robotcore.util.MovingStatistics loopStats = new MovingStatistics(50);
        ElapsedTime loopTime = new ElapsedTime();
        RollingAverage avgLoopTime = new RollingAverage(50);
        while (opModeIsActive()) {
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
                String fld = motorData(0);
                String frd = motorData(1);
                String rld = motorData(2);
                String rrd = motorData(3);
                String redOrBlue = getAlliance();
                double lps = 1.0 / loopTime.seconds();
                loopTime.reset();
                loopStats.add(lps);
                telemetry.addData("Alliance", redOrBlue);
                telemetry.addData("FL", fld);
                telemetry.addData("FR", frd);
                telemetry.addData("RL", rld);
                telemetry.addData("RR", rrd);
                telemetry.addData("lX", gamepad1.left_stick_x);
                telemetry.addData("lY", gamepad1.left_stick_y);
                telemetry.addData("rX", gamepad1.right_stick_x);
                telemetry.addData("rY", gamepad1.right_stick_y);
                telemetry.addData("lt", gamepad1.left_trigger);
                telemetry.addData("LPS", String.format("%.1f avg %.1f", lps, loopStats.getMean()));

                ptel.addData("Alliance", redOrBlue);
                ptel.addData("FL", fld);
                ptel.addData("FR", frd);
                ptel.addData("RL", rld);
                ptel.addData("RR", rrd);
                ptel.addData("lX", gamepad1.left_stick_x);
                ptel.addData("lY", gamepad1.left_stick_y);
                ptel.addData("rX", gamepad1.right_stick_x);
                ptel.addData("rY", gamepad1.right_stick_y);
                ptel.addData("lt", gamepad1.left_trigger);
                ptel.addData("LPS", String.format("%.1f avg %.1f", lps, loopStats.getMean()));
            }
            ptel.update();
            telemetry.update();
        }
    }

    public static boolean triggered(double d) {
        return d > triggerThreshold;
    }

    public String motorData(int which) {
        DcMotorEx motor = motors[which];
        double pos = motor.getCurrentPosition();
        double vel = motor.getVelocity();
        double amps = motor.getCurrent(CurrentUnit.AMPS);
        motorAmps[which].addNumber((int) Math.round(amps * 1000));
        return String.format(
            "%.2f %.2f/s %.3fA (mean: %.3fA)",
            pos,
            vel,
            amps,
            motorAmps[which].getAverage() / 1000.0
        );
    }
}
