package org.firstinspires.ftc.twenty403.helpers;

// A simple interface to wrap up an Encoder interface
public interface IEncoder {
    void setDirection(boolean reversed);
    int getPosition();
    double getVelocity();
}
