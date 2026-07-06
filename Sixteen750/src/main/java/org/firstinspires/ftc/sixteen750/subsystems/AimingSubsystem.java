package org.firstinspires.ftc.sixteen750.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.hardware.servo.Servo;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.Subsystem;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Setup;

@Configurable
public class AimingSubsystem implements Loggable, Subsystem {

    public static double HOOD_POS = 0.5; // 0.5 1.0
    public static double HoodPosDown = 0.45; // 0.5 1.0
    public static double HoodPosMiddle = 0.66;
    public static double HoodPosUp = .81; // 0.5 1.0
    public static double HOD_POS_UP_AUTO_ONLY = 0.90;
    public static double HOD_POS_UP_AUTO_ONLY2 = 0.80;
    public double BangBangAdjust;
    public static double BangBangConstant = 0.0014; // multiplier for ratio of encoder ticks per second to hood adjustment in terms of servo pos ie 0-1 still needs some tuning

    public static double LEVER_POS = 0.7; //.65
    public static double LEVER_POS_GO = 0.4; //0.2
    public static double LastHoodPos = 0;
    public static double HOOD_DOWN_THRESHOLD = 38;
    public static double HOOD_MIDDLE_THRESHOLD = 102;
    public static double DAMN_THAT_IS_A_BIG_NUMBER = Math.pow(2, 8);
    public static double BangBangLowerBound = 0;
    public static double BangBangUpperBound = (BangBangLowerBound / 2 + 25) * -1;

    @Log.Number(name = "leverPos")
    public double leverPos;

    @Log.Number(name = "hoodPos")
    public double hoodPos;

    LimelightSubsystem ls;

    boolean hasHardware;
    Servo hood;
    Servo lever;

    public AimingSubsystem(Hardware h, LimelightSubsystem lls) {
        hasHardware = Setup.Connected.AIMINGSUBSYSTEM;
        // Do stuff in here
        if (hasHardware) {
            hood = h.hood;
            lever = h.lever;
            this.ls = lls;
            CommandScheduler.register(this);
        } else {
            hood = null;
            lever = h.lever;
        }
    }

    public void setHoodPos(double w) {
        if (hasHardware) {
            hoodPos = w;
            hood.setPosition(w);
        }
    }

    public void setLeverPos(double w) {
        if (hasHardware) {
            leverPos = w;
            lever.setPosition(w);
        }
    }

    public void Aim() {
        //theres gonna be a lot of math here to aim
        setHoodPos(HOOD_POS);
    }

    public void testHoodUp() {
        setHoodPos(HoodPosUp);
    }

    public void testHoodUpAutoOnly() {
        setHoodPos(HOD_POS_UP_AUTO_ONLY);
    }

    public void testHoodUpAutoOnly2() {
        setHoodPos(HOD_POS_UP_AUTO_ONLY2);
    }

    public void testHoodDown() {
        setHoodPos(HoodPosDown);
    }

    public void StopBall() {
        setLeverPos(LEVER_POS);
    }

    public void GoBall() {
        setLeverPos(LEVER_POS_GO);
    }

    public double BangBangBounds() {
        double x = ls.getDistance();
        if (x > -1 && x <= 90) {
            BangBangLowerBound = 250;
        } else {
            if (x > 90 && x <= DAMN_THAT_IS_A_BIG_NUMBER) {
                BangBangLowerBound = 400;
            }
        }
        return BangBangLowerBound;
    }

    public void BangBang() {
        if (
            LauncherSubsystem.err < BangBangLowerBound && LauncherSubsystem.err > BangBangUpperBound
        ) {
            // we dont want the hood having a stroke when the lancher starts spinning up cause error is really high so limit to to only when its within shot drop range
            BangBangAdjust = LauncherSubsystem.err * -BangBangConstant;
        } else BangBangAdjust = 0;
    }

    public double AutoHoodPos() {
        double x = ls.getDistance();
        if (x > -1 && x <= HOOD_DOWN_THRESHOLD) {
            LastHoodPos = HoodPosDown + BangBangAdjust;
        } else {
            if (x > HOOD_DOWN_THRESHOLD && x <= HOOD_MIDDLE_THRESHOLD) {
                LastHoodPos = HoodPosMiddle + BangBangAdjust;
            } else {
                if (x > HOOD_MIDDLE_THRESHOLD && x <= DAMN_THAT_IS_A_BIG_NUMBER) {
                    LastHoodPos = HoodPosUp + BangBangAdjust;
                }
            }
        }
        return LastHoodPos;
    }

    public void DistanceHoodPos() {
        if (ls != null) setHoodPos(AutoHoodPos());
    }

    @Override
    public void periodic() {
        BangBangBounds();
        DistanceHoodPos();
        BangBang();
        AutoHoodPos();
    }
}
