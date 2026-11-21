
package org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems;

import com.qualcomm.robotcore.hardware.Servo;

public class Turret {

    // ═════════════════════════ SUBSYSTEM VARIABLES ═════════════════════════

    private final Servo left;
    private final Servo right;


    // ═════════════════════════ GEAR CONSTANTS ═════════════════════════

    private static final double TURRET_SERVO_GEAR_TEETH = 60;
    private static final double TURRET_SHAFT_INPUT_GEAR_TEETH = 20;
    private static final double TURRET_SHAFT_OUTPUT_GEAR_TEETH = 65;
    private static final double TURRET_RING_GEAR_TEETH = 169;


    // ═════════════════════════ CALIBRATION CONSTANTS ═════════════════════════

    private static final double SERVO_TRAVEL_DEG = 320;
    private static final double CENTER_DEG = 0;
    private static final double CENTER_POSITION = 0.5;


    // ═════════════════════════ CALCULATED CONSTANTS ═════════════════════════

    // Calculate turret rotation from servo rotation
    private static final double DEG_PER_SERVO_DEG =
            (TURRET_SERVO_GEAR_TEETH / TURRET_SHAFT_INPUT_GEAR_TEETH)
                    * (TURRET_SHAFT_OUTPUT_GEAR_TEETH / TURRET_RING_GEAR_TEETH);

    private static final double TURRET_TRAVEL_DEG = SERVO_TRAVEL_DEG * DEG_PER_SERVO_DEG;
    private static final double POSITION_PER_DEG = 1.0 / TURRET_TRAVEL_DEG;


    // ═════════════════════════ CONSTRUCTOR ═════════════════════════

    public Turret(Servo left, Servo right) {
        this.left = left;
        this.right = right;

        right.setDirection(Servo.Direction.REVERSE);
    }


    // ═════════════════════════ AIMING METHODS ═════════════════════════

    // Calculates the turret angle toward a field target
    public static double bearingToGoal(double x, double y, double headingRad, double goalX, double goalY) {
        double dx = goalX - x;
        double dy = goalY - y;

        double fieldBearingDeg = Math.toDegrees(Math.atan2(dy, dx));

        return normalizeDeg(fieldBearingDeg - Math.toDegrees(headingRad));
    }

    // Moves the turret to the target angle
    public void aimAt(double targetDeg) {
        double position = CENTER_POSITION + (targetDeg - CENTER_DEG) * POSITION_PER_DEG;

        left.setPosition(clamp(position, 0, 1));
        right.setPosition(clamp(position, 0, 1));
    }


    // ═════════════════════════ HELPER METHODS ═════════════════════════

    private static double normalizeDeg(double deg) {
        double w = (deg + 180) % 360;
        if (w < 0) w += 360;
        return w - 180;
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}
