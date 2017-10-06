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
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.MatrixF;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.ArrayList;
import java.util.List;

/*
 Эта программа 2016-2017 гг. иллюстрирует основы использования локатора Vuforia для определения позиционирования и ориентации робота на поле FTC.
 Код структурирован, как автономный период.

 Vuforia использует камеру телефона для осмотра его окружения и пытается найти целевые изображения.

 Когда изображения идентифицированны, Vuforia может определить положение и ориентацию изображения относительно камеры.
 Этот пример кода, который объединяет эту информацию с знанием того, где целевые изображения находятся на поле, для определения местоположения камеры.

 Этот пример предполагает конфигурацию поля «алмаз», где красные и синие альянсовые станции примыкают к углу поля, наиболее удаленного от аудитории.
 С точки зрения аудитории, красная станция водителя находится справа.
 Две цели видения расположены на двух стенах, наиболее близких к аудитории, с которыми они сталкиваются.
 Камни находятся на красной стороне поля, а чипы находятся на синей стороне.

 Окончательный расчет затем использует местоположение камеры на роботе для определения расположения и ориентации робота на поле.

 @see VuforiaLocalizer
 @see VuforiaTrackableDefaultListener
 см. ftc_app / doc / tutorial / FTC_FieldCoordinateSystemDefinition.pdf

 Используйте Android Studio, чтобы скопировать этот класс и вставьте его в папку кода вашей команды с новым именем.
 Удалите или закомментируйте строку @Disabled, чтобы добавить эту программу в список на Driver Station.

 ВАЖНО: Чтобы использовать эту программу, вам необходимо получить свой лицензионный ключ Vuforia. Для объяснения {@link ConceptVuforiaNavigation}.
*/

@Autonomous(name="KTM Vuforia Navigation", group ="WIP")
@Disabled
public class VuforiaNavigation extends LinearOpMode {

    public static final String TAG = "Vuforia Navigation Sample";

    OpenGLMatrix lastLocation = null;

    /*
     {@link #vuforia} - это переменная, которую мы будем использовать для хранения нашего экземпляра Vuforia.
     Движок локализации.
    */

    VuforiaLocalizer vuforia;

