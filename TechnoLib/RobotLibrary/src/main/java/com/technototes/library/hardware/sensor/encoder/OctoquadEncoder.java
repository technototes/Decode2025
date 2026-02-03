package com.technototes.library.hardware.sensor.encoder;

import com.qualcomm.hardware.digitalchickenlabs.OctoQuad;

public class OctoquadEncoder implements VelocityEncoder {

    protected OctoQuad octoQuad;
    protected int portNumber;
    protected double zero;

    public OctoquadEncoder(OctoQuad o, int port) {
        octoQuad = o;
        portNumber = port;
        zero = 0;
    }

    @Override
    public void setDirection(boolean reversed) {
        octoQuad.setSingleEncoderDirection(
            portNumber,
            reversed ? OctoQuad.EncoderDirection.REVERSE : OctoQuad.EncoderDirection.FORWARD
        );
    }

    @Override
    public void zeroEncoder() {
        zero = octoQuad.readSinglePosition_Caching(portNumber);
    }

    @Override
    public double getPosition() {
        return octoQuad.readSinglePosition_Caching(portNumber) - zero;
    }

    @Override
    public double getVelocity() {
        return octoQuad.readSingleVelocity_Caching(portNumber);
    }

    @Override
    public double getSensorValue() {
        return getPosition();
    }
}
