package org.firstinspires.ftc.teamcode.OpMode;
import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.*;
import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.RESET_GOAL;
import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.RESET_GARDEN;
import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.RESET_TOP;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Aim;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.ColorSensor;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Flywheel;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Gate;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Intake;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Light;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Ramp;

import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Transfer;
import org.firstinspires.ftc.teamcode.Configs.Globals.PoseStorage;
import org.firstinspires.ftc.teamcode.Configs.Globals.Robot;
import org.firstinspires.ftc.teamcode.Configs.Globals.ShooterConstants;
import org.firstinspires.ftc.teamcode.Configs.Globals.Zone;

import java.util.Objects;

@TeleOp(name = "Teleop", group = "StarterBot")
//@Disabled
public class Teleop extends OpMode {

    // ───────────────────────── Robot & subsystems ─────────────────────────
    Robot robot;
    Flywheel flywheel;
    Intake intake;
    Transfer transfer;
    Ramp ramp;
    Gate gate;
    ColorSensor colorSensor;
    Light intakeLight;
    Light shooterLight;
    Aim aim;

    // ───────────────────────── Alliance select ─────────────────────────
    final String[] options = {"Blue", "Red"};
    int selectedOption = 0;
    boolean upLast = false;
    boolean downLast = false;
    boolean isBlue;

    // ───────────────────────── Drive / aiming ─────────────────────────
    boolean  headingLock    = false;
    double[] goalPose = GOAL_POSE_BLUE;
    double turnPower;
    double flip;


    // ───────────────────────── Intake / ramp state ─────────────────────────
    boolean intakeOn = false;
    boolean reverseOn = false;
    boolean ramp_on = false;
    boolean lastFull = false;   // field, outside loop()

    // ───────────────────────── Zones ─────────────────────────
    boolean zone1;
    boolean zone2;

    // ───────────────────────── Timer ─────────────────────────
    ElapsedTime timer = new ElapsedTime();
    double secs;
    double waitTime = 1.5;//Second

    // ───────────────────────── Pose mirroring ─────────────────────────
    private final PoseFactory mirroredFactory = PoseFactory.radians().mirrorX(70.75).mirrorY(70.75);





    // ═════════════════════════ INIT ═════════════════════════
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
        colorSensor = new ColorSensor(robot.colorSensor);
        intakeLight = new Light(robot.intakeLight);
        shooterLight = new Light(robot.shooterLight);
        aim = new Aim();


    }

    // ═════════════════════════ INIT LOOP ═════════════════════════
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

    // ═════════════════════════ START ═════════════════════════
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


    // ═════════════════════════ LOOP ═════════════════════════
    @Override
    public void loop() {
        robot.periodic();
        Scheduler.execute(); // runs anything scheduled — nothing's scheduled yet, but it's plugged in and ready

        robot.flywheel.setVelocity(ShooterConstants.flywheelSpeed(aim.getDistanceToGoal()));

        if(gamepad2.dpad_down){
            robot.follower.setPose(m(RESET_GARDEN));
        }
        if(gamepad2.dpad_up){
            robot.follower.setPose(m(RESET_TOP));
        }
        if(gamepad2.dpad_left|| gamepad1.dpad_right){
            robot.follower.setPose(m(RESET_GOAL));
        }


        //region Drive
        if (gamepad1.rightBumperWasPressed()) {
            headingLock = !headingLock;
        }

        turnPower = aim.update(robot, headingLock, goalPose, gamepad1.right_stick_x);

        flip = isBlue ? 1 : -1;
        DrivePowers powers = ManualDrive.fieldCentric(
                flip * gamepad1.left_stick_y,
                flip * gamepad1.left_stick_x,
                turnPower,
                robot.follower.pose().heading()
        );
        robot.follower.manual(powers);
        //endregion



        //region Shoot
        zone1 = Zone.contains(redLaunchZone1, robot.follower.pose().x(), robot.follower.pose().y());
        zone2 = Zone.contains(redLaunchZone2, robot.follower.pose().x(), robot.follower.pose().y());
        if (zone1 || zone2) {
            shooterLight.blue();
        } else {
            shooterLight.off();
        }


        if (gamepad1.y) {
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
        if(colorSensor.full() && !lastFull){
            timer.reset();
            if(timer.seconds() > secs) {
                intake.off();
                intakeLight.green();
            }
        }
        else{
            intakeLight.red();
        }
        lastFull = colorSensor.full();

        //endregion


        //region Ramp
        if(gamepad1.rightStickButtonWasPressed()){
            ramp_on = !ramp_on;
        }

        if(ramp_on){
            ramp.down();
        } else if (!ramp_on) {
            ramp.up();
        }
        //endregion




        //region Telemetry
        telemetry.addData("Triggers", "left (%.2f), right (%.2f)", gamepad1.left_trigger, gamepad1.right_trigger);
        telemetry.addData("Heading", "%.1f°", Math.toDegrees(robot.follower.pose().heading()));
        telemetry.addData("Loop Hz", robot.getLoopTimeHz());
        telemetry.update();
        //endregion
    }

    // ═════════════════════════ HELPERS ═════════════════════════
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