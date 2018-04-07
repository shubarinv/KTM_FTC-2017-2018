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

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.Objects;


@Autonomous(name = "AUTO Red Left", group = "AutoOP")
//@Disabled
public class autoRedLeft extends LinearOpMode {
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
    private CRServo s1RelicExtRet = null;
    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;
    private boolean wasExecuted = false;
    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor m1Drive = null;
    private DcMotor m2Drive = null;
    private DcMotor m3Drive = null;
    private DcMotor m4Drive = null;
    private DcMotor m5Lift = null;
    private CRServo s1TopClaw = null;
    private Servo s4Kicker = null;
    private Servo s3Rotation = null;
    private Servo s5Shovel = null;
    private DcMotor m6Intake = null;
    private boolean isPositioned = false;
    /*
     * Functions
     */

    void putBox() {
        setMotorsPowerTimed(-0.2, 0.2, 0.2, -0.2, 1200);//движение назад
        sleep(100);
        setMotorsPowerTimed(0.2, -0.2, -0.2, 0.2, 300);//движение вперёд
        rotateClaw(0);
        setMotorsPowerTimed(0.2, -0.2, -0.2, 0.2, 300);//движение вперёд
        setMotorsPowerTimed(-0.2, 0.2, 0.2, -0.2, 600);//движение назад
        setMotorsPowerTimed(0.1, -0.1, -0.1, 0.1, 500);//движение вперёд
        rotateClaw(0.8);
    }

    // Rotate claw
    void rotateClaw(double rotate) { //if rotate true then rotate to  180 . else to 0
        s3Rotation.setPosition(rotate);
    }

    void setMotorsPower(double D1_power, double D2_power, double D3_power, double D4_power) { //Warning: Эта функция включит моторы но, выключить их надо будет после выполнения какого либо условия
        // Send power to wheels
        m1Drive.setPower(D1_power);
        m2Drive.setPower(D2_power);
        m3Drive.setPower(D3_power);
        m4Drive.setPower(D4_power);
    }

    void setMotorsPowerTimed(double m1_power, double m2_power, double m3_power, double m4_power, long ms) {
        m1Drive.setPower(m1_power);
        m2Drive.setPower(m2_power);
        m3Drive.setPower(m3_power);
        m4Drive.setPower(m4_power);
        sleep(ms);
        chassisStopMovement();
    }

    void chassisStopMovement() {
        m1Drive.setPower(0);
        m2Drive.setPower(0);
        m3Drive.setPower(0);
        m4Drive.setPower(0);
    }

    String getColor() {
        // button is transitioning to a pressed state. Toggle the LED.
        cdim.setDigitalChannelState(LED_CHANNEL, true);

        double[] hue_arr = new double[5];
        //для точности 4 измерения
        for (int j = 0; j < 4; j++) {
            // convert the RGB values to HSV values.
            telemetry.addData("Blue", sensorRGB.blue());
            telemetry.addData("Red", sensorRGB.red());
            telemetry.update();
            sleep(500);
            Color.RGBToHSV((sensorRGB.red() * 255) / 800, (sensorRGB.green() * 255) / 800, (sensorRGB.blue() * 255) / 800, hsvValues);
            double hue = hsvValues[0];
            hue_arr[j] = hue;
        }

        //Находим среднее арифметическое

        double hue_sr = 0;
        for (int j = 0; j < 4; j++) {
            hue_sr += hue_arr[j];
        }
        hue_sr = hue_sr / 4;
        //
        if (hue_sr > 110 && hue_sr < 290) {
            return "Blue";
        } else {
            return "Red";
        }
    }


