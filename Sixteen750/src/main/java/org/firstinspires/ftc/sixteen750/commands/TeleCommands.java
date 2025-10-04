package org.firstinspires.ftc.sixteen750.commands;

import com.technototes.library.command.Command;

import org.firstinspires.ftc.sixteen750.subsystems.AimingSubsystem;
import org.firstinspires.ftc.sixteen750.subsystems.BrakeSubsystem;
import org.firstinspires.ftc.sixteen750.subsystems.DrivebaseSubsystem;
import org.firstinspires.ftc.sixteen750.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.sixteen750.subsystems.LauncherSubsystem;

public class TeleCommands {

    public static Command Launch(LauncherSubsystem ls) {
        return Command.create(ls::Launch);
    }
    public static Command Stop(LauncherSubsystem ls) {
        return Command.create(ls::Stop);
    }

    public static Command Intake(IntakeSubsystem is) {
        return Command.create(is::Intake);
    }
    public static Command IntakeStop(IntakeSubsystem is) {
        return Command.create(is::StopIntake);
    }

    public static Command Spit(IntakeSubsystem is) {
        return Command.create(is::Spit);
    }

    public static Command EngageBrake(BrakeSubsystem bs) {
        return Command.create(bs::Engage);
    }

    public static Command DisengageBrake(BrakeSubsystem bs) {
        return Command.create(bs::Disengage);
    }

    public static Command Aim(AimingSubsystem as) {
        return Command.create(as::Aim);
    }
    public static Command HoodUp(AimingSubsystem as){ return Command.create(as::testHoodUp);}
    public static Command HoodDown(AimingSubsystem as){ return Command.create(as::testHoodDown);}


    public static Command LeverStop(AimingSubsystem as) {
        return Command.create(as::StopBall);
    }

    public static Command LeverGo(AimingSubsystem as) {
        return Command.create(as::GoBall);
    }
}
