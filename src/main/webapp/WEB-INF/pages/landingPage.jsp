<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!doctype html>
<html lang="ru">
<head>
<meta charset="utf-8">
<title>MAXISERVICE</title>

<link rel="stylesheet" href="../../resources/css/style.css">
<link rel="shortcut icon" href="img/favicon.png">

<script type="text/javascript" src="http://code.jquery.com/jquery-2.1.3.min.js"></script>
<script type="text/javascript" src="/resources/js/inputmask.js" defer></script>
<script type="text/javascript" src="/resources/js/script.js" defer></script>
<script src="http://malsup.github.com/jquery.form.js"></script>
<script>

$(document).ready(function () {


    userLogin();
    managerLogin();
    userRegistration();
    recoverPassword();
    managerApprovePhone();

    var locationWithoutParametrs = window.location.href.split("?")[0];
    window.history.pushState("object or string", "Title", locationWithoutParametrs);
});
/////////////////////////////// USER LOGIN ////////////////////////////////////////////////////////////////////////////

function userLogin() {
    if (window.location.search.indexOf("?userLoginError") > -1) {
        $("a[data-popup = signin]").click();
        $("#userLoginErrorMessage").toggle();
    }
}

/////////////////////////////// MANAGER LOGIN //////////////////////////////////////////////////////////////////////////
function managerLogin() {
    if (window.location.search.indexOf("?managerLoginError") > -1) {
        $("a[data-popup = signin-manager]").click();
        $("#managerLoginErrorMessage").toggle();
    }
}
/////////////////////////////// USER REGISTRATION //////////////////////////////////////////////////////////////////////
function userRegistration() {

    var options = {};
    var messageLabel = $("#userRegistrateMessage");

    options.beforeSerialize = function () {
        return checkPasswordLength($("#userPassword"), $("#userRegistrateMessage"));
    }

    options.beforeSubmit = function () {
        $("#submitUserRegistrationForm").prop("disabled", true);
    }

    options.success = function (data) {

        var status = data.status;

        if (status == 0) {

            $("#userRegistrateForm").hide();
            $("#userRegistrateMessageSend").show();
        }
        else if (status == 1) {
            messageLabel.css('color', '#e66262');
            messageLabel.text("Пользователь с таким email уже существует.");
        }
        else if (status == 2) {
            messageLabel.css('color', '#e66262');
            messageLabel.text("Письмо уже отправлено на этот email.");
        }
        messageLabel.show();
        $("#submitUserRegistrationForm").prop("disabled", false);
    };

    options.error = function (error) {

        messageLabel.css('color', '#e66262');
        messageLabel.text("Произошла техническая ошибка в работе сервиса." +
                "\n Пожалуйста, попробуйте позже или обратитесь в техническую поддержку.");
        messageLabel.show();
        $("#submitUserRegistrationForm").prop("disabled", false);
    };

    $('#userRegistrateForm').ajaxForm(options);

    $("a[data-popup=signup]").click(function () {
        $("#successUserRegistration").hide();
        $("#wrongLinkUserRegistration").hide();
    });


    var emailConfirmed = getUrlParameter("emailConfirmed");

    if (emailConfirmed) {

        $("a[data-popup=signup]").click(function () {
            $("#userRegistrateForm").show();
            $("#successUserRegistration").hide();
            $("#wrongLinkUserRegistration").hide();
        });

        if (emailConfirmed.indexOf("true") > -1) {
            $("a[data-popup=signup]").click();
            $("#userRegistrateForm").hide();
            $("#successUserRegistration").show();
        }
        else if (emailConfirmed.indexOf("false") > -1) {
            $("a[data-popup=signup]").click();
            $("#userRegistrateForm").hide();
            $("#wrongLinkUserRegistration").show();
        }
    }
}

/////////////////////////////// RECOVER PASSWORD //////////////////////////////////////////////////////////////////////
function recoverPassword() {
    sendMassage();
    changePassword();
}