    @Override
    public void runOpMode() {
        s1RelicExtRet = hardwareMap.get(CRServo.class, "s1 top claw");
        s1RelicExtRet.setPower(0);
        /*
         * To start up Vuforia, tell it the view that we wish to use for camera monitor (on the RC phone);
         * If no camera monitor is desired, use the parameterless constructor instead (commented out below).
         */
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
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

        //Обработка исключений
        // m1_drive

        try {
            //Chassis
            m1Drive = hardwareMap.get(DcMotor.class, "m1 drive");
            m2Drive = hardwareMap.get(DcMotor.class, "m2 drive");
            m3Drive = hardwareMap.get(DcMotor.class, "m3 drive");
            m4Drive = hardwareMap.get(DcMotor.class, "m4 drive");

            m5Lift = hardwareMap.get(DcMotor.class, "m5 lift");
            s1TopClaw = hardwareMap.get(CRServo.class, "s1 top claw");
            s4Kicker = hardwareMap.get(Servo.class, "s4 kick");
            odsSensor = hardwareMap.get(OpticalDistanceSensor.class, "sensor_ods");
            s3Rotation = hardwareMap.get(Servo.class, "s3 rotation");
            s5Shovel = hardwareMap.get(Servo.class, "s5 shovel");
            m6Intake = hardwareMap.get(DcMotor.class, "m6 intake");
        } catch (RuntimeException e) {
            telemetry.addData("INIT", "Error occurred during init");
            telemetry.update();
        }
        // Конец обработки исключений
        m1Drive.setDirection(DcMotor.Direction.FORWARD);
        m2Drive.setDirection(DcMotor.Direction.FORWARD);
        m3Drive.setDirection(DcMotor.Direction.FORWARD);
        m4Drive.setDirection(DcMotor.Direction.FORWARD);
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
            s1RelicExtRet.setPower(0);
            if (wasExecuted) {
                telemetry.addData("Autonomous: ", "DONE");
            }

            if (!wasExecuted) {
                RelicRecoveryVuMark vuMark = null;
                for (int tick = 5; tick < 4000; tick += 1) {
                    vuMark = RelicRecoveryVuMark.from(relicTemplate);
                    if (vuMark != RelicRecoveryVuMark.UNKNOWN) {
                        break;
                    }

                }

                telemetry.addData("VUMARK", vuMark);
                telemetry.update();
        /*
        STEP 1 -Trying to kick jewel
        */

                rotateClaw(0.8);// so that boxes won't fall off
                sleep(800);
                s4Kicker.setPosition(0.75);
                sleep(500);

                telemetry.addData("Step-1", "Running");
                String jewel_color = getColor();
                telemetry.addData("AdaFruit", jewel_color);
                telemetry.update();
                if (Objects.equals(jewel_color, "Blue")) {
                    setMotorsPowerTimed(0.1, 0.1, 0.1, 0.1, 300);//поворот против часовой
                    setMotorsPowerTimed(-0.1, -0.1, -0.1, -0.1, 300);//поворот по часовой
                } else {
                    setMotorsPowerTimed(-0.1, -0.1, -0.1, -0.1, 300);//поворот против часовой
                    setMotorsPowerTimed(0.1, 0.1, 0.1, 0.1, 300);//поворот по часовой
                }
                s4Kicker.setPosition(0);
                cdim.setDigitalChannelState(LED_CHANNEL, false);
                // requestOpModeStop();

        /*
        STEP 2 -Cryptobox related
        */
                setMotorsPowerTimed(-0.2, 0.2, 0.2, -0.2, 1000);//движение вперёд

                double fieldColor;
                int tick;
                double fieldColorReadings = 0;
                for (tick = 0; tick < 600; tick += 10) {
                    setMotorsPower(-0.15, 0.15, -0.15, 0.15);
                    fieldColor = odsSensor.getLightDetected();
                    fieldColorReadings += fieldColor;
                    //telemetry.addData("Readings ", fieldColor);
                    //telemetry.update();
                    sleep(10);
                }
                setMotorsPower(0, 0, 0, 0);

                double fieldColorSR = fieldColorReadings / 60;
                telemetry.addData("SR", fieldColorSR);
                telemetry.update();

                sleep(3000);
                for (tick = 0; tick < 2000; tick += 2) {
                    //telemetry.addData("Centring loop", "iteration: " + tick);
                    //telemetry.addData(" ", odsSensor.getLightDetected());
                    //telemetry.update();
                    fieldColor = odsSensor.getLightDetected();
                    setMotorsPower(-0.15, 0.15, 0.15, -0.15);
                    if (fieldColor > fieldColorSR * 1.5) {
                        telemetry.addData("Centring loop", "line Found 1");
                        //  telemetry.addData("Centring loop", fieldColor);
                        telemetry.update();

                        break;
                    }
                    if (isStopRequested()) {
                        break;
                    }
                    sleep(2);
                }
                setMotorsPower(0, 0, 0, 0);
                sleep(1000);
                sleep(200);
                for (tick = 5; tick < 500; tick += 2) {
                    setMotorsPower(-0.15, 0.15, 0.15, -0.15);
                    if (odsSensor.getLightDetected() < fieldColorSR * 1.3) {
                        chassisStopMovement();
                        break;
                    }
                    sleep(2);
                }
                telemetry.addData("LINE", "1 line LOS");
                telemetry.update();
                sleep(1000);
                chassisStopMovement();
                int drivetime = 0;
                while (odsSensor.getLightDetected() < fieldColorSR * 1.3) {
                    cdim.setDigitalChannelState(LED_CHANNEL, true);
                    if (isStopRequested()) {
                        break;
                    }
                    telemetry.addData("Centring loop", "coasting");
                    telemetry.update();
                    sleep(1);
                    setMotorsPower(-0.15, 0.15, 0.15, -0.15);
                    drivetime += 1;
                    if (odsSensor.getLightDetected() > fieldColorSR * 1.5) {
                        break;
                    }
                }
                if (odsSensor.getLightDetected() > fieldColorSR * 1.5) {
                    telemetry.addData("Centring loop", "line Found 2 (break)");
                    telemetry.update();
                    sleep(400);
                    cdim.setDigitalChannelState(LED_CHANNEL, false);
                    isPositioned = true;
                    setMotorsPowerTimed(0.2, -0.2, -0.2, 0.2, (drivetime / 4));
                    sleep(500);
                }
                setMotorsPowerTimed(0.2, 0.2, 0.2, 0.2, 800);//поворот против часовой
                if (vuMark == RelicRecoveryVuMark.RIGHT) {
                    telemetry.addData("Vumark", " RIGHT");
                    telemetry.update();
                    setMotorsPowerTimed(0.1, 0.1, -0.1, -0.1, 300);// Slide left
                } else if (vuMark == RelicRecoveryVuMark.CENTER) {
                    telemetry.addData("Vumark", " CENTER");
                    telemetry.update();

                } else if (vuMark == RelicRecoveryVuMark.LEFT) {
                    telemetry.addData("Vumark", " LEFT");
                    telemetry.update();
                    setMotorsPowerTimed(-0.1, -0.1, 0.1, 0.1, 300);// Slide right
                } else {
                    telemetry.addData("Line", "(X)NOT VISIBLE");
                    telemetry.update();

                }
                putBox();
                wasExecuted = true;
            }
        }
    }
}
