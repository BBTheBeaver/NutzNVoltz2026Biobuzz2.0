
package org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems;

import com.qualcomm.robotcore.hardware.Servo;

import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.GATE_CLOSE;
import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.GATE_OPEN;

public class Gate {

    // ═════════════════════════ SUBSYSTEM VARIABLES ═════════════════════════

    private final Servo gate;


    // ═════════════════════════ CONSTRUCTOR ═════════════════════════

    public Gate(Servo gate) {
        this.gate = gate;
    }


    // ═════════════════════════ GATE CONTROL METHODS ═════════════════════════

    // Opens the gate
    public void open() {
        gate.setPosition(GATE_OPEN);
    }

    // Closes the gate
    public void close() {
        gate.setPosition(GATE_CLOSE);
    }
}
