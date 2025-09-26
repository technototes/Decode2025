package org.firstinspires.ftc.sixteen750.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.technototes.library.hardware.servo.Servo;

import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Setup;

@Configurable
public class BrakeSubsystem {

    public static double BRAKE_POS = 0.5; // 0.5 1.0
    public static double GO_POS = 0.1;
    boolean hasHardware;
    Servo brake;

    public BrakeSubsystem(Hardware h) {
        hasHardware = Setup.Connected.BRAKESUBSYSTEM;
        // Do stuff in here
        if (hasHardware) {
            brake = h.brake;
        } else {
            brake = null;
        }
    }

    public void Engage() {
        if (hasHardware) {
            brake.setPosition(BRAKE_POS);
        }
    }
    public void Disengage() {
        if (hasHardware) {
            brake.setPosition(GO_POS);
        }
    }
}
