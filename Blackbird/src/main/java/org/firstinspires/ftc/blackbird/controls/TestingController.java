package org.firstinspires.ftc.blackbird.controls;

import com.bylazar.configurables.annotations.Configurable;
import com.technototes.library.command.Command;
import com.technototes.library.control.CommandButton;
import com.technototes.library.control.CommandGamepad;
import org.firstinspires.ftc.blackbird.Robot;
import org.firstinspires.ftc.blackbird.Setup;
import org.firstinspires.ftc.blackbird.commands.TeleCommands;

public class TestingController {

    public Robot robot;
    public CommandGamepad gamepad;

    public CommandButton motorPowerButton;
    public CommandButton motorVelocityButton;
    public CommandButton TurretMoveToPose;
    public CommandButton TurretMoveToPose90;

    public TestingController(CommandGamepad g, Robot r) {
        robot = r;
        gamepad = g;
        AssignNamedControllerButton();
        bindButtonControls();
    }

    private void AssignNamedControllerButton() {
        // motorPowerButton = gamepad.ps_circle;
        // motorVelocityButton = gamepad.ps_triangle;
        TurretMoveToPose = gamepad.ps_cross;
        TurretMoveToPose90 = gamepad.ps_square;
    }

    private void bindButtonControls() {
        // motorPowerButton.whenPressed(TeleCommands.MotorPowerTest(robot));
        // motorPowerButton.whenReleased(TeleCommands.)
        // motorVelocityButton.whenPressed(TeleCommands.MotorVelocityTest(robot));
        TurretMoveToPose.whenPressed(Command.create(robot.turretSubsystem::turretGoToZero));
        TurretMoveToPose90.whenPressed(Command.create(robot.turretSubsystem::turretGoTo90));
    }
}