    @Override public void runOpMode() {
        /*
         Чтобы запустить Vuforia, скажем ему, что мы хотим использовать для монитора камеры (на RC-телефоне);
         Если монитор камеры не нужен, используйте вместо этого конструктор без параметров (см. Ниже).
        */

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        // ИЛИ ... Не активируйте просмотр монитора камеры, чтобы сохранить мощность.
        // VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters ();

        /*
         ВАЖНО: вам необходимо получить свой лицензионный ключ для использования Vuforia. Строка ниже, с 'parameters.vuforiaLicenseKey' инициализируется только для иллюстрации и не будет функционировать.
         Лицензионный ключ Vuforia 'Development' может быть получен бесплатно от разработчика Vuforia на веб-сайте https://developer.vuforia.com/license-manager.

         Лицензионные ключи Vuforia всегда содержат 380 символов и выглядят так, как будто они содержат в основном случайные данные.
         В качестве примера приведен пример фрагмента действительного ключа: ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
         Как только вы получили лицензионный ключ, скопируйте строку с веб-сайта Vuforia и вставьте его в свой код на следующей строке, между двойными кавычками.
        */

        parameters.vuforiaLicenseKey = "AfQcHkL/////AAAAGd5Auzk+t0CxnAw8xKONnjke+r6gFs0KfKK8LsB35FsX6bnhXZmEN+0f3blTVk7nI4xjKNob63Ps1Jpp/JS25hHc083okOZzcTsBlA5qz2hJK3LFNWyZv59kjCUyqbc3qS7dTXJ4i4/JD9t+IeyvGH9G9xPwV7DNmcuNeT7o+YDn3cI7zgUcVcrdFM8t22/wGkmiCz5TfY5A0BMETyriYX6BzlVuwGtMfXdp9CYDQ+ZhZTRNjPfvKlNyLLxVycIiM1p4nprW2UnySO11fmTkUZR9Ofqr+gbHj0VNm7gUEz77s/cHTl+swX84pxpOhm1QJeO0wuNw4c5siQpizcWHPMhJCDRFqRmTQ3LBpcMJWjTx";

        /*
         Мы также укажем, какую камеру на RC мы хотим использовать.
         Здесь мы выбрали заднюю (HiRes) камеру (для большего диапазона), но для соревновательного робота передняя камера может быть более удобной.
        */

        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        /*
         Загрузите наборы данных для объектов, которые мы хотим отслеживать.
         Эти конкретные наборы хранятся в части «активов» нашего приложения (вы увидите их на Android Studio «Проект» находится там слева от экрана).
         Вы можете создавать собственные наборы данных с Vuforia Target Manager: https://developer.vuforia.com/target-manager.
         PDF-файлы для примера «StonesAndChips» и наборы данных можно найти в этом проекте в каталоге документаций.
        */

        VuforiaTrackables stonesAndChips = this.vuforia.loadTrackablesFromAsset("StonesAndChips");
        VuforiaTrackable redTarget = stonesAndChips.get(0);
        redTarget.setName("RedTarget");   //Камни

        VuforiaTrackable blueTarget  = stonesAndChips.get(1);
        blueTarget.setName("BlueTarget"); //Чипы

        // Для удобства соберите все отслеживаемые объекты в одной легкопереходной коллекции

        List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
        allTrackables.addAll(stonesAndChips);

        /*
         Мы используем единицы измерения в миллиметрах здесь, потому что это рекомендуемые единицы измерения для значений размеров, указанные в XML для отслеживания треков ImageTarget в наборах данных.
         Например.: <ImageTarget name = "stones" size = "247 173" />
         Вы не *должны* использовать здесь mm, но единицы здесь и единицы, используемые в XML файлах конфигурации *должны* соответствовать математике для правильной работы.
        */

        float mmPerInch        = 25.4f;
        float mmBotWidth       = 18 * mmPerInch;            // ... или что-то подходящее для вашего робота
        float mmFTCFieldWidth  = (12*12 - 2) * mmPerInch;   // поле FTC составляет ~ 11'10 "от центра к центру стеклянных панелей

        /*
         Чтобы локализация работала, нам нужно сообщить системе, в которой каждую цель мы хотим использовать для навигации на поле, и нам нужно указать, где на роботе находится телефон.
         Эти спецификации представлены в виде матриц преобразования <em>. </ Em>
         Матрицы преобразования представляют собой центральную, важную концепцию в математике, которая здесь связана с локализацией.
         См. <a href="https://en.wikipedia.org/wiki/Transformation_matrix"> матрица преобразования </a> для получения подробной информации.
         Как правило, вы столкнетесь с матрицами преобразования как с экземплярами класса {@link OpenGLMatrix}.

         По большей части вам не нужно понимать детали математики того, как работает трансформация
         матрицы(насколько это увлекательно). Просто помните эти ключевые моменты:
         <Ол>

             <li> Вы можете объединить два преобразования, чтобы создать третью комбинацию эффекта обоих из них.
             Если, например, у вас есть преобразование вращения R и трансляционное преобразование T,
             то объединенная матрица преобразования RT, которая выполняет сначала поворот, а затем перевод
             (@code RT = T.multiplied (R)}. То есть преобразования преобразуются в <em> reverse </ em> в том хронологическом порядке, в котором они были применены. </ li>

             <li> Общим способом создания полезных преобразований является использование методов в {@link OpenGLMatrix} класса и класса Orientation.
             См., Например, {@link OpenGLMatrix # translation (float, float, float)}, {@link OpenGLMatrix # rotation (AngleUnit, float, float, float, float)} и
             {@link Orientation # getRotationMatrix (AxesReference, AxesOrder, AngleUnit, float, float, float)}.
             Связанные с этим методы в {@link OpenGLMatrix}, такие как {@link OpenGLMatrix # rotated (AngleUnit, float, float, float, float)},
             являются синтаксическими сокращениями для создания нового преобразования и затем сразу же умножают на него приемник, который может быть удобным в разы. </ li>

             <li> Если вы хотите открыть черный ящик матрицы преобразования, чтобы понять, что он делает внутри, используйте {@link MatrixF # getTranslation ()},
             чтобы узнать, сколько transform будет перемещать вас по x, y и z и использовать
             {@link Orientation # getOrientation (MatrixF, AxesReference, AxesOrder, AngleUnit)}, чтобы определить вращательное движение, что преобразование будет распространяться.
             См. {@link #format (OpenGLMatrix)} ниже для примера. </ Li>

         </ Ол>

         Этот пример помещает изображение «камней» на стене периметра слева от
          стены красной станции водителя. Подобно местоположению Red Beacon на Res-Q

         Этот пример помещает изображение «чипов» на стене периметра справа от
          станции Blue Driver. Как и расположение Blue Beacon на Res-Q

         См. Папку doc этого проекта для описания условных обозначений Axis.

         Первоначально цель концептуально лежит в начале координат поля
         (центр поля), вверх.

         В этой конфигурации система координат цели совпадает с координатой поля.

         В реальной ситуации мы также учитываем вертикальное (Z) смещение цели,
         но для простоты мы игнорируем это здесь; для реального робота вы захотите это исправить.

         Чтобы разместить цель камней на стене Red Audience:
         - Сначала мы поворачиваем 90 вокруг оси X поля, чтобы перевернуть ее вертикально
         - Затем мы поворачиваем 90 вокруг поля Z, чтобы противостоять ему от аудитории.
         - Наконец, мы переводим его обратно по оси X к красной стене аудитории.
        */

        OpenGLMatrix redTargetLocationOnField = OpenGLMatrix

                // Затем мы переводим цель на КРАСНУЮ СТЕНУ. Наш перевод здесь - отрицательный перевод в X.

                .translation(-mmFTCFieldWidth/2, 0, 0)
                .multiplied(Orientation.getRotationMatrix(

                        // Во-первых, в фиксированной (полевой) системе координат мы поворачиваем 90 в X, затем 90 в Z

                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 90, 0));
        redTarget.setLocation(redTargetLocationOnField);
        RobotLog.ii(TAG, "Red Target=%s", format(redTargetLocationOnField));

       /*
        Чтобы разместить цель «Камни» на стене «Голубая аудитория»:
         - Сначала мы поворачиваем 90 вокруг оси X поля, чтобы перевернуть ее вертикально
         - Наконец, мы переводим его вдоль оси Y в сторону синей стены аудитории.
       */

        OpenGLMatrix blueTargetLocationOnField = OpenGLMatrix

                /*
                 Затем мы переведем цель на стену Blue Audience.
                 Наш перевод здесь - положительный перевод в Y.
                */

                .translation(0, mmFTCFieldWidth/2, 0)
                .multiplied(Orientation.getRotationMatrix(

                        // Во-первых, в фиксированной (полевой) системе координат мы поворачиваем на 90 в X

                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 0, 0));
        blueTarget.setLocation(blueTargetLocationOnField);
        RobotLog.ii(TAG, "Blue Target=%s", format(blueTargetLocationOnField));

        /*
         Создайте матрицу преобразования, описывающую, где телефон находится на роботе.
         Положите телефон справа от робота с экраном, стоящим в (см. наш выбор камеры BACK выше) и в ландшафтном режиме.
         Начиная с выравнивания между осями робота и телефона, это вращение -90 вдоль оси Y.

         При определении того, является ли поворот положительным или отрицательным, считайте себя,
         как вниз (положительная) ось вращения от положительного к началу координат. Положительные вращения
         затем CCW и отрицательные вращения CW. Пример: рассмотрим возможность поиска положительной Z
         оси в направлении начала координат. Положительное вращение вокруг Z (т. Е. Вращение, параллельное X-Y
         плоскости) - это CCW, как обычно можно ожидать от обычной классической 2D-геометрии.
        */

        OpenGLMatrix phoneLocationOnRobot = OpenGLMatrix
                .translation(mmBotWidth/2,0,0)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.YZY,
                        AngleUnit.DEGREES, -90, 0, 0));
        RobotLog.ii(TAG, "phone=%s", format(phoneLocationOnRobot));

        /*
         Позвольте послушным слушателям, о которых мы заботимся, знать, где находится телефон. Мы знаем, что каждый
         слушатель является {@link VuforiaTrackableDefaultListener} и может так безопасно отбрасываться, потому что
         мы сами не установили слушателя другого типа.
        */

        ((VuforiaTrackableDefaultListener)redTarget.getListener()).setPhoneInformation(phoneLocationOnRobot, parameters.cameraDirection);
        ((VuforiaTrackableDefaultListener)blueTarget.getListener()).setPhoneInformation(phoneLocationOnRobot, parameters.cameraDirection);

        /*
         Краткое руководство: вот как будет работать математика:

         C = phoneLocationOnRobot  maps   phone coords -> robot coords
         P = tracker.getPose()     maps   image target coords -> phone coords
         L = redTargetLocationOnField maps   image target coords -> field coords

         Так

         C.inverted()              maps   robot coords -> phone coords
         P.inverted()              maps   phone coords -> imageTarget coords

         Положите всё это вместе,

         L x P.inverted() x C.inverted() maps robot coords to field coords.

         @see VuforiaTrackableDefaultListener#getRobotLocation()
        */

        // Ждём пока начнётся игра

        telemetry.addData(">", "Press Play to start tracking");
        telemetry.update();
        waitForStart();

        // Начните отслеживать нужные нам наборы данных.

        stonesAndChips.activate();

        while (opModeIsActive()) {

            for (VuforiaTrackable trackable : allTrackables) {

                /*
                 getUpdatedRobotLocation () вернет значение null, если новая информация отсутствует с
                 последнего раза, когда был сделан вызов, или если отслеживаемый вид не отображается в данный момент.
                 getRobotLocation () вернет значение null, если отслеживаемый объект не отображается в данный момент.
                */

                telemetry.addData(trackable.getName(), ((VuforiaTrackableDefaultListener)trackable.getListener()).isVisible() ? "Visible" : "Not Visible");

                OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener)trackable.getListener()).getUpdatedRobotLocation();
                if (robotLocationTransform != null) {
                    lastLocation = robotLocationTransform;
                }
            }


            //Сообщите о том, где находился последний робот (если мы знаем).


            if (lastLocation != null) {
                //  RobotLog.vv(TAG, "robot=%s", format(lastLocation));
                telemetry.addData("Pos", format(lastLocation));
            } else {
                telemetry.addData("Pos", "Unknown");
            }
            telemetry.update();
        }
    }

    //Простая утилита, которая извлекает информацию о местоположении из матрицы преобразованияи форматирует его в форме, приемлемой для человека.

    String format(OpenGLMatrix transformationMatrix) {
        return transformationMatrix.formatAsTransform();
    }
}