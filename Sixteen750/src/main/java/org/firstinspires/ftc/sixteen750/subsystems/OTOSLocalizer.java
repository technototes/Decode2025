package org.firstinspires.ftc.sixteen750.subsystems;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.localization.Localizer;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;

public class OTOSLocalizer implements Localizer {

    SparkFunOTOS sparky;

    public OTOSLocalizer(SparkFunOTOS sparky) {
        this.sparky = sparky;
        sparky.initialize();
    }

    @NonNull
    @Override
    public Pose2d getPoseEstimate() {
        SparkFunOTOS.Pose2D loc = sparky.getPosition();
        return new Pose2d(loc.x, loc.y, loc.h);
    }

    @Override
    public void setPoseEstimate(@NonNull Pose2d pose2d) {
        sparky.setPosition(
            new SparkFunOTOS.Pose2D(pose2d.getX(), pose2d.getY(), pose2d.getHeading())
        );
    }

    @Nullable
    @Override
    public Pose2d getPoseVelocity() {
        SparkFunOTOS.Pose2D loc = sparky.getVelocity();
        return new Pose2d(loc.x, loc.y, loc.h);
    }

    @Override
    public void update() {
        //we dont think that we need anything here
    }
}
