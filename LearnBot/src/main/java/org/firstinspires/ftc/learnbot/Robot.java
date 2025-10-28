package org.firstinspires.ftc.learnbot;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.learnbot.controls.DriverController;
import org.firstinspires.ftc.learnbot.helpers.StartingPosition;
import org.firstinspires.ftc.learnbot.subsystems.DrivebaseSubsystem;
import org.firstinspires.ftc.learnbot.subsystems.FeedingSubsystem;
import org.firstinspires.ftc.learnbot.subsystems.LauncherSubsystem;
import org.firstinspires.ftc.learnbot.subsystems.RROTOSLocalizer;
import org.firstinspires.ftc.learnbot.subsystems.SafetySubsystem;

@Configurable
public class Robot implements Loggable {

    @Log(name = "H")
    public double gyro;

    @Log(name = "X")
    public double xv;

    @Log(name = "Y")
    public double yv;

    @Log(name = "R")
    public double rv;

    @Log(name = "Launcher")
    public double launcherVelociy;

    public StartingPosition position;
    public Alliance alliance;
    public double initialVoltage;

    public DrivebaseSubsystem drivebaseSubsystem;
    public SafetySubsystem safetySubsystem;
    public LauncherSubsystem launcherSubsystem;
    public FeedingSubsystem feedingSubsystem;
    public RROTOSLocalizer localizer;
    public Follower follower;

    public Robot(Hardware hw, Alliance team, StartingPosition pos) {
        this.position = pos;
        this.alliance = team;
        this.initialVoltage = hw.voltage();
        if (Setup.Connected.ODOSUBSYSTEM) {
            this.localizer = new RROTOSLocalizer(hw.odo);
        } else {
            this.localizer = null;
        }
        if (Setup.Connected.DRIVEBASE) {
            this.drivebaseSubsystem = new DrivebaseSubsystem(
                hw.fl,
                hw.fr,
                hw.rl,
                hw.rr,
                hw.imu,
                localizer
            );
        }
        if (Setup.Connected.LAUNCHER) {
            this.launcherSubsystem = new LauncherSubsystem(hw);
        }
        if (Setup.Connected.FEED) {
            this.feedingSubsystem = new FeedingSubsystem(hw);
        }
        if (Setup.Connected.SAFETYSUBSYSTEM) {
            this.safetySubsystem = new SafetySubsystem(hw);
        }
    }

    public void atStart() {}

    public Follower getFollower() {
        return follower;
    }
}
