package com.technototes.library.structure;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;

public abstract class ValidationOpMode extends OpMode {

    public List<LynxModule> hubs;
    protected TelemetryManager panelsTelemetry = null;

    public final double getVoltage() {
        double volts = 0;
        for (LynxModule lm : hubs) {
            volts += lm.getInputVoltage(VoltageUnit.VOLTS);
        }
        return volts / hubs.size();
    }

    protected final void clearBulkCache() {
        for (LynxModule hub : hubs) {
            hub.clearBulkCache();
        }
    }

    public final boolean anyDpadReleased() {
        return (
            gamepad1.dpadUpWasReleased() ||
            gamepad1.dpadDownWasReleased() ||
            gamepad1.dpadLeftWasReleased() ||
            gamepad1.dpadRightWasReleased()
        );
    }

    public final boolean anyButtonsReleased() {
        return (
            gamepad1.aWasReleased() || gamepad1.bWasReleased() || gamepad1.xWasReleased() || gamepad1.yWasReleased()
        );
    }

    public ValidationOpMode() {
        super();
    }

    @Override
    public void init() {
        hubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : hubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();
    }

    @Override
    public void loop() {
        clearBulkCache();
        telemetry.update();
        panelsTelemetry.update();
    }

    public void addData(String caption, String data) {
        telemetry.addData(caption, data);
        panelsTelemetry.addData(caption, data);
    }

    public void addData(String caption, double d) {
        telemetry.addData(caption, d);
        panelsTelemetry.addData(caption, d);
    }

    public void addLine(String line) {
        telemetry.addLine(line);
        panelsTelemetry.addLine(line);
    }
}
