package org.firstinspires.ftc.sixteen750.controls;

import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.ParallelRaceGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.control.CommandButton;
import com.technototes.library.control.CommandGamepad;
import com.technototes.library.control.Stick;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;
import org.firstinspires.ftc.sixteen750.commands.auto.DriveAutoCommand;
import org.firstinspires.ftc.sixteen750.commands.auto.TurnAutoCommand;

public class OperatorController {

    public Robot robot;
    public CommandGamepad gamepad;
    public CommandButton driveForward;
    public CommandButton turn;

    public OperatorController(CommandGamepad g, Robot r) {
        robot = r;
        gamepad = g;
        AssignNamedControllerButton();
        bindButtonControls();
    }

    private void AssignNamedControllerButton() {
        driveForward = gamepad.ps_triangle;
        turn = gamepad.ps_circle;
    }

    private void bindButtonControls() {
        driveForward.whenPressed(
            new ParallelRaceGroup(new WaitCommand(1), new DriveAutoCommand(robot.follower, 0.5))
        );
        turn.whenPressed(new TurnAutoCommand(robot.follower, 0.2));
    }
}
