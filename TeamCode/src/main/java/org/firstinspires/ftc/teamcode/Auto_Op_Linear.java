/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * This file illustrates the concept of driving a path based on time.
 * It uses the common Pushbot hardware class to define the drive on the robot.
 * The code is structured as a LinearOpMode
 * <p>
 * The code assumes that you do NOT have encoders on the wheels,
 * otherwise you would use: PushbotAutoDriveByEncoder;
 * <p>
 * The desired path in this example is:
 * - Drive forward for 3 seconds
 * - Spin right for 1.3 seconds
 * - Drive Backwards for 1 Second
 * - Stop and close the claw.
 * <p>
 * The code is written in a simple form with no optimizations.
 * However, there are several ways that this type of sequence could be streamlined,
 * <p>
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@Autonomous(name = "KTM autonomous", group = "WIP")
//@Disabled
public class Auto_Op_Linear extends LinearOpMode {

    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();

    private DcMotor m1_Drive = null;
    private DcMotor m2_Drive = null;
    private DcMotor m3_Drive = null;
    private DcMotor m4_Drive = null;
    private DcMotor m5_Lift = null;
    private Servo s1_top_Claw = null;
    private Servo s2_bottom_Claw = null;
    private Servo s3_rotation = null;


    /*
     * Functions
     */

    void grab_box(boolean top_clamp, boolean top_release, boolean bottom_clamp, boolean bottom_release) {
        if (top_clamp) {
            s1_top_Claw.setPosition(0.30);
        }
        if (top_release) {
            s1_top_Claw.setPosition(0);
        }
        if (bottom_clamp) {
            s2_bottom_Claw.setPosition(0.30);
        }
        if (bottom_release) {
            s2_bottom_Claw.setPosition(0);
        }
    }

    // Lift claw
    void lift_claw(double lift_power) {
        m5_Lift.setPower(lift_power);
    }

    // Rotate claw
    void rotate_claw(boolean rotate) { //if rotate true then rotate to  180 . else to 0
        if (rotate) {
            s3_rotation.setPosition(1);
        } else {
            s3_rotation.setPosition(0);
        }
    }

    void set_Motors_Power(double D1_power, double D2_power, double D3_power, double D4_power) { //Warning: Эта функция включит моторы но, выключить их надо будет после выполнения какого либо условия
        // Send power to wheels
        m1_Drive.setPower(D1_power);
        m2_Drive.setPower(D2_power);
        m3_Drive.setPower(D3_power);
        m4_Drive.setPower(D4_power);
    }

    void set_Motors_Power_timed(double m1_power, double m2_power, double m3_power, double m4_power, long seconds) {
        m1_Drive.setPower(m1_power);
        m2_Drive.setPower(m2_power);
        m3_Drive.setPower(m3_power);
        m4_Drive.setPower(m4_power);
        sleep(seconds * 1000);
        chassis_stop_movement();
    }

    void chassis_stop_movement() {
        m1_Drive.setPower(0);
        m2_Drive.setPower(0);
        m3_Drive.setPower(0);
        m4_Drive.setPower(0);
    }


    @Override
    public void runOpMode() {

        /*
         * Initialize the drive system variables.
         * The init() method of the hardware class does all the work here
         */
        m1_Drive = hardwareMap.get(DcMotor.class, "m1 drive");
        m2_Drive = hardwareMap.get(DcMotor.class, "m2 drive");
        m3_Drive = hardwareMap.get(DcMotor.class, "m3 drive");
        m4_Drive = hardwareMap.get(DcMotor.class, "m4 drive");
        m5_Lift = hardwareMap.get(DcMotor.class, "m5 lift");
        s1_top_Claw = hardwareMap.get(Servo.class, "s1 top claw");
        s2_bottom_Claw = hardwareMap.get(Servo.class, "s2 bottom claw");
        s3_rotation = hardwareMap.get(Servo.class, "s3 rotation");

        m1_Drive.setDirection(DcMotor.Direction.FORWARD);
        m2_Drive.setDirection(DcMotor.Direction.FORWARD);
        m3_Drive.setDirection(DcMotor.Direction.FORWARD);
        m4_Drive.setDirection(DcMotor.Direction.FORWARD);
        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Ready to run");    //
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // Step through the path, ensuring that the Auto mode has not been stopped along the way

        // Step 1:  Drive forward, backward, rotate for one second
        set_Motors_Power_timed(1, 0, 1, 0, 1);//move forward
        set_Motors_Power_timed(0, 1, 0, 1, 1);//move backward
        set_Motors_Power_timed(1, -1, 1, -1, 1);//Rotate to left
        set_Motors_Power_timed(-1, 1, -1, 1, 1);//Rotate to right
        runtime.reset();
        while (opModeIsActive() && (runtime.seconds() < 3.0)) {
            telemetry.addData("Running step ", "1");
            telemetry.update();
        }

        // Step 2:  Grab box and release it
        grab_box(true, false, true, false);
        sleep(1000);
        grab_box(false, true, false, true);

        runtime.reset();
        while (opModeIsActive() && (runtime.seconds() < 1.3)) {
            telemetry.addData("Running step ", "2");
            telemetry.update();
        }

        // Step 3:  rotate claw
        rotate_claw(true);
        sleep(2000);
        rotate_claw(false);

        runtime.reset();
        while (opModeIsActive() && (runtime.seconds() < 1.0)) {
            telemetry.addData("Running step ", "3");
            telemetry.update();
        }

        // Step 4:  test lifting claw
        lift_claw(0.2);
        sleep(1000);
        lift_claw(-0.2);
        sleep(1000);
        lift_claw(0);
        telemetry.addData("All steps ", "Complete");
        telemetry.update();
        sleep(1000);
    }
}
