package org.firstinspires.ftc.twenty403.opmodes.auto.pathtesting;

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
import org.firstinspires.ftc.twenty403.commands.auto.Testing;
import org.firstinspires.ftc.twenty403.helpers.StartingPosition;

//TODO: figure out where the other splineTest is coming from (duplicate spline error)
@Autonomous(name = "SplineTest1", preselectTeleOp = "Driving w/Turbo!")
@SuppressWarnings("unused")
public class SplineTest1 extends CommandOpMode {

    public Hardware hardware;
    public Robot robot;

    @Override
    public void uponInit() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.RED, StartingPosition.Net);
        robot.drivebaseSubsystem.setPoseEstimate(AutoConstants.OBSERVATION_START.toPose());
        //        safety = new SafetyTestController(driverGamepad, robot);
        //robot.safetySubsystem.startMonitoring();
        CommandScheduler.scheduleForState(
            new SequentialCommandGroup(
                Testing.SplineTest1(robot),
                Testing.SplineTest2(robot),
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
