var widgetHtml = '<div class="msw-overlay"></div>\
<div class="msw-widget">\
    <div class="msw-head">\
    <div class="msw-close"></div>\
    </div>\
<div class="msw-content">\
    <div class="msw-title">Хотите, напишем здесь любой текст?</div>\
    <form class="msw-form">\
        <input type="text" class="msw-input">\
            <button class="msw-submit">Позвоните мне!</button>\
        </form>\
        <div class="msw-info">\
            <p>Свободных операторов на линии: ${managersFree}</p>\
            <p>В процессе разговора с клиентами: ${managesBusy}</p>\
            <p>Обслуженных вызовов за сегодня: ${callsHandledToday}</p>\
        </div>\
    </div>\
    <div class="msw-foot">Получите больше какой-нибудь рыбы с MaxiService.com</div>\
</div>\
<div class="msw-button" style="display: none"><div class="msw-phone"></div></div>';


document.addEventListener("load", getWidgetProperties());


function getWidgetProperties() {

    xmlhttp = new XMLHttpRequest();


    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
            var widgetData = JSON.parse(xmlhttp.responseText);
            drawWidget(widgetData);
        }
    }

    xmlhttp.open("POST", "//77.47.228.33:8080/getWidgetProperties", true);
    xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xmlhttp.send("servicedSiteId=" + servicedSiteId);
}


function drawWidget(widgetData) {

    var widgetProperties = widgetData.widgetProperties;
    var widgetInfo = widgetData.widgetInfo;

////////////////////////////////////////////////////// Show once per day ///////////////////////////////////////////////////////////////////

    function getCookie(cname) {
        var name = cname + "=";
        var ca = document.cookie.split(';');
        for (var i = 0; i < ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) == ' ') c = c.substring(1);
            if (c.indexOf(name) == 0) return c.substring(name.length, c.length);
        }
    }

    function setCookie(cname, cvalue, exdays) {
        var d = new Date();
        d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
        var expires = "expires=" + d.toUTCString();
        document.cookie = cname + "=" + cvalue + "; " + expires;
    }

    if (widgetProperties.showOncePerDay && getCookie("widgetWasShownToday") == "")
        return;


////////////////////////////////////////////////////// Show once per uer ///////////////////////////////////////////////////////////////////

    if (widgetProperties.showOnceForOneUser && getCookie("widgetWasShownForThisUser") == "")
        return;


    widgetHtml = widgetHtml.replace('${managersFree}', widgetInfo.managersFree);
    widgetHtml = widgetHtml.replace('${managesBusy}', widgetInfo.managesBusy);
    widgetHtml = widgetHtml.replace('${callsHandledToday}', widgetInfo.callsHandledToday);

    document.body.innerHTML += widgetHtml;

    var mswbody = document.body.children;
    var mswwidget = document.getElementsByClassName('msw-widget')[0];
    var mswoverlay = document.getElementsByClassName('msw-overlay')[0];
    var mswbutton = document.getElementsByClassName('msw-button')[0];
    var mswclose = document.getElementsByClassName('msw-close')[0];
    var mswsubmit = document.getElementsByClassName('msw-submit')[0];


    function showIcon() {
        mswbutton.style.display = 'block';
    }


    function hideIcon() {
        mswbutton.style.display = 'none';
    }

    function showWidget() {
        mswwidget.classList.add('active');
        mswoverlay.classList.add('active');
        for (var i = 0; i < mswbody.length; i++)
            if (!mswbody[i].classList.contains('msw-widget'))
                mswbody[i].classList.add('msw-blur');

        if (widgetProperties.showOnceForOneUser && getCookie("widgetWasShownForThisUser") != "")
            setCookie("widgetWasShownForThisUser", "", 10000);
        if (widgetProperties.showOncePerDay && getCookie("widgetWasShownToday") != "")
            setCookie("widgetWasShownToday", "", 1);
    }

    function hideWidget() {

        mswwidget.classList.remove('active');
        mswoverlay.classList.remove('active');
        for (var i = 0; i < mswbody.length; i++) {
            mswbody[i].classList.remove('msw-blur');
        }
    }


    function registrateVisibilityListener(onFocus, onBlur) {
        var hidden, visibilityChange;
        if (typeof document.hidden !== "undefined") { // Opera 12.10 and Firefox 18 and later support
            hidden = "hidden";
            visibilityChange = "visibilitychange";
        } else if (typeof document.mozHidden !== "undefined") {
            hidden = "mozHidden";
            visibilityChange = "mozvisibilitychange";
        } else if (typeof document.msHidden !== "undefined") {
            hidden = "msHidden";
            visibilityChange = "msvisibilitychange";
        } else if (typeof document.webkitHidden !== "undefined") {
            hidden = "webkitHidden";
            visibilityChange = "webkitvisibilitychange";
        }

        function handleVisibilityChange() {
            if (document[hidden] && typeof onBlur !== 'undefined') {
                onBlur();
            } else if (typeof onFocus !== 'undefined') {
                onFocus();
            }
        }

        // Warn if the browser doesn't support addEventListener or the Page Visibility API
        if (typeof document.addEventListener === "undefined" ||
            typeof document[hidden] === "undefined") {
            alert("This demo requires a browser, such as Google Chrome or Firefox, that supports the Page Visibility API.");
        } else {
            // Handle page visibility change
            document.addEventListener(visibilityChange, handleVisibilityChange, false);
        }
    }

    function drawingAttention() {
        console.log('drawingAttention');
        setTimeout(function () {
            var msg = 'Warning!';
            if (document.hidden) {
                document.title = document.title == msg ? ' ' : msg;
                drawingAttention();
            }
            else {
                document.title = ' ';
            }
        }, 1000);
    }


    if (widgetProperties.showPhoneButton) {
        setTimeout(function () {
            showIcon();
        }, widgetProperties.secondsBeforeButtonShows * 1000);
    }
    else {
        setTimeout(function () {
            showWidget();
        }, widgetProperties.secondsBeforeFormShows * 1000);
    }


    var onblur = function () {
        setTimeout(drawingAttention, widgetProperties.secondsBeforeDrawingAttentionToTheInactiveTab * 1000);
    }

    var onfocus = function () {
        document.title = ' ';
    }

    registrateVisibilityListener(onfocus, onblur);

    mswclose.onclick = function () {
        hideWidget();
        if (widgetProperties.showPhoneButton)
            showIcon();
    };

    if (widgetProperties.showPhoneButton)
        mswbutton.onclick = function () {
            showWidget();
            hideIcon();
        };


    function makeCall() {

        var clientNumber = document.getElementsByClassName("msw-input")[0].value;
        xmlhttp.open("POST", "//77.47.228.33:8080/call", true);
        xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xmlhttp.send("clientNumber=" + clientNumber + "&siteId=" + servicedSiteId);
    }

    mswsubmit.onclick = makeCall;
}