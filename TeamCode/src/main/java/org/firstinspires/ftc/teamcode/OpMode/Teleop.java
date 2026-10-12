package org.firstinspires.ftc.teamcode.OpMode;
import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.*;
import static org.firstinspires.ftc.teamcode.Configs.Globals.Poses.*;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.ivy.Scheduler;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.pedropathing.controllers.PIDController;

import org.firstinspires.ftc.teamcode.Configs.Globals.Robot;

import java.util.Objects;

@TeleOp(name = "Teleop", group = "StarterBot")
//@Disabled
public class Teleop extends OpMode {
    Robot robot;

    boolean otherSide = false;
    double headingError   = 0;
    double distanceToGoal = 0;
    boolean  headingLock    = false;
    double[] goalPose = GOAL_POSE_BLUE;

    private final PIDController headingController =
            new PIDController(2.5, 0.0, 0.1);

    boolean intakeOn = false;
    boolean reverseOn = false;
    boolean ramp = false;
    boolean left_flower = false;
    boolean right_flower = false;



    final String[] options = {"Blue", "Red"};
    int selectedOption = 0;
    boolean upLast = false;
    boolean downLast = false;





    @Override
    public void init() {
        robot = new Robot(hardwareMap);
        Scheduler.reset();
        robot.follower.setPose(Start);
        telemetry.addData("Status", "Initialized");
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
        } else {
            goalPose = GOAL_POSE_RED;
        }

    }

    @Override
    public void loop() {
        robot.periodic();
        Scheduler.execute(); // runs anything scheduled — nothing's scheduled yet, but it's plugged in and ready

        robot.launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
        robot.launcher2.setVelocity(LAUNCHER_TARGET_VELOCITY);


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


        // Intake toggle
        if (gamepad1.leftBumperWasPressed()) {
            intakeOn = !intakeOn;
            if (intakeOn) {
                reverseOn = false;
            }
        }

        if(gamepad1.yWasPressed()){
            ramp = !ramp;
        }

        if(ramp){
            robot.right_ramp_servo.setPosition(90/300.0);
            robot.left_ramp_servo.setPosition(90/300.0);
        } else if (!ramp) {
            robot.right_ramp_servo.setPosition(0/300.0);
            robot.left_ramp_servo.setPosition(0/300.0);
        }

        if (intakeOn) {
            robot.intake_transfer.setPower(INTAKE_FORWARD);
        } else if (gamepad1.leftBumperWasPressed()) {
            robot.intake_transfer.setPower(INTAKE_REVERSE);
        } else {
            robot.intake_transfer.setPower(0);
        }


        if(gamepad2.left_bumper){
            robot.left_blocker.setPosition(90/300.0);
            robot.intake_transfer.setPower(INTAKE_FORWARD);
        }
        if(gamepad2.right_bumper){
            robot.right_blocker.setPosition(90/300.0);
            robot.intake_transfer.setPower(INTAKE_FORWARD);
        }

        if(gamepad2.leftTriggerWasPressed()){
            left_flower = !left_flower;
        }
        if(gamepad2.rightTriggerWasPressed())
        {
            right_flower = !right_flower;
        }

        if(left_flower){
            robot.left_flower.setPosition(90/300.0);
        } else if (!left_flower) {
            robot.left_flower.setPosition(0/300.0);
        }

        if(right_flower){
            robot.right_flower.setPosition(90/300.0);
        } else if (!right_flower) {
            robot.right_flower.setPosition(0/300.0);
        }


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
