package org.firstinspires.ftc.learnbot;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.learnbot.helpers.StartingPosition;

@Configurable
public class Robot implements Loggable {

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

    public void snail() {
        follower.setMaxPowerScaling(Setup.DriveSettings.SNAIL_SPEED);
    }

    public void normal() {
        follower.setMaxPowerScaling(Setup.DriveSettings.NORMAL_SPEED);
    }

    public void auto() {
        follower.setMaxPowerScaling(Setup.DriveSettings.AUTO_SCALING);
    }

    public void turbo() {
        follower.setMaxPowerScaling(Setup.DriveSettings.TURBO_SPEED);
    }
}
