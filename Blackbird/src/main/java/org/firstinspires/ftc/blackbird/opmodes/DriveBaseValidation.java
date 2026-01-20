package org.firstinspires.ftc.blackbird.opmodes;

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
import org.firstinspires.ftc.blackbird.Setup.Connected;
import org.firstinspires.ftc.blackbird.Setup.HardwareNames;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

@Configurable
@TeleOp(name = "Drivebase Testbed")
@SuppressWarnings("unused")
public class DriveBaseValidation extends LinearOpMode {

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
    public CommandAxis trigger;
    public CommandButton button;
    public MotorConfig[] motors;

    @Override
    public void runOpMode() throws InterruptedException {
        // First, get the hardware
        motors = new MotorConfig[] {
            Connected.DRIVEBASE
                ? new MotorConfig(hardwareMap, HardwareNames.FL_DRIVE_MOTOR, true, () ->
                      triggered(gamepad1.left_trigger)
                  )
                : null,
            Connected.DRIVEBASE
                ? new MotorConfig(hardwareMap, HardwareNames.FR_DRIVE_MOTOR, false, () ->
                      triggered(gamepad1.right_trigger)
                  )
                : null,
            Connected.DRIVEBASE
                ? new MotorConfig(hardwareMap, HardwareNames.RL_DRIVE_MOTOR, true, () ->
                      gamepad1.left_bumper
                  )
                : null,
            Connected.DRIVEBASE
                ? new MotorConfig(hardwareMap, HardwareNames.RR_DRIVE_MOTOR, false, () ->
                      gamepad1.right_bumper
                  )
                : null,
            Connected.LAUNCHERSUBSYSTEM
                ? new MotorConfig(hardwareMap, HardwareNames.LAUNCHER_MOTOR1, false, () ->
                      gamepad1.dpad_up
                  )
                : null,
            Connected.LAUNCHERSUBSYSTEM
                ? new MotorConfig(hardwareMap, HardwareNames.LAUNCHER_MOTOR2, true, () ->
                      gamepad1.dpad_up
                  )
                : null,
            Connected.INTAKESUBSYSTEM
                ? new MotorConfig(hardwareMap, HardwareNames.INTAKE_MOTOR, false, () ->
                      gamepad1.dpad_down
                  )
                : null,
            Connected.TURRETSUBSYSTEM
                ? new MotorConfig(hardwareMap, HardwareNames.TURRET, false, () ->
                      gamepad1.dpad_left
                  )
                : null,
        };
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
            for (MotorConfig m : motors) {
                if (m == null) {
                    continue;
                }
                m.setVelo();
                addData(m.name, m.toString());
            }
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
