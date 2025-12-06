package org.firstinspires.ftc.twenty403.opmodes.autos;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.Pose;
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
import org.firstinspires.ftc.twenty403.commands.FeedCMD;
import org.firstinspires.ftc.twenty403.commands.PPPathCommand;
import org.firstinspires.ftc.twenty403.helpers.HeadingHelper;
import org.firstinspires.ftc.twenty403.helpers.StartingPosition;

@Configurable
@Autonomous(name = "BlueBigTriScore", preselectTeleOp = "Two Controller Drive \uD83D\uDDFF")
@SuppressWarnings("unused")
public class BlueBigTriScore extends CommandOpMode {

    public Robot robot;
    public Hardware hardware;
    private Paths p;

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.BLUE, StartingPosition.Unspecified);
        SparkFunOTOS otos = hardwareMap.get(SparkFunOTOS.class, Setup.HardwareNames.OTOS);
        otos.calibrateImu();
        robot.follower = AutoConstants.createFollower(hardwareMap);
        p = new Paths(robot.follower);
        Pose start = p.start.setHeading(Math.toRadians(37));
        robot.follower.setPose(p.start);
        telemetry.addData("Pose:", robot.follower.getPose());
        robot.follower.update();
        CommandScheduler.register(robot.launcherSubsystem);
        CommandScheduler.scheduleForState( new SequentialCommandGroup(
                        FeedCMD.Feed(robot),
                        new PPPathCommand(robot.follower, p.BlueGoalToEscape),
                CommandScheduler::terminateOpMode
                ),
                OpModeState.RUN);
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
