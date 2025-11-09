package org.firstinspires.ftc.learnbot.subsystems;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.Subsystem;

public class VisionSubsystem implements Subsystem, Loggable {
    @Log(name="Viz")
    public static String status = "";

    public Limelight3A limelight;

    public VisionSubsystem(Limelight3A ll) {
        limelight = ll;
    }

    public void LookForAprilTags() {
        status = "Looking for AprilTags";
    }

}
