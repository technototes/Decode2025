package org.firstinspires.ftc.learnbot;

import com.bylazar.configurables.annotations.Configurable;
import com.technototes.library.logger.Loggable;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.learnbot.components.Pedro;
import org.firstinspires.ftc.learnbot.helpers.StartingPosition;
import org.firstinspires.ftc.learnbot.subsystems.TargetSubsystem;

@Configurable
public class Robot implements Loggable {

    public Alliance alliance;
    public StartingPosition position;

    public double initialVoltage;
    // Subsystems:
    // (Currently, Mouse only has a single subsystem: The drivebase)
    public Pedro.Component drivebase;
    public TargetSubsystem vision;

    public Robot(Hardware hw, Alliance team, StartingPosition pos) {
        this.position = pos;
        this.alliance = team;
        this.initialVoltage = hw.voltage();
        if (Setup.Connected.LIMELIGHT) {
            vision = new TargetSubsystem(hw.limelight);
        } else {
            vision = null;
        }
        if (Setup.Connected.DRIVEBASE) {
            // Note that vision may be null, but the drivebase is okay with this.
            this.drivebase = new Pedro.Component(hw.follower, vision, team);
        }
    }

    public void atStart() {}
}
