package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
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
public class TeleOP extends LinearOpMode {
    private static final int LED_CHANNEL = 5;
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    //Chassis

    private DcMotor m5Lift = null;
    private Servo s5Shovel = null;

    //-------
    double magic(double input) {
        return Math.signum(input) * Math.pow(Math.abs(input), 2);
    }

    /*
     * Functions declaration
     */


    //Lift claw
    void liftClaw(double lift_power) {
        m5Lift.setPower(lift_power);
    }

    void shovelTrigger(double shovel_pos) {
        s5Shovel.setPosition(shovel_pos);
    }

    void setPowerTimed(DcMotor motor, double power, long milliseconds) {
        motor.setPower(power);
        sleep(milliseconds);
        motor.setPower(0);

    }

    void setPowerTimed(CRServo Crservo, double power, long milliseconds) {
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
        DcMotor m1Drive = hardwareMap.get(DcMotor.class, "m1 drive");
        DcMotor m2Drive = hardwareMap.get(DcMotor.class, "m2 drive");
        DcMotor m3Drive = hardwareMap.get(DcMotor.class, "m3 drive");
        DcMotor m4Drive = hardwareMap.get(DcMotor.class, "m4 drive");
        m5Lift = hardwareMap.get(DcMotor.class, "m5 lift");
        DcMotor m6Intake = hardwareMap.get(DcMotor.class, "m6 intake");
        CRServo s1RelicExtRet = hardwareMap.get(CRServo.class, "s1 top claw");
        //s2_bottom_Claw = hardwareMap.get(CRServo.class, "s2 bottom claw");
        Servo s3Rotation = hardwareMap.get(Servo.class, "s3 rotation");
        Servo s4Kicker = hardwareMap.get(Servo.class, "s4 kick");
        s5Shovel = hardwareMap.get(Servo.class, "s5 shovel");
        Servo s6RelicClaw = hardwareMap.get(Servo.class, "s6 relic claw");
        Servo s7RelicArm = hardwareMap.get(Servo.class, "s7 relic arm");

        //sensor
        TouchSensor touchSensor = hardwareMap.get(TouchSensor.class, "sensor touch");

        //-------
        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        m1Drive.setDirection(DcMotor.Direction.FORWARD);
        m2Drive.setDirection(DcMotor.Direction.FORWARD);
        m3Drive.setDirection(DcMotor.Direction.FORWARD);
        m4Drive.setDirection(DcMotor.Direction.FORWARD);
        m5Lift.setDirection(DcMotor.Direction.FORWARD);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            s4Kicker.setPosition(0.1);
            /*
             * Chassis movement
             */
            //Setup a variable for each drive wheel to save power level for telemetry
            double m1DrivePower;
            double m2DrivePower;
            double m3DrivePower;
            double m4DrivePower;

            // POV Mode uses right stick to go forward and right to slide.
            // - This uses basic math to combine motions and is easier to drive straight.
            double driveL = -gamepad1.left_stick_y;
            double driveR = -gamepad1.right_stick_y;
            double clawLiftL = gamepad2.left_trigger;
            double clawLiftR = gamepad2.right_trigger;
            double clawRotation = -gamepad2.right_stick_y;
            float relic = gamepad2.left_stick_x;
            boolean relicArmExtend = gamepad2.dpad_left;
            boolean relicArmHalt = gamepad2.dpad_right;

            double intakeMotor = gamepad2.right_stick_y; // to motor if y<0 p=0 else prop
            boolean slideLeftBump = gamepad1.left_bumper;
            boolean slideRightBump = gamepad1.right_bumper;
            boolean relicClawUp = gamepad2.dpad_up;
            boolean relicClawDown = gamepad2.dpad_down;
            boolean relicPartExt = gamepad2.left_bumper;
            boolean relicPartRet = gamepad2.right_bumper;


            DeviceInterfaceModule cdim = hardwareMap.deviceInterfaceModule.get("dim");
            //Slide Related
            double slide;
            double slideL = gamepad1.left_trigger;
            double slideR = gamepad1.right_trigger;
            if (slideL < slideR) {
                slide = slideR * -1;
            } else {
                slide = slideL;
            }
            if (slideLeftBump) {
                slide = 0.3;
            }
            if (slideRightBump) {
                slide = -0.3;
            }
            m1DrivePower = magic(driveL - slide);
            m2DrivePower = magic(driveR - slide);
            m3DrivePower = magic(driveR + slide);
            m4DrivePower = magic(driveL + slide);

            double max = Math.max(Math.max(m1DrivePower, m2DrivePower), Math.max(m3DrivePower, m4DrivePower));
            // Send calculated power to wheels
            if (max >= 1) {
                m1Drive.setPower(m1DrivePower * -1 / max);
                m2Drive.setPower(m2DrivePower / max);
                m3Drive.setPower(m3DrivePower / max);
                m4Drive.setPower(m4DrivePower * -1 / max);
            } else {
                m1Drive.setPower(m1DrivePower * -1);
                m2Drive.setPower(m2DrivePower);
                m3Drive.setPower(m3DrivePower);
                m4Drive.setPower(m4DrivePower * -1);
            }
            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Motors", "m1Drive (%.2f), m2Drive (%.2f), m3Drive (%.2f), m4Drive (%.2f)", m1DrivePower, m2DrivePower, m3DrivePower, m4DrivePower);
            telemetry.update();

            /*
             * End of chassis related code.
             */

            // Claw rotation
            if (clawRotation > 0) {
                s3Rotation.setPosition(0.78 - clawRotation * 0.78);
            } else {
                s3Rotation.setPosition(0.78);
            }
            shovelTrigger(intakeMotor);
            if (intakeMotor > 0) {
                m6Intake.setPower(intakeMotor);
            } else {
                m6Intake.setPower(0);
            }
            if (gamepad2.left_stick_y < 0) {
                m6Intake.setPower(gamepad2.left_stick_y);
            }

            // Claw_lift
            if (clawLiftL != 0) {
                liftClaw(-clawLiftL);
            } else if (clawLiftR != 0) {
                liftClaw(clawLiftR);
            } else {
                liftClaw(-clawLiftL);
            }

            //Partially AutoOP
            if (relic == 0) {
                if (relicArmExtend) { //DO NOT FORKING CHANGE
                    setPowerTimed(s1RelicExtRet, 1, 450);
                    while (!touchSensor.isPressed()) {
                        s1RelicExtRet.setPower(0.5);
                    }
                    s1RelicExtRet.setPower(0);
                    s7RelicArm.setPosition(0.6);
                    sleep(500);
                    s7RelicArm.setPosition(0.8);


                }
                if (relicArmHalt) {
                    s7RelicArm.setPosition(0.3);
                    m1Drive.setPower(-0.5);
                    m2Drive.setPower(0.5);
                    m3Drive.setPower(-0.1);
                    m4Drive.setPower(0.1);
                    sleep(300);
                    m1Drive.setPower(0);
                    m2Drive.setPower(0);
                    m3Drive.setPower(0);
                    m4Drive.setPower(0);
                    sleep(300);
                    setPowerTimed(s1RelicExtRet, -1, 450);
                    for (int tick = 0; tick < 2000; tick += 10) {
                        s1RelicExtRet.setPower(-0.5);
                        if (touchSensor.isPressed() || isStopRequested()) {
                            s1RelicExtRet.setPower(0.4);
                            sleep(150);
                            s1RelicExtRet.setPower(0);

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

            s1RelicExtRet.setPower(relic * 0.2);


            //Relic arm_small
            if (relicClawUp) { //DO NOT CHANGE
                s6RelicClaw.setPosition(0);
                sleep(600);
                s7RelicArm.setPosition(0.95);
                sleep(600);
                s6RelicClaw.setPosition(1);
                sleep(600);
                s7RelicArm.setPosition(0.8);
            } else if (relicClawDown) {
                s7RelicArm.setPosition(0.95);
                s6RelicClaw.setPosition(0);
                sleep(600);
                s7RelicArm.setPosition(0.8);
            }
            if (relicPartExt) {
                s7RelicArm.setPosition(0.8);
            }
            if (relicPartRet) {
                s7RelicArm.setPosition(0.3);

            }
            cdim.setDigitalChannelState(LED_CHANNEL, false);
        }
    }
}
