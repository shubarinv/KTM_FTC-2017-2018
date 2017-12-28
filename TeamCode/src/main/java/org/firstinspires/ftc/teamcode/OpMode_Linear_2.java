package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 * <p>
 * This particular OpMode just executes a basic Tank Drive Teleop for a four wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 * <p>
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name = "KTM TeleOp 2 (ALT)", group = "Linear Opmode")

//@Disabled
public class OpMode_Linear_2 extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    //Chassis
    private DcMotor m1_Drive = null;
    private DcMotor m2_Drive = null;
    private DcMotor m3_Drive = null;
    private DcMotor m4_Drive = null;
    private DcMotor m5_Lift = null;
    private CRServo s1_top_Claw = null;
    private CRServo s2_bottom_Claw = null;
    private Servo s3_rotation = null;
    private Servo s4_kicker = null;

    //-------
    double magic(double input) {
        return Math.signum(input) * Math.pow(Math.abs(input), 2);
    }

    /*
      * Functions declaration
    */

    // Grab box
    void grab_box(boolean top_clamp, boolean top_release, boolean bottom_clamp, boolean bottom_release) {
        if (top_clamp) {
            s1_top_Claw.setDirection(CRServo.Direction.FORWARD);
            s1_top_Claw.setPower(1);
        } else if (top_release) {
            s1_top_Claw.setDirection(CRServo.Direction.REVERSE);
            s1_top_Claw.setPower(0);
        } else {
            s1_top_Claw.setPower(0.5);
        }
        if (bottom_clamp) {
            s2_bottom_Claw.setDirection(CRServo.Direction.FORWARD);
            s2_bottom_Claw.setPower(1);
        } else if (bottom_release) {
            s2_bottom_Claw.setDirection(CRServo.Direction.REVERSE);
            s2_bottom_Claw.setPower(0);
        } else {
            s2_bottom_Claw.setPower(0.5);
        }
    }


    //Lift claw
    void lift_claw(double lift_power) {
        m5_Lift.setPower(lift_power);
    }

    //rotate claw
    void rotate_claw(boolean rotate) { //if rotate true then rotate to  180 . else to 0
        if (rotate) {
            s3_rotation.setPosition(1);
        } else {
            s3_rotation.setPosition(0);
        }
    }

    void lift_stick(boolean lift) { //if rotate true then rotate to  180 . else to 0
        if (lift) {
            s4_kicker.setPosition(1);
        } else {
            s4_kicker.setPosition(0);
        }
    }
    // Grab another box
    // Place box to shelf


    /*
       *Relic related
    */
    // Grab relic
    // Extend grabbing component
    // Retract grabbing component


    /**
     * End of functions declaration
     */

    @Override
    public void runOpMode() {

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).

        // Chassis
        m1_Drive = hardwareMap.get(DcMotor.class, "m1 drive");
        m2_Drive = hardwareMap.get(DcMotor.class, "m2 drive");
        m3_Drive = hardwareMap.get(DcMotor.class, "m3 drive");
        m4_Drive = hardwareMap.get(DcMotor.class, "m4 drive");
        m5_Lift = hardwareMap.get(DcMotor.class, "m5 lift");
        s1_top_Claw = hardwareMap.get(CRServo.class, "s1 top claw");
        s2_bottom_Claw = hardwareMap.get(CRServo.class, "s2 bottom claw");
        s3_rotation = hardwareMap.get(Servo.class, "s3 rotation");
        s4_kicker = hardwareMap.get(Servo.class, "s4 kick");
        //-------
        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        m1_Drive.setDirection(DcMotor.Direction.FORWARD);
        m2_Drive.setDirection(DcMotor.Direction.FORWARD);
        m3_Drive.setDirection(DcMotor.Direction.FORWARD);
        m4_Drive.setDirection(DcMotor.Direction.FORWARD);
        m5_Lift.setDirection(DcMotor.Direction.FORWARD);
        boolean stick_lifted = false;

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            s4_kicker.setPosition(0);
            /*
               * Chassis movement
            */
            //Setup a variable for each drive wheel to save power level for telemetry
            double m1_Drive_Power;
            double m2_Drive_Power;
            double m3_Drive_Power;
            double m4_Drive_Power;
            double m5_lift_Power;

            // POV Mode uses right stick to go forward and right to slide.
            // - This uses basic math to combine motions and is easier to drive straight.
            double drive_L = gamepad1.left_stick_y;
            double drive_R = -gamepad1.right_stick_y;
            double claw_lift = gamepad2.left_stick_y;
            float claw_clamp_top = gamepad2.left_trigger;
            float claw_clamp_bottom = gamepad2.right_trigger;
            boolean claw_release_top = gamepad2.left_bumper;
            boolean claw_release_bottom = gamepad2.right_bumper;
            boolean claw_rotation_l = gamepad2.dpad_left;
            boolean claw_rotation_r = gamepad2.dpad_right;
            //Slide Related
            double slide;
            double slide_L = gamepad1.left_trigger;
            double slide_R = gamepad1.right_trigger;
            if (slide_L < slide_R) {
                slide = slide_R * -1;
            } else {
                slide = slide_L;
            }
            double A = Math.abs(drive_L) + Math.abs(drive_R) + Math.abs(slide);
            if (A <= 1) {
                m1_Drive_Power = drive_L - slide;
                m2_Drive_Power = drive_R - slide;
                m3_Drive_Power = drive_R + slide;
                m4_Drive_Power = drive_L + slide;
            } else {


                drive_L = drive_L / A;
                drive_R = drive_R / A;
                slide = slide / A;
                m1_Drive_Power = (drive_L - slide)*2;
                m2_Drive_Power = (drive_R - slide)*2;
                m3_Drive_Power = (drive_R + slide)*2;
                m4_Drive_Power = (drive_L + slide)*2;
            }
            // Send calculated power to wheels

            m1_Drive.setPower(magic(m1_Drive_Power));
            m2_Drive.setPower(magic(m2_Drive_Power));
            m3_Drive.setPower(magic(m3_Drive_Power));
            m4_Drive.setPower(magic(m4_Drive_Power));
            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Motors", "m1_Drive (%.2f), m2_Drive (%.2f), m3_Drive (%.2f), m4_Drive (%.2f)", m1_Drive_Power, m2_Drive_Power, m3_Drive_Power, m4_Drive_Power);
            telemetry.update();

            /*
            * End of chassis related code.
            * Begin Claw related code
             */

            // Grab box
            if (claw_clamp_top > 0.2) {
                grab_box(true, false, false, false);
            }
            if (claw_clamp_bottom > 0.2) {
                grab_box(false, false, true, false);
            }
            if (claw_release_top) {
                grab_box(false, true, false, false);
            }
            if (claw_release_bottom) {
                grab_box(false, false, false, true);
            }
            if (gamepad2.y == true) {
                lift_stick(stick_lifted);
                stick_lifted = !stick_lifted;
            }
            // Claw rotation
            if (claw_rotation_l) {
                rotate_claw(true); // Rotate claw to left
            }
            if (claw_rotation_r) {
                rotate_claw(false); // Rotate claw to right
            }

            // Claw lift
            lift_claw(magic(claw_lift));

        }
    }
}
