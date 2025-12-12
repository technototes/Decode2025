package org.firstinspires.ftc.sixteen750.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.technototes.library.hardware.servo.Servo;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.Subsystem;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Setup;

@Configurable
public class BrakeSubsystem implements Loggable, Subsystem {

    public static double BRAKE_POS = 0.7; // 0.5 1.0
    public static double GO_POS = 0.5;

    @Log(name = "brakePos")
    public double brakePos;

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

    public void setBrakePos(double w) {
        if (hasHardware) {
            brakePos = w;
            brake.setPosition(w);
        }
    }

    public void Engage() {
        setBrakePos(BRAKE_POS);
    }

    public void Disengage() {
        setBrakePos(GO_POS);
    }
}
