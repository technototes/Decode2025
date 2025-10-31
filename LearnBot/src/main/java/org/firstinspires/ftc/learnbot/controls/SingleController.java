package org.firstinspires.ftc.learnbot.controls;

import com.technototes.library.command.CommandScheduler;
import com.technototes.library.control.CommandButton;
import com.technototes.library.control.CommandGamepad;
import com.technototes.library.control.Stick;
import org.firstinspires.ftc.learnbot.Robot;
import org.firstinspires.ftc.learnbot.Setup;
import org.firstinspires.ftc.learnbot.commands.EZCmd;
import org.firstinspires.ftc.learnbot.commands.driving.JoystickDriveCommand;

public class SingleController {

    public Robot robot;
    public Setup setup;
    public CommandGamepad gamepad;

    public Stick driveLeftStick, driveRightStick;
    public CommandButton resetGyroButton, driveStraight, turboButton;

    public SingleController(CommandGamepad g, Robot r, Setup s) {
        this.robot = r;
        this.setup = s;
        gamepad = g;

        AssignNamedControllerButton();

        if (Setup.Connected.DRIVEBASE) {
            bindDriveControls();
        }
    }

    private void AssignNamedControllerButton() {
        resetGyroButton = gamepad.rightStickButton;
        driveLeftStick = gamepad.leftStick;
        driveRightStick = gamepad.rightStick;
        turboButton = gamepad.leftStickButton;
        driveStraight = gamepad.rightTrigger.getAsButton(0.5);
    }

    public void bindDriveControls() {
        CommandScheduler.scheduleJoystick(
            new JoystickDriveCommand(robot.follower, driveLeftStick, driveRightStick)
        );
        // turboButton.whenPressed(EZCmd.Drive.TurboMode(robot.drivebaseSubsystem));
        // turboButton.whenReleased(EZCmd.Drive.NormalMode(robot.drivebaseSubsystem));
        // resetGyroButton.whenPressed(EZCmd.Drive.ResetGyro(robot.drivebaseSubsystem));
    }
}
