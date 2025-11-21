package org.firstinspires.ftc.twenty403.opmodes.autos;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.twenty403.AutoConstants;
import org.firstinspires.ftc.twenty403.Hardware;
import org.firstinspires.ftc.twenty403.Paths;
import org.firstinspires.ftc.twenty403.Robot;
import org.firstinspires.ftc.twenty403.Setup;
import org.firstinspires.ftc.twenty403.commands.PPPathCommand;
import org.firstinspires.ftc.twenty403.helpers.HeadingHelper;
import org.firstinspires.ftc.twenty403.helpers.StartingPosition;

@Configurable
@Autonomous(name = "BlueSmallTriMove", preselectTeleOp = "Two Controller Drive \uD83D\uDDFF")
@SuppressWarnings("unused")
public class BlueSmallTriMove extends CommandOpMode {

    public Robot robot;
    public Hardware hardware;
    Paths p = null;

    public double MAX_POWER = .75;

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.BLUE, StartingPosition.Unspecified);
        SparkFunOTOS otos = hardwareMap.get(SparkFunOTOS.class, Setup.HardwareNames.OTOS);
        otos.calibrateImu();
        robot.follower = AutoConstants.createFollower(hardwareMap);
        p = new Paths(robot.follower);
        robot.follower.setPose(p.BlueSmall);
        telemetry.addData("Pose:", robot.follower.getPose());
//        robot.follower.setMaxPower(MAX_POWER);
        robot.follower.update();
        CommandScheduler.scheduleForState(new SequentialCommandGroup(new PPPathCommand(robot.follower, p.starttobluegoal), CommandScheduler::terminateOpMode
                ),
                OpModeState.RUN
        );;
    }

    @Override
    public void initLoop() {
        telemetry.addData("Pose:", robot.follower.getPose());
        robot.follower.update();
    }

    @Override
    public void uponStart() {
        //        EZCmd.Drive.ResetGyro(robot.follower);
        robot.atStart();
    }

        public void end() {
            HeadingHelper.savePose(robot.follower.getPose());
        }
    @Override
    public void runLoop() {
        telemetry.addData("Pose:", robot.follower.getPose());
        //        if (robot.follower.getHeading() != robot.follower.getCurrentPath().getPose()) {}
        robot.follower.update();
    }

}
