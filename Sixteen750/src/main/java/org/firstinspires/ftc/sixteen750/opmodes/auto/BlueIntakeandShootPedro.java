package org.firstinspires.ftc.sixteen750.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.commands.LLSetup;
import org.firstinspires.ftc.sixteen750.commands.PedroPathCommand;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;
import org.firstinspires.ftc.sixteen750.commands.auto.Paths;
import org.firstinspires.ftc.sixteen750.controls.DriverController;
import org.firstinspires.ftc.sixteen750.helpers.HeadingHelper;
import org.firstinspires.ftc.sixteen750.helpers.StartingPosition;

@Autonomous(name = "BlueIntakeandShootPedro", preselectTeleOp = "Dual Control")
@SuppressWarnings("unused")
public class BlueIntakeandShootPedro extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.RED, StartingPosition.Net);
        Paths p = new Paths(robot.follower);
        robot.follower.setStartingPose(p.getStart());
        CommandScheduler.register(robot.limelightSubsystem);
        CommandScheduler.scheduleForState(
            new SequentialCommandGroup(
                new LLSetup(robot),
                TeleCommands.GateUp(robot),
                TeleCommands.Intake(robot),
                TeleCommands.HoodUpAutoOnly(robot),
                new PedroPathCommand(robot.follower, p.launch),
                Paths.Launching3Balls(robot),
                // new WaitCommand(0.5),
                new PedroPathCommand(robot.follower, p.launchtointake1),
                // new WaitCommand(1),
                new PedroPathCommand(robot.follower, p.intake1tolaunch),
                // new WaitCommand(2),
                Paths.AutoLaunching3Balls(robot),
                new PedroPathCommand(robot.follower, p.launchtointake2),
                // new WaitCommand(4),
                new PedroPathCommand(robot.follower, p.intake2tolaunch),
                Paths.AutoLaunching3Balls(robot),
                // new WaitCommand(4),
                new PedroPathCommand(robot.follower, p.launchtopark),
                // new WaitCommand(4),
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
        HeadingHelper.savePose(robot.follower.getPose());
        robot.limelightSubsystem.LimelightTurnOff();
    }
}
