package org.firstinspires.ftc.blackbird.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.hardware.servo.Servo;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.LogConfig;
import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.Subsystem;
import org.firstinspires.ftc.blackbird.Hardware;
import org.firstinspires.ftc.blackbird.Setup;

@Configurable
public class AimingSubsystem implements Loggable, Subsystem {

    public static double HOOD_POS = 0.5; // 0.5 1.0
    public static double HOOD_POS_UP = 1; // 0.5 1.0
    public static double HOD_POS_UP_AUTO_ONLY = 0.90;
    public static double HOD_POS_UP_AUTO_ONLY2 = 0.80;

    public static double HOOD_POS_DOWN = 0.4; // 0.5 1.0

    public static double LEVER_POS = 0.7; //.65
    public static double LEVER_POS_GO = 0.4; //0.2

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
        setHoodPos(HOOD_POS_UP);
    }

    public void testHoodUpAutoOnly() {
        setHoodPos(HOD_POS_UP_AUTO_ONLY);
    }

    public void testHoodUpAutoOnly2() {
        setHoodPos(HOD_POS_UP_AUTO_ONLY2);
    }

    public void testHoodDown() {
        setHoodPos(HOOD_POS_DOWN);
    }

    public void StopBall() {
        setLeverPos(LEVER_POS);
    }

    public void GoBall() {
        setLeverPos(LEVER_POS_GO);
    }

    public void DistanceHoodPos() {
        if (ls.getDistance() > -1 && ls.getDistance() < 35) {
            setHoodPos(HOOD_POS_DOWN);
        } else {
            setHoodPos(HOOD_POS_UP);
        }
    }

    @Override
    public void periodic() {
        DistanceHoodPos();
    }
}
