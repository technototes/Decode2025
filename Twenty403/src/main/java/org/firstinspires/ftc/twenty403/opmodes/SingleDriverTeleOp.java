package org.firstinspires.ftc.twenty403.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.twenty403.AutoConstants;
import org.firstinspires.ftc.twenty403.Hardware;
import org.firstinspires.ftc.twenty403.Robot;
import org.firstinspires.ftc.twenty403.Setup;
import org.firstinspires.ftc.twenty403.commands.EZCmd;
import org.firstinspires.ftc.twenty403.controls.SingleController;
import org.firstinspires.ftc.twenty403.helpers.StartingPosition;

@SuppressWarnings("unused")
@TeleOp(name = "OneDriverTeleOp")
public class SingleDriverTeleOp extends CommandOpMode {

    public Robot robot;
    public Setup setup;
    public SingleController controls;
    public Hardware hardware;

    @Override
    public void uponInit() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.NONE, StartingPosition.Unspecified);
        controls = new SingleController(driverGamepad, robot, setup);
        robot.drivebaseSubsystem.setPoseEstimate(AutoConstants.OBSERVATION_START.toPose());
        CommandScheduler.scheduleForState(
            EZCmd.Drive.NormalMode(robot.drivebaseSubsystem), //was ResetGyro cmd
            OpModeState.INIT
        );
    }

    @Override
    public void uponStart() {
        robot.atStart();
    }
}
