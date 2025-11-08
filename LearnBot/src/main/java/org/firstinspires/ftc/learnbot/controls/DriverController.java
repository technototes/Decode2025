package org.firstinspires.ftc.learnbot.controls;

import com.technototes.library.command.CycleCommandGroup;
import com.technototes.library.control.CommandButton;
import com.technototes.library.control.CommandGamepad;
import com.technototes.library.control.Stick;
import com.technototes.library.logger.Loggable;
import org.firstinspires.ftc.learnbot.Hardware;
import org.firstinspires.ftc.learnbot.Robot;
import org.firstinspires.ftc.learnbot.Setup.Connected;
import org.firstinspires.ftc.learnbot.Setup.OtherSettings;
import org.firstinspires.ftc.learnbot.commands.Driver;

public class DriverController implements Loggable {

    public Robot robot;
    public CommandGamepad gamepad;
    public Hardware hardware;

    public Stick driveLeftStick, driveRightStick;
    public CommandButton resetGyroButton;
    public CommandButton botFieldToggleButton;
    public CommandButton turboButton;
    public CommandButton snailButton;
    public CommandButton normalButton;
    public CommandButton visionButton;
    public CommandButton straightButton;
    public CommandButton snap90Button;
    public CommandButton squareButton;
    public CommandButton tangentButton;
    public CommandButton holdPosButton;
    public Driver stickDriver;

    public DriverController(CommandGamepad g, Robot r) {
        this.robot = r;
        gamepad = g;

        AssignNamedControllerButton();
        if (Connected.DRIVEBASE) {
            bindDriveControls();
        }
    }

    private void AssignNamedControllerButton() {
        driveLeftStick = gamepad.leftStick;
        driveRightStick = gamepad.rightStick;

        snap90Button = gamepad.rightBumper;
        squareButton = gamepad.rightTrigger.getAsButton(OtherSettings.TRIGGER_THRESHOLD);
        tangentButton = gamepad.leftBumper;
        straightButton = gamepad.leftTrigger.getAsButton(OtherSettings.TRIGGER_THRESHOLD);

        resetGyroButton = gamepad.ps_options;
        botFieldToggleButton = gamepad.ps_share;

        turboButton = gamepad.dpadUp;
        normalButton = gamepad.dpadRight;
        snailButton = gamepad.dpadDown;
        holdPosButton = gamepad.dpadLeft;
    }

    public void bindDriveControls() {
        stickDriver = new Driver(robot.follower, driveLeftStick, driveRightStick);

        turboButton.whenPressed(stickDriver::SetTurboSpeed);
        normalButton.whenPressed(stickDriver::SetNormalSpeed);
        snailButton.whenPressed(stickDriver::SetSnailSpeed);

        if (Connected.LIMELIGHT) {
            visionButton.whenPressedReleased(
                stickDriver::EnableVisionDriving,
                stickDriver::EnableFreeDriving
            );
        }

        snap90Button.whenPressedReleased(
            stickDriver::EnableSnap90Driving,
            stickDriver::EnableFreeDriving
        );
        squareButton.whenPressedReleased(
            stickDriver::EnableSquareDriving,
            stickDriver::EnableFreeDriving
        );
        straightButton.whenPressedReleased(
            stickDriver::EnableStraightDriving,
            stickDriver::EnableFreeDriving
        );
        tangentButton.whenPressedReleased(
            stickDriver::EnableTangentialDriving,
            stickDriver::EnableFreeDriving
        );
        holdPosButton.whenPressedReleased(stickDriver::StayPut, stickDriver::ResumeDriving);

        resetGyroButton.whenPressed(stickDriver::ResetGyro);
        // This is a nifty feature students built last year: We can *cycle* through commands!
        botFieldToggleButton.whenPressed(
            new CycleCommandGroup(
                stickDriver::SetRobotCentricDriveMode,
                stickDriver::SetFieldCentricDriveMode
            )
        );
    }
}
