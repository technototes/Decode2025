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
import org.firstinspires.ftc.learnbot.commands.DriveCmds;
import org.firstinspires.ftc.learnbot.commands.JoystickDriveCommand;

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
    public JoystickDriveCommand stickDriver;

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
        visionButton = gamepad.dpadLeft;
    }

    public void bindDriveControls() {
        stickDriver = new JoystickDriveCommand(robot.follower, driveLeftStick, driveRightStick);

        turboButton.whenPressed(DriveCmds.TurboMode(stickDriver));
        normalButton.whenPressed(DriveCmds.NormalMode(stickDriver));
        snailButton.whenPressed(DriveCmds.SnailMode(stickDriver));

        if (Connected.LIMELIGHT) {
            visionButton.whenPressed(DriveCmds.AutoAim(stickDriver));
            visionButton.whenReleased(DriveCmds.NormalMode(stickDriver));
        }

        snap90Button.whenPressed(stickDriver::EnableSnap90Driving);
        snap90Button.whenReleased(stickDriver::EnableFreeDriving);
        squareButton.whenPressed(stickDriver::EnableSquareDriving);
        squareButton.whenReleased(stickDriver::EnableFreeDriving);
        straightButton.whenPressed(stickDriver::EnableStraightDriving);
        straightButton.whenReleased(stickDriver::EnableFreeDriving);
        tangentButton.whenPressed(stickDriver::EnableTangentialDriving);
        tangentButton.whenReleased(stickDriver::EnableFreeDriving);

        resetGyroButton.whenPressed(DriveCmds.ResetGyro(stickDriver));
        botFieldToggleButton.whenPressed(
            new CycleCommandGroup(
                stickDriver::SetRobotCentricDriveMode,
                stickDriver::SetFieldCentricDriveMode
            )
        );
    }
}
