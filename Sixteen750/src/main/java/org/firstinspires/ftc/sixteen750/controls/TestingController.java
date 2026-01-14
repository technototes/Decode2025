package org.firstinspires.ftc.sixteen750.controls;

import com.bylazar.configurables.annotations.Configurable;
import com.technototes.library.command.Command;
import com.technototes.library.control.CommandButton;
import com.technototes.library.control.CommandGamepad;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.Setup;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;
public class TestingController {

    public Robot robot;
    public CommandGamepad gamepad;

    public CommandButton motorPowerButton;
    public CommandButton motorVelocityButton;
    public CommandButton TurretMoveToPose;

    public TestingController(CommandGamepad g, Robot r) {
        robot = r;
        gamepad = g;
        AssignNamedControllerButton();
        bindButtonControls();
    }

    private void AssignNamedControllerButton() {
//        motorPowerButton = gamepad.ps_circle;
//        motorVelocityButton = gamepad.ps_triangle;
        TurretMoveToPose = gamepad.ps_cross;
    }

    private void bindButtonControls() {
//        motorPowerButton.whenPressed(TeleCommands.MotorPowerTest(robot));
//        // motorPowerButton.whenReleased(TeleCommands.)/
//        motorVelocityButton.whenPressed(TeleCommands.MotorVelocityTest(robot));
        TurretMoveToPose.whenPressed(Command.create(robot.turretSubsystem::turretzero));
    }
}
