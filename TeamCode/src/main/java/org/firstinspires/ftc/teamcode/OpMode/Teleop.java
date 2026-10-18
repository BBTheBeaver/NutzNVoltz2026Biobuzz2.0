package org.firstinspires.ftc.teamcode.OpMode;
import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.*;
import static org.firstinspires.ftc.teamcode.Configs.Globals.Poses.*;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.ivy.Scheduler;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.pedropathing.controllers.PIDController;

import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Flywheel;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Gate;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Intake;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Ramp;

import org.firstinspires.ftc.teamcode.Configs.CommandBase.Commands.SetRamp;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.Commands.SpinFlywheel;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.Commands.Shoot;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.Commands.SetIntake;

import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Transfer;
import org.firstinspires.ftc.teamcode.Configs.Globals.Robot;

import java.util.Objects;

@TeleOp(name = "Teleop", group = "StarterBot")
//@Disabled
public class Teleop extends OpMode {

    boolean otherSide = false;
    double headingError   = 0;
    double distanceToGoal = 0;
    boolean  headingLock    = false;
    double[] goalPose = GOAL_POSE_BLUE;

    private final PIDController headingController =
            new PIDController(2.5, 0.0, 0.1);

    boolean intakeOn = false;
    boolean reverseOn = false;
    boolean ramp_on = false;
    boolean left_flower = false;
    boolean right_flower = false;



    final String[] options = {"Blue", "Red"};
    int selectedOption = 0;
    boolean upLast = false;
    boolean downLast = false;

    Robot robot;
    Flywheel flywheel;
    Intake intake;
    Transfer transfer;
    Ramp ramp;
    Gate gate;





    @Override
    public void init() {
        robot = new Robot(hardwareMap);
        Scheduler.reset();
        telemetry.addData("Status", "Initialized");

        flywheel = new Flywheel(robot.flywheel);
        intake = new Intake(robot.intake);
        transfer = new Transfer(robot.transfer);
        ramp = new Ramp(robot.left_ramp_servo, robot.right_ramp_servo);
        gate = new Gate(robot.gate);


        //Driver 1: Intake, Heading lock, Drive, Ramp
        //Driver 2: Shoot
        //Intake/transfer on one motor
        //Each shooter has a different motor
        //drivetrain Parelle plate
        //different servo on each side to unblock shooter
        //Deflector on 2 servos
        //Flower take using 2 servos
    }

    @Override
    public void init_loop() {
        if (gamepad1.dpad_down && !downLast) selectedOption = (selectedOption + 1) % options.length;
        if (gamepad1.dpad_up   && !upLast)   selectedOption = (selectedOption - 1 + options.length) % options.length;
        upLast   = gamepad1.dpad_up;
        downLast = gamepad1.dpad_down;

        telemetry.addLine("=== ALLIANCE SELECT ===");
        for (int i = 0; i < options.length; i++) {
            telemetry.addData(i == selectedOption ? "> " : "  ", options[i]);
        }
        telemetry.addLine("DPAD up/down to switch, Start to begin");
        telemetry.update();
    }

    @Override
    public void start() {
        boolean isBlue = Objects.equals(options[selectedOption], "Blue");
        if (isBlue) {
            goalPose = GOAL_POSE_BLUE;
            robot.follower.setPose(BLUE_START);
        } else {
            goalPose = GOAL_POSE_RED;
            robot.follower.setPose(RED_START);
        }

    }

    @Override
    public void loop() {
        robot.periodic();
        Scheduler.execute(); // runs anything scheduled — nothing's scheduled yet, but it's plugged in and ready

        SpinFlywheel.on(flywheel);


        //region Heading Lock
        if (gamepad1.rightTriggerWasPressed()){
            otherSide = !otherSide;
        }
        // ── RightBumper — toggle heading lock ──────────────────────────────────────
        if (gamepad1.rightBumperWasPressed()) {
            headingLock = !headingLock;
        }

        double turnPower;
        if(headingLock) {
            double dx;
            double dy;

            if (otherSide){
                dx = goalPose[0] - robot.follower.pose().x();
                dy = goalPose[1] - OTHERSIDE  - robot.follower.pose().y();
            }else{
                dx = goalPose[0] - robot.follower.pose().x();
                dy = goalPose[1] - robot.follower.pose().y();
            }
            double kV = -0.75; // start here, tune sign/magnitude by watching behavior while strafing
            double denom = dx * dx + dy * dy;
            double headingVelFF = denom > 1e-6
                    ? (dx * robot.follower.velocity().vy - dy * robot.follower.velocity().vx) / denom
                    : 0;
            distanceToGoal = Math.hypot(dx, dy);
            headingError = normalizeAngle(Math.atan2(dy, dx) + Math.PI - robot.follower.pose().heading());
            turnPower = headingController.calculate(0, headingError) + kV * headingVelFF;
        }else {
            turnPower = gamepad1.right_stick_x;
        }

        // Field-centric drive
        DrivePowers powers = ManualDrive.fieldCentric(
                -gamepad1.left_stick_y,
                -gamepad1.left_stick_x,
                turnPower,
                robot.follower.pose().heading()
        );

        robot.follower.manual(powers);
        //endregion


        //region Shoot
        if(gamepad1.y)
        {
            gate.open();
            intake.on();
            transfer.on();
            //schedule(Shoot.whileHeld(gate, intake, transfer, () -> gamepad1.y));
        }
        //endregion



        //region Intake
        if (gamepad1.leftBumperWasPressed()) {
            intakeOn = !intakeOn;
            if (intakeOn) {
                reverseOn = false;
            }
        }


        if (intakeOn) {
            intake.on();
        } else if (gamepad1.leftBumperWasPressed()) {
            intake.reverse();
        } else {
            intake.off();
        }
        //endregion


        //region Ramp
        if(gamepad1.aWasPressed()){
            ramp_on = !ramp_on;
        }

        if(ramp_on){
            ramp.down();
        } else if (!ramp_on) {
            ramp.up();
        }
        //endregion




        // Telemetry
        telemetry.addData("Triggers", "left (%.2f), right (%.2f)", gamepad1.left_trigger, gamepad1.right_trigger);
        telemetry.addData("Heading", "%.1f°", Math.toDegrees(robot.follower.pose().heading()));
        telemetry.addData("Loop Hz", robot.getLoopTimeHz());
        telemetry.update();
    }

    private static double normalizeAngle(double angle) {
        return Math.atan2(Math.sin(angle), Math.cos(angle));
    }
}
