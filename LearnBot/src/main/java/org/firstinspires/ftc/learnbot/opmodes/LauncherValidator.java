package org.firstinspires.ftc.learnbot.opmodes;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.technototes.library.hardware.motor.EncodedMotor;
import java.util.List;
import org.firstinspires.ftc.learnbot.component.LauncherComponent;
import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;

@Configurable
@SuppressWarnings("unused")
@TeleOp(name = "Launcher Cfg")
public class LauncherValidator extends LinearOpMode {

    public static String MotorName = "rr";

    @Override
    public void runOpMode() throws InterruptedException {
        DcMotorEx m = this.hardwareMap.get(DcMotorEx.class, MotorName);
        List<LynxModule> hubs = hardwareMap.getAll(LynxModule.class);
        LauncherComponent lc = new LauncherComponent(new EncodedMotor<>(m, MotorName), null, () -> {
            double volt = 0;
            double count = 0;
            for (LynxModule lm : hubs) {
                count += 1;
                volt += lm.getInputVoltage(VoltageUnit.VOLTS);
            }
            return volt / count;
        });
        telemetry.addLine("Press dpad for feedfwd, buttons for validation");
        telemetry.update();
        waitForStart();
        while (true) {
            if (
                gamepad1.dpadUpWasReleased() ||
                gamepad1.dpadDownWasReleased() ||
                gamepad1.dpadLeftWasReleased() ||
                gamepad1.dpadRightWasReleased()
            ) {
                lc.feedFwdHelper(telemetry, gamepad1, this::opModeIsActive);
                break;
            } else if (
                gamepad1.aWasReleased() ||
                gamepad1.bWasReleased() ||
                gamepad1.xWasReleased() ||
                gamepad1.yWasReleased()
            ) {
                while (true) {
                    telemetry.addLine(
                        "Press left trigger for Launcher1, right trigger for Launcher2, dpad (any) to stop"
                    );
                    if (gamepad1.dpad_up || gamepad1.dpad_down) {
                        break;
                    } else {
                        telemetry.addLine(
                            lc.hardwareValidation(gamepad1.left_trigger, gamepad1.right_trigger)
                        );
                    }
                    telemetry.update();
                }
            }
        }
    }
}
