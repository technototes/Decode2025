package org.firstinspires.ftc.sixteen750.commands;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.technototes.library.command.Command;

public class LLPipelineChangeCommand implements Command {
    public Limelight3A limelight;
    public int pipeline;

   public LLPipelineChangeCommand(Limelight3A l, int p) {
       limelight = l;
       pipeline = p;
   }


    // for getting if end im not sure how to get bot pose yet
    @Override
    public boolean isFinished() {
        if (limelight.getLatestResult().getPipelineIndex() == pipeline) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void execute() {
       limelight.pipelineSwitch(pipeline);
    }


}
