
package org.firstinspires.ftc.teamcode.Configs.Globals;

public class ShooterConstants {

    // Calculates flywheel speed using a polynomial
    private static final double MIN_SPEED = 0;  // placeholders, use your tested values
    private static final double MAX_SPEED = 5000;

    // Calculates flywheel speed using a polynomial
    public static double flywheelSpeed(double x) {
        double speed = (((-0.00378682 * x + 0.442826) * x - 17.72511) * x + 274.43342) * x;
        return clamp(speed, MIN_SPEED, MAX_SPEED);
    }


    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double ballFlightTime(double x){
        return (((8.06981e-7 * x + 0.000156793) * x - 0.01136) * x + 0.38197) * x - 4.37576;
    }

    public static double angleOffset(double x){
        return x;
    }
}
