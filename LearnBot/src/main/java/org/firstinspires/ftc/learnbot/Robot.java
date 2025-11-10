package org.firstinspires.ftc.learnbot;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.technototes.library.logger.Loggable;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.learnbot.helpers.StartingPosition;
import org.firstinspires.ftc.learnbot.subsystems.PedroDrivebaseSubsystem;

@Configurable
public class Robot implements Loggable {

    public Alliance alliance;
    public StartingPosition position;

    public double initialVoltage;
    // Subsystems:
    // (Currently, Mouse only has a single subsystem: The drivebase)
    public PedroDrivebaseSubsystem drivebase;

    public Robot(Hardware hw, Alliance team, StartingPosition pos) {
        this.position = pos;
        this.alliance = team;
        this.initialVoltage = hw.voltage();
        if (Setup.Connected.DRIVEBASE) {
            if (Setup.Connected.LIMELIGHT) {
                this.drivebase = new PedroDrivebaseSubsystem(hw.follower, hw.limelight, team);
            } else {
                this.drivebase = new PedroDrivebaseSubsystem(hw.follower, team);
            }
        }
    }

    public void atStart() {}
}
