package org.firstinspires.ftc.swervebot;

import com.pedropathing.follower.Follower;
import com.technototes.library.logger.Loggable;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.swervebot.helpers.StartingPosition;
import org.firstinspires.ftc.swervebot.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.swervebot.subsystems.LauncherSubsystem;
import org.firstinspires.ftc.swervebot.subsystems.LimelightSubsystem;
import org.firstinspires.ftc.swervebot.swerveutil.SimpleCoaxSwerveDriveSubsystem;

public class Robot implements Loggable {

    public StartingPosition position;
    public Alliance alliance;

    public double initialVoltage;

    public LauncherSubsystem launcherSubsystem;
    public IntakeSubsystem intakeSubsystem;
    public LimelightSubsystem limelightSubsystem;
    public SimpleCoaxSwerveDriveSubsystem swerveDriveSubsystem;
    public Follower follower;
    private Hardware hardware;

    public Robot(Hardware hw, Alliance team, StartingPosition pos) {
        this.position = pos;
        this.alliance = team;
        this.hardware = hw;
        this.initialVoltage = hw.voltage();

        if (Setup.Connected.INTAKESUBSYSTEM) {
            this.intakeSubsystem = new IntakeSubsystem(hw);
        }
        if (Setup.Connected.LAUNCHERSUBSYSTEM) {
            this.launcherSubsystem = new LauncherSubsystem(hw);
        }
        if (Setup.Connected.LIMELIGHTSUBSYSTEM) {
            this.limelightSubsystem = new LimelightSubsystem(hw);
        }
        if (Setup.Connected.DRIVEBASE) {
            follower = AutoConstants.createFollower(hw.map);
        }
        if (Setup.Connected.SWERVESUBSYSTEM) {
            this.swerveDriveSubsystem = new SimpleCoaxSwerveDriveSubsystem(hw);
        }
    }

    public Hardware getHardware() {
        return hardware;
    }

    public Follower getFollower() {
        return follower;
    }

    public void prepForStart() {}
}
