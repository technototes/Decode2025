package org.firstinspires.ftc.twenty403.subsystems;

import static org.firstinspires.ftc.twenty403.subsystems.LauncherSubsystem.MOTOR_VELOCITY;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.hardware.motor.CRServo;
import com.technototes.library.hardware.motor.EncodedMotor;
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
        if (LauncherSubsystem.top.getVelocity() == MOTOR_VELOCITY) {
            if (hasHardware) {
                bottomRight.setPower(CRSERVO_SPEED);
                bottomLeft.setPower(-CRSERVO_SPEED);
            }
        } else {
            moveball();
        }
    }



    public void stop() {
        if (hasHardware) {
            bottomLeft.setPower(0);
            bottomRight.setPower(0);
        }
    }
}
