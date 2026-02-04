package org.firstinspires.ftc.crossbones.opmodes.autos;

import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.crossbones.AutoConstants;
import org.firstinspires.ftc.crossbones.Hardware;
import org.firstinspires.ftc.crossbones.Robot;
import org.firstinspires.ftc.crossbones.Setup;
import org.firstinspires.ftc.crossbones.commands.auto.DriveAutoCommand;
import org.firstinspires.ftc.crossbones.controls.DriverController;
import org.firstinspires.ftc.crossbones.helpers.StartingPosition;

@Autonomous(name = "DriveForward", preselectTeleOp = "Two Controller Drive \uD83D\uDDFF")
@SuppressWarnings("unused")
public class MoveForward extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.RED, StartingPosition.Net);
        SparkFunOTOS otos = hardwareMap.get(SparkFunOTOS.class, Setup.HardwareNames.OTOS);
        otos.calibrateImu();
        robot.follower = AutoConstants.createFollower(hardwareMap);
        //robot.drivebase.setPoseEstimate(AutoConstants.BACKWARD.toPose());
        CommandScheduler.scheduleForState(
            new SequentialCommandGroup(
                new DriveAutoCommand(robot.follower, 0.5),
                new WaitCommand(0.3),
                new DriveAutoCommand(robot.follower, 0),
                CommandScheduler::terminateOpMode
            ),
            OpModeState.RUN
        );
        robot.follower.update();
    }

    @Override
    public void uponStart() {
        robot.atStart();
    }

    @Override
    public void runLoop() {
        telemetry.addData("Pose:", robot.follower.getPose());
        //        if (robot.follower.getHeading() != robot.follower.getCurrentPath().getPose()) {}
        robot.follower.update();
    }
    //    public void end() {
    //        HeadingHelper.savePose(robot.follower.getPose());
    //    }
}
