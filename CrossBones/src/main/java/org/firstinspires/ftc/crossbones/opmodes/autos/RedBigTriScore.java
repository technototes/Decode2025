package org.firstinspires.ftc.crossbones.opmodes.autos;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.crossbones.AutoConstants;
import org.firstinspires.ftc.crossbones.Hardware;
import org.firstinspires.ftc.crossbones.Paths;
import org.firstinspires.ftc.crossbones.Robot;
import org.firstinspires.ftc.crossbones.Setup;
import org.firstinspires.ftc.crossbones.commands.FeedCMD;
import org.firstinspires.ftc.crossbones.commands.PPPathCommand;
import org.firstinspires.ftc.crossbones.commands.auto.DriveAutoCommand;
import org.firstinspires.ftc.crossbones.helpers.HeadingHelper;
import org.firstinspires.ftc.crossbones.helpers.StartingPosition;

@Configurable
@Autonomous(name = "RedBigTriScore", preselectTeleOp = "Two Controller Drive \uD83D\uDDFF")
@SuppressWarnings("unused")
public class RedBigTriScore extends CommandOpMode {

    public Robot robot;
    public Hardware hardware;
    public PathChain bluesmalltobluegoal;
    public Paths p = null;

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.BLUE, StartingPosition.Unspecified);
        SparkFunOTOS otos = hardwareMap.get(SparkFunOTOS.class, Setup.HardwareNames.OTOS);
        otos.calibrateImu();
        robot.follower = AutoConstants.createFollower(hardwareMap);
        p = new Paths(robot.follower);
        Pose start = p.Rstart.setHeading(Math.toRadians(143));
        robot.follower.setPose(start);
        CommandScheduler.register(robot.launcherSubsystem);
        CommandScheduler.scheduleForState(
            new SequentialCommandGroup(
                FeedCMD.Feed(robot),
                new DriveAutoCommand(robot.follower, -.5),
                new WaitCommand(.3),
                new DriveAutoCommand(robot.follower, .5, -.5, -.5, .5),
                new WaitCommand(.9),
                new DriveAutoCommand(robot.follower, 0),
                CommandScheduler::terminateOpMode
            ),
            OpModeState.RUN
        );
        telemetry.addData("Pose:", robot.follower.getPose());
        robot.follower.update();
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
        //        if (robot.follower.getHeading() != robot.follower.getCurr      entPath().getPose()) {}
        robot.follower.update();
    }
}
