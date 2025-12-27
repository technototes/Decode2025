package org.firstinspires.ftc.learnbot.subsystems;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.technototes.library.hardware.sensor.DigitalSensor;
import com.technototes.library.util.Alliance;
import java.util.function.Supplier;
import org.firstinspires.ftc.learnbot.Hardware;

public class AllianceDetection implements Supplier<Alliance> {

    DigitalSensor redSwitch, blueSwitch;

    public AllianceDetection(DigitalSensor red, DigitalSensor blue) {
        redSwitch = red;
        blueSwitch = blue;
    }

    public AllianceDetection(HardwareMap hwm, String red, String blue) {
        redSwitch = new DigitalSensor(hwm.get(DigitalChannel.class, red), red);
        blueSwitch = new DigitalSensor(hwm.get(DigitalChannel.class, blue), blue);
    }

    public boolean isRed() {
        return !redSwitch.getValue();
    }

    public boolean isBlue() {
        return !blueSwitch.getValue();
    }

    public boolean isNeutral() {
        return !isRed() && !isBlue();
    }

    public boolean isError() {
        return isRed() && isBlue();
    }

    @Override
    public Alliance get() {
        boolean r = isRed();
        boolean b = isBlue();
        if (r == b) {
            return Alliance.NONE;
        } else if (r) {
            return Alliance.RED;
        } else /* if (b) */ {
            return Alliance.BLUE;
        }
    }
}
