package org.firstinspires.ftc.sixteen750.controls;

import com.technototes.library.command.CommandScheduler;
import com.technototes.library.control.CommandAxis;
import com.technototes.library.control.CommandButton;
import com.technototes.library.control.CommandGamepad;
import com.technototes.library.control.Stick;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.Setup;
import org.firstinspires.ftc.sixteen750.commands.PedroDriver;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;
import org.firstinspires.ftc.sixteen750.commands.driving.DrivingCommands;
import org.firstinspires.ftc.sixteen750.subsystems.LimelightSubsystem;

public class DriverController {

    public Robot robot;
    public Hardware hardware;
    public CommandGamepad gamepad;

    public Stick driveLeftStick, driveRightStick;
    public CommandButton resetGyroButton;
    public CommandButton snailButton;
    public CommandButton launchButton;
    public CommandButton spitButton;
    public CommandButton intakeButton;

    public CommandButton MotorDecrease;
    public CommandButton MotorIncrease;
    public CommandButton gateButton;
    public CommandButton brakeButton;
    public CommandButton hoodButton;
    public CommandButton override;
    public CommandButton hooddownButton;
    public CommandButton holdButton;
    public CommandAxis intakeTrigger;
    public CommandAxis autoAim;
    public CommandAxis spitTrigger;
    public CommandAxis AutoOrient;
    public PedroDriver pedroDriver;

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
        //autoAim = gamepad.leftTrigger;
        // turboButton = gamepad.leftBumper;
        snailButton = gamepad.leftBumper;
        launchButton = gamepad.rightBumper;
        spitButton = gamepad.ps_square;
        brakeButton = gamepad.ps_triangle;
        hoodButton = gamepad.dpadUp;
        hooddownButton = gamepad.dpadDown;
        AutoOrient = gamepad.leftTrigger;
        MotorDecrease = gamepad.dpadLeft;
        MotorIncrease = gamepad.dpadRight;
        gateButton = gamepad.ps_circle;
        holdButton = gamepad.dpadLeft;
    }

    public void bindDriveControls() {
        pedroDriver = new PedroDriver(robot.follower, driveLeftStick, driveRightStick, robot.limelightSubsystem);
        CommandScheduler.scheduleJoystick(pedroDriver);

        // turboButton.whenPressed(DrivingCommands.TurboDriving(robot.drivebase));
        // turboButton.whenReleased(DrivingCommands.NormalDriving(robot.drivebase));
        snailButton.whenPressedReleased(DrivingCommands.SnailDriving(pedroDriver),
                DrivingCommands.NormalDriving(pedroDriver));
        resetGyroButton.whenPressed(DrivingCommands.ResetGyro(pedroDriver));
        MotorDecrease.whenPressed(TeleCommands.DecreaseMotor(robot));
        MotorIncrease.whenPressed(TeleCommands.IncreaseMotor(robot));

        if (Setup.Connected.LIMELIGHTSUBSYSTEM) {
//            AutoOrient.whenPressed(DrivingCommands.AutoOrient(pedroDriver));
        }
        // autoAim.whilePressed(new LLPipelineChangeCommand(hardware.limelight, Setup.HardwareNames.AprilTag_Pipeline));
    }

    public void bindLaunchControls() {
        launchButton.whenPressed(TeleCommands.Launch(robot));
        launchButton.whenReleased(TeleCommands.StopLaunch(robot));
    }

    public void bindIntakeControls() {
        spitButton.whenPressed(TeleCommands.Spit(robot));
        spitButton.whenReleased(TeleCommands.IntakeStop(robot));
        intakeTrigger.whenPressed(TeleCommands.Intake(robot));
        intakeTrigger.whenReleased(TeleCommands.IntakeStop(robot));
        intakeButton.whenPressed(TeleCommands.Intake(robot));
        intakeButton.whenReleased(TeleCommands.IntakeStop(robot));
    }

    // spitTrigger.whilePressed(TeleCommands.Spit(robot.intakeSubsystem));
    // spitTrigger.whileReleased(TeleCommands.Intake(robot.intakeSubsystem));

    public void bindBrakeControls() {
        brakeButton.whilePressed(TeleCommands.EngageBrake(robot));
        brakeButton.whenReleased(TeleCommands.DisengageBrake(robot));
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
        gateButton.whenPressed(TeleCommands.GateDown(robot));
        gateButton.whenReleased(TeleCommands.GateUp(robot));

        // hoodButton.whenPressed(new CycleCommandGroup(
        //         TeleCommands.HoodUp(robot.aimingSubsystem),
        //         TeleCommands.HoodDown(robot.aimingSubsystem)
        // ));
        hoodButton.whenPressed(TeleCommands.HoodUp(robot));
        hooddownButton.whenPressed(TeleCommands.HoodDown(robot));

        holdButton.whilePressed(TeleCommands.HoldIntake(robot));
        holdButton.whenReleased(TeleCommands.IntakeStop(robot));
    }
}
