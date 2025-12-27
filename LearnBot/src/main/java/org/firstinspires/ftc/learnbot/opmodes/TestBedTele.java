package org.firstinspires.ftc.learnbot.opmodes;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.MovingStatistics;
import com.technototes.library.control.CommandAxis;
import com.technototes.library.control.CommandButton;
import java.util.function.BooleanSupplier;
import org.firstinspires.ftc.learnbot.Setup.*;
import org.firstinspires.ftc.learnbot.subsystems.AllianceDetection;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

@SuppressWarnings("unused")
@Configurable
@TeleOp(name = "Test Bed", group = "--Testing--")
@SuppressLint("DefaultLocale")
public class TestBedTele extends LinearOpMode {

    public static int samples = 100;

    public static class MotorConfig {

        DcMotorEx motor;
        MovingStatistics stats;
        BooleanSupplier trigger;
        String name;
        String status;

        public MotorConfig(HardwareMap hwmap, String nm, boolean reversed, BooleanSupplier trig) {
            trigger = trig;
            name = nm;
            motor = hwmap.get(DcMotorEx.class, name);
            motor.setDirection(reversed ? Direction.REVERSE : Direction.FORWARD);
            stats = new MovingStatistics(samples);
            status = nm;
        }

        public void setVelo() {
            motor.setVelocity(trigger.getAsBoolean() ? motorVelocity : 0, AngleUnit.RADIANS);
            double pos = motor.getCurrentPosition();
            double vel = motor.getVelocity();
            double amps = motor.getCurrent(CurrentUnit.AMPS);
            stats.add(amps);
            status = String.format(
                "%.2f %.2f/s %.3fA (mean: %.3fA)",
                pos,
                vel,
                amps,
                stats.getMean()
            );
        }

        @Override
        @NonNull
        public String toString() {
            return status;
        }
    }

    public static double motorPower = 0.2;
    // Radians/second?
    public static double motorVelocity = Math.PI / 2;
    public static double triggerThreshold = 0.2;
    public TelemetryManager ptel;
    public AllianceDetection allianceDetector;
    public CommandAxis trigger;
    public CommandButton button;
    public MotorConfig[] motors;

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
            HardwareNames.ALLIANCE_SWITCH_RED,
            HardwareNames.ALLIANCE_SWITCH_BLUE
        );
        if (Connected.DRIVEBASE) {
            motors = new MotorConfig[4];
            motors[0] = new MotorConfig(hardwareMap, HardwareNames.FLMOTOR, true, () ->
                triggered(gamepad1.left_trigger)
            );
            motors[1] = new MotorConfig(hardwareMap, HardwareNames.FRMOTOR, true, () ->
                triggered(gamepad1.right_trigger)
            );
            motors[2] = new MotorConfig(hardwareMap, HardwareNames.RLMOTOR, true, () ->
                gamepad1.left_bumper
            );
            motors[3] = new MotorConfig(hardwareMap, HardwareNames.RRMOTOR, true, () ->
                gamepad1.right_bumper
            );
        } else {
            motors = null;
        }

        ptel = PanelsTelemetry.INSTANCE.getTelemetry();
        waitForStart();
        MovingStatistics loopStats = new MovingStatistics(samples);
        MovingStatistics avgLoopTime = new MovingStatistics(samples);
        ElapsedTime loopTime = new ElapsedTime();
        while (opModeIsActive()) {
            double lps = 1.0 / loopTime.seconds();
            loopTime.reset();
            loopStats.add(lps);
            double mean = loopStats.getMean();
            double stddev = loopStats.getStandardDeviation();
            addData("Alliance", getAlliance());
            if (Connected.DRIVEBASE) {
                for (MotorConfig m : motors) {
                    m.setVelo();
                    addData(m.name, m.toString());
                }
            }
            addData("lX", String.format("%.3f", gamepad1.left_stick_x));
            addData("lY", String.format("%.3f", gamepad1.left_stick_y));
            addData("rX", String.format("%.3f", gamepad1.right_stick_x));
            addData("rY", String.format("%.3f", gamepad1.right_stick_y));
            addData("lT", String.format("%.3f", gamepad1.left_trigger));
            addData("rT", String.format("%.3f", gamepad1.right_trigger));
            addData("LPS", String.format("%.1f avg %.1f stddev %.2f", lps, mean, stddev));
            ptel.update();
            telemetry.update();
        }
    }

    private void addData(String caption, String data) {
        telemetry.addData(caption, data);
        ptel.addData(caption, data);
    }

    public static boolean triggered(double d) {
        return d > triggerThreshold;
    }
}