//Send Message
function sendMassage() {
    var options = {};
    var messageLabel = $("#recoverPasswordMessage");
    options.beforeSubmit = function () {
        $("#submitRecoverPasswordForm").prop("disabled", true);
    }
    options.success = function (data) {
        var status = JSON.parse(data).status;
        if (status == 0) {
            $("#recoverPasswordForm").hide();
            $("#recoverPasswordMessageSend").show();
        }
        else if (status == 1) {
            messageLabel.css('color', '#e66262');
            messageLabel.text("Пользователя с таким email не существует.");
        }
        messageLabel.show();
        $("#submitRecoverPasswordForm").prop("disabled", false);
    };
    options.error = function (error) {
        messageLabel.css('color', '#e66262');
        messageLabel.text("Произошла техническая ошибка в работе сервиса." +
                "\n Пожалуйста, попробуйте позже или обратитесь в техническую поддержку.");
        messageLabel.show();
        $("#submitRecoverPasswordForm").prop("disabled", false);
    };
    $('#recoverPasswordForm').ajaxForm(options);
}

//Change password
function changePassword() {
    var tokenValid = getUrlParameter("recoverPasswordTokenValid");
    if (tokenValid != null)
        $("a[data-popup='changePassword']").click();
    tokenValid = tokenValid == "true";
    if (tokenValid) {
        var recoverPasswordToken = getUrlParameter("recoverPasswordToken");
        $("#token").val(recoverPasswordToken);
    }
    else {
        $("#changePasswordForm").hide();
        $("#wrongToken").show();
    }

    $("#changePasswordSubmit").click(function () {

        var password1 = $("#userPassword1");
        var password2 = $("#userPassword2");

        if (password1.val() != password2.val()) {
            $("#changePasswordMessage").show();
            $("#changePasswordMessage").text("Оба введенных пароля должны быть идентичны!");
            return false;
        }
        if (!checkPasswordLength(password1, $("#changePasswordMessage")))
            return false;


        $("#changePasswordMessage").hide();
        $("#changePasswordSubmit").prop("disabled", true);
        var data = {};
        data.newPassword = password1.val();
        data.token = $("#token").val();

        $.ajax({
            type: "POST",
            url: "/changePassword",
            data: data
        })
                .always(function () {
                    $("#changePasswordSubmit").prop("disabled", false);
                })
                .success(function (data) {
                    data = JSON.parse(data);
                    if (data.status == 0) {
                        $("#changePasswordForm").hide();
                        $("#passwordWasChanged").show();
                    }
                    else if (data.status == 1) {
                        $("#changePasswordForm").hide();
                        $("#wrongToken").show();
                    }
                })
                .fail(function (error) {
                    $("#changePasswordMessage").show();
                    $("#changePasswordMessage").text("Произошла техническая ошибка в работе сервиса." +
                            "\n Пожалуйста, попробуйте позже или обратитесь в техническую поддержку.");
                });
        return false;

    });
}

/////////////////////////////// MANAGER (RECOVER PASSWORD)/(REGISTRATE) //////////////////////////////////////////////////////////////////////

