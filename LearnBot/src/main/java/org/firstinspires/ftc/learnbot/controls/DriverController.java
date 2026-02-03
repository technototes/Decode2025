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
import org.firstinspires.ftc.learnbot.components.Launcher;
import org.firstinspires.ftc.learnbot.components.Pedro;

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
    public CommandButton rotateModeButton;
    public CommandButton driveModeButton;
    public CommandButton holdPosButton;
    public CommandButton snapRotButton;
    public Command stickDriver;
    public CycleCommandGroup rotationCommand;
    public CommandButton launch1Button;
    public CommandButton launch2Button;

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

        rotateModeButton = gamepad.rightBumper;
        driveModeButton = gamepad.leftBumper;

        resetGyroButton = gamepad.ps_options;
        botFieldToggleButton = gamepad.ps_share;

        turboButton = gamepad.dpadUp;
        normalButton = gamepad.dpadRight;
        snailButton = gamepad.dpadDown;
        holdPosButton = gamepad.dpadLeft;
        snapRotButton = gamepad.ps_triangle;

        visionButton = gamepad.ps_circle;
        launch1Button = gamepad.ps_square;
        launch2Button = gamepad.ps_cross;
    }

    public void bindDriveControls() {
        stickDriver = Pedro.Commands.JoystickDrive(driveLeftStick, driveRightStick);

        turboButton.whenPressed(Pedro.Commands.TurboSpeed());
        normalButton.whenPressed(Pedro.Commands.NormalSpeed());
        snailButton.whenPressed(Pedro.Commands.SnailSpeed());

        if (Connected.LIMELIGHT) {
            visionButton.whenPressedReleased(
                robot.drivebase::SetVisionDriving,
                robot.drivebase::ResumeDriving
            );
        }

        rotationCommand = new CycleCommandGroup(
            robot.drivebase::SetHoldRotation,
            robot.drivebase::SetTangentRotation,
            robot.drivebase::SetBidirectionalRotation,
            robot.drivebase::SetVisionRotation,
            // robot.drivebase::SetTargetBasedRotation,
            robot.drivebase::SetFreeRotation
        );

        rotateModeButton.whenPressed(rotationCommand);
        driveModeButton.whenPressed(
            new CycleCommandGroup(
                robot.drivebase::SetSquareMotion,
                // robot.drivebase::SetTargetBasedMotion,
                robot.drivebase::SetFreeMotion
            )
        );
        holdPosButton.whenPressedReleased(robot.drivebase::StayPut, robot.drivebase::ResumeDriving);
        snapRotButton.whenPressedReleased(robot.drivebase::SetSnapRotation, () -> {
            robot.drivebase.SetFreeRotation();
            rotationCommand.reset();
        });
        resetGyroButton.whenPressed(robot.drivebase::ResetGyro);
        // This is a nifty feature students built last year: We can *cycle* through commands!
        botFieldToggleButton.whenReleased(
            new CycleCommandGroup(
                robot.drivebase::SetRobotCentricMode,
                robot.drivebase::SetFieldCentricMode
            )
        );
        launch1Button.whenPressed(Launcher.Commands.AutoLaunch1());
        launch2Button.whenPressed(Launcher.Commands.AutoLaunch2());
    }
}
