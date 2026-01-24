package org.firstinspires.ftc.sixteen750.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import com.technototes.library.util.HeadingHelper;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.commands.LLSetup;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;
import org.firstinspires.ftc.sixteen750.commands.auto.DriveAutoCommand;
import org.firstinspires.ftc.sixteen750.commands.auto.Paths;
import org.firstinspires.ftc.sixteen750.controls.DriverController;
import org.firstinspires.ftc.sixteen750.helpers.StartingPosition;
import org.firstinspires.ftc.sixteen750.subsystems.LauncherSubsystem.LauncherCommand;

@Autonomous(name = "FarZoneDriveForward", preselectTeleOp = "Dual Control")
@SuppressWarnings("unused")
public class FarZoneDriveForward extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.RED, StartingPosition.Net);
        // robot.drivebase.setPoseEstimate(PathConstants.BACKWARD.toPose());
        hardware.rl.setDirection(DcMotorSimple.Direction.REVERSE);
        hardware.rr.setDirection(DcMotorSimple.Direction.FORWARD);
        hardware.fl.setDirection(DcMotorSimple.Direction.REVERSE);
        hardware.fr.setDirection(DcMotorSimple.Direction.FORWARD);
        CommandScheduler.register(robot.limelightSubsystem);
        CommandScheduler.scheduleForState(
            new SequentialCommandGroup(
                new LLSetup(robot),
                LauncherCommand.SetFarShoot(),
                LauncherCommand.Launch(),
                TeleCommands.HoodUp(robot),
                new WaitCommand(2),
                Paths.AutoLaunching3Balls(robot),
                LauncherCommand.StopLaunch(),
                TeleCommands.IntakeStop(robot),
                new WaitCommand(18),
                new DriveAutoCommand(robot.follower, 0.5),
                new WaitCommand(.3),
                new DriveAutoCommand(robot.follower, 0),
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