function managerApprovePhone() {

    sendCode();
    checkCode();
    changeManagerParameters();

    var canChangeRegistrateToChangePassword = true;

    function sendCode() {

        var registrate;

        $("#managerForgotPasswordLink").click(function () {

            if (!canChangeRegistrateToChangePassword)
                return;
            $(".registrateManager").hide();
            $(".changePasswordManager").show();
            $("#sendCodeToManagerMessage").hide();
            registrate = false;
        });

        $(".manager-registrate-link").click(function () {

            if (!canChangeRegistrateToChangePassword)
                return;
            $(".registrateManager").show();
            $(".changePasswordManager").hide();
            $("#sendCodeToManagerMessage").hide();
            registrate = true;
        });


        $("#submitSendCodeToManagerForm").click(function () {

            $("#submitSendCodeToManagerForm").prop("disabled", true);

            function error() {
                $("#sendCodeToManagerMessage").show();
                $("#sendCodeToManagerMessage").text("Произошла техническая ошибка в работе сервиса." +
                        "\n Пожалуйста, попробуйте позже или обратитесь в техническую поддержку.");
                $("#submitSendCodeToManagerForm").prop("disabled", false);
            }


            var data = {
                phone: $("#managerPhone").val(),
                registrate: registrate
            }

            $.ajax({
                type: "POST",
                url: "/serviceManager/registrate/submitCode",
                data: data
            })

                    .always(function () {
                        $("#submitSendCodeToManagerForm").prop("disabled", false);
                    })

                    .success(function (data) {

                        var status = data.status;

                        if (status == 0) {
                            $("#sendCodeToManagerForm").hide();
                            $("#checkCodeManagerForm").show();
                            canChangeRegistrateToChangePassword = false;
                        }
                        if (status == 2) {
                            $("#sendCodeToManagerMessage").show();
                            $("#sendCodeToManagerMessage").text("Менеджер с таким номером телефона уже существует.");
                        }
                        if (status == 3) {
                            $("#sendCodeToManagerMessage").show();
                            $("#sendCodeToManagerMessage").text("Менеджера с таким номером телефона не существует.");
                        }
                        else if (status == 1)
                            error();

                    })

                    .fail(function (error) {
                        error();
                    })

            return false;
        });
    }


    function checkCode() {

        var options = {};

        options.beforeSubmit = function () {
            $("#submitСheckCodeManagerForm").prop("disabled", true);
        }

        options.error = function (error) {
            $("#checkCodeManageMessage").show();
            $("#checkCodeManageMessage").text("Произошла техническая ошибка в работе сервиса." +
                    "\n Пожалуйста, попробуйте позже или обратитесь в техническую поддержку.");
            $("#submitСheckCodeManagerForm").prop("disabled", false);
        };

        options.success = function (data) {
            var status = data.status;
            if (status == 0) {
                $("#checkCodeManagerForm").hide();
                $("#changeManagerParameters").show();
            }
            else if (status == 1) {
                $("#checkCodeManageMessage").show();
                $("#checkCodeManageMessage").text("Введен неверный код.");
            }

            $("#submitСheckCodeManagerForm").prop("disabled", false);
        };

        $('#checkCodeManagerForm').ajaxForm(options);
    }

    function changeManagerParameters() {

        var options = {};

        options.beforeSerialize = function () {

            var password1 = $("#managePassword1");
            var password2 = $("#managePassword2");

            if (password1.val() != password2.val()) {
                $("#changeManagerParametersMessage").show();
                $("#changeManagerParametersMessage").text("Оба введенных пароля должны быть идентичны!");
                return false;
            }
            if (!checkPasswordLength(password1, $("#changeManagerParametersMessage")))
                return false;
        }

        options.beforeSubmit = function () {
            $(".submitChangeManagerParametersForm").prop("disabled", true);
        }

        options.error = function (error) {
            $("#changeManagerParametersMessage").show();
            $("#changeManagerParametersMessage").text("Произошла техническая ошибка в работе сервиса." +
                    "\n Пожалуйста, попробуйте позже или обратитесь в техническую поддержку.");
            $(".submitChangeManagerParametersForm").prop("disabled", false);
        };

        options.success = function (data) {
            var status = data.status;
            if (status == 0) {
                $("#changeManagerParameters").hide();
                $("#parametersSuccessfullyChanged").show();
            }
            else if (status != 0) {
                this.error();
            }

            $(".submitChangeManagerParametersForm").prop("disabled", false);
        };

        $('#changeManagerParameters').ajaxForm(options);
    }
}


/////////////////////////////// UTILS //////////////////////////////////////////////////////////////////////////////

function getUrlParameter(sParam) {
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++) {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) {
            return sParameterName[1];
        }
    }
}

function checkPasswordLength(passwordInput, message) {

    var password = passwordInput.val();
    if (password.length < 5) {
        message.show();
        message.text("Пароль должен состоять не менее чем из 5 символов.");
        return false;
    }
    else {
        message.hide();
        return true;
    }
}


</script>
</head>
<body>

<div style="height: 100%; width: 100%;  position: absolute; top: 40%; left: 0; text-align: center">

    <a href="#" style="color: black" data-popup="signin">ВХОД В СЕРВИС ДЛЯ ПОЛЬЗОВАТЕЛЕЙ</a><br>
    <a href="#" style="color: black" data-popup="signin-manager">ВХОД В СЕРВИС ДЛЯ МЕНЕДЖЕРОВ</a><br>
    <a href="#" style="color: black" data-popup="signup">РЕГИСТРАЦИЯ ДЛЯ ПОЛЬЗОВАТЕЛЕЙ</a><br>
    <a class="manager-registrate-link" href="#" style="color: black" data-popup="registrate-manager">РЕГИСТРАЦИЯ ДЛЯ
        МЕНЕДЖЕРОВ</a>

