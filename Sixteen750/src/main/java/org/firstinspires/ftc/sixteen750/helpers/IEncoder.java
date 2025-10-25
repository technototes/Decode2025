package org.firstinspires.ftc.sixteen750.helpers;

// A simple interface to wrap up an Encoder interface
public interface IEncoder {
    void setDirection(boolean reversed);
    int getPosition();
    double getVelocity();
}
