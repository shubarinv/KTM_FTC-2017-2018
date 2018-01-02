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

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.Objects;


@Autonomous(name = "AUTO_Red_Left", group = "WIP")
//@Disabled
public class Auto_Red_Left extends LinearOpMode {

    /* ADAFRUIT */
    // we assume that the LED pin of the RGB sensor is connected to
    // digital port 5 (zero indexed).
    private static final int LED_CHANNEL = 5;
    ColorSensor sensorRGB;
    DeviceInterfaceModule cdim;
    // hsvValues is an array that will hold the hue, saturation, and value information.
    float hsvValues[] = {0F, 0F, 0F};
    // values is a reference to the hsvValues array.
    final float values[] = hsvValues;
    // bPrevState and bCurrState represent the previous and current state of the button.
    boolean bPrevState = false;
    boolean bCurrState = false;
    // bLedOn represents the state of the LED.
    boolean bLedOn = true;
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
    /*
     * Functions
     */

    void grab_box(boolean top_clamp, boolean top_release, boolean bottom_clamp, boolean bottom_release) {
        // DEPERECATED
        assert true;
    }

    // Lift claw
    void lift_claw(double lift_power, long ms) {
        // DEPERCATED
        assert true;
    }

    // Rotate claw
    void rotate_claw(boolean rotate) { //if rotate true then rotate to  180 . else to 0
        // DEPRECATED
        assert true;
    }

    void set_Motors_Power(double D1_power, double D2_power, double D3_power, double D4_power) { //Warning: Эта функция включит моторы но, выключить их надо будет после выполнения какого либо условия
        // Send power to wheels
        m1_Drive.setPower(D1_power);
        m2_Drive.setPower(D2_power);
        m3_Drive.setPower(D3_power);
        m4_Drive.setPower(D4_power);
    }

    void set_Motors_Power_timed(double m1_power, double m2_power, double m3_power, double m4_power, long ms) {
        m1_Drive.setPower(m1_power);
        m2_Drive.setPower(m2_power);
        m3_Drive.setPower(m3_power);
        m4_Drive.setPower(m4_power);
        sleep(ms);
        chassis_stop_movement();
    }

    void chassis_stop_movement() {
        m1_Drive.setPower(0);
        m2_Drive.setPower(0);
        m3_Drive.setPower(0);
        m4_Drive.setPower(0);
    }

    String get_color() {
        // check the status of the x button on gamepad.
        bCurrState = true;

        // check for button-press state transitions.
        if (bCurrState != bPrevState) {

            // button is transitioning to a pressed state. Toggle the LED.
            cdim.setDigitalChannelState(LED_CHANNEL, bLedOn);
        }

        // update previous state variable.
        bPrevState = bCurrState;

        // convert the RGB values to HSV values.
        telemetry.addData("Blue", sensorRGB.blue());
        telemetry.addData("Red", sensorRGB.red());
        telemetry.update();
        sleep(2000);
        Color.RGBToHSV((sensorRGB.red() * 255) / 800, (sensorRGB.green() * 255) / 800, (sensorRGB.blue() * 255) / 800, hsvValues);

        double hue = hsvValues[0];
        telemetry.addData("HUE", hue);
        if (hue > 200 && hue < 260) {
            return "Blue";
        } else if (hue < 50 || hue > 330) {
            return "Red";
        }
        return "Error";
    }


