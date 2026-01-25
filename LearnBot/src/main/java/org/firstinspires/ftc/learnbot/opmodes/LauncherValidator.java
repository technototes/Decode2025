package org.firstinspires.ftc.learnbot.opmodes;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.technototes.library.hardware.motor.EncodedMotor;
import java.util.List;
import java.util.function.DoubleSupplier;
import org.firstinspires.ftc.learnbot.component.LauncherComponent;
import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;

@Configurable
@SuppressWarnings("unused")
@TeleOp(name = "Launcher Cfg")
public class LauncherValidator extends LinearOpMode {

    public static String MotorName = "rr";

    public boolean anyDpad() {
        return (
            gamepad1.dpadUpWasReleased() ||
            gamepad1.dpadDownWasReleased() ||
            gamepad1.dpadLeftWasReleased() ||
            gamepad1.dpadRightWasReleased()
        );
    }

    public boolean anyButtons() {
        return (
            gamepad1.aWasReleased() ||
            gamepad1.bWasReleased() ||
            gamepad1.xWasReleased() ||
            gamepad1.yWasReleased()
        );
    }

    @Override
    public void runOpMode() throws InterruptedException {
        // We need to get the voltage for
        List<LynxModule> hubs = hardwareMap.getAll(LynxModule.class);
        DoubleSupplier getVoltage = () -> {
            double volts = 0;
            for (LynxModule lm : hubs) {
                volts += lm.getInputVoltage(VoltageUnit.VOLTS);
            }
            return volts / hubs.size();
        };
        EncodedMotor<DcMotorEx> m = new EncodedMotor<>(
            hardwareMap.get(DcMotorEx.class, MotorName),
            MotorName
        );
        LauncherComponent lc = new LauncherComponent(m, null, getVoltage);
        waitForStart();
        telemetry.addLine(">>> Press the dpad for feedfwd <<<");
        telemetry.addLine(">>> Press any button for validation <<<");
        telemetry.update();
        while (opModeIsActive()) {
            if (anyDpad()) {
                lc.feedFwdHelper(telemetry, gamepad1, this::opModeIsActive);
                break;
            } else if (anyButtons()) {
                while (opModeIsActive() && !anyDpad()) {
                    telemetry.addLine(">>> Press left trigger for Launcher1 control");
                    telemetry.addLine(">>> Press right trigger for Launcher2 control");
                    telemetry.addLine(">>> Hit the dpad to stop");
                    telemetry.addLine(
                        lc.hardwareValidation(gamepad1.left_trigger, gamepad1.right_trigger)
                    );
                    telemetry.update();
                }
            }
        }
    }
}
