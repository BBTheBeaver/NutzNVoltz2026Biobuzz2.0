
package org.firstinspires.ftc.teamcode.Configs.Autos;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.ivy.Command;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Flywheel;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Intake;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Ramp;
import org.firstinspires.ftc.teamcode.Configs.Globals.AutoConfig;
import org.firstinspires.ftc.teamcode.Configs.Globals.Robot;
import org.firstinspires.ftc.teamcode.OpMode.Auto;

public class ParkAuto implements AutoRoutine {

    // ═════════════════════════ POSE FACTORIES ═════════════════════════

    private final PoseFactory mirroredFactory = PoseFactory.radians().mirrorX(70.75).mirrorY(70.75);
    private final PoseFactory poseFactory = PoseFactory.degrees();


    // ═════════════════════════ PATH POSES ═════════════════════════

    private final Pose start = poseFactory.of(9, 132.4562, 270);
    private final Pose path1Top = poseFactory.of(12.5936, 119.3795, 270);
    private final Pose path1Bottom = poseFactory.of(12.5936, 90, 270);


    // ═════════════════════════ AUTO SETTINGS ═════════════════════════

    private final boolean blue;

    public ParkAuto(boolean blue) {
        this.blue = blue;
    }


    // ═════════════════════════ POSE HELPERS ═════════════════════════

    private Pose m(Pose pose) {
        if (blue) {
            return mirroredFactory.of(pose.x(), pose.y(), pose.heading());
        }
        return pose;
    }

    @Override
    public Pose getStartPose() {
        return m(start);
    }


    // ═════════════════════════ AUTO BUILD ═════════════════════════

    @Override
    public Command build(Robot robot) {
        Flywheel flywheel = new Flywheel(robot.flywheel);
        Intake intake = new Intake(robot.intake);
        Ramp ramp = new Ramp(robot.left_ramp_servo, robot.right_ramp_servo);

        return sequential(
                follow(robot.follower, Path1())
        );
    }


    // ═════════════════════════ PATHS ═════════════════════════


    public Path Path1() {
        if (AutoConfig.parkTop) {
            return Paths.curve(m(start), m(path1Top), m(path1Top)).linear(m(start), m(path1Top));
        }
        return curve(m(start), m(path1Top), m(path1Bottom)).linear(m(start), m(path1Bottom));
    }

}
