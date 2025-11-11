package org.firstinspires.ftc.learnbot.controls;

import com.technototes.library.command.CycleCommandGroup;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.control.CommandButton;
import com.technototes.library.control.CommandGamepad;
import com.technototes.library.control.Stick;
import com.technototes.library.logger.Loggable;
import org.firstinspires.ftc.learnbot.Hardware;
import org.firstinspires.ftc.learnbot.Robot;
import org.firstinspires.ftc.learnbot.Setup.Connected;
import org.firstinspires.ftc.learnbot.Setup.OtherSettings;
import org.firstinspires.ftc.learnbot.commands.JoystickDrive;
import org.firstinspires.ftc.learnbot.subsystems.PedroDrivebaseSubsystem;

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
    public JoystickDrive stickDriver;

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

        visionButton = gamepad.ps_circle;
    }

    public void bindDriveControls() {
        stickDriver = new JoystickDrive(robot.drivebase, driveLeftStick, driveRightStick);

        turboButton.whenPressed(robot.drivebase::SetTurboSpeed);
        normalButton.whenPressed(robot.drivebase::SetNormalSpeed);
        snailButton.whenPressed(robot.drivebase::SetSnailSpeed);

        if (Connected.LIMELIGHT) {
            visionButton.whenPressedReleased(
                robot.drivebase::SetVisionRotation,
                robot.drivebase::SetFreeRotation
            );
        }

        rotateModeButton.whenPressed(
            new CycleCommandGroup(
                robot.drivebase::SetSnapRotation,
                robot.drivebase::SetHoldRotation,
                robot.drivebase::SetTangentRotation,
                //robot.drivebase::SetTargetBasedRotation,
                robot.drivebase::SetFreeRotation
            )
        );
        driveModeButton.whenPressed(
            new CycleCommandGroup(
                robot.drivebase::SetSquareMotion,
                //robot.drivebase::SetTargetBasedMotion,
                robot.drivebase::SetFreeMotion
            )
        );
        holdPosButton.whenPressedReleased(robot.drivebase::StayPut, robot.drivebase::ResumeDriving);

        resetGyroButton.whenPressed(robot.drivebase::ResetGyro);
        // This is a nifty feature students built last year: We can *cycle* through commands!
        botFieldToggleButton.whenReleased(
            new CycleCommandGroup(
                robot.drivebase::SetRobotCentricMode,
                robot.drivebase::SetFieldCentricMode
            )
        );
    }
}
