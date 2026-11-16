package org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems;

import com.qualcomm.robotcore.hardware.NormalizedColorSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class DistanceSensor {
    private static final double FULL_DISTANCE_CM = 5.0; // tune this

    private final NormalizedColorSensor sensor;

    public DistanceSensor(NormalizedColorSensor sensor){
        this.sensor = sensor;
        sensor.setGain(2);
    }

    public boolean full(){
        return getDistance() <= FULL_DISTANCE_CM;
    }

    public double getDistance(){
        return ((com.qualcomm.robotcore.hardware.DistanceSensor) sensor).getDistance(DistanceUnit.CM);
    }
}
