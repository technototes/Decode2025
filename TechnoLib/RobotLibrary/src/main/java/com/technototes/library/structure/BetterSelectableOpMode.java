package com.technototes.library.structure;

import com.pedropathing.telemetry.SelectScope;
import com.pedropathing.telemetry.Selector;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class BetterSelectableOpMode extends OpMode {

    private final Selector<Supplier<OpMode>> selector;
    private OpMode selectedOpMode;
    private static final String[] MESSAGE = {
        "Use the d-pad to move the cursor.",
        "Press right bumper or dpad right to select.",
        "Press left bumper to dpad left go back.",
    };

    public BetterSelectableOpMode(String name, Consumer<SelectScope<Supplier<OpMode>>> opModes) {
        selector = Selector.create(name, opModes, MESSAGE);
        selector.onSelect(opModeSupplier -> {
            onSelect();
            selectedOpMode = opModeSupplier.get();
            selectedOpMode.gamepad1 = gamepad1;
            selectedOpMode.gamepad2 = gamepad2;
            selectedOpMode.telemetry = telemetry;
            selectedOpMode.hardwareMap = hardwareMap;
            selectedOpMode.init();
        });
    }

    protected void onSelect() {}

    protected void onLog(List<String> line) {}

    @Override
    public final void init() {}

    @Override
    public final void init_loop() {
        if (selectedOpMode == null) {
            if (gamepad1.dpadUpWasPressed() || gamepad2.dpadUpWasPressed()) {
                selector.decrementSelected();
            } else if (gamepad1.dpadDownWasPressed() || gamepad2.dpadDownWasPressed()) {
                selector.incrementSelected();
            } else if (
                gamepad1.rightBumperWasPressed() ||
                gamepad2.rightBumperWasPressed() ||
                gamepad1.dpadRightWasPressed() ||
                gamepad2.dpadRightWasPressed()
            ) {
                selector.select();
            } else if (
                gamepad1.leftBumperWasPressed() ||
                gamepad2.leftBumperWasPressed() ||
                gamepad1.dpadLeftWasPressed() ||
                gamepad2.dpadLeftWasPressed()
            ) {
                selector.goBack();
            }

            List<String> lines = selector.getLines();
            for (String line : lines) {
                telemetry.addLine(line);
            }
            onLog(lines);
        } else {
            selectedOpMode.init_loop();
        }
    }

    @Override
    public final void start() {
        if (selectedOpMode == null) throw new RuntimeException("No OpMode selected!");
        selectedOpMode.start();
    }

    @Override
    public final void loop() {
        selectedOpMode.loop();
    }

    @Override
    public final void stop() {
        if (selectedOpMode != null) selectedOpMode.stop();
    }
}
