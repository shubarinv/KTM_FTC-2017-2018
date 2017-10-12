package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
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

@TeleOp(name = "KTM TeleOp", group = "Linear Opmode")

//@Disabled
public class OpMode_Linear extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    //Chassis
    private DcMotor m1_Drive = null;
    private DcMotor m2_Drive = null;
    private DcMotor m3_Drive = null;
    private DcMotor m4_Drive = null;
    private DcMotor m5_Lift = null;
    private Servo s1_top_Claw = null;
    private Servo s2_bottom_Claw = null;

    //-------
    double magic(double input) {
        return Math.signum(input) * Math.pow(Math.abs(input), 2);
    }


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
        //-------
        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        m1_Drive.setDirection(DcMotor.Direction.FORWARD);
        m2_Drive.setDirection(DcMotor.Direction.FORWARD);
        m3_Drive.setDirection(DcMotor.Direction.FORWARD);
        m4_Drive.setDirection(DcMotor.Direction.FORWARD);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            /*
            ++
               ##Chassis movement
            ++
            */
            //Setup a variable for each drive wheel to save power level for telemetry
            double m1_Drive_Power;
            double m2_Drive_Power;
            double m3_Drive_Power;
            double m4_Drive_Power;

            // POV Mode uses right stick to go forward and right to slide.
            // - This uses basic math to combine motions and is easier to drive straight.
            double drive = -gamepad1.right_stick_y;
            double slide = gamepad1.right_stick_x;
            double rotation = -gamepad1.left_stick_x;
            double A = Math.abs(rotation) + Math.abs(drive) + Math.abs(slide);
            if (A <= 1) {
                m1_Drive_Power = rotation - drive - slide;
                m2_Drive_Power = rotation + drive - slide;
                m3_Drive_Power = rotation + drive + slide;
                m4_Drive_Power = rotation - drive + slide;
            } else {
                rotation = rotation / A;
                drive = drive / A;
                slide = slide / A;
                m1_Drive_Power = rotation - drive - slide;
                m2_Drive_Power = rotation + drive - slide;
                m3_Drive_Power = rotation + drive + slide;
                m4_Drive_Power = rotation - drive + slide;
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
            ++
               ##Box grabbing and movement
            ++
            */

            // TODO: 10.10.2017 Grab box
            // TODO: 10.10.2017 Rotate box if needed
            // TODO: 10.10.2017 Grab another box
            // TODO: 10.10.2017 Place box to shelf

            /*
            ++
               ##Relic related
            ++
            */
            // TODO: 10.10.2017 Grab relic
            // TODO: 10.10.2017 Extend grabbing component
            // TODO: 10.10.2017 Retract grabbing component


        }
    }
}
