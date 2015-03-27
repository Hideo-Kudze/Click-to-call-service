<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html lang="ru">

<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
<head>
    <meta charset="utf-8">
    <title>MAXISERVICE</title>

    <link rel="stylesheet" href="/resources/css/style000.css">
    <link rel="shortcut icon" href="/resources/favicon0.htm">
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7/jquery.js"></script>
    <script src="http://malsup.github.com/jquery.form.js"></script>
    <script>


        function presetValues() {
            $("body").hide();
            var data = {
                "type": "WidgetProperties"
            };
            data = JSON.stringify(data);
            var response = $.ajax({
                type: "POST",
                url: "/domainObject/read",
                contentType: "application/json",
                async: false,
                data: data
            }).responseText;
            response = JSON.parse(response)[0];
            for (var property in response) {
                if (response.hasOwnProperty(property)) {
                    var inputElement = $("#" + property)[0];
                    if (inputElement == null)
                        continue;
                    if (inputElement.type == "text")
                        inputElement.value = response[property];
                    else if (inputElement.type == "checkbox")
                        inputElement.checked = response[property];
                }
            }
            $("body").show();
        }

        function updateDbEntity() {
            id = "a";
            $("#update").click(function () {
                $("#update").prop('disabled', true);
                var dbInputs = $("[data-db]");
                var data = {
                    "type": "WidgetProperties",
                    "id": id
                };
                for (var i = 0; i < dbInputs.length; i++) {
                    var dbInput = dbInputs[i];
                    if (dbInput.type == "text")
                        data[dbInput.id] = dbInput.value;
                    else if (dbInput.type == "checkbox")
                        data[dbInput.id] = dbInput.checked;
                }
                data = JSON.stringify(data);
                $.ajax({
                    type: "POST",
                    data: data,
                    url: "/domainObject/update",
                    contentType: "application/json",
                    data: data,
                    complete: function () {
                        $("#update").prop('disabled', false);
                    }
                });
            })
        }


        $(function () {
            presetValues();
            updateDbEntity();
        })

    </script>
</head>
<body>
<header class="header">
    <div class="wrapper">
        <div class="logo fl"><span>MAXI</span>SERVICE</div>
        <div class="fr">
            <div class="balance ib">10.83$</div>
            <div class="topup ib">Пополнить баланс</div>
            <div class="links ib ib-list">
                <a href="http://alanev.ru/works/maxiservice/#MAXISERVICE.jsp" class="link-profile"></a>
                <a href="http://alanev.ru/works/maxiservice/#MAXISERVICE.jsp" class="link-audio"></a>
                <a href="http://alanev.ru/works/maxiservice/#MAXISERVICE.jsp" class="link-money"></a>
                <a href="http://alanev.ru/works/maxiservice/#MAXISERVICE.jsp" class="link-exit"></a>
            </div>
        </div>
    </div>
