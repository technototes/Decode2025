package org.firstinspires.ftc.learnbot.subsystems;

import android.annotation.SuppressLint;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.Subsystem;

public class VisionSubsystem implements Subsystem, Loggable {

    @Log(name = "Viz")
    public static String status = "";

    public Limelight3A limelight;

    public VisionSubsystem(Limelight3A ll) {
        limelight = ll;
        limelight.start();
    }

    public void setPipeline(int targetPipeline) {
        limelight.pipelineSwitch(targetPipeline);
        status = String.format("Pipeline #%d", targetPipeline);
    }

    public int getCurPipeline() {
        return limelight.getLatestResult().getPipelineIndex();
    }

    public LLResult getCurResult() {
        LLResult result = limelight.getLatestResult();
        return (result != null && result.isValid()) ? result : null;
    }

    // TODO: Could add some sort of diagnostics to the periodic function
    @Override
    public void periodic() {
        // status = limelight.getStatus()
    }
}
