package org.firstinspires.ftc.learnbot.commands;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.technototes.library.command.Command;
import org.firstinspires.ftc.learnbot.subsystems.VisionSubsystem;

public class LLPipelineChangeCommand implements Command {

    public VisionSubsystem viz;
    public int pipeline;

    public LLPipelineChangeCommand(VisionSubsystem v, int p) {
        viz = v;
        pipeline = p;
    }

    @Override
    public void execute() {
        viz.setPipeline(pipeline);
    }

    // for getting if end im not sure how to get bot pose yet
    @Override
    public boolean isFinished() {
        return viz.getCurPipeline() == pipeline;
    }
}
