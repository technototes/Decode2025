package org.firstinspires.ftc.sixteen750.commands;

import com.technototes.library.command.Command;

import org.firstinspires.ftc.sixteen750.Robot;

public class TeleCommands {

    public static Command Launch(Robot r) {
        return Command.create(r.launcherSubsystem::Launch);
    }
    public static Command StopLaunch(Robot r) {
        return Command.create(r.launcherSubsystem::Stop);
    }
    public static Command IncreaseMotor(Robot r) {
        return Command.create(r.launcherSubsystem::IncreaseMotorSpeed);
    }
    public static Command DecreaseMotor(Robot r) {
        return Command.create(r.launcherSubsystem::DecreaseMotorSpeed);
    }
    public static Command Intake(Robot r) {
        return Command.create(r.intakeSubsystem::Intake);
    }
    public static Command IntakeStop(Robot r) {
        return Command.create(r.intakeSubsystem::StopIntake);
    }

    public static Command Spit(Robot r) {
        return Command.create(r.intakeSubsystem::Spit);
    }

    public static Command EngageBrake(Robot r) {
        return Command.create(r.brakeSubsystem::Engage);
    }
    public static Command HoldIntake(Robot r) {
        return Command.create(r.intakeSubsystem::Hold);
    }

    public static Command DisengageBrake(Robot r) {
        return Command.create(r.brakeSubsystem::Disengage);
    }

    public static Command Aim(Robot r) {
        return Command.create(r.aimingSubsystem::Aim);
    }
    public static Command HoodUp(Robot r){ return Command.create(r.aimingSubsystem::testHoodUp);}
    public static Command HoodDown(Robot r){ return Command.create(r.aimingSubsystem::testHoodDown);}


    public static Command GateUp(Robot r) {
        return Command.create(r.aimingSubsystem::StopBall);
    }

    public static Command GateDown(Robot r) {
        return Command.create(r.aimingSubsystem::GoBall);
    }
}
