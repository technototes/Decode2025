package com.technototes.library.command;

import java.util.function.BooleanSupplier;

/**
 * Each time this command(group) is scheduled, the next one
 * in the series will execute. When the last one is executed,
 * it starts over at the first one.
 *
 * The most obvious application is for "toggle" command:
 * something like this:
 * clawToggle = new CycleCommandGroup(claw::open, claw::close);
 */
public class CycleCommandGroup extends ParallelRaceGroup {

    protected int currentState = 0;

    public CycleCommandGroup(Command... commands) {
        // You can't use 'this' in a lambda expression passed in the constructor
        // as it *might* not be fully constructed. So, instead, we create the commands
        // array, then add them to the command group after already constructing the
        // ParallelRaceGroup.
        super();
        assert commands.length > 0;
        ConditionalCommand[] conditionalCommands = new ConditionalCommand[commands.length];
        for (int i = 0; i < commands.length; i++) {
            final int finalI = i; // Need a capture-by-value for this one...
            conditionalCommands[i] = new ConditionalCommand(() -> this.currentState == finalI, commands[i]);
        }
        addCommands(conditionalCommands);
    }

    @Override
    public boolean isFinished() {
        boolean fin = super.isFinished();
        if (fin) {
            currentState = (currentState + 1) % this.commandMap.size();
        }
        return fin;
    }

    public void reset() {
        currentState = 0;
    }
}
