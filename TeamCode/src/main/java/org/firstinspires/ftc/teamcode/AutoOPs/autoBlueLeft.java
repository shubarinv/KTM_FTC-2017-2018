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
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.teamcode.robot;

import java.util.Objects;


@Autonomous(name = "Blue Left", group = "AutoOP")
//@Disabled
public class autoBlueLeft extends robot {
    DeviceInterfaceModule cdim;
    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;
    private boolean wasExecuted = false;
    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    private boolean isPositioned = false;

    @Override
    public void runOpMode() {
        //Vuforia Related Variables
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = "AfQcHkL/////AAAAGd5Auzk+t0CxnAw8xKONnjke+r6gFs0KfKK8LsB35FsX6bnhXZmEN+0f3blTVk7nI4xjKNob63Ps1Jpp/JS25hHc083okOZzcTsBlA5qz2hJK3LFNWyZv59kjCUyqbc3qS7dTXJ4i4/JD9t+IeyvGH9G9xPwV7DNmcuNeT7o+YDn3cI7zgUcVcrdFM8t22/wGkmiCz5TfY5A0BMETyriYX6BzlVuwGtMfXdp9CYDQ+ZhZTRNjPfvKlNyLLxVycIiM1p4nprW2UnySO11fmTkUZR9Ofqr+gbHj0VNm7gUEz77s/cHTl+swX84pxpOhm1QJeO0wuNw4c5siQpizcWHPMhJCDRFqRmTQ3LBpcMJWjTx";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);
        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary
        cdim = hardwareMap.deviceInterfaceModule.get("dim");
        //Инициализация наследуется из robot.java
        initHW(hardwareMap);

        //Это для LED
        cdim.setDigitalChannelMode(LED_CHANNEL, DigitalChannel.Mode.OUTPUT);


        cdim.setDigitalChannelState(LED_CHANNEL, false); // Отключаем LED
        log("Ready");
        runtime.reset();
        waitForStart();

        relicTrackables.activate();
        while (opModeIsActive()) {
            if (wasExecuted) {
                telemetry.addData("Autonomous: ", "DONE");
            }

            if (!wasExecuted) {
                /* Это определение VuMark */
                log("Смотрим Vumark", runtime.seconds());
                int relicType = getRelic(relicTemplate);

                /*  Это для сбития Jewel */
                rotateClaw(0.8);// so that boxes won't fall off
                sleep(800);
                s4Kicker.setPosition(0.75);
                sleep(500);
                cdim.setDigitalChannelState(LED_CHANNEL, true);
                telemetry.addData("Step-1", "Running");
                String jewel_color = getColor();
                log("Jewel color: " + jewel_color + "Runtime ", runtime.seconds());
                telemetry.addData("AdaFruit", jewel_color);
                telemetry.update();
                if (Objects.equals(jewel_color, "Blue")) {
                    setMotorsPowerTimed(-0.1, -0.1, -0.1, -0.1, 300);//поворот по часовой
                    setMotorsPowerTimed(0.1, 0.1, 0.1, 0.1, 300);//поворот против часовой

                } else {
                    setMotorsPowerTimed(0.1, 0.1, 0.1, 0.1, 300);//поворот по часовой
                    setMotorsPowerTimed(-0.1, -0.1, -0.1, -0.1, 300);//поворот против часовой
                }
                s4Kicker.setPosition(0.05);
                cdim.setDigitalChannelState(LED_CHANNEL, false);
                // requestOpModeStop();

                /*  Это съезд с камня и езда с поиском линии до CryptoBox */
                setMotorsPowerTimed(0.22, -0.2, -0.2, 0.2, 1300);// Съезд с камня
                double fieldColorSR = getFieldColorSR(0.22, -0.2, 0, 0);

                int tick;
                double fieldColor;
                /* Поиск первой линии */
                for (tick = 0; tick < 1000; tick += 2) {
                    fieldColor = odsSensor.getLightDetected();
                    setMotorsPower(0.2, -0.2, 0.2, -0.2);
                    if (fieldColor > fieldColorSR * shininessCoefficient) {
                        log("Found First Line " + fieldColor, runtime.seconds());
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
                /* Пытаемся потерять линию */

                for (tick = 0; tick < 500; tick += 2) {
                    setMotorsPower(0.1, -0.1, 0.1, -0.1);
                    if (odsSensor.getLightDetected() < fieldColorSR * shininessCoefficient && tick > 100) {
                        log("Потеряна Первая линия ", runtime.seconds());
                        telemetry.addData("LINE", "1 line LOS");
                        telemetry.update();
                        chassisStopMovement();
                        break;
                    }
                    sleep(2);
                }
                chassisStopMovement();
                sleep(200);

                int drivetime = 0;

                /* Едем и ищем вторую линию */
                for (tick = 0; tick < 1000; tick += 2) {
                    if (isStopRequested()) {
                        break;
                    }
                    sleep(2);
                    setMotorsPower(0.20, -0.20, 0.20, -0.20);
                    drivetime += 2;
                    if (odsSensor.getLightDetected() > fieldColorSR * shininessCoefficient) {
                        break;
                    }
                }
                chassisStopMovement();
                /* Если нашли линию */
                if (odsSensor.getLightDetected() > fieldColorSR * shininessCoefficient) {
                    log("Вторая линия найдена " + odsSensor.getLightDetected(), runtime.seconds());
                    chassisStopMovement();
                    telemetry.addData("Centring loop", "line Found 2 (break)");
                    telemetry.update();
                }

                sleep(200);

                setMotorsPowerTimed(-0.25, 0.25, -0.25, 0.25, (drivetime / 2)+100);

                log("Вернулся к центральной полке ", runtime.seconds());
                sleep(200);

                /* Начало движения к нужной полке */
                log("Повернулся на 180 градусов ", runtime.seconds());
                sleep(200);

                if (relicType == 3) {
                    telemetry.addData("Vumark", " RIGHT");
                    telemetry.update();
                    setMotorsPowerTimed(0.3, -0.3, 0.3, -0.3, 500);// Slide left
                } else if (relicType == 2) {
                    telemetry.addData("Vumark", " CENTER");
                    telemetry.update();
                } else if (relicType == 1) {
                    telemetry.addData("Vumark", " LEFT");
                    telemetry.update();
                    setMotorsPowerTimed(-0.3, 0.3, -0.3, 0.3, 500);// Slide right
                }
                sleep(200);
                putBox();

                wasExecuted = true;
                telemetry.clearAll();
                telemetry.addData("LOG", printLog());
                telemetry.update();
            }
        }
    }
}
