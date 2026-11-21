
package org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class ColorSensor {

    // ═════════════════════════ CONSTANTS ═════════════════════════

    private static final double FULL_DISTANCE_CM = 5.0; // tune this


    // ═════════════════════════ SUBSYSTEM VARIABLES ═════════════════════════

    private final NormalizedColorSensor sensor;


    // ═════════════════════════ CONSTRUCTOR ═════════════════════════

    public ColorSensor(NormalizedColorSensor sensor) {
        this.sensor = sensor;
        sensor.setGain(2);
    }


    // ═════════════════════════ SENSOR METHODS ═════════════════════════

    public boolean full() {
        return getDistance() <= FULL_DISTANCE_CM;
    }

    public double getDistance() {
        return ((DistanceSensor) sensor).getDistance(DistanceUnit.CM);
    }
}
