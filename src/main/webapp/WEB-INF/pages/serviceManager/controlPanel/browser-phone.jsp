<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!doctype html>
<html lang="ru">
<head>
<meta charset="utf-8">
<title>MAXISERVICE</title>

<link rel="stylesheet" href="/resources/css/style.css">
<link rel="stylesheet" href="/resources/css/perfect-scrollbar.min.css">
<link rel="shortcut icon" href="/resources/img/favicon.png">


<script type="text/javascript" src="http://code.jquery.com/jquery-2.1.1.min.js"></script>
<script type="text/javascript" src="/resources/js/inputmask.js" defer></script>
<script type="text/javascript" src="/resources/js/script.js" defer></script>
<script type="text/javascript" src="/resources/js/perfect-scrollbar.min.js" defer></script>

<script type="text/javascript" src="//static.twilio.com/libs/twiliojs/1.1/twilio.min.js"></script>
<script type="text/javascript" src="/resources/stomp.js"></script>
<script type="text/javascript" src="/resources/sockjs-0.3.js"></script>
<script type="text/javascript" src="/resources/howler.js"></script>

<script type="text/javascript">


$(document).ready(function () {

    var mswbody = document.body.children;
    var mswwidget = document.getElementsByClassName('msw-widget')[0];
    var mswoverlay = document.getElementsByClassName('msw-overlay')[0];
    var mswbutton = document.getElementsByClassName('msw-button')[0];
    var mswclose = document.getElementsByClassName('msw-close')[0];
    mswbutton.onclick = function () {
        mswwidget.classList.add('active');
        mswoverlay.classList.add('active');
        mswbutton.classList.add('active');
        for (var i = 0; i < mswbody.length; i++) {
            mswbody[i].classList.add('msw-blur');
        }
    }
    mswclose.onclick = function () {
        mswremove()
    };
    mswoverlay.onclick = function () {
        mswremove()
    };
    function mswremove() {
        mswwidget.classList.remove('active');
        mswoverlay.classList.remove('active');
        mswbutton.classList.remove('active');
        for (var i = 0; i < mswbody.length; i++) {
            mswbody[i].classList.remove('msw-blur');
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    var clientNumber;
    var callUuid;
    var sound;
    var hangUpAfter30Seconds;
    var ringing = false;
    var hangupWasPressed = false;
    var reason;
    var initialized = false;


////////////////////////////////////////////////////// Site logs ///////////////////////////////////////////////////////////////////


    function setSiteLogs(logs) {

        var logsMap = {};
        for (var i = 0; i < logs.length; i++) {
            var log = logs[i];

            if (!logsMap[log.domain])
                logsMap[log.domain] = {
                    callsToday: 0,
                    callsMissed: 0,
                    totalDurationOfCalls: 0}

            var logFromMap = logsMap[log.domain];

            var logDate = new Date(log.time);
            if (logDate.setHours(0, 0, 0, 0) == new Date().setHours(0, 0, 0, 0))
                logFromMap.callsToday++;

            if (log.status == "NO_ONE_MANAGER_PICK_UP_THE_PHONE")
                logFromMap.callsMissed++;

            logFromMap.totalDurationOfCalls += log.duration;
        }

        var callLogsSpans = $(".siteDomain");

        for (var i = 0; i < callLogsSpans.length; i++) {
            var callLogsSpan = callLogsSpans[i];
            var domain = $(callLogsSpan).text();
            var siteLog = logsMap[domain];


            var serviceSiteLogElement = $(callLogsSpan).parent().parent();

            var callsToday = siteLog.callsToday;
            serviceSiteLogElement.find(".all-calls span").text(callsToday)

            var callsMissed = siteLog.callsMissed;
            serviceSiteLogElement.find(".missed-calls span").text(callsMissed)

            var totalDurationOfCalls = siteLog.totalDurationOfCalls;
            totalDurationOfCalls = toHHMMSS(totalDurationOfCalls);
            serviceSiteLogElement.find(".time-calls span").text(totalDurationOfCalls)


        }
    }

    function toHHMMSS(sec_num) {
        var hours = Math.floor(sec_num / 3600);
        var minutes = Math.floor((sec_num - (hours * 3600)) / 60);
        var seconds = sec_num - (hours * 3600) - (minutes * 60);

        if (hours < 10) {
            hours = "0" + hours;
        }
        if (minutes < 10) {
            minutes = "0" + minutes;
        }
        if (seconds < 10) {
            seconds = "0" + seconds;
        }
        var time = hours + ':' + minutes + ':' + seconds;
        return time;
    }


////////////////////////////////////////////////////// Call logs ///////////////////////////////////////////////////////////////////

    data = {phoneNumber: "${manager.phoneNumber}"}
    data = JSON.stringify(data);

    function updateLogs() {

        $.ajax({
            url: "/serviceManager/getCallLogByManagerNumber",
            contentType: "application/json",
            method: "POST",
            data: data
        }).success(function (logs) {

            $(".calls tr:not(.title)").remove();
            setLogs(logs);
            setSiteLogs(logs);
        });

    }

    function setLogs(logs) {

        logs.sort(function (l1, l2) {
            return l2.time - l1.time
        })

        for (var i = 0; i < logs.length; i++) {
            log = logs[i];
            if (log.status == "CALL_FROM_BROWSER")
                addLogToTable($("#outgoingCalls"), log);
            else if (log.status == "NO_ONE_MANAGER_PICK_UP_THE_PHONE" ||
                    log.status == "DISCONNECTED" ||
                    log.status == "ANOTHER_MANGER_TOOK_CALL")
                addLogToTable($("#missedCalls"), log);
            else if (log.status == "SUCCESSFUL")
                addLogToTable($("#incomingCalls"), log);
        }

        function addLogToTable(table, callLog) {

            var clientNumber = callLog.clientNumber;

            var date = new Date(callLog.time);
            var minutesHours = date.getHours() + ":" + date.getMinutes();

            var day = date.getDay();
            var month = date.getMonth();
            var dayMonth = pad(day, 2) + "." + pad(month, 2);

            var domain = callLog.domain;

            var status;
            if (callLog.status == "SUCCESSFUL")
                status = "Успешно завершенный звонок.";
            else if (callLog.status == "ALL_MANAGERS_ARE_BUSY")
                status = "Все менеджеры заняты";
            else if (callLog.status == "NO_ONE_MANAGER_PICK_UP_THE_PHONE")
                status = "Никто не обработал вызов";
            else if (callLog.status == "NOT_ENOUGH_MONEY")
                status = "На вашем счету закончились деньги.";
            else if (callLog.status == "ANOTHER_MANGER_TOOK_CALL")
                status = "Другой менеджер обработал звонок.";
            else if (callLog.status == "HANGUP_PHONE")
                status = "Соединение было разорвано.";
            else if (callLog.status == "CALL_FROM_BROWSER")
                status = "Звонок из браузера.";
            else if (callLog.status == "TECHNICAL_ERROR")
                status = "Техническая ошибка.";

            var row =
                    "<tr>\
                            <td>" + clientNumber + "</td>\
                        <td>" + minutesHours + ",<br>" + dayMonth + "</td>\
                        <td>" + domain + "</td>\
                        <td>" + status + "</td>\
                </tr>"
            $(table).find("tr:first").after(row);
        }

        function pad(n, width, z) {
            z = z || '0';
            n = n + '';
            return n.length >= width ? n : new Array(width - n.length + 1).join(z) + n;
        }
    }

    updateLogs();


////////////////////////////////////////////////////// STOMP init ///////////////////////////////////////////////////////////////////

    function connect() {
        var ws = new SockJS("/serviceManager/browser-phone");
        stompClient = Stomp.over(ws);

        stompClient.connect('guest', 'guest', onConnect);


        function onConnect() {

            stompClient.send("/serviceManager/controlPanel/singletonControl", {}, "null");

            stompClient.subscribe("/user/browserPhone/singletonControl", function (message) {

                var body = JSON.parse(message.body);
                var single = body.single;

                if (single && !initialized)
                    init();
                else if (!single && !initialized) {
                    alert('Second window.');
                }
            });
        }

        function init() {
////////////////////////////////////////////////////// PingPong ///////////////////////////////////////////////////////////////////

            stompClient.subscribe("/user/browserPhone/ping", function (message) {

                var body = JSON.parse(message.body);
                var massageId = body.id;

                var data = {"id": massageId};
                stompClient.send("/serviceManager/controlPanel/ping", {}, JSON.stringify(data));
            });

//////////////////////////////////////////////////////Call to browser Stomp///////////////////////////////////////////////////////////////////

            stompClient.subscribe("/user/browserPhone/makeCall", function (message) {

                var body = JSON.parse(message.body);
                clientNumber = body.clientNumber;
                callUuid = body.callUuid;
                var domain = body.domain;
                ringing = true;

                sound = new Howl({
                    urls: ['/resources/call.ogg'],
                    autoplay: true,
                    loop: true,
                    volume: 0.5
                });

                function dontAnswer() {

                    if (ringing)
                        hangup("dontAnswer");
                }

                hangUpAfter30Seconds = setTimeout(dontAnswer, 30 * 1000);
                $(".domain").text(domain)
                popupOpen("incomnig-call");
                enableButtons(true, true);

            });

            stompClient.subscribe("/user/browserPhone/stopCall", function (message) {
                hangup("hangupByAPI")
            });

            enableButtons(true, false);
            initialized = true;
        }

    }


////////////////////////////////////// Button listeners ///////////////////////////////////////////////////////////////////

    Twilio.Device.setup("${token}", {debug: true});
    var connection = null;
    var callToBrowser = false;


    $("#call").click(function (event) {

        event.preventDefault();
        hangupWasPressed = false;
        var params;

        if (!ringing) {
            clientNumber = $('#tocall').val();
            params = {tocall: clientNumber, managerPhone: '${manager.phoneNumber}'};
        }
        else {
            clearInterval(hangUpAfter30Seconds);
            sound.stop();
            ringing = false;
            params = {managerPhone: '${manager.phoneNumber}'};
        }


        connection = Twilio.Device.connect(params);

        enableButtons(false, true);
    });

    function hangup(reason) {


        if (ringing) {
            enableButtons(true, false);
            clearInterval(hangUpAfter30Seconds);
            sound.stop();
            ringing = false;
            var data = {"reason": reason};
            stompClient.send("/serviceManager/controlPanel/callEnded", {}, JSON.stringify(data));
        }
        else
            enableButtons(false, false);

        popupClose();
        hangupWasPressed = true;
        Twilio.Device.disconnectAll();

        setTimeout(updateLogs, 1000 * 7);
    }

    $("#hangup").click(function (event) {
        event.preventDefault();
        hangup("mangerHangPhone");
    });

//////////////////////////////////////////////////////Callbacks///////////////////////////////////////////////////////////////////

    Twilio.Device.ready(function (device) {
        // $('#status').text('Ready to start call');

        var video = document.querySelector("#videoElement");

        navigator.getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia || navigator.oGetUserMedia;

        if (navigator.getUserMedia) {
            navigator.getUserMedia({audio: true}, handleAudio, audioError);
        }

        function handleAudio(stream) {
            connect();
        }


        function audioError(e) {
            //  $('#status').text("Error wit microphone permission.");
        }

    });


    Twilio.Device.offline(function (device) {
        //  $('#status').text('Offline');
    });

    Twilio.Device.error(function (error) {

        //  $('#status').text(error.message);

        if (ringing)
            hangup('error');
    });

    Twilio.Device.connect(function (conn) {
        // $('#status').text("Successfully established call");

        enableButtons(false, true);

        if (hangupWasPressed)
            Twilio.Device.disconnectAll();
    });

    Twilio.Device.disconnect(function (conn) {

        //  $('#status').text("Call ended");
        enableButtons(true, false);
        callUuid = null;
    });


    function enableButtons(callButton, hangupButton) {

        $('.call')[0].disabled = !callButton;
        $('.hangup')[0].disabled = !hangupButton;
    }

////////////////////////////////////////////////////// MANAGER DAO  ///////////////////////////////////////////////////////////////////

    function valueChanged() {

        var afk = $(".phone-button.active").length > 0;
        var receiveCallInBrowser = $(".switch-active.active").length > 0;

        data = {
            phoneNumber: "+380630405349",
            afk: afk,
            receiveCallInBrowser: receiveCallInBrowser
        }

        data = JSON.stringify(data);

        $.ajax({
            url: "/serviceManager/update",
            contentType: "application/json",
            method: "POST",
            data: data
        });
    }


    $('.phone-button div').click(function () {
        valueChanged();
    });

    $('.icon-switch-not-active, .icon-switch-active div').click(function () {
        valueChanged();
    });


    if (!${manager.receiveCallInBrowser})
        toggleSwitchButton();

    if (${manager.afk})
        togglePhoneButton();

})
;


</script>
</head>
<body>
<header class="header">
    <div class="wrapper">
        <div class="logo fl"><span>MAXI</span>SERVICE</div>
        <div class="fr">
            <div class="links ib ib-list">
                <a href="#" class="link-profile"></a>
                <a href="#" class="link-settings" data-popup="incomnig-call"></a>
                <a href="#" class="link-exit"></a>
            </div>
            <div class="my-number">Мой номер:<span>${manager.phoneNumber}</span></div>
            <div class="switch-active active">
                <div class="switch-active-inner fl">
                    <div><b>Активен</b> приём звонков в браузере.</div>
                    <div class="instruction">Для деактивации переключите тумблер справа.</div>
                    <i class="icon-switch-active"></i>
                </div>
                <div class="warning">Если закрыть браузер, звонки будут поступать на телефон.</div>
            </div>
            <div class="switch-not-active">
                <div class="switch-not-active-inner fl">
                    <div><b>Не активен</b> приём звонков в браузере.</div>
                    <div class="instruction">Для активации переключите тумблер справа.</div>
                    <i class="icon-switch-not-active"></i>
                </div>
            </div>
        </div>
    </div>
</header>
<div class="content wrapper">
    <div class="wrapper-phone">
        <div class="phone-button active">
            <div class="receiv active"></div>
            <div class="not-receiv"></div>
        </div>

        <c:forEach var="servicedSite" items="${manager.servicedSites}">
            <div class="connect-site">
                <div class="title fl">
                    <span class="connect">Подключен сайт</span>
                    <span class="siteDomain">${servicedSite.domain}</span>
                    <span class="status fr">нет звонков</span>
                </div>
                <div class="fl col-left">
                    <div class="all-calls">Всего звонков сегодня:<span></span></div>
                    <div class="missed-calls">Пропущенные вызовы:<span></span></div>
                </div>
                <div class="fr col-right">
                    <div class="time-calls">Общая длительность всех звонков:<span></span></div>
                    <div class="fish-calls">Ещё какая-то очень длинная рыба:<span></span></div>
                </div>
            </div>
        </c:forEach>
        <div class="call-number clear">
            <div class="title">
                <span class="connect">Набор номера:</span>
                <input id="tocall" type="text" class="phone phone-number ib">

                <div class="time ib">Длительность звонка:
                    <span>00:00:00</span>
                </div>
            </div>
            <div class="numbers fl">
                <ul>
                    <li>1</li>
                    <li>2</li>
                    <li>3</li>
                    <li>4</li>
                    <li>5</li>
                    <li>6</li>
                    <li>7</li>
                    <li>8</li>
                    <li>9</li>
                    <li>+</li>
                    <li>0</li>
                    <li class="star-sharp">
                        <div class="star">*</div>
                        <div class="sharp">#</div>
                    </li>
                    <li class="green call"></li>
                    <li class="red hangup"></li>
                </ul>
            </div>
            <ul class="type-call">
                <li class="red active">Пропущенные</li>
                <li class="grey">Исходящие</li>
                <li class="green">Входящие</li>
            </ul>
            <div class="calls">
                <ul>
                    <li class="active">
                        <table id="missedCalls">
                            <tr class="title">
                                <td>Номер</td>
                                <td>Время</td>
                                <td>Источник</td>
                                <td>Причина</td>
                            </tr>
                        </table>
                    </li>
                    <li>
                        <table id="outgoingCalls">
                            <tr class="title">
                                <td>Номер</td>
                                <td>Время</td>
                                <td>Источник</td>
                                <td>Причина</td>
                            </tr>
                        </table>
                    </li>
                    <li>
                        <table id="incomingCalls">
                            <tr class="title">
                                <td>Номер</td>
                                <td>Время</td>
                                <td>Источник</td>
                                <td>Причина</td>
                            </tr>
                        </table>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</div>
<footer class="footer">
    <div class="wrapper">
        <div class="cols clear">
            <div class="col">
                <div class="title">КОНТАКТЫ</div>
                <a href="#">+7 (495) 215-06-06</a>
                <a href="#">potok@maxiservice.ru</a>
            </div>
            <div class="col">
                <div class="title">КОНТАКТЫ</div>
                <a href="#">+7 (495) 215-06-06</a>
                <a href="#">potok@maxiservice.ru</a>
            </div>
            <div class="col">
                <div class="title">КОНТАКТЫ</div>
                <a href="#">+7 (495) 215-06-06</a>
                <a href="#">potok@maxiservice.ru</a>
            </div>
            <div class="col">
                <div class="title">КОНТАКТЫ</div>
                <a href="#">+7 (495) 215-06-06</a>
                <a href="#">potok@maxiservice.ru</a>
            </div>
            <div class="col">
                <div class="title">КОНТАКТЫ</div>
                <a href="#">+7 (495) 215-06-06</a>
                <a href="#">potok@maxiservice.ru</a>
            </div>
        </div>
        <p class="copyright fl">ООО «БМР» Общество с ограниченной ответственностью «MS AlterMedia» <br>Юридический
            адрес: 109316, г. Москва, Волгоградский пр-т, дом 43, корпус 3 <br>ИНН 7723866732 КПП 772201001 ОГРН
            1137746246395<br><a href="#">Политика конфиденциальности Полезное Словарь</a></p>

        <div class="fr">
            <div class="phone">+7 (495) 215-06-06</div>
            <div class="logo"><span>MAXI</span>SERVICE</div>
        </div>
    </div>
</footer>

<div class="layout">
    <div class="layout-overlay"></div>
    <div class="popup popup-incomnig-call">
        <form class="form form-incomnig-call">
            <h2 class="incomnig-call-title">Входящий вызов <span>(+ 79 645 756 52)</span></h2>

            <div class="content-popup">
                <div class="source">Источник:<span class="domain">bublik-trade.com</span></div>
                <div class="col">
                    <button class="button-reply call"></button>
                </div>
                <div class="col">
                    <button class="button-drop hangup"></button>
                </div>
                <div class="col">
                    <div class="reply">Ответить</div>
                </div>
                <div class="col">
                    <div class="drop">Сбросить</div>
                </div>
                <div class="time">Длительность звонка:<span>00:00:00</span></div>
            </div>
        </form>
    </div>
</div>

<div class="msw-button">
    <div class="msw-phone"></div>
</div>
<div class="msw-overlay"></div>
<div class="msw-widget">
    <div class="msw-head">
        <div class="msw-close"></div>
    </div>
    <div class="msw-content">
        <div class="msw-title">Хотите, напишем здесь любой текст?</div>
        <form class="msw-form">
            <input type="text" class="msw-input">
            <button class="msw-submit">Позвоните мне!</button>
        </form>
        <div class="msw-info">
            <p>Свободных операторов на линии: 1</p>

            <p>В процессе разговора с клиентами: 4</p>

            <p>Обслуженных вызовов за сегодня: 13</p>
        </div>
    </div>
    <div class="msw-foot">Получите больше какой-нибудь рыбы с MaxiService.com</div>
</div>
<style type="text/css">
@font-face {
    font-family: 'Open Sans';
    font-style: normal;
    font-weight: 300;
    src: local('Open Sans Light'), local('OpenSans-Light'), url(http://fonts.gstatic.com/s/opensans/v10/DXI1ORHCpsQm3Vp6mXoaTZ1r3JsPcQLi8jytr04NNhU.woff) format('woff')
}

.msw-blur {
    filter: url('data:image/svg+xml;utf8,%3Csvg%20xmlns%3D%22http%3A//www.w3.org/2000/svg%22%3E%3Cfilter%20id%3D%22blur%22%3E%3CfeGaussianBlur%20stdDeviation%3D%223%22/%3E%3C/filter%3E%3C/svg%3E#blur');
    -webkit-filter: blur(2px);
    filter: progid:DXImageTransform.Microsoft.Blur(pixelRadius=2)
}

.msw-button:after, .msw-phone, .msw-phone:after {
    background: url('data:image/png;base64,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') 100px 100px no-repeat
}

.msw-button {
    position: fixed;
    bottom: 50px;
    right: 50px;
    width: 120px;
    height: 120px;
    border: 1px solid rgba(0, 0, 0, .2);
    border-radius: 50%;
    background-position: center;
    text-align: center;
    line-height: 120px;
    -webkit-transition: .5s;
    transition: .5s;
    cursor: pointer
}

.msw-button:after, .msw-button:before {
    content: '';
    position: absolute;
    left: 5px;
    top: 5px;
    width: 111px;
    height: 111px;
    border-radius: 50%
}

.msw-button:before {
    transition: .5s
}

.msw-button:after {
    background-position: -10px -10px;
    -webkit-transition: box-shadow 5s;
    transition: box-shadow 5s;
    -webkit-animation: loading 13s linear 0s infinite;
    animation: loading 13s linear 0s infinite
}

.msw-phone {
    display: inline-block;
    vertical-align: middle;
    width: 93px;
    height: 93px;
    margin: 0 auto;
    -webkit-border-radius: 50%;
    border-radius: 50%;
    line-height: 93px;
    background-color: rgba(0, 0, 0, .2)
}

.msw-phone:after {
    content: '';
    display: inline-block;
    vertical-align: middle;
    width: 53px;
    height: 53px;
    background-position: -141px -10px;
    -webkit-animation: shaking 3s linear 0s infinite;
    animation: shaking 3s linear 0s infinite
}

.msw-button:hover {
    border: 1px solid rgba(70, 207, 153, .2)
}

.msw-button:hover:after {
    content: none
}

.msw-button:hover:before {
    -webkit-box-shadow: 0 0 0 3px #46cf99 inset;
    box-shadow: 0 0 0 3px #46cf99 inset
}

.msw-button:hover .msw-phone {
    background-position: -141px -83px;
    -webkit-animation: pulsing 2s linear 0s infinite;
    animation: pulsing 2s linear 0s infinite
}

.msw-button:hover .msw-phone:after {
    content: none
}

.msw-button.active {
    opacity: 0
}

.msw-overlay, .msw-widget {
    z-index: -100;
    -webkit-transition: .5s;
    transition: .5s;
    opacity: 0
}

.msw-overlay.active, .msw-widget.active {
    z-index: 100000;
    opacity: 1
}

.msw-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.2)
}

.msw-widget {
    position: fixed;
    top: 50%;
    left: 50%;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    width: 800px;
    height: 400px;
    margin: 100px;
    margin: -200px 0 0 -400px;
    -webkit-border-radius: 20px;
    border-radius: 20px;
    background: rgba(0, 0, 0, 0.5);
    font: 300 14px 'Open Sans';
    color: #dadada;
    overflow: hidden
}

.msw-widget.msw-blur {
    filter: none;
}

.msw-head {
    height: 56px;
    background: #313131
}

.msw-close {
    float: right;
    margin: 10px 23px;
    width: 36px;
    height: 36px;
    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAABLCAMAAAAvfNUlAAAAnFBMVEUAAADa2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tphYWHOzs5bW1vKyspwcHC+vr64uLhra2vExMR1dXXS0tKkpKSbm5uurq4a2FCpAAAAJXRSTlMABfP6p/cX3NgUdwrsZdBEITAp3z44xVnMyJuTENM1aR1nakptsULIHwAAAllJREFUSMfNlNd64jAQhSVZcgNMNdW0ZE9MEnZDsu//bitwGUkYDHd7LjCf+dGZ0RRmarCX867ndedyP2CN4rMpDE1n/Jrph4CQ+9Tn3E/3UgBh30E6SyDodYwXvQBYdkwmmkPF3LGPFeaRwYQIhuxKwwBhTXUWWPusQf4ai8pxicBmyCLAsswLirxcR4UixxAxu6kY4TmhGQJeRPArY6TR9vKWB5jpxxS9siiYZsR0x8Vfe5iefxJVAn1vEREzKFMXGLAtJKv0ikVWxFsxWhJbJi9udFZ2YYKa0X6SJUjNMqu5f2Z29CpFwroo4qCzXi2GRegyD3Zh0y4S3yo0vBqi3DF3IdduI5KZWvu2XRk4MRroi8S3ArevINVM8diZV2BdZqoSvzyQEpTYmmWZaKb6KsIdlcUqsGYouJAKbLZKbCY1jDtGq/DWpnukfetBiG4PQutIJTRS94eT/nJrzP0nFwatnt4m4jza9Gj1PLPESIMXmZzXYSJfBuw/0USOFbTUWE6aCb4SMCRWDdnFHhx5bgdlIRoUZtZECjRKjAyGrFzLmooEbkpUvRLijsKq1+/qkiNX9yGPa2iFUvn3CaQ/3yi10lAd9fvh60TM22+vil3XC7WOh6+cmHdUmjAJizpdM5BsDJdyGYyZgkPlLgPFAJc6OgzgQvh8O3zkuA9pL8qRIPegj79XRzHlMjmO+sMOfOwyxcO+AmkyldGnfZlWWSiYo1UWq8CaIWMqsNUqP7lp/XMyWoV7bU33YPtqBW2D0D5SzwynplQzo0YPLoz21dO+xJ5bh/8AjcCzVW2DUckAAAAASUVORK5CYII=') 0 0 no-repeat;
    cursor: pointer
}

.msw-close:hover {
    background-position: 0 bottom
}

.msw-content {
    width: 530px;
    margin: 55px auto
}

.msw-form {
    margin: 30px 0 0
}

.msw-title {
    font-size: 26px
}

.msw-input, .msw-submit {
    display: inline-block;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    height: 38px
}

.msw-submit {
    width: 186px;
    border: 2px solid #ededed;
    -webkit-border-radius: 20px;
    border-radius: 20px;
    -webkit-box-shadow: none;
    box-shadow: none;
    background: none;
    font: 300 18px 'Open Sans';
    color: #ededed
}

.msw-submit:hover {
    border: none;
    background: #313131;
    cursor: pointer
}

.msw-input {
    display: inline-block;
    width: 240px;
    padding: 0 18px;
    border: 1px dashed #ededed;
    margin: 0 20px 0 0;
    border-radius: 20px;
    -webkit-box-shadow: none;
    box-shadow: none;
    background: none;
    font: 300 18px 'Open Sans';
    color: #dadada
}

.msw-info {
    margin-top: 34px;
    line-height: 24px
}

.msw-foot {
    padding-right: 20px;
    background: #313131;
    text-align: right;
    font-size: 14px;
    line-height: 26px
}

@keyframes loading {
    0% {
        transform: rotate(0deg)
    }
    100% {
        transform: rotate(360deg)
    }
}

@-webkit-keyframes loading {
    0% {
        -webkit-transform: rotate(0deg)
    }
    100% {
        -webkit-transform: rotate(360deg)
    }
}

@keyframes shaking {
    0% {
        transform: rotate(0deg)
    }
    10% {
        transform: rotate(20deg)
    }
    20% {
        transform: rotate(0deg)
    }
    30% {
        transform: rotate(20deg)
    }
    40% {
        transform: rotate(0deg)
    }
}

@-webkit-keyframes shaking {
    0% {
        -webkit-transform: rotate(0deg)
    }
    10% {
        -webkit-transform: rotate(20deg)
    }
    20% {
        -webkit-transform: rotate(0deg)
    }
    30% {
        -webkit-transform: rotate(20deg)
    }
    40% {
        -webkit-transform: rotate(0deg)
    }
}

@keyframes pulsing {
    0% {
        opacity: 1
    }
    50% {
        opacity: .2
    }
    100% {
        opacity: 1
    }
}

@-webkit-keyframes pulsing {
    0% {
        opacity: 1
    }
    50% {
        opacity: .2
    }
    100% {
        opacity: 1
    }
}
</style>
</body>
</html>