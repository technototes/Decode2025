package org.firstinspires.ftc.sixteen750.controls;

import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.CycleCommandGroup;
import com.technototes.library.control.CommandAxis;
import com.technototes.library.control.CommandButton;
import com.technototes.library.control.CommandGamepad;
import com.technototes.library.control.Stick;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.Setup;
import org.firstinspires.ftc.sixteen750.Setup.OtherSettings;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;
import org.firstinspires.ftc.sixteen750.commands.driving.DrivingCommands;
import org.firstinspires.ftc.sixteen750.commands.driving.JoystickDriveCommand;

public class DriverController {

    public Robot robot;
    public CommandGamepad gamepad;

    public Stick driveLeftStick, driveRightStick;
    public CommandButton resetGyroButton;
    public CommandButton turboButton;
    public CommandButton snailButton;
    public CommandButton launchButton;
    public CommandButton spitButton;
    public CommandButton leverButton;
    public CommandButton brakeButton;
    public CommandButton hoodButton;
    public CommandButton override;
    public CommandAxis intakeTrigger;
    public CommandAxis driveStraighten;
    public CommandAxis drive45;

    public DriverController(CommandGamepad g, Robot r) {
        this.robot = r;
        gamepad = g;
        override = g.leftTrigger.getAsButton(0.5);

        AssignNamedControllerButton();
        if (Setup.Connected.DRIVEBASE) {
            bindDriveControls();
        }
        if (Setup.Connected.LAUNCHERSUBSYSTEM) {
            bindLaunchControls();
        }
        if (Setup.Connected.INTAKESUBSYSTEM) {
            bindIntakeControls();
        }
        if (Setup.Connected.BRAKESUBSYSTEM) {
            bindBrakeControls();
        }
        if (Setup.Connected.AIMINGSUBSYSTEM) {
            bindAimControls();
        }
    }

    private void AssignNamedControllerButton() {
        resetGyroButton = gamepad.ps_options;
        driveLeftStick = gamepad.leftStick;
        driveRightStick = gamepad.rightStick;
        driveStraighten = gamepad.rightTrigger;
        intakeTrigger = gamepad.leftTrigger;
        //drive45 = gamepad.leftTrigger;
        turboButton = gamepad.leftBumper;
        snailButton = gamepad.rightBumper;
        launchButton = gamepad.ps_circle;
        spitButton = gamepad.ps_square;
        leverButton = gamepad.ps_cross;
        brakeButton = gamepad.ps_triangle;
        hoodButton = gamepad.dpadUp;
    }

    public void bindDriveControls() {
        CommandScheduler.scheduleJoystick(
            new JoystickDriveCommand(
                robot.drivebase,
                driveLeftStick,
                driveRightStick,
                driveStraighten,
                drive45
            )
        );

        turboButton.whenPressed(DrivingCommands.TurboDriving(robot.drivebase));
        turboButton.whenReleased(DrivingCommands.NormalDriving(robot.drivebase));
        snailButton.whenPressed(DrivingCommands.SnailDriving(robot.drivebase));
        snailButton.whenReleased(DrivingCommands.NormalDriving(robot.drivebase));
        resetGyroButton.whenPressed(DrivingCommands.ResetGyro(robot.drivebase));
    }

    public void bindLaunchControls() {
        launchButton.whenPressed(TeleCommands.Launch(robot.launcherSubsystem));
        launchButton.whenReleased(TeleCommands.Stop(robot.launcherSubsystem));
    }

    public void bindIntakeControls() {
        spitButton.whilePressed(TeleCommands.Spit(robot.intakeSubsystem));
        spitButton.whileReleased(TeleCommands.Intake(robot.intakeSubsystem));
        intakeTrigger
            .getAsButton(OtherSettings.TRIGGER_THRESHOLD)
            .whilePressed(TeleCommands.Intake(robot.intakeSubsystem));
        intakeTrigger
            .getAsButton(OtherSettings.TRIGGER_THRESHOLD)
            .whileReleased(TeleCommands.IntakeStop(robot.intakeSubsystem));
    }

    public void bindBrakeControls() {
        brakeButton.whenPressed(
            new CycleCommandGroup(
                TeleCommands.EngageBrake(robot.brakeSubsystem),
                TeleCommands.DisengageBrake(robot.brakeSubsystem)
            )
        );
    }

    public void bindAimControls() {
        boolean yippee = true;

        if (yippee) {
            leverButton.whenPressed(TeleCommands.LeverStop(robot.aimingSubsystem));
            yippee = false;
        } else {
            leverButton.whenPressed(TeleCommands.LeverGo(robot.aimingSubsystem));
            yippee = true;
        }

        hoodButton.whenPressed(
            new CycleCommandGroup(
                TeleCommands.HoodUp(robot.aimingSubsystem),
                TeleCommands.HoodDown(robot.aimingSubsystem)
            )
        );
    }
}
