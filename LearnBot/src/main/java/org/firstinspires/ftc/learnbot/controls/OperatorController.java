package org.firstinspires.ftc.learnbot.controls;

import com.technototes.library.command.Command;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.control.CommandButton;
import com.technototes.library.control.CommandGamepad;
import com.technototes.library.control.Stick;
import org.firstinspires.ftc.learnbot.Robot;
import org.firstinspires.ftc.learnbot.Setup;

public class OperatorController {

    public Robot robot;
    public CommandGamepad gamepad;

    public OperatorController(CommandGamepad g, Robot r) {
        robot = r;
        gamepad = g;
        AssignNamedControllerButton();
        BindControls();
    }

    private void AssignNamedControllerButton() {}

    public void BindControls() {}
}
