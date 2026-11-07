package org.firstinspires.ftc.teamcode.OpMode;
import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.*;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
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
import org.firstinspires.ftc.teamcode.Configs.Globals.PoseStorage;
import org.firstinspires.ftc.teamcode.Configs.Globals.Robot;
import org.firstinspires.ftc.teamcode.Configs.Globals.ShooterTable;

import java.util.Objects;

@TeleOp(name = "Teleop", group = "StarterBot")
//@Disabled
public class Teleop extends OpMode {

    boolean otherSide = false;
    double headingError   = 0;
    double distanceToGoal = 0;
    boolean  headingLock    = false;
    double[] goalPose = GOAL_POSE_BLUE;

    boolean intakeOn = false;
    boolean reverseOn = false;
    boolean ramp_on = false;
    boolean isBlue;

    private final PoseFactory mirroredFactory = PoseFactory.radians().mirrorX(70.75).mirrorY(70.75);


    double holdX, holdY;
    private final PIDController headingController = new PIDController(2.5, 0.0, 0.1);
    private final PIDController holdXController = new PIDController(0.08, 0.0, 0.01);
    private final PIDController holdYController = new PIDController(0.08, 0.0, 0.01);

    boolean positionHold = false;



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
        isBlue = Objects.equals(options[selectedOption], "Blue");

        if (isBlue) {
            goalPose = GOAL_POSE_BLUE;
        } else {
            goalPose = GOAL_POSE_RED;
        }

        if (PoseStorage.lastAutoPose != null) {
            robot.follower.setPose(PoseStorage.lastAutoPose);
        }

    }



    @Override
    public void loop() {
        robot.periodic();
        Scheduler.execute(); // runs anything scheduled — nothing's scheduled yet, but it's plugged in and ready

        robot.flywheel.setVelocity(ShooterTable.speedFor(distanceToGoal));



        //region Heading Lock/Driving
        if (robot.follower.pose().y() > 75) {
            goalPose[1] = 97.0;
        } else {
            goalPose[1] = 47.0;
        }
        double dx = goalPose[0] - robot.follower.pose().x();
        double dy = goalPose[1] - robot.follower.pose().y();
        distanceToGoal = Math.hypot(dx, dy);

        // ── RightBumper — toggle heading lock ──────────────────────────────────────
        if (gamepad1.rightBumperWasPressed()) {
            headingLock = !headingLock;
        }
        double turnPower;
        if(headingLock) {
            double kV = -0.75; // start here, tune sign/magnitude by watching behavior while strafing
            double denom = dx * dx + dy * dy;
            double headingVelFF = denom > 1e-6
                    ? (dx * robot.follower.velocity().vy - dy * robot.follower.velocity().vx) / denom
                    : 0;
            headingError = normalizeAngle(Math.atan2(dy, dx) + Math.PI - robot.follower.pose().heading());
            headingError = normalizeAngle(Math.atan2(dy, dx) + Math.PI - robot.follower.pose().heading());
            if (Math.abs(headingError) < Math.toRadians(1)) {
                turnPower = 0;
            } else {
                turnPower = headingController.calculate(0, headingError) + kV * headingVelFF;
            }
        }else {
            turnPower = gamepad1.right_stick_x;
            //isBlue ? gamepad1.right_stick_x : gamepad1.right_stick_x;
        }

        double flip = isBlue ? 1 : -1;

        double driveY = flip * gamepad1.left_stick_y;
        double driveX = flip * gamepad1.left_stick_x;
        boolean driverMoving = Math.abs(gamepad1.left_stick_x) > 0.05
                || Math.abs(gamepad1.left_stick_y) > 0.05;

        // Toggle position hold (X)
        if (gamepad1.xWasPressed()) {
            positionHold = !positionHold;
            holdX = robot.follower.pose().x();
            holdY = robot.follower.pose().y();
        }

        if (positionHold) {
            if (driverMoving) {
                // driving: keep moving the target with the robot
                holdX = robot.follower.pose().x();
                holdY = robot.follower.pose().y();
            } else {
                // idle: push back toward the saved spot
                double errX = holdX - robot.follower.pose().x();
                double errY = holdY - robot.follower.pose().y();

                driveY += clamp(holdYController.calculate(0, errY), 0.5);
                driveX += clamp(holdXController.calculate(0, errX), 0.5);
            }
        }

        DrivePowers powers = ManualDrive.fieldCentric(
                driveY,
                driveX,
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
        } else if (gamepad1.leftTriggerWasPressed()) {
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

    private Pose m(Pose pose) {
        if (isBlue) {
            return mirroredFactory.of(pose.x(), pose.y(), pose.heading());
        }
        return pose;
    }

    private static double clamp(double v, double max) {
        return Math.max(-max, Math.min(max, v));
    }
}