</div>

<div class="layout" style="display: none;">
    <div class="layout-overlay"></div>
    <div class="popup popup-signin" style="display: none;">
        <h2 class="popup-title">ВХОД В СЕРВИС</h2>


        <form class="form form-popup" action="/spring_security_check" method="POST">
            <input type="email" class="input-text" placeholder="E-mail" required="" name="j_username">
            <input type="password" class="input-text" placeholder="Пароль" required="" name="j_password">
            <a href="#" class="fl" data-popup="recoverPassword">Забыли пароль?</a>
            <a href="#" class="fr" data-popup="signup">Регистрация</a>
            <input type="submit" class="button" value="Войти в сервис"/>
            <input type="checkbox" id="c-remember1" class="input-c" name="_spring_security_remember_me">
            <label for="c-remember1">Запомнить меня и входить автоматически</label><br>
            <label id="userLoginErrorMessage" style="color: #e66262; display: none" hidden="hidden">Неверный email или
                пароль.</label>
        </form>
    </div>

    <div class="popup popup-recoverPassword" style="display: none;">
        <h2 class="popup-title">
            СМЕНА ПАРОЛЯ
            <span id="recoverPasswordMessageSend"
                  style="color: #46cf98; display: none">Письмо было успешно отправлено.</span>
        </h2>

        <form id="recoverPasswordForm" class="form form-popup" action="/recoverPassword" method="POST">
            <input type="email" class="input-text" placeholder="E-mail" required="" name="email">
            <input id="submitRecoverPasswordForm" type="submit" class="button" value="Сменить пароль"/><br>
            <label id="recoverPasswordMessage" style="color: #e66262; display: none"/>
        </form>
    </div>

    <a href="#" data-popup="changePassword"></a>

    <div class="popup popup-changePassword" style="display: none;">
        <h2 class="popup-title">
            СМЕНА ПАРОЛЯ
            <span id="wrongToken" style="color: #e66262; display: none">Ссылка неверна или устарела.</span>
            <span id="passwordWasChanged" style="color: #46cf98; display: none">Пароль был успешно изменен.</span>
        </h2>

        <form id="changePasswordForm" class="form form-popup" action="/spring_security_check" method="POST">
            <input id="userPassword1" type="password" class="input-text" placeholder="Пароль" required="">
            <input id="userPassword2" type="password" class="input-text" placeholder="Подтверждение пароля" required="">
            <input id="token" type="hidden"/>
            <input id="changePasswordSubmit" type="button" class="button" value="Сменить пароль"/><br>
            <label id="changePasswordMessage" style="color: #e66262; display: none" hidden="hidden">Неверный email или
                пароль.</label>
        </form>
    </div>

    <div class="popup popup-signin-manager" style="display: none;">
        <h2 class="popup-title">ВХОД В СЕРВИС<span>ДЛЯ МЕНЕДЖЕРОВ</span></h2>

        <form class="form form-popup" action="/serviceManager/manager_spring_security_check" method="POST">
            <input type="tel" class="input-text" placeholder="Телефон" required="" name="j_username">
            <input type="password" class="input-text" placeholder="Пароль" required="" name="j_password">
            <a id="managerForgotPasswordLink" href="#" class="fl" data-popup="registrate-manager">Забыли пароль?</a>
            <a href="#" class="fr manager-registrate-link" data-popup="registrate-manager">Регистрация</a>
            <input type="submit" class="button" value="Войти в сервис"/>
            <input type="checkbox" id="c-remember2" class="input-c" name="_spring_security_remember_me">
            <label for="c-remember2">Запомнить меня и входить автоматически</label>
            <label id="managerLoginErrorMessage" style="color: #e66262; display: none" hidden="hidden">Неверный телефон
                или пароль.</label>
        </form>
    </div>


    <div class="popup popup-signup" style="display: none;">
        <h2 class="popup-title">
            РЕГИСТРАЦИЯ
            <span id="userRegistrateMessageSend" style="display: none">Письмо было успешно отправлено.</span>
            <span id="successUserRegistration" style="display: none">Регистрация успешно завершена.</span>
            <span id="wrongLinkUserRegistration"
                  style="color: #e66262; display: none">Ссылка неверная или устарела.</span>
        </h2>

        <form id="userRegistrateForm" class="form form-popup" action="registrate" method="POST">
            <input type="text" class="input-text" placeholder="Имя" required="" name="name">
            <input type="email" class="input-text" placeholder="E-mail" required="" name="email">
            <input id="userPassword" type="password" class="input-text" placeholder="Пароль" required=""
                   name="password">

            <p>Все поля формы обязательны для заполнения.</p>
            <input id="submitUserRegistrationForm" type="submit" class="button"
                   value="Начать пользоваться сервисом"/><br>
            <label id="userRegistrateMessage" style="color: #e66262; display: none"/>
        </form>
    </div>

    <div class="popup popup-registrate-manager" style="display: none;">

        <div id="sendCodeToManagerForm">

            <h2 class="popup-title registrateManager">РЕГИСТРАЦИЯ<span>ДЛЯ МЕНЕДЖЕРОВ</span></h2>

            <h2 class="popup-title changePasswordManager">СМЕНА ПАРОЛЯ<span>ДЛЯ МЕНЕДЖЕРОВ</span></h2>

            <form class="form form-popup" action="/serviceManager/registrate/submitCode" method="post">
                <input id="managerPhone" type="tel" class="input-text" placeholder="Номер телефона" required=""
                       name="phone">

                <p>Все поля формы обязательны для заполнения.</p>
                <input id="submitSendCodeToManagerForm" type="button" class="button"
                       value="Отправить sms с кодом"/><br>
                <label id="sendCodeToManagerMessage" style="color: #e66262; display: none" hidden="hidden"/>
            </form>
        </div>

        <div id="checkCodeManagerForm" style="display: none;">

            <h2 class="popup-title">ПОДТВЕРЖДЕНИЕ КОДА</h2>

            <form class="form form-popup" action="/serviceManager/registrate/checkCode" method="post">
                <input type="text" class="input-text" placeholder="Код" required="" name="code">

                <p>Все поля формы обязательны для заполнения.</p>
                <input id="submitСheckCodeManagerForm" type="submit" class="button" value="Подтвердить код"><br>
                <label id="checkCodeManageMessage" style="color: #e66262; display: none" hidden="hidden"/>
            </form>
        </div>

        <div id="changeManagerParameters" style="display: none;">

            <h2 class="popup-title registrateManager">РЕГИСТРАЦИЯ<span>ДЛЯ МЕНЕДЖЕРОВ</span></h2>

            <h2 class="popup-title changePasswordManager">СМЕНА ПАРОЛЯ<span>ДЛЯ МЕНЕДЖЕРОВ</span></h2>

            <form class="form form-popup" action="/serviceManager/registrate/updateManager" method="post" novalidate>
                <input type="text" class="input-text registrateManager" placeholder="Имя" required="" name="name">
                <input type="email" class="input-text registrateManager" placeholder="Email" required="" name="email">
                <input id="managePassword1" type="password" class="input-text" placeholder="Пароль" required=""
                       name="password">
                <input id="managePassword2" type="password" class="input-text" placeholder="Подтверждение пароля"
                       required="">

                <p>Все поля формы обязательны для заполнения.</p>
                <input class="submitChangeManagerParametersForm registrateManager button" type="submit"
                       value="Начать пользоваться сервисом">
                <input class="submitChangeManagerParametersForm changePasswordManager button" type="submit"
                       value="Изменить пароль">
                <br>
                <label id="changeManagerParametersMessage" style="color: #e66262; display: none" hidden="hidden"/>
            </form>
        </div>

        <div id="parametersSuccessfullyChanged" style="display: none;">
            <form class="form form-popup"/>

            <h2 class="popup-title registrateManager">РЕГИСТРАЦИЯ<span>ДЛЯ МЕНЕДЖЕРОВ</span>
                <br>
                <br>
                <span>РЕГИСТРАЦИЯ УСПЕШНО ЗАВЕРШЕНА</span>
            </h2>

            <h2 class="popup-title changePasswordManager">СМЕНА ПАРОЛЯ<span>ДЛЯ МЕНЕДЖЕРОВ</span></span>
                <br>
                <br>
                <span>ПАРОЛЬ УСПЕШНО ИЗМЕНЕН</span>
            </h2>

            </form>
        </div>
    </div>
</div>
</body>
</html>