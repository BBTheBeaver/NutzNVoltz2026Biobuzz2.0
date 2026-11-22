
package org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Configs.Globals.Robot;

public class Turret {

    // ───────────────────────── Subsystem Variables ─────────────────────────
    private final Servo left;
    private final Servo right;

    private double distanceToGoal = 0;
    private double virtualGoalX = 0;
    private double virtualGoalY = 0;
    private double turretAngleDeg = 0;


    // ───────────────────────── Gear Constants ─────────────────────────
    private static final double TURRET_SERVO_GEAR_TEETH = 60;
    private static final double TURRET_SHAFT_INPUT_GEAR_TEETH = 20;
    private static final double TURRET_SHAFT_OUTPUT_GEAR_TEETH = 65;
    private static final double TURRET_RING_GEAR_TEETH = 169;


    // ───────────────────────── Calibration Constants ─────────────────────────
    private static final double SERVO_TRAVEL_DEG = 320;
    private static final double CENTER_DEG = 0;
    private static final double CENTER_POSITION = 0.5;

    // Set to 180 if turret 0 deg points at the REAR of the robot. Test with the robot standing still.
    private static final double TURRET_ZERO_OFFSET_DEG = 0;

    // Ball flight time in seconds (tune this, or replace with a distance-based fit)
    private static final double BALL_FLIGHT_TIME = 0.5;


    // ───────────────────────── Calculated Constants ─────────────────────────
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
    }


    // ═════════════════════════ UPDATE ═════════════════════════
    // Call every loop. Works out the virtual goal, shooting distance, and turret angle.
    public void update(Robot robot, double[] goalPose) {
        double x = robot.follower.pose().x();
        double y = robot.follower.pose().y();
        double heading = robot.follower.pose().heading();

        double goalY = y > 75 ? 97.0 : 47.0;

        // Shift the goal against our velocity so the shot lands while moving
        virtualGoalX = goalPose[0] - robot.follower.velocity().vx * BALL_FLIGHT_TIME;
        virtualGoalY = goalY - robot.follower.velocity().vy * BALL_FLIGHT_TIME;

        distanceToGoal = Math.hypot(virtualGoalX - x, virtualGoalY - y);

        turretAngleDeg = bearingToGoal(x, y, heading, virtualGoalX, virtualGoalY);
        aimAt(turretAngleDeg);
    }


    // ═════════════════════════ AIMING METHODS ═════════════════════════
    // Calculates the turret angle toward a field target
    public static double bearingToGoal(double x, double y, double headingRad, double goalX, double goalY) {
        double dx = goalX - x;
        double dy = goalY - y;

        double fieldBearingDeg = Math.toDegrees(Math.atan2(dy, dx));

        return normalizeDeg(fieldBearingDeg - Math.toDegrees(headingRad) + TURRET_ZERO_OFFSET_DEG);
    }

    // Moves the turret to the target angle
    public void aimAt(double targetDeg) {
        double position = CENTER_POSITION + (targetDeg - CENTER_DEG) * POSITION_PER_DEG;
        position = Range.clip(position, 0, 1);

        left.setPosition(position);
        right.setPosition(position);
    }


    // ═════════════════════════ GETTERS ═════════════════════════
    public double getDistanceToGoal() {
        return distanceToGoal;
    }

    public double getVirtualGoalX() {
        return virtualGoalX;
    }

    public double getVirtualGoalY() {
        return virtualGoalY;
    }

    public double getTurretAngleDeg() {
        return turretAngleDeg;
    }


    // ═════════════════════════ HELPER METHODS ═════════════════════════
    private static double normalizeDeg(double deg) {
        double w = (deg + 180) % 360;
        if (w < 0) w += 360;
        return w - 180;
    }
}
