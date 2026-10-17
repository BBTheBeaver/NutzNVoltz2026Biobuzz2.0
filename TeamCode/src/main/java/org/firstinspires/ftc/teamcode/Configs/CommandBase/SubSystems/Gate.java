package org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems;

import com.qualcomm.robotcore.hardware.Servo;
import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.*;

public class Gate {
    private final Servo left_gate;

    private final Servo right_gate;

    public Gate(Servo left_gate, Servo right_gate){
        this.left_gate = left_gate;
        this.right_gate = right_gate;
    }

    public void left_gate_open(){
        left_gate.setPosition(GATE_OPEN);
    }

    public void left_gate_close(){
        left_gate.setPosition(GATE_CLOSE);
    }

    public void right_gate_open(){
        right_gate.setPosition(GATE_OPEN);
    }

    public void right_gate_close(){
        right_gate.setPosition((GATE_CLOSE));
    }

}
