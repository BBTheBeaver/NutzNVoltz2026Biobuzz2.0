package org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems;

import static com.pedropathing.ivy.commands.Commands.waitUntil;

import com.pedropathing.ivy.Command;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;

public class Limelight {

    private static final int[] BLUE_IDS = {38, 39, 40, 41, 42, 43, 44, 45 };
    private static final int[] RED_IDS  = {30, 31, 32, 33, 34, 35, 36, 37};

    private final Limelight3A limelight;

    public Limelight(Limelight3A limelight) {
        this.limelight = limelight;
    }

    public void start() {
        limelight.pipelineSwitch(0);
        limelight.start();
    }

    // true if any of your alliance's tags is visible (pass true for blue, false for red)
    public boolean hasTag(boolean blue) {
        int[] ids = blue ? BLUE_IDS : RED_IDS;
        LLResult result = limelight.getLatestResult();
        if (result == null || !result.isValid()) return false;

        for (LLResultTypes.FiducialResult tag : result.getFiducialResults()) {
            for (int id : ids) {
                if (tag.getFiducialId() == id) return true;
            }
        }
        return false;
    }

    // command that finishes once one of your alliance's tags is visible
    public Command waitForTag(boolean blue) {
        return waitUntil(() -> hasTag(blue));
    }
}