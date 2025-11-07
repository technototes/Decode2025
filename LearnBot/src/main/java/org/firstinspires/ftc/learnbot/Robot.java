package org.firstinspires.ftc.learnbot;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.technototes.library.logger.Loggable;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.learnbot.helpers.StartingPosition;

@Configurable
public class Robot implements Loggable {

    public StartingPosition position;
    public Alliance alliance;

    public double initialVoltage;
    // Subsystems:
    // (Currently, Mouse only has a single subsystem: THe drivebase)
    public Follower follower;

    public Robot(Hardware hw, Alliance team, StartingPosition pos) {
        this.position = pos;
        this.alliance = team;
        this.initialVoltage = hw.voltage();
        if (Setup.Connected.DRIVEBASE) {
            this.follower = DrivingConstants.createFollower(hw.map);
        }
    }

    public void atStart() {}
}
