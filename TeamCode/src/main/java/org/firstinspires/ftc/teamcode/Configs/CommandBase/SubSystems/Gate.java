package org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems;

import com.qualcomm.robotcore.hardware.Servo;
import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.*;

public class Gate {
    private final Servo gate;

    public Gate(Servo gate){
        this.gate = gate;
    }

    public void open(){
        gate.setPosition(GATE_OPEN);
    }

    public void close(){
        gate.setPosition(GATE_CLOSE);
    }

}
