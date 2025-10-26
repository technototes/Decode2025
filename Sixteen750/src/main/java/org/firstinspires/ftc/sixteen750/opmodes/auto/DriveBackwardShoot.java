package org.firstinspires.ftc.sixteen750.opmodes.auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.ParallelCommandGroup;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;
import org.firstinspires.ftc.sixteen750.commands.auto.DriveAutoCommand;
import org.firstinspires.ftc.sixteen750.controls.DriverController;
import org.firstinspires.ftc.sixteen750.helpers.HeadingHelper;
import org.firstinspires.ftc.sixteen750.helpers.StartingPosition;

@Autonomous(name = "DriveBackwardShoot", preselectTeleOp = "Dual Control")
@SuppressWarnings("unused")
public class DriveBackwardShoot extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;

    @Override
    public void uponInit() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.RED, StartingPosition.Net);
        //robot.drivebase.setPoseEstimate(AutoConstants.BACKWARD.toPose());
        hardware.rl.setDirection(DcMotorSimple.Direction.FORWARD);
        hardware.rr.setDirection(DcMotorSimple.Direction.REVERSE);
        hardware.fl.setDirection(DcMotorSimple.Direction.FORWARD);
        hardware.fr.setDirection(DcMotorSimple.Direction.REVERSE);
        CommandScheduler.scheduleForState(
            new SequentialCommandGroup(
                new ParallelCommandGroup(
                    new DriveAutoCommand(robot.drivebase, 0.5),
                    TeleCommands.Intake(robot),
                    TeleCommands.GateUp(robot),
                    TeleCommands.HoodUp(robot)
                ),
                new WaitCommand(1),
                new ParallelCommandGroup(
                    new DriveAutoCommand(robot.drivebase, 0),
                    TeleCommands.Launch(robot)
                ),
                new WaitCommand(4),
                TeleCommands.GateDown(robot),
                new WaitCommand(0.1),
                TeleCommands.GateUp(robot),
                new WaitCommand(0.5),
                TeleCommands.GateDown(robot),
                new WaitCommand(0.1),
                TeleCommands.GateUp(robot),
                new WaitCommand(0.5),
                TeleCommands.GateDown(robot),
                new WaitCommand(2),
                TeleCommands.StopLaunch(robot),
                TeleCommands.IntakeStop(robot),
                CommandScheduler::terminateOpMode
            ),
            OpModeState.RUN
        );
    }

    public void uponStart() {
        robot.prepForStart();
    }

    public void end() {
        HeadingHelper.savePose(robot.drivebase.getPoseEstimate());
    }
}
