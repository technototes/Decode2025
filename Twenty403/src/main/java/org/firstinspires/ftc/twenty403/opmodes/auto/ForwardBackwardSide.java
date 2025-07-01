package org.firstinspires.ftc.twenty403.opmodes.auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.twenty403.AutoConstants;
import org.firstinspires.ftc.twenty403.Hardware;
import org.firstinspires.ftc.twenty403.Robot;
import org.firstinspires.ftc.twenty403.commands.EZCmd;
import org.firstinspires.ftc.twenty403.commands.auto.Paths;
import org.firstinspires.ftc.twenty403.controls.DriverController;
import org.firstinspires.ftc.twenty403.controls.SafetyTestController;
import org.firstinspires.ftc.twenty403.helpers.StartingPosition;

//TODO: figure out where the other splineTest is coming from (duplicate spline error)
@Autonomous(name = "ForwardBackwardSide", preselectTeleOp = "Driving w/Turbo!")
@SuppressWarnings("unused")
public class ForwardBackwardSide extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public SafetyTestController safety;
    public Hardware hardware;

    @Override
    public void uponInit() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.RED, StartingPosition.Observation);
        robot.drivebaseSubsystem.setPoseEstimate(AutoConstants.REST.toPose());
        //        safety = new SafetyTestController(driverGamepad, robot);
        //robot.safetySubsystem.startMonitoring();
        CommandScheduler.scheduleForState(
            new SequentialCommandGroup(
                Paths.ForwardBackwardSide(robot),
                EZCmd.Drive.RecordHeading(robot.drivebaseSubsystem),
                CommandScheduler::terminateOpMode
            ),
            OpModeState.RUN
        );
        //        CommandScheduler.scheduleForState(
        //            new SafetyStartCommand(robot.safetySubsystem),
        //            OpModeState.RUN
        //        );
    }
}
