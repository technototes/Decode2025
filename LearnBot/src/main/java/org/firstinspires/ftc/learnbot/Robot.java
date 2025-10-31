package org.firstinspires.ftc.learnbot;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.learnbot.helpers.StartingPosition;

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

    public StartingPosition position;
    public Alliance alliance;

    public double initialVoltage;
    public Follower follower;

    public Robot(Hardware hw, Alliance team, StartingPosition pos) {
        this.position = pos;
        this.alliance = team;
        this.initialVoltage = hw.voltage();
        if (Setup.Connected.DRIVEBASE) {
            this.follower = AutoConstants.createFollower(hw.map);
        }
    }

    public void atStart() {}

    public Follower getFollower() {
        return follower;
    }
}
