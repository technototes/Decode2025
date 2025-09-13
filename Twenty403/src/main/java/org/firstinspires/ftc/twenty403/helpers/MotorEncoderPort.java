package org.firstinspires.ftc.twenty403.helpers;

import com.technototes.library.hardware.sensor.encoder.MotorEncoder;
import com.technototes.library.hardware.sensor.encoder.MotorEncoder.Direction;

public class MotorEncoderPort implements IEncoder {

    protected MotorEncoder motorPort;

    public MotorEncoderPort(MotorEncoder motor) {
        motorPort = motor;
    }

    @Override
    public void setDirection(boolean reversed) {
        motorPort.setDirection(reversed ? Direction.REVERSE : Direction.FORWARD);
    }

    @Override
    public int getPosition() {
        return motorPort.getCurrentPosition();
    }

    @Override
    public double getVelocity() {
        return motorPort.getCorrectedVelocity();
    }
}
