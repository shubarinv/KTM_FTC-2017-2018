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

package org.firstinspires.ftc.teamcode.AutoOPs;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;


@Autonomous(name = "AUTO DEBUG", group = "AutoOP")
//@Disabled
public class Auto_DEBUG extends LinearOpMode {
    /* ADAFRUIT */
    // we assume that the LED pin of the RGB sensor is connected to
    // digital port 5 (zero indexed).
    private static final int LED_CHANNEL = 5;
    ColorSensor sensorRGB;
    DeviceInterfaceModule cdim;
    // hsvValues is an array that will hold the hue, saturation, and value information.
    float hsvValues[] = {0F, 0F, 0F};
    // bLedOn represents the state of the LED.
    boolean bLedOn = false;
    OpticalDistanceSensor odsSensor;  // Hardware Device Object
    private CRServo s1_Relic_ext_ret = null;
    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;
    private boolean wasExecuted = false;
    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor m1_Drive = null;
    private DcMotor m2_Drive = null;
    private DcMotor m3_Drive = null;
    private DcMotor m4_Drive = null;
    private DcMotor m5_Lift = null;
    private CRServo s1_top_Claw = null;
    private CRServo s2_bottom_Claw = null;
    private Servo s4_kicker = null;
    private Servo s3_rotation = null;
    private Servo s5_shovel = null;
    private DcMotor m6_intake = null;
  /*
  * Functions
  */

    @Override
    public void runOpMode() {
        s1_Relic_ext_ret = hardwareMap.get(CRServo.class, "s1 top claw");
        s1_Relic_ext_ret.setPower(0);

        //Обработка исключений
        // m1_drive

        try {
            m1_Drive = hardwareMap.get(DcMotor.class, "m1 drive");
        } catch (RuntimeException e) {
            m1_Drive = null;
            telemetry.addData("EXCEPTION", "Отвалился m1_Drive");
        }
        // m2_drive
        try {
            m2_Drive = hardwareMap.get(DcMotor.class, "m2 drive");
        } catch (RuntimeException e) {
            m2_Drive = null;
            telemetry.addData("EXCEPTION", "Отвалился m2_Drive");
        }
        // m3_drive
        try {
            m3_Drive = hardwareMap.get(DcMotor.class, "m3 drive");
        } catch (RuntimeException e) {
            m3_Drive = null;
            telemetry.addData("EXCEPTION", "Отвалился m3_Drive");
        }
        // m4_drive
        try {
            m4_Drive = hardwareMap.get(DcMotor.class, "m4 drive");
        } catch (RuntimeException e) {
            m4_Drive = null;
            telemetry.addData("EXCEPTION", "Отвалился m4_Drive");
        }
        // m5_lift
        try {
            m5_Lift = hardwareMap.get(DcMotor.class, "m5 lift");
        } catch (RuntimeException e) {
            m5_Lift = null;
            telemetry.addData("EXCEPTION", "Отвалился m5_lift");
        }
        // s1_top_Claw
        try {
            s1_top_Claw = hardwareMap.get(CRServo.class, "s1 top claw");
        } catch (RuntimeException e) {
            s1_top_Claw = null;
            telemetry.addData("EXCEPTION", "Отвалился s1 top claw");
        }
        // s2_bottom_Claw
        try {
            s2_bottom_Claw = hardwareMap.get(CRServo.class, "s2 bottom claw");
        } catch (RuntimeException e) {
            s2_bottom_Claw = null;
            telemetry.addData("EXCEPTION", "Отвалился s2 bottom claw");
        }
        //s4_kicker
        try {
            s4_kicker = hardwareMap.get(Servo.class, "s4 kick");
        } catch (RuntimeException e) {
            s4_kicker = null;
            telemetry.addData("EXCEPTION", "Отвалился s4 kick(палка)");
        }
        s3_rotation = hardwareMap.get(Servo.class, "s3 rotation");
        s5_shovel = hardwareMap.get(Servo.class, "s5 shovel");
        // Конец обработки исключений
        m1_Drive.setDirection(DcMotor.Direction.FORWARD);
        m2_Drive.setDirection(DcMotor.Direction.FORWARD);
        m3_Drive.setDirection(DcMotor.Direction.FORWARD);
        m4_Drive.setDirection(DcMotor.Direction.FORWARD);
        telemetry.addData("press", "play");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            // Это нужно для запуска автономки 1 раз
            if (wasExecuted) {
                requestOpModeStop();
            } else {

                m1_Drive.setPower(0.2);
                m2_Drive.setPower(-0.2);
                m3_Drive.setPower(0.2);
                m4_Drive.setPower(-0.2);
                m5_Lift.setPower(0);//Подъем платформы
                sleep(500);
                s5_shovel.setPosition(0);
                s3_rotation.setPosition(0);

                wasExecuted = true;
            }
        }
    }
}

