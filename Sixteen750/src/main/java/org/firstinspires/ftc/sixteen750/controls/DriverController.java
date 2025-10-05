package org.firstinspires.ftc.sixteen750.controls;

import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.CycleCommandGroup;
import com.technototes.library.control.CommandAxis;
import com.technototes.library.control.CommandButton;
import com.technototes.library.control.CommandGamepad;
import com.technototes.library.control.Stick;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.Setup;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;
import org.firstinspires.ftc.sixteen750.commands.driving.DrivingCommands;
import org.firstinspires.ftc.sixteen750.commands.driving.JoystickDriveCommand;

public class DriverController {

    public Robot robot;
    public CommandGamepad gamepad;

    public Stick driveLeftStick, driveRightStick;
    // Need to explain why this is better than a single line to students:
    public CommandButton resetGyroButton;
    public CommandButton turboButton;
    public CommandButton snailButton;
    public CommandButton launchButton;
    public CommandButton leverButton;
    public CommandButton brakeButton;
    public CommandButton hoodButton;
    public CommandButton hooddownButton;
    public CommandButton leverdownButton;
    public CommandButton leverupButton;
    public CommandButton override;
    public CommandAxis intakeTrigger;
    public CommandAxis spitTrigger;
    public CommandButton AutoOrient;
    public static double triggerThreshold = 0.1;

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
        intakeTrigger = gamepad.rightTrigger;
        //drive45 = gamepad.leftTrigger;
        //turboButton = gamepad.leftBumper;
        //snailButton = gamepad.rightBumper;
        launchButton = gamepad.rightBumper;
        spitTrigger = gamepad.leftTrigger;
        leverButton = gamepad.ps_cross;
        brakeButton = gamepad.ps_triangle;
        hoodButton = gamepad.dpadUp;
        hooddownButton = gamepad.dpadDown;
        AutoOrient = gamepad.ps_share;
        leverdownButton = gamepad.dpadLeft;
        leverupButton = gamepad.dpadRight;
    }

    public void bindDriveControls() {
        CommandScheduler.scheduleJoystick(
            new JoystickDriveCommand(robot.drivebase, driveLeftStick, driveRightStick)
        );

        //        turboButton.whenPressed(DrivingCommands.TurboDriving(robot.drivebase));
        //        turboButton.whenReleased(DrivingCommands.NormalDriving(robot.drivebase));
        //        snailButton.whenPressed(DrivingCommands.SnailDriving(robot.drivebase));
        //        snailButton.whenReleased(DrivingCommands.NormalDriving(robot.drivebase));
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
        /* Need to explain why this doesn't work to students:
      
        if (spitTrigger.getAsDouble() > triggerThreshold) {
            TeleCommands.Spit(robot.intakeSubsystem);
        } else {
            TeleCommands.IntakeStop(robot.intakeSubsystem);
        }
        if (intakeTrigger.getAsDouble() > triggerThreshold){
            TeleCommands.Intake(robot.intakeSubsystem);
        } else {
            TeleCommands.IntakeStop(robot.intakeSubsystem);
        }
        
        Instead, we do this:
        (getAsButton creates a button that is "pressed" when the axis is above the threshold)
        */
        spitTrigger
            .getAsButton(triggerThreshold)
            .whenPressed(TeleCommands.Spit(robot.intakeSubsystem));
        spitTrigger
            .getAsButton(triggerThreshold)
            .whenReleased(TeleCommands.IntakeStop(robot.intakeSubsystem));
        intakeTrigger
            .getAsButton(triggerThreshold)
            .whenPressed(TeleCommands.Intake(robot.intakeSubsystem));
        intakeTrigger
            .getAsButton(triggerThreshold)
            .whenReleased(TeleCommands.IntakeStop(robot.intakeSubsystem));
        //        spitTrigger.whilePressed(TeleCommands.Spit(robot.intakeSubsystem));
        //        spitTrigger.whileReleased(TeleCommands.Intake(robot.intakeSubsystem));
    }

    public void bindBrakeControls() {
        brakeButton.whenPressed(
            new CycleCommandGroup(
                TeleCommands.EngageBrake(robot.brakeSubsystem),
                TeleCommands.DisengageBrake(robot.brakeSubsystem)
            )
        );
    }

    boolean yippee = true;

    public void bindAimControls() {
        //                if(yippee) {
        //                    leverButton.whenPressed(
        //                    TeleCommands.LeverStop(robot.aimingSubsystem));
        //                    yippee = false;
        //                }
        //                else {
        //                    leverButton.whenPressed(
        //                    TeleCommands.LeverGo(robot.aimingSubsystem));
        //                    yippee = true;
        //                }
        leverdownButton.whenPressed(TeleCommands.LeverGo(robot.aimingSubsystem));
        leverupButton.whenPressed(TeleCommands.LeverStop(robot.aimingSubsystem));

        //        hoodButton.whenPressed(new CycleCommandGroup(
        //                TeleCommands.HoodUp(robot.aimingSubsystem),
        //                TeleCommands.HoodDown(robot.aimingSubsystem)
        //        ));
        hoodButton.whenPressed(TeleCommands.HoodUp(robot.aimingSubsystem));
        hooddownButton.whenPressed(TeleCommands.HoodDown(robot.aimingSubsystem));
    }
}
