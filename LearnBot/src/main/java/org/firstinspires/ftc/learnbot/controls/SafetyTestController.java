package org.firstinspires.ftc.learnbot.controls;

import com.technototes.library.control.CommandButton;
import com.technototes.library.control.CommandGamepad;
import org.firstinspires.ftc.learnbot.Robot;
import org.firstinspires.ftc.learnbot.Setup;

public class SafetyTestController {

    public Robot robot;
    public CommandGamepad gamepad;

    public CommandButton odoFFail;
    public CommandButton odoRFail;
    public CommandButton wheelflFail, wheelfrFail, wheelrlFail, wheelrrFail;

    public SafetyTestController(CommandGamepad g, Robot r) {
        this.robot = r;
        gamepad = g;

        AssignNamedControllerButton();
    }

    private void AssignNamedControllerButton() {
        odoFFail = gamepad.leftBumper;
        odoRFail = gamepad.rightBumper;
        wheelflFail = gamepad.ps_triangle;
        wheelfrFail = gamepad.ps_square;
        wheelrlFail = gamepad.ps_cross;
        wheelrrFail = gamepad.ps_circle;
    }

    public void bindDriveControls() {}
}
