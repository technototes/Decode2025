package org.firstinspires.ftc.blackbird.opmodes;

import androidx.annotation.NonNull;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.MovingStatistics;
import com.technototes.library.control.CommandAxis;
import com.technototes.library.control.CommandButton;
import java.util.List;
import java.util.function.BooleanSupplier;
import org.firstinspires.ftc.blackbird.Setup.Connected;
import org.firstinspires.ftc.blackbird.Setup.HardwareNames;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

@Configurable
@TeleOp(name = "Hardware Testbed")
@SuppressWarnings("unused")
public class HardwareValidation extends LinearOpMode {

    public static int samples = 100;

    // This is a helper for collecting info about a given motor
    public static class MotorConfig {

        DcMotorEx motor;
        MovingStatistics stats;
        BooleanSupplier trigger;
        BooleanSupplier revTrigger;
        String name;
        double pow_or_velo;
        boolean velocity;

        String status;

        public MotorConfig(
            boolean v,
            double porv,
            HardwareMap hwmap,
            String nm,
            boolean reversed,
            BooleanSupplier trig
        ) {
            this(v, porv, hwmap, nm, reversed, trig, null);
        }

        public MotorConfig(
            boolean v,
            HardwareMap hwmap,
            String nm,
            boolean reversed,
            BooleanSupplier trig
        ) {
            this(v, v ? motorVelocity : motorPower, hwmap, nm, reversed, trig, null);
        }

        public MotorConfig(
            boolean v,
            HardwareMap hwmap,
            String nm,
            boolean reversed,
            BooleanSupplier trig,
            BooleanSupplier revTrig
        ) {
            this(v, v ? motorVelocity : motorPower, hwmap, nm, reversed, trig, revTrig);
        }

        public MotorConfig(
            boolean v,
            double porv,
            HardwareMap hwmap,
            String nm,
            boolean reversed,
            BooleanSupplier trig,
            BooleanSupplier revTrig
        ) {
            velocity = v;
            pow_or_velo = porv;
            trigger = trig;
            revTrigger = revTrig;
            name = nm;
            motor = hwmap.get(DcMotorEx.class, name);
            motor.setDirection(reversed ? Direction.REVERSE : Direction.FORWARD);
            stats = new MovingStatistics(samples);
            status = nm;
        }

        public void set() {
            double pow = (revTrigger != null && revTrigger.getAsBoolean())
                ? -pow_or_velo
                : trigger.getAsBoolean()
                    ? pow_or_velo
                    : 0;
            if (velocity) {
                motor.setVelocity(pow, AngleUnit.RADIANS);
            } else {
                motor.setPower(pow);
            }
            double pos = motor.getCurrentPosition();
            double vel = motor.getVelocity();
            double amps = motor.getCurrent(CurrentUnit.AMPS);
            stats.add(amps);
            status = String.format(
                "%s%.2f %.2ftks %.2ftps %.3fA (avg: %.3fA)",
                velocity ? "V" : "P",
                pow,
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
        List<LynxModule> hubs = hardwareMap.getAll(LynxModule.class);
        hubs.forEach(e -> e.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL));

        // First, get the hardware
        motors = new MotorConfig[] {
            Connected.DRIVEBASE
                ? new MotorConfig(true, hardwareMap, HardwareNames.FL_DRIVE_MOTOR, true, () ->
                      triggered(gamepad1.left_trigger)
                  )
                : null,
            Connected.DRIVEBASE
                ? new MotorConfig(true, hardwareMap, HardwareNames.FR_DRIVE_MOTOR, false, () ->
                      triggered(gamepad1.right_trigger)
                  )
                : null,
            Connected.DRIVEBASE
                ? new MotorConfig(true, hardwareMap, HardwareNames.RL_DRIVE_MOTOR, true, () ->
                      gamepad1.left_bumper
                  )
                : null,
            Connected.DRIVEBASE
                ? new MotorConfig(true, hardwareMap, HardwareNames.RR_DRIVE_MOTOR, false, () ->
                      gamepad1.right_bumper
                  )
                : null,
            Connected.LAUNCHERSUBSYSTEM
                ? new MotorConfig(
                      false,
                      0.4,
                      hardwareMap,
                      HardwareNames.LAUNCHER_MOTOR1,
                      false,
                      () -> gamepad1.options
                  )
                : null,
            Connected.LAUNCHERSUBSYSTEM
                ? new MotorConfig(
                      false,
                      1.0,
                      hardwareMap,
                      HardwareNames.LAUNCHER_MOTOR2,
                      true,
                      () -> gamepad1.dpad_up
                  )
                : null,
            Connected.INTAKESUBSYSTEM
                ? new MotorConfig(false, 1.0, hardwareMap, HardwareNames.INTAKE_MOTOR, true, () ->
                      gamepad1.dpad_down
                  )
                : null,
            Connected.TURRETSUBSYSTEM
                ? new MotorConfig(
                      false,
                      0.5,
                      hardwareMap,
                      HardwareNames.TURRET,
                      false,
                      () -> gamepad1.dpad_left,
                      () -> gamepad1.dpad_right
                  )
                : null,
        };
        ptel = PanelsTelemetry.INSTANCE.getTelemetry();
        waitForStart();
        MovingStatistics loopStats = new MovingStatistics(samples);
        MovingStatistics avgLoopTime = new MovingStatistics(samples);
        ElapsedTime loopTime = new ElapsedTime();
        while (opModeIsActive()) {
            hubs.forEach(LynxModule::clearBulkCache);
            double lps = 1.0 / loopTime.seconds();
            loopTime.reset();
            loopStats.add(lps);
            double mean = loopStats.getMean();
            double stddev = loopStats.getStandardDeviation();
            for (MotorConfig m : motors) {
                if (m == null) {
                    continue;
                }
                m.set();
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
