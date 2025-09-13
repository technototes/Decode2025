package org.firstinspires.ftc.twenty403.helpers;

import com.qualcomm.hardware.digitalchickenlabs.OctoQuad;
import com.qualcomm.hardware.digitalchickenlabs.OctoQuad.EncoderDirection;

public class OctoquadEncoder implements IEncoder {

    protected OctoQuad octoQuad;
    protected int portNumber;

    public OctoquadEncoder(OctoQuad o, int port) {
        octoQuad = o;
        portNumber = port;
    }

    @Override
    public void setDirection(boolean reversed) {
        octoQuad.setSingleEncoderDirection(
            portNumber,
            reversed ? EncoderDirection.REVERSE : EncoderDirection.FORWARD
        );
    }

    @Override
    public int getPosition() {
        return octoQuad.readSinglePosition(portNumber);
    }

    @Override
    public double getVelocity() {
        return octoQuad.readSingleVelocity(portNumber);
    }
}
