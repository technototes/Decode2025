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
    public CommandButton snailButton;
    public CommandButton launchButton;
    public CommandButton spitButton;
    public CommandButton intakeButton;

    public CommandButton gateButtonOpen;
    public CommandButton gateButtonClosed;
    public CommandButton gateButton;
    public CommandButton brakeButton;
    public CommandButton hoodButton;
    public CommandButton override;
    public CommandButton hooddownButton;
    public CommandButton holdButton;
    public CommandAxis intakeTrigger;
    public CommandAxis spitTrigger;
    public CommandAxis AutoOrient;

    public static double triggerThreshold = 0.1;

    public DriverController(CommandGamepad g, Robot r) {
        this.robot = r;
        gamepad = g;
        override = g.leftTrigger.getAsButton(0.5);
        override = g.rightTrigger.getAsButton(0.5);

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
        intakeTrigger = gamepad.rightTrigger;
        intakeButton = gamepad.ps_cross;
        // drive45 = gamepad.leftTrigger;
        // turboButton = gamepad.leftBumper;
        snailButton = gamepad.leftBumper;
        launchButton = gamepad.rightBumper;
        spitButton = gamepad.ps_square;
        brakeButton = gamepad.ps_triangle;
        hoodButton = gamepad.dpadUp;
        hooddownButton = gamepad.dpadDown;
        AutoOrient = gamepad.leftTrigger;
        //gateButtonOpen = gamepad.dpadLeft;
        //gateButtonClosed = gamepad.dpadRight;
        gateButton = gamepad.ps_circle;
        holdButton = gamepad.dpadLeft;
    }

    public void bindDriveControls() {
        CommandScheduler.scheduleJoystick(
            new JoystickDriveCommand(robot.drivebase, driveLeftStick, driveRightStick)
        );

        // turboButton.whenPressed(DrivingCommands.TurboDriving(robot.drivebase));
        // turboButton.whenReleased(DrivingCommands.NormalDriving(robot.drivebase));
        snailButton.whenPressed(DrivingCommands.SnailDriving(robot.drivebase));
        snailButton.whenReleased(DrivingCommands.NormalDriving(robot.drivebase));
        resetGyroButton.whenPressed(DrivingCommands.ResetGyro(robot.drivebase));
        if (Setup.Connected.LIMELIGHT) {
            AutoOrient.whenPressed(DrivingCommands.AutoOrient(robot.drivebase));
        }
    }

    public void bindLaunchControls() {
        launchButton.whenPressed(TeleCommands.Launch(robot.launcherSubsystem));
        launchButton.whenReleased(TeleCommands.Stop(robot.launcherSubsystem));
    }

    public void bindIntakeControls() {
        spitButton.whenPressed(TeleCommands.Spit(robot.intakeSubsystem));
        spitButton.whenReleased(TeleCommands.IntakeStop(robot.intakeSubsystem));
        intakeTrigger.whenPressed(TeleCommands.Intake(robot.intakeSubsystem));
        intakeTrigger.whenReleased(TeleCommands.IntakeStop(robot.intakeSubsystem));
        intakeButton.whenPressed(TeleCommands.Intake(robot.intakeSubsystem));
        intakeButton.whenReleased(TeleCommands.IntakeStop(robot.intakeSubsystem));
    }

    // spitTrigger.whilePressed(TeleCommands.Spit(robot.intakeSubsystem));
    // spitTrigger.whileReleased(TeleCommands.Intake(robot.intakeSubsystem));

    public void bindBrakeControls() {
        brakeButton.whenPressed(
            new CycleCommandGroup(
                TeleCommands.EngageBrake(robot.brakeSubsystem),
                TeleCommands.DisengageBrake(robot.brakeSubsystem)
            )
        );
    }

    public void bindAimControls() {
        // if(yippee) {
        //     leverButton.whenPressed(
        //     TeleCommands.LeverStop(robot.aimingSubsystem));
        //     yippee = false;
        // } else {
        //     leverButton.whenPressed(
        //     TeleCommands.LeverGo(robot.aimingSubsystem));
        //     yippee = true;
        // }
        gateButton.whenPressed(TeleCommands.LeverGo(robot.aimingSubsystem));
        gateButton.whenReleased(TeleCommands.LeverStop(robot.aimingSubsystem));

        // hoodButton.whenPressed(new CycleCommandGroup(
        //         TeleCommands.HoodUp(robot.aimingSubsystem),
        //         TeleCommands.HoodDown(robot.aimingSubsystem)
        // ));
        hoodButton.whenPressed(TeleCommands.HoodUp(robot.aimingSubsystem));
        hooddownButton.whenPressed(TeleCommands.HoodDown(robot.aimingSubsystem));

        holdButton.whenPressed(TeleCommands.Hold(robot.intakeSubsystem));
        holdButton.whenReleased(TeleCommands.IntakeStop(robot.intakeSubsystem));
    }
}
