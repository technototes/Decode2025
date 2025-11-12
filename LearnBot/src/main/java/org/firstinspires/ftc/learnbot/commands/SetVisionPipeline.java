package org.firstinspires.ftc.learnbot.commands;

import com.technototes.library.command.Command;
import org.firstinspires.ftc.learnbot.subsystems.VisionSubsystem;

public class SetVisionPipeline implements Command {

    public VisionSubsystem viz;
    public int pipeline;

    public SetVisionPipeline(VisionSubsystem v, int p) {
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
        return viz.getCurrentPipeline() == pipeline;
    }
}
