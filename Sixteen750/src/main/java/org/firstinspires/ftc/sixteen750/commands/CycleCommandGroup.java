package org.firstinspires.ftc.sixteen750.commands;

import com.technototes.library.command.Command;

public class CycleCommandGroup implements Command {
    protected Command[] commands;
    protected int currentState = 0;
    public CycleCommandGroup(Command...commands){
        assert commands.length > 0;
        this.commands = commands;
    }
    public void execute(){
        commands[currentState].run();
        currentState = (currentState + 1) % commands.length;
    }
}
