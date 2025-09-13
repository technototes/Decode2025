package org.firstinspires.ftc.twenty403.controls;

import com.technototes.library.control.CommandButton;
import com.technototes.library.control.CommandGamepad;
import org.firstinspires.ftc.twenty403.Robot;
import org.firstinspires.ftc.twenty403.Setup;
import org.firstinspires.ftc.twenty403.commands.auto.SafetyTestOdoFCommand;
import org.firstinspires.ftc.twenty403.commands.auto.SafetyTestOdoRCommand;
import org.firstinspires.ftc.twenty403.commands.auto.SafetyTestWheelFLCommand;
import org.firstinspires.ftc.twenty403.commands.auto.SafetyTestWheelFRCommand;
import org.firstinspires.ftc.twenty403.commands.auto.SafetyTestWheelRLCommand;
import org.firstinspires.ftc.twenty403.commands.auto.SafetyTestWheelRRCommand;

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
        if (Setup.Connected.SAFETYSUBSYSTEM) {
            bindDriveControls();
        }
    }

    private void AssignNamedControllerButton() {
        odoFFail = gamepad.leftBumper;
        odoRFail = gamepad.rightBumper;
        wheelflFail = gamepad.ps_triangle;
        wheelfrFail = gamepad.ps_square;
        wheelrlFail = gamepad.ps_cross;
        wheelrrFail = gamepad.ps_circle;
    }

    public void bindDriveControls() {
        odoFFail.whenPressed(new SafetyTestOdoFCommand(robot.safetySubsystem));
        odoRFail.whenPressed(new SafetyTestOdoRCommand(robot.safetySubsystem));
        wheelflFail.whenPressed(new SafetyTestWheelFLCommand(robot.safetySubsystem));
        wheelfrFail.whenPressed(new SafetyTestWheelFRCommand(robot.safetySubsystem));
        wheelrlFail.whenPressed(new SafetyTestWheelRLCommand(robot.safetySubsystem));
        wheelrrFail.whenPressed(new SafetyTestWheelRRCommand(robot.safetySubsystem));
    }
}
