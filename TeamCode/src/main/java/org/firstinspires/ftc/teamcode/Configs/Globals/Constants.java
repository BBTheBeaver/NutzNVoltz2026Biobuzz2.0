
package org.firstinspires.ftc.teamcode.Configs.Globals;

import android.graphics.Color;

public class Constants {

    // ═════════════════════════ LAUNCHER ═════════════════════════

    public static final int LAUNCHER_TARGET_VELOCITY = 1150; // 2678 RPM
    public static final int LAUNCHER_MIN_VELOCITY = 1100; // 2571 RPM


    // ═════════════════════════ INTAKE ═════════════════════════

    public static final double INTAKE_FORWARD = 1.0;
    public static final double INTAKE_REVERSE = -0.5;


    // ═════════════════════════ SERVOS ═════════════════════════

    public static final double GATE_OPEN = 90.0 / 300.0;
    public static final double GATE_CLOSE = 0.0 / 300.0;

    public static final double RAMP_DOWN = 90.0 / 300.0;
    public static final double RAMP_UP = 0.0 / 300.0;


    // ═════════════════════════ GOAL POSES ═════════════════════════

    // Goal pose [x_inches, y_inches] (Pedro field coordinates)
    // Blue alliance default — mirrored for Red in TeleOp initialize()
    public static double[] GOAL_POSE_BLUE = { 88.0, 47.0 }; // -50 for the other side
    public static double[] GOAL_POSE_RED = { 58.0, 47.0 }; // -50 for the other side


    // ═════════════════════════ LAUNCH ZONES ═════════════════════════

    public static double[] redLaunchZone1 = {
            22.53, 141.5,
            22.53, 105,
            119, 105,
            119, 141.5
    };

    public static double[] redLaunchZone2 = {
            118.97, 0,
            118.97, 30,
            22.5, 30,
            22.5, 0
    };
}
