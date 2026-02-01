package org.firstinspires.ftc.swervebot.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.swervebot.Hardware;
import org.firstinspires.ftc.swervebot.Robot;
import org.firstinspires.ftc.swervebot.Setup;
import org.firstinspires.ftc.swervebot.controls.OperatorController;
import org.firstinspires.ftc.swervebot.controls.SingleController;
import org.firstinspires.ftc.swervebot.controls.TestingController;
import org.firstinspires.ftc.swervebot.helpers.StartingPosition;

@TeleOp(name = "TestingController")
public class ServoTestingOpMode extends CommandOpMode {

    public Robot robot;
    public Setup setup;
    public TestingController controls;
    public Hardware hardware;

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.NONE, StartingPosition.Unspecified);
        controls = new TestingController(codriverGamepad, robot);
        //        CommandScheduler
        //
        //            .scheduleForState(new DroneStart(robot.drone), OpModeState.INIT);
    }
}
