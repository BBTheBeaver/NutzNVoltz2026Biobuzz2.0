package org.firstinspires.ftc.teamcode.Configs.CommandBase.Commands;

import static com.pedropathing.ivy.commands.Commands.instant;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.commands.Commands.waitUntil;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.*;

import com.pedropathing.ivy.Command;

import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Gate;
import org.firstinspires.ftc.teamcode.Configs.Globals.Robot;

import java.util.function.BooleanSupplier;

public class Shoot {

    // Auto — fixed duration, no outside input
    public static Command auto(Gate gate, Robot robot) {
        return sequential(
                instant(() -> {
                    gate.left_gate_open();
                    gate.right_gate_open();
                    robot.intake_transfer.setPower(INTAKE_FORWARD);
                }),

                waitMs(2000),

                instant(() -> {
                    gate.left_gate_close();
                    gate.right_gate_close();
                    robot.intake_transfer.setPower(0);
                })
        );
    }

    // Teleop — runs for as long as "held" keeps returning true
    public static Command whileHeld(Gate gate, Robot robot, BooleanSupplier held, boolean pollen) {
        return sequential(
                instant(() -> {
                    if (pollen) {
                        gate.right_gate_open();
                    } else {
                        gate.left_gate_open();
                    }
                    robot.intake_transfer.setPower(INTAKE_FORWARD);
                }),

                waitUntil(() -> !held.getAsBoolean()),

                instant(() -> {
                    gate.left_gate_close();
                    gate.right_gate_close();
                    robot.intake_transfer.setPower(0);
                })
        );
    }
}