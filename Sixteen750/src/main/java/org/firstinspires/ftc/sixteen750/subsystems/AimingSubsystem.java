package org.firstinspires.ftc.sixteen750.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.technototes.library.hardware.servo.Servo;

import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Setup;

@Configurable
public class AimingSubsystem {

    public static double HOOD_POS = 0.5; // 0.5 1.0
    public static double LEVER_POS = 0.1;
    public static double LEVER_POS_GO = 0.5;
    boolean hasHardware;
    Servo hood;
    Servo lever;

    public AimingSubsystem(Hardware h) {
        hasHardware = Setup.Connected.AIMINGSUBSYSTEM;
        // Do stuff in here
        if (hasHardware) {
            hood = h.hood;
            lever = h.lever;
        } else {
            hood = null;
            lever = h.lever;
        }
    }

    public void Aim() {
        if (hasHardware) {
            //theres gonna be a lot of math here to aim
            hood.setPosition(HOOD_POS);
        }
    }
    public void StopBall() {
        if (hasHardware) {
            lever.setPosition(LEVER_POS);
        }
    }
    public void GoBall() {
        if (hasHardware) {
            lever.setPosition(LEVER_POS_GO);
        }
    }

}