    @Override
    public void runOpMode() {
        /*
         * To start up Vuforia, tell it the view that we wish to use for camera monitor (on the RC phone);
         * If no camera monitor is desired, use the parameterless constructor instead (commented out below).
         */
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
        //Vuforia API key
        parameters.vuforiaLicenseKey = "AfQcHkL/////AAAAGd5Auzk+t0CxnAw8xKONnjke+r6gFs0KfKK8LsB35FsX6bnhXZmEN+0f3blTVk7nI4xjKNob63Ps1Jpp/JS25hHc083okOZzcTsBlA5qz2hJK3LFNWyZv59kjCUyqbc3qS7dTXJ4i4/JD9t+IeyvGH9G9xPwV7DNmcuNeT7o+YDn3cI7zgUcVcrdFM8t22/wGkmiCz5TfY5A0BMETyriYX6BzlVuwGtMfXdp9CYDQ+ZhZTRNjPfvKlNyLLxVycIiM1p4nprW2UnySO11fmTkUZR9Ofqr+gbHj0VNm7gUEz77s/cHTl+swX84pxpOhm1QJeO0wuNw4c5siQpizcWHPMhJCDRFqRmTQ3LBpcMJWjTx";
        /*
         * We also indicate which camera on the RC that we wish to use.
         * Here we chose the back (HiRes) camera (for greater range), but
         * for a competition robot, the front camera might be more convenient.
         */
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary
        /*
         * Initialize the drive system variables.
         * The init() method of the hardware class does all the work here
         */
        m1_Drive = hardwareMap.get(DcMotor.class, "m1 drive");
        m2_Drive = hardwareMap.get(DcMotor.class, "m2 drive");
        m3_Drive = hardwareMap.get(DcMotor.class, "m3 drive");
        m4_Drive = hardwareMap.get(DcMotor.class, "m4 drive");
        m5_Lift = hardwareMap.get(DcMotor.class, "m5 lift");
        s1_top_Claw = hardwareMap.get(CRServo.class, "s1 top claw");
        s2_bottom_Claw = hardwareMap.get(CRServo.class, "s2 bottom claw");
        s4_kicker = hardwareMap.get(Servo.class, "s4 kick");


        m1_Drive.setDirection(DcMotor.Direction.FORWARD);
        m2_Drive.setDirection(DcMotor.Direction.FORWARD);
        m3_Drive.setDirection(DcMotor.Direction.FORWARD);
        m4_Drive.setDirection(DcMotor.Direction.FORWARD);
        /* AdaFruit */

        // get a reference to our DeviceInterfaceModule object.
        cdim = hardwareMap.deviceInterfaceModule.get("dim");

        // set the digital channel to output mode.
        // remember, the Adafruit sensor is actually two devices.
        // It's an I2C sensor and it's also an LED that can be turned on or off.
        cdim.setDigitalChannelMode(LED_CHANNEL, DigitalChannel.Mode.OUTPUT);

        // get a reference to our ColorSensor object.
        sensorRGB = hardwareMap.colorSensor.get("sensor_color");

        // turn the LED on in the beginning, just so user will know that the sensor is active.
        cdim.setDigitalChannelState(LED_CHANNEL, bLedOn);
        telemetry.addData("AdaFruit", "Ready");
        telemetry.update();
        waitForStart();

        relicTrackables.activate();
        while (opModeIsActive()) {
            if (wasExecuted) {
                telemetry.addData("Autonomous: ", "DONE");
            }
            RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);
            if (!wasExecuted) {
                telemetry.addData("AutoOP", "Running nominally");
                telemetry.update();

                s4_kicker.setPosition(0.4);
                grab_box(true,false,true,false);
                lift_claw(0.1,250);
                telemetry.addData("AdaFruit", get_color());
                telemetry.update();
                /*
                STEP 1 -Trying to kick jewel
                */
                telemetry.addData("Step-1", "Running");
                telemetry.update();
                String jewel_color=get_color();
                if (Objects.equals(jewel_color, "Blue")) {
                    set_Motors_Power_timed(0.2, 0.2, 0.2, 0.2, 300);//поворот против часовой
                    set_Motors_Power_timed(-0.2, -0.2, -0.2, -0.2, 300);//поворот по часовой
                } else if (Objects.equals(jewel_color, "Red")) {
                    set_Motors_Power_timed(-0.2, -0.2, -0.2, -0.2, 300);//поворот по часовой
                    set_Motors_Power_timed(0.2, 0.2, 0.2, 0.2, 300);//поворот против часовой
                } else {
                    telemetry.addData("AdaFruit", "ERROR RECOGNISING COLOR");
                    telemetry.addData("Step-1", "FAILED");
                }
        s4_kicker.setPosition(0.1);

                telemetry.addData("Step-1", "DONE");
                telemetry.update();
                /*
                STEP 2 -Cryptobox related
                */
                if (vuMark == RelicRecoveryVuMark.RIGHT) {
                    telemetry.addData("Vumark", " RIGHT");
                    telemetry.update();
                    grab_box(true, false, false, true);
                    sleep(100);
                    lift_claw(0.3, 1250);
                    sleep(100);
                    set_Motors_Power_timed(0.2, -0.2, -0.2, 0.2, 2100);//движение назад
                    set_Motors_Power_timed(0.2, 0.2, 0.2, 0.2, 1350);//поворот против часовой
                    lift_claw(-0.3, 1250);
                    sleep(100);
                    grab_box(false, true, false, false);
                    set_Motors_Power_timed(-0.2, 0.2, 0.2, -0.2, 1000);//движение вперёд
                    sleep(100);
                    set_Motors_Power_timed(0.2, -0.2, -0.2, 0.2, 1000);//движение назад

                    wasExecuted = true;
                } else if (vuMark == RelicRecoveryVuMark.CENTER) {
                    telemetry.addData("Vumark", " CENTER");
                    telemetry.update();

                    grab_box(true, false, false, true);
                    sleep(500);
                    lift_claw(0.3, 1250);
                    sleep(100);
                    set_Motors_Power_timed(0.2, -0.2, -0.2, 0.2, 2750);//движение назад
                    set_Motors_Power_timed(0.2, 0.2, 0.2, 0.2, 1350);//поворот против часовой
                    lift_claw(-0.3, 1250);
                    sleep(100);
                    grab_box(false, true, false, false);
                    set_Motors_Power_timed(-0.2, 0.2, 0.2, -0.2, 1000);//движение вперёд
                    sleep(100);
                    set_Motors_Power_timed(0.2, -0.2, -0.2, 0.2, 1000);//движение назад

                    wasExecuted = true;
                } else if (vuMark == RelicRecoveryVuMark.LEFT) {
                    telemetry.addData("Vumark", " LEFT");
                    telemetry.update();
                    grab_box(true, false, false, true);
                    sleep(500);
                    lift_claw(0.3, 1250);
                    sleep(100);
                    set_Motors_Power_timed(0.2, -0.2, -0.2, 0.2, 3400);//движение назад
                    set_Motors_Power_timed(0.2, 0.2, 0.2, 0.2, 1250);//поворот по часовой на 90 градусов
                    lift_claw(-0.3, 1250);
                    sleep(100);
                    grab_box(false, true, false, false);
                    set_Motors_Power_timed(-0.2, 0.2, 0.2, -0.2, 1000);//движение вперёд
                    sleep(100);
                    set_Motors_Power_timed(0.2, -0.2, -0.2, 0.2, 1000);//движение назад
                }
                    wasExecuted = true;
            }
            telemetry.update();
        }
    }
}
