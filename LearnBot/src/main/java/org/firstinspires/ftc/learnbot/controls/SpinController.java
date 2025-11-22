package org.firstinspires.ftc.learnbot.controls;

import com.technototes.library.command.Command;
import com.technototes.library.command.CycleCommandGroup;
import com.technototes.library.control.CommandButton;
import com.technototes.library.control.CommandGamepad;
import com.technototes.library.control.Stick;
import com.technototes.library.logger.Loggable;
import org.firstinspires.ftc.learnbot.Hardware;
import org.firstinspires.ftc.learnbot.Robot;
import org.firstinspires.ftc.learnbot.Setup.Connected;
import org.firstinspires.ftc.learnbot.SpinningBot;
import org.firstinspires.ftc.learnbot.commands.JoystickDrive;

public class SpinController implements Loggable {

    public SpinningBot robot;
    public CommandGamepad gamepad;
    public Hardware hardware;

    public CommandButton incTarget;
    public CommandButton decTarget;
    public CommandButton startSpin;
    public CommandButton stopSpin;

    public SpinController(CommandGamepad g, SpinningBot r) {
        this.robot = r;
        gamepad = g;

        AssignNamedControllerButton();
        if (Connected.DRIVEBASE) {
            bindDriveControls();
        }
    }

    private void AssignNamedControllerButton() {
        incTarget = gamepad.dpadUp;
        decTarget = gamepad.dpadDown;
        startSpin = gamepad.rightBumper;
        stopSpin = gamepad.leftBumper;
    }

    public void bindDriveControls() {
        incTarget.whenPressed(robot.spin::increase);
        decTarget.whenPressed(robot.spin::decrease);
        startSpin.whenPressed(robot.spin::start);
        stopSpin.whenPressed(robot.spin::stop);
    }
}