</header>
<div class="content wrapper">
    <aside>
        <ul class="menu">
            <li><a href="http://alanev.ru/works/maxiservice/#MAXISERVICE.jsp" class="menu-statistics">СТАТИСТИКА</a>
            </li>
            <li>
                <a href="http://alanev.ru/works/maxiservice/#MAXISERVICE.jsp" class="menu-settings active">НАСТРОЙКИ</a>
                <ul class="submenu">
                    <li class="active">Общие настройки</li>
                    <li><a href="http://alanev.ru/works/maxiservice/#MAXISERVICE.jsp">Перехватчик звонков</a></li>
                    <li><a href="http://alanev.ru/works/maxiservice/#MAXISERVICE.jsp">Он-лайн консультант</a></li>
                    <li><a href="http://alanev.ru/works/maxiservice/#MAXISERVICE.jsp">Уведомления</a></li>
                </ul>
            </li>
            <li><a href="http://alanev.ru/works/maxiservice/#MAXISERVICE.jsp" class="menu-installation">УСТАНОВКА</a>
            </li>
            <li><a href="http://alanev.ru/works/maxiservice/#MAXISERVICE.jsp" class="menu-design">ДИЗАЙН</a></li>
        </ul>
    </aside>
    <main>
        <div class="breadcrumbs">
            <a href="http://alanev.ru/works/maxiservice/#MAXISERVICE.jsp">Maxiservice main</a>
            <a href="http://alanev.ru/works/maxiservice/#MAXISERVICE.jsp">Seonews.ru</a>
            <a href="http://alanev.ru/works/maxiservice/#MAXISERVICE.jsp">Настройки</a>
            <span>Общие настройки</span>
        </div>
        <ul class="settings">
            <li>
                <p class="title">Включить перехватчик звонков</p>

                <p>Если выключен, то все дальнейшие опции перехватчика недоступны</p>

                <div class="value">
                    <input type="checkbox" data-db id="showCallBackWidget" class="input-checkbox">
                    <label for="showCallBackWidget"><span class="on">ВКЛ</span><span class="off">ВЫКЛ</span></label>
                </div>
            </li>
            <li>
                <p class="title">Включить он-лайн консультанта</p>

                <p>Если выключен, то все дальнейшие опции он-лайн консультанта недоступны</p>

                <div class="value">
                    <input type="checkbox" data-db id="showOnlineConsultant" class="input-checkbox">
                    <label for="showOnlineConsultant"><span class="on">ВКЛ</span><span class="off">ВЫКЛ</span></label>
                </div>
            </li>
            <li>
                <p class="title">Отображать на сайте активный виджет заказа звонка</p>

                <p>Время, через которое показывается активная кнопка заказа звонка</p>

                <div class="value">
                    <input type="checkbox" data-db id="showPhoneButton" class="input-checkbox">
                    <label for="showPhoneButton"><span class="on">ВКЛ</span><span class="off">ВЫКЛ</span></label>
                </div>
            </li>
            <li>
                <p class="title">Время, через которое показывается активная кнопка заказа звонка</p>

                <p>Сколько времени должно пройти от момента захода на сайт посетителя до появления виджета заказа
                    звонка</p>

                <div class="value">
                    <input type="text" data-db id="secondsBeforeButtonShows" class="input-text input-value">
                    <label>сек.</label>
                </div>
            </li>
            <li>
                <p class="title">Задержка отображения виджета</p>

                <p>Через какое время виджет будет показываться посетителю</p>

                <div class="value">
                    <input type="text" data-db id="secondsBeforeFormShows"
                           class="input-text input-value">
                    <label>сек.</label>
                </div>
            </li>
            <li>
                <p class="title">Включить привлечение внимания к неактивной вкладке</p>

                <p>Сколько времени должно пройти от момента перехода в другую вкладку браузера до привлечения внимания к
                    вкладке</p>

                <div class="value">
                    <input type="checkbox" data-db id="drawingAttentionToTheInactiveTab" class="input-checkbox">
                    <label for="drawingAttentionToTheInactiveTab"><span class="on">ВКЛ</span><span
                            class="off">ВЫКЛ</span></label>
                </div>
            </li>
            <li>
                <p class="title">Время, через которое включается привлечение внимания к неактивной вкладке</p>

                <p>Если включено, то при переходе на другую вкладку вкладка вашего сайта начнёт мигать</p>

                <div class="value">
                    <input type="text" data-db id="secondsBeforeDrawingAttentionToTheInactiveTab"
                           class="input-text input-value">
                    <label>сек.</label>
                </div>
            </li>
        </ul>
        <input type="button" class="button" id="update" value="Продолжить"/>
    </main>
</div>
<footer class="footer">
    <div class="wrapper">
        <div class="cols clear">
            <div class="col">
                <div class="title">КОНТАКТЫ</div>
                <a href="http://alanev.ru/works/maxiservice/#MAXISERVICE.jsp">+7 (495) 215-06-06</a>
                <a href="http://alanev.ru/works/maxiservice/#MAXISERVICE.jsp">potok@maxiservice.ru</a>
            </div>
            <div class="col">
                <div class="title">КОНТАКТЫ</div>
                <a href="http://alanev.ru/works/maxiservice/#MAXISERVICE.jsp">+7 (495) 215-06-06</a>
                <a href="http://alanev.ru/works/maxiservice/#MAXISERVICE.jsp">potok@maxiservice.ru</a>
            </div>
            <div class="col">
                <div class="title">КОНТАКТЫ</div>
                <a href="http://alanev.ru/works/maxiservice/#MAXISERVICE.jsp">+7 (495) 215-06-06</a>
                <a href="http://alanev.ru/works/maxiservice/#MAXISERVICE.jsp">potok@maxiservice.ru</a>
            </div>
            <div class="col">
                <div class="title">КОНТАКТЫ</div>
                <a href="http://alanev.ru/works/maxiservice/#MAXISERVICE.jsp">+7 (495) 215-06-06</a>
                <a href="http://alanev.ru/works/maxiservice/#MAXISERVICE.jsp">potok@maxiservice.ru</a>
            </div>
            <div class="col">
                <div class="title">КОНТАКТЫ</div>
                <a href="http://alanev.ru/works/maxiservice/#MAXISERVICE.jsp">+7 (495) 215-06-06</a>
                <a href="http://alanev.ru/works/maxiservice/#MAXISERVICE.jsp">potok@maxiservice.ru</a>
            </div>
        </div>
        <p class="copyright fl">ООО «БМР» Общество с ограниченной ответственностью «MS AlterMedia» <br>Юридический
            адрес: 109316, г. Москва, Волгоградский пр-т, дом 43, корпус 3 <br>ИНН 7723866732 КПП 772201001 ОГРН
            1137746246395<br><a href="http://alanev.ru/works/maxiservice/#MAXISERVICE.jsp">Политика конфиденциальности
                Полезное Словарь</a></p>

        <div class="fr">
            <div class="phone">+7 (495) 215-06-06</div>
            <div class="logo"><span>MAXI</span>SERVICE</div>
        </div>
    </div>
</footer>
</body>
</html>
<!-- This document saved from http://alanev.ru/works/maxiservice/ -->
