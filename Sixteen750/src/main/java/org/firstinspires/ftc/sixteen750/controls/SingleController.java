package org.firstinspires.ftc.sixteen750.controls;

import com.technototes.library.command.CommandScheduler;
import com.technototes.library.control.CommandButton;
import com.technototes.library.control.CommandGamepad;
import com.technototes.library.control.Stick;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.Setup;
import org.firstinspires.ftc.sixteen750.commands.PedroDriver;
import org.firstinspires.ftc.sixteen750.commands.driving.DrivingCommands;

public class SingleController {

    public Robot robot;
    public Setup setup;
    public CommandGamepad gamepad;
    public Stick driveLeftStick, driveRightStick;
    public CommandButton resetGyroButton, driveStraight, turboButton, snailButton;
    public PedroDriver pedroDriver;

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
        //drive buttons
        resetGyroButton = gamepad.rightStickButton;
        driveLeftStick = gamepad.leftStick;
        driveRightStick = gamepad.rightStick;
        driveStraight = gamepad.ps_circle;

        turboButton = gamepad.rightBumper;
        snailButton = gamepad.leftBumper;
    }

    public void bindDriveControls() {
        pedroDriver = new PedroDriver(
            robot.follower,
            driveLeftStick,
            driveRightStick,
            robot.limelightSubsystem
        );
        CommandScheduler.scheduleJoystick(pedroDriver);
        turboButton.whenPressed(DrivingCommands.TurboDriving(pedroDriver));
        turboButton.whenPressed(DrivingCommands.TurboDriving(pedroDriver));
        turboButton.whenReleased(DrivingCommands.NormalDriving(pedroDriver));
        snailButton.whenPressed(DrivingCommands.SnailDriving(pedroDriver));
        snailButton.whenReleased(DrivingCommands.NormalDriving(pedroDriver));

        resetGyroButton.whenPressed(DrivingCommands.ResetGyro(pedroDriver));
    }
}
