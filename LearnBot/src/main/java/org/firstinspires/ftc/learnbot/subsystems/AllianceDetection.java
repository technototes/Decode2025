package org.firstinspires.ftc.learnbot.subsystems;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.technototes.library.util.Alliance;
import java.util.function.Supplier;
import org.firstinspires.ftc.learnbot.Hardware;

public class AllianceDetection implements Supplier<Alliance> {

    DigitalChannel redSwitch, blueSwitch;

    public AllianceDetection(Hardware hw) {
        redSwitch = hw.redSwitch;
        blueSwitch = hw.blueSwitch;
    }

    public AllianceDetection(DigitalChannel red, DigitalChannel blue) {
        redSwitch = red;
        blueSwitch = blue;
    }

    public AllianceDetection(HardwareMap hwm, String red, String blue) {
        redSwitch = hwm.get(DigitalChannel.class, red);
        blueSwitch = hwm.get(DigitalChannel.class, blue);
    }

    public boolean isRed() {
        return !redSwitch.getState();
    }

    public boolean isBlue() {
        return !blueSwitch.getState();
    }

    public boolean isNeutral() {
        return !isRed() && !isBlue();
    }

    public boolean isError() {
        return isRed() && isBlue();
    }

    @Override
    public Alliance get() {
        if (isRed()) {
            return Alliance.RED;
        }
        if (isBlue()) {
            return Alliance.BLUE;
        }
        return Alliance.NONE;
    }
}
