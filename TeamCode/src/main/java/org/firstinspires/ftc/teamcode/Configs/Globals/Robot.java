
package org.firstinspires.ftc.teamcode.Configs.Globals;

import static com.qualcomm.hardware.lynx.LynxModule.DebugGroup.I2C;
import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.*;

import com.pedropathing.follower.Follower;
import com.pedropathing.utils.Timer;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.SwitchableLight;

import org.firstinspires.ftc.teamcode.pedro.PedroConstants;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Robot {

    // ───────────────────────── Drive / Pedro ─────────────────────────
    public final Follower follower;
    public final List<LynxModule> hubs;


    // ───────────────────────── Loop Timing ─────────────────────────
    public final Timer loop = new Timer();
    public double loops = 0, lastloop = 0, loopTime = 0;


    // ───────────────────────── Hardware: Shooter ─────────────────────────
    public final DcMotorEx flywheel;
    public final DcMotorEx flywheel2;
    public final Servo gate;
    public final Servo shooterLight;


    // ───────────────────────── Hardware: Intake / Transfer ─────────────────────────
    public final DcMotor intake;
    public final DcMotor transfer;
    public final NormalizedColorSensor colorSensor;
    public final Servo intakeLight;


    // ───────────────────────── Hardware: Ramp ─────────────────────────
    public final Servo left_ramp_servo;
    public final Servo right_ramp_servo;


    // ───────────────────────── Hardware: Vision ─────────────────────────
    public final Limelight3A limelight;


    // ───────────────────────── Hardware: Turret ─────────────────────────
    public final Servo leftTurretServo;
    public final Servo rightTurretServo;


    // ═════════════════════════ CONSTRUCTOR ═════════════════════════
    public Robot(HardwareMap hw) {

        // Drive initialization
        follower = PedroConstants.createFollower(hw);
        hubs = hw.getAll(LynxModule.class);

        for (LynxModule hub : hubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }

        /*
         * Setting zeroPowerBehavior to BRAKE enables a "brake mode". This causes the motor to
         * slow down much faster when it is coasting. This creates a much more controllable
         * drivetrain. As the robot stops much quicker.
         */
        hw.get(DcMotor.class, "lf").setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        hw.get(DcMotor.class, "rf").setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        hw.get(DcMotor.class, "lr").setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        hw.get(DcMotor.class, "rr").setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        // Hardware mapping
        intake = hw.get(DcMotor.class, "intake");
        transfer = hw.get(DcMotor.class, "transfer");

        flywheel = hw.get(DcMotorEx.class, "flywheel");
        flywheel2 = hw.get(DcMotorEx.class, "flywheel2");

        left_ramp_servo = hw.get(Servo.class, "leftRampServo");
        right_ramp_servo = hw.get(Servo.class, "rightRampServo");

        gate = hw.get(Servo.class, "gate");

        limelight = hw.get(Limelight3A.class, "limelight");
        colorSensor = hw.get(NormalizedColorSensor.class, "colorSensor");

        intakeLight = hw.get(Servo.class, "intakeLight");
        shooterLight = hw.get(Servo.class, "shooterLight");

        leftTurretServo = hw.get(Servo.class, "leftTurretServo");
        rightTurretServo = hw.get(Servo.class, "rightTurretSErvo");


        // Motor and servo configuration
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        transfer.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        rightTurretServo.setDirection(Servo.Direction.REVERSE);

        flywheel2.setDirection(DcMotorSimple.Direction.REVERSE);

        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheel.setPIDFCoefficients(
                DcMotor.RunMode.RUN_USING_ENCODER,
                new PIDFCoefficients(52.6, 0, 0, 11.7)
        );


        // Initial servo positions
        gate.setPosition(GATE_CLOSE);

        left_ramp_servo.setPosition(RAMP_UP);
        right_ramp_servo.setPosition(RAMP_UP);


        // Vision initialization
        limelight.setPollRateHz(100);
        limelight.start();


        // Color sensor initialization
        colorSensor.setGain(2);

        if (colorSensor instanceof SwitchableLight) {
            ((SwitchableLight) colorSensor).enableLight(true);
        }


        loop.reset();
        periodic();
    }


    // ═════════════════════════ PERIODIC ═════════════════════════
    public void periodic() {
        loops++;

        if (loops > 10) {
            double now = loop.get(TimeUnit.MICROSECONDS);
            loopTime = (now - lastloop) / loops;
            lastloop = now;
            loops = 0;
        }

        follower.update();
    }


    // ═════════════════════════ LOOP TIME GETTERS ═════════════════════════
    public double getLoopTimeMs() {
        return loopTime;
    }

    public double getLoopTimeHz() {
        return 1000 / loopTime;
    }
}
