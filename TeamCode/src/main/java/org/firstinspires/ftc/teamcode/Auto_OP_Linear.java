package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
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


@TeleOp(name = "KTM AUTO_OP", group = "Linear Auto Opmode")

//@Disabled
public class Auto_OP_Linear extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    //Chassis
    private DcMotor m1_Drive = null;
    private DcMotor m2_Drive = null;
    private DcMotor m3_Drive = null;
    private DcMotor m4_Drive = null;

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
            assert true;
            //Autonomous period starts
            // TODO: 10.10.2017 Determine color of opposite team
            // TODO: 10.10.2017 Kick the ball that have color of opposite team
            // TODO: 10.10.2017 Scan Vumark
            // TODO: 10.10.2017 Determine shelf location
            // TODO: 10.10.2017 Move to shelf
            // TODO: 10.10.2017 Place cube in required location
            // TODO: 10.10.2017 Determine location of starting point
            // TODO: 10.10.2017 Return to starting point

        }
    }

    void chassis_move(double D1_power, double D2_power, double D3_power, double D4_power) { //Warning: Эта функция включит моторы но, выключить их надо будет после выполнения какого либо условия
        // Send power to wheels
        m1_Drive.setPower(D1_power);
        m2_Drive.setPower(D2_power);
        m3_Drive.setPower(D3_power);
        m4_Drive.setPower(D4_power);
    }

    void chassis_timed_move(double D1_power, double D2_power, double D3_power, double D4_power, long seconds) {
        m1_Drive.setPower(D1_power);
        m2_Drive.setPower(D2_power);
        m3_Drive.setPower(D3_power);
        m4_Drive.setPower(D4_power);
        sleep(seconds * 1000);
        chassis_stop_movement();
    }

    void chassis_stop_movement() {
        m1_Drive.setPower(0);
        m2_Drive.setPower(0);
        m3_Drive.setPower(0);
        m4_Drive.setPower(0);
    }

}

