package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
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

    private static final int LED_CHANNEL = 5;
    TouchSensor touchSensor;  // Hardware Device Object
    ColorSensor sensorRGB;
    DeviceInterfaceModule cdim;
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    //Chassis
    private DcMotor m1_Drive = null;
    private DcMotor m2_Drive = null;
    private DcMotor m3_Drive = null;
    private DcMotor m4_Drive = null;
    private DcMotor m5_Lift = null;
    private DcMotor m6_intake = null;
    private CRServo s1_Relic_ext_ret = null;
    //private CRServo s2_bottom_Claw = null;
    private Servo s3_rotation = null;
    private Servo s4_kicker = null;
    private Servo s5_shovel = null;
    private Servo s6_relic_claw = null;
    private Servo s7_relic_arm = null;

    //-------
    double magic(double input) {
        return Math.signum(input) * Math.pow(Math.abs(input), 2);
    }

  /*
  * Functions declaration
  */


    //Lift claw
    void lift_claw(double lift_power) {
        m5_Lift.setPower(lift_power);
    }

    void shovel_trigger(double shovel_pos) {
        s5_shovel.setPosition(shovel_pos);
    }

    void lift_stick(boolean lift) { //if rotate true then rotate to  180 . else to 0
        if (lift) {
            s4_kicker.setPosition(1);
        } else {
            s4_kicker.setPosition(0);
        }
    }

    void setPower_Timed(DcMotor motor, double power, long milliseconds) {
        motor.setPower(power);
        sleep(milliseconds);
        motor.setPower(0);

    }
    void setPower_Timed(CRServo Crservo, double power, long milliseconds) {
        Crservo.setPower(power);
        sleep(milliseconds);
        Crservo.setPower(0);

    }

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
        m6_intake = hardwareMap.get(DcMotor.class, "m6 intake");
        s1_Relic_ext_ret = hardwareMap.get(CRServo.class, "s1 top claw");
        //s2_bottom_Claw = hardwareMap.get(CRServo.class, "s2 bottom claw");
        s3_rotation = hardwareMap.get(Servo.class, "s3 rotation");
        s4_kicker = hardwareMap.get(Servo.class, "s4 kick");
        s5_shovel = hardwareMap.get(Servo.class, "s5 shovel");
        s6_relic_claw = hardwareMap.get(Servo.class, "s6 relic claw");
        s7_relic_arm = hardwareMap.get(Servo.class, "s7 relic arm");

        //sensor
        touchSensor = hardwareMap.get(TouchSensor.class, "sensor touch");

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

            s4_kicker.setPosition(0.05);
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
            double drive_L = -gamepad1.left_stick_y;
            double drive_R = -gamepad1.right_stick_y;
            double claw_lift_l = gamepad2.left_trigger;
            double claw_lift_r = gamepad2.right_trigger;
            double claw_rotation = -gamepad2.right_stick_y;
            float relic = gamepad2.left_stick_x;
            boolean relic_arm_extend = gamepad2.dpad_left;
            boolean relic_arm_halt = gamepad2.dpad_right;

            double intake_motor = gamepad2.right_stick_y; // to motor if y<0 p=0 else prop
            boolean slide_left_bump = gamepad1.left_bumper;
            boolean slide_right_bump = gamepad1.right_bumper;
            boolean relic_claw_up = gamepad2.dpad_up;
            boolean relic_claw_down = gamepad2.dpad_down;


            cdim = hardwareMap.deviceInterfaceModule.get("dim");
            //Slide Related
            double slide;
            double slide_L = gamepad1.left_trigger;
            double slide_R = gamepad1.right_trigger;
            if (slide_L < slide_R) {
                slide = slide_R * -1;
            } else {
                slide = slide_L;
            }
            if (slide_left_bump) {
                slide = 0.3;
            }
            if (slide_right_bump) {
                slide = -0.3;
            }
            m1_Drive_Power = magic(drive_L - slide);
            m2_Drive_Power = magic(drive_R - slide);
            m3_Drive_Power = magic(drive_R + slide);
            m4_Drive_Power = magic(drive_L + slide);

            double max = Math.max(Math.max(m1_Drive_Power, m2_Drive_Power), Math.max(m3_Drive_Power, m4_Drive_Power));
            // Send calculated power to wheels
            if (max >= 1) {
                m1_Drive.setPower(m1_Drive_Power * -1 / max);
                m2_Drive.setPower(m2_Drive_Power / max);
                m3_Drive.setPower(m3_Drive_Power / max);
                m4_Drive.setPower(m4_Drive_Power * -1 / max);
            } else {
                m1_Drive.setPower(m1_Drive_Power * -1);
                m2_Drive.setPower(m2_Drive_Power);
                m3_Drive.setPower(m3_Drive_Power);
                m4_Drive.setPower(m4_Drive_Power * -1);
            }
            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Motors", "m1_Drive (%.2f), m2_Drive (%.2f), m3_Drive (%.2f), m4_Drive (%.2f)", m1_Drive_Power, m2_Drive_Power, m3_Drive_Power, m4_Drive_Power);
            telemetry.update();

      /*
      * End of chassis related code.
      */

            // Claw rotation
            if (claw_rotation > 0) {
                s3_rotation.setPosition(0.78 - claw_rotation * 0.78);
            } else {
                s3_rotation.setPosition(0.78);
            }
            shovel_trigger(intake_motor);
            if (intake_motor>0) {
                m6_intake.setPower(intake_motor);
            } else {
                m6_intake.setPower(0);
            }
            if(gamepad2.left_stick_y<0){
                m6_intake.setPower(gamepad2.left_stick_y);
            }

            // Claw_lift
            if (claw_lift_l != 0) {
                lift_claw(-claw_lift_l);
            } else if (claw_lift_r != 0) {
                lift_claw(claw_lift_r);
            } else {
                lift_claw(-claw_lift_l);
            }

            //Partially AutoOP
            if (relic == 0) {
                if (relic_arm_extend) { //DO NOT FORKING CHANGE
                    setPower_Timed(s1_Relic_ext_ret, 1, 450);
                    while (!touchSensor.isPressed()) {
                        s1_Relic_ext_ret.setPower(0.5);
                    }
                    s1_Relic_ext_ret.setPower(0);
                    s7_relic_arm.setPosition(0.7);
                    sleep(500);
                    s7_relic_arm.setPosition(0.9);


                }
                if (relic_arm_halt) {
                    s7_relic_arm.setPosition(0.3);
                    m1_Drive.setPower(-0.5);
                    m2_Drive.setPower(0.5);
                    m3_Drive.setPower(-0.1);
                    m4_Drive.setPower(0.1);
                    sleep(300);
                    m1_Drive.setPower(0);
                    m2_Drive.setPower(0);
                    m3_Drive.setPower(0);
                    m4_Drive.setPower(0);
                    sleep(300);
                    setPower_Timed(s1_Relic_ext_ret, -1, 450);
                    for (int tick = 0; tick < 2000; tick += 10) {
                        s1_Relic_ext_ret.setPower(0.5);

                        if (touchSensor.isPressed() || isStopRequested()) {
                            s1_Relic_ext_ret.setPower(0);
                            break;
                        }
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }
            //relic arm

            s1_Relic_ext_ret.setPower(relic * 0.2);


            //Relic arm_small
            if (relic_claw_up) { //DO NOT CHANGE
                s7_relic_arm.setPosition(1);
                sleep(200);
                s6_relic_claw.setPosition(1);
            } else if (relic_claw_down) {
                s6_relic_claw.setPosition(0);
                sleep(300);
                s7_relic_arm.setPosition(0.9);
            }
            cdim.setDigitalChannelState(LED_CHANNEL, false);
        }
    }
}
