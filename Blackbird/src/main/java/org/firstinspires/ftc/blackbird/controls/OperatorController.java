package org.firstinspires.ftc.blackbird.controls;

import com.technototes.library.command.CommandScheduler;
import com.technototes.library.control.CommandButton;
import com.technototes.library.control.CommandGamepad;
import com.technototes.library.control.Stick;
import org.firstinspires.ftc.blackbird.Robot;
import org.firstinspires.ftc.blackbird.commands.TeleCommands;

public class OperatorController {

    public Robot robot;
    public CommandGamepad gamepad;

    public OperatorController(CommandGamepad g, Robot r) {
        robot = r;
        gamepad = g;
        AssignNamedControllerButton();
        bindButtonControls();
    }

    private void AssignNamedControllerButton() {}

    private void bindButtonControls() {}
}
