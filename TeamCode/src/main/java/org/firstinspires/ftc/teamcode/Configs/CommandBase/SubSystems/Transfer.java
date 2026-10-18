package org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems;

import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.RAMP_DOWN;
import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.RAMP_UP;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

public class Transfer {
    private final DcMotor motor;


    public Transfer(DcMotor motor) {
        this.motor = motor;
    }

    // Turns on the intake
    public void off() {
        motor.setPower(0);
    }

    public void on (){
        motor.setPower(1);
    }
}
