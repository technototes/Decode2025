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
@TeleOp(name = "Launcher FeedFwd Calc")
public class LaucherFeedFwd extends LinearOpMode {

    public static String MotorName = "fl";

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
        waitForStart();
        lc.feedFwdHelper(telemetry, gamepad1, this::opModeIsActive);
    }
}
