package org.firstinspires.ftc.teamcode.Configs.Globals;


import static com.qualcomm.hardware.lynx.LynxModule.DebugGroup.I2C;

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

import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.*;

import org.firstinspires.ftc.teamcode.pedro.PedroConstants;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Robot {
    public final Follower follower;
    public final List<LynxModule> hubs;
    public final Timer loop = new Timer();
    public double loops = 0, lastloop = 0, loopTime = 0;

    //hardware
    public final DcMotorEx flywheel;
    public final DcMotor intake;
    public final DcMotor transfer;
    public final Servo left_ramp_servo;
    public final Servo right_ramp_servo;
    public final Servo gate;
    public final Limelight3A limelight;
    public final NormalizedColorSensor colorSensor;
    public final Servo intakeLight;
    public final Servo shooterLight;




    public Robot(HardwareMap hw){
        follower = PedroConstants.createFollower(hw);
        hubs = hw.getAll(LynxModule.class);
        for(LynxModule hub:hubs){
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



        // ----- Non-drivetrain hardware init -----
        intake = hw.get(DcMotor.class, "intake");
        transfer = hw.get(DcMotor.class, "transfer");

        flywheel = hw.get(DcMotorEx.class, "flywheel");

        left_ramp_servo = hw.get(Servo.class, "leftRampServo");
        right_ramp_servo = hw.get(Servo.class, "rightRampServo");

        gate = hw.get(Servo.class, "gate");

        limelight = hw.get(Limelight3A.class, "limelight");
        colorSensor = hw.get(NormalizedColorSensor.class, "colorSensor");
        intakeLight = hw.get(Servo.class, "intakeLight");
        shooterLight = hw.get(Servo.class, "shooterLight");


        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        transfer.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        gate.setPosition(GATE_CLOSE);

        left_ramp_servo.setPosition(RAMP_UP);
        right_ramp_servo.setPosition(RAMP_UP);

        limelight.setPollRateHz(100);
        limelight.start();

        colorSensor.setGain(2);
        if (colorSensor instanceof SwitchableLight) {
            ((SwitchableLight) colorSensor).enableLight(true);
        }
        // -----------------------------------------

        loop.reset();
        periodic();
    }

    public void periodic(){
        loops++;
        if(loops > 10){
            double now = loop.get(TimeUnit.MICROSECONDS);
            loopTime = (now-lastloop) / loops;
            lastloop = now;
            loops = 0;
        }
        follower.update();
    }
    public double getLoopTimeMs() { return loopTime;}
    public double getLoopTimeHz() { return 1000/loopTime;}

}