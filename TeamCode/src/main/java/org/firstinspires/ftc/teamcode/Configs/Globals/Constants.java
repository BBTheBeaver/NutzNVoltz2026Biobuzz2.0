package org.firstinspires.ftc.teamcode.Configs.Globals;

import android.graphics.Color;

public class Constants {
    public static final int LAUNCHER_TARGET_VELOCITY = 1150; //2678 RPM
    public static final int LAUNCHER_MIN_VELOCITY = 1100; //2571 RPM

    public static final double INTAKE_FORWARD = 1.0;
    public static final double INTAKE_REVERSE = -0.5;

    public static final double GATE_OPEN = 90.0/300.0;
    public static final double GATE_CLOSE = 0.0/300.0;

    public static final double RAMP_DOWN = 90.0/300.0;
    public static final double RAMP_UP = 0.0/300.0;

    // ─── Goal pose [x_inches, y_inches] (Pedro field coords) ─────────────────
    // Blue alliance default — mirrored for Red in TeleOp initialize()
    public static double[] GOAL_POSE_BLUE = {88.0,  47.0};//-50 For the other side
    public static double[] GOAL_POSE_RED  = {58.0, 47.0};//-50 for the other side

    private final Color kBlueTarget = new Color(0.143, 0.427, 0.429);
    private final Color kGreenTarget = new Color(0.197, 0.561, 0.240);
    private final Color kRedTarget = new Color(0.561, 0.232, 0.114);
    private final Color kYellowTarget = new Color(0.361, 0.524, 0.113);




    public static double OTHERSIDE;

}
