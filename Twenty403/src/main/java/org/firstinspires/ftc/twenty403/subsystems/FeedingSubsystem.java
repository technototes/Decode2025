package org.firstinspires.ftc.twenty403.subsystems;

import static org.firstinspires.ftc.twenty403.subsystems.LauncherSubsystem.TARGET_MOTOR_VELOCITY;

import com.bylazar.configurables.annotations.Configurable;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.hardware.motor.CRServo;

import org.firstinspires.ftc.twenty403.Hardware;
import org.firstinspires.ftc.twenty403.Setup;

@Configurable
public class FeedingSubsystem {


    public static double CRSERVO_SPEED = -1; // 0.15 0.25

    boolean hasHardware;
    CRServo bottomLeft, bottomRight;

    public FeedingSubsystem(Hardware h) {
        hasHardware = Setup.Connected.LAUNCHER;
        // Do stuff in here
        if (hasHardware) {
            bottomLeft = h.bottomLeft;
            bottomRight = h.bottomRight;
        } else {
            bottomLeft = null;
            bottomRight = null;
        }
    }



    public void moveball() {
        if (hasHardware) {
            // Doesn't work with configurable launcher velocities
            // Also hardcodes behavior in subsystem instead of letting command do that
//            if (LauncherSubsystem.top.getVelocity() < TARGET_MOTOR_VELOCITY) {
//                // Do not feed the ball if the launcher has not reached the
//                // target velocity. We don't want bland throws.
//                return;
//            }
            bottomRight.setPower(CRSERVO_SPEED);
            bottomLeft.setPower(-CRSERVO_SPEED);
        }
    }



    public void stop() {
        if (hasHardware) {
            bottomLeft.setPower(0);
            bottomRight.setPower(0);
        }
    }
}
