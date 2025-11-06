package com.technototes.library.control;

import com.technototes.library.command.Command;
import java.util.ArrayList;
import java.util.function.DoubleSupplier;
import java.util.function.Function;

/**
 * Class for command axis for the gamepad
 *
 * @author Alex Stedman
 */
public class CommandAxis extends AxisBase implements CommandInput<CommandAxis> {

    private CommandButton button;
    private ArrayList<CommandAxis> buttons;

    /**
     * Make a command axis
     *
     * @param supplier The axis supplier
     */
    public CommandAxis(DoubleSupplier supplier) {
        super(supplier);
        button = null;
        buttons = null;
    }

    /**
     * Make a command axis
     *
     * @param supplier  The axis supplier
     * @param threshold The threshold to trigger to make the axis behave as a button
     */
    public CommandAxis(DoubleSupplier supplier, double threshold) {
        super(supplier, threshold);
    }

    @Override
    public CommandAxis getInstance() {
        return this;
    }

    @Override
    public CommandAxis setTriggerThreshold(double threshold) {
        super.setTriggerThreshold(threshold);
        return this;
    }

    public CommandAxis schedulePressed(Function<DoubleSupplier, Command> f) {
        return whilePressed(f.apply(this));
    }

    public CommandAxis schedule(Function<Double, Command> f) {
        return schedule(f.apply(this.getAsDouble()));
    }

    @Override
    public CommandAxis setInverted(boolean invert) {
        return (CommandAxis) super.setInverted(invert);
    }

    public CommandButton getAsButton() {
        if (button == null) {
            button = new CommandButton(this);
        }
        return button;
    }

    public CommandButton getAsButton(double threshold) {
        if (buttons == null) {
            buttons = new ArrayList<>(1);
        }
        for (CommandAxis b : buttons) {
            if (Math.abs(b.getTriggerThreshold() - threshold) < 1e-5) {
                return b.getAsButton();
            }
        }
        CommandAxis a = new CommandAxis(this, threshold);
        buttons.add(a);
        return a.getAsButton();
    }

    // You have to manually schedule the 'getAsButton...' things, which is confusing for students
    // (and Mentors :D ) so instead, I'm going to pass the periodic function on down the line.
    // This does have the downsize of leaking any getAsButton objects, but I'm just not that
    // worried about it...
    @Override
    public void periodic() {
        super.periodic();
        if (button != null) {
            button.periodic();
        }
        if (buttons != null) {
            for (CommandAxis b : buttons) {
                b.periodic();
            }
        }
    }
}
