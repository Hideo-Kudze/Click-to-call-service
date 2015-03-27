<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <title>MAXISERVICE</title>

    <link rel="stylesheet" href="../../resources/css/style.css">
    <link rel="shortcut icon" href="img/favicon.png">

    <script type="text/javascript" src="http://code.jquery.com/jquery-2.1.1.min.js" defer></script>
    <script type="text/javascript" src="/resources/js/inputmask.js" defer></script>
    <script type="text/javascript" src="/resources/js/script.js" defer></script>
</head>
<body>
<header class="header">
    <div class="wrapper">
        <div class="logo fl"><span>MAXI</span>SERVICE</div>
        <div class="fr">
            <div class="balance ib">10.83$</div>
            <div class="topup ib">Пополнить баланс</div>
            <div class="links ib ib-list">
                <a href="#" class="link-profile" data-popup="signin"></a>
                <a href="#" class="link-audio" data-popup="signin-manager"></a>
                <a href="#" class="link-money" data-popup="signup"></a>
                <a href="#" class="link-exit" data-popup="signup-manager"></a>
            </div>
        </div>
    </div>
</header>
<div class="content wrapper">
    <aside>
        <ul class="menu">
            <li>
                <a href="#" class="menu-home active">ГЛАВНАЯ</a>
                <ul class="submenu">
                    <c:forEach items="${servicedSites}" var="servicedSite">
                        <li><a href="#">${servicedSite.siteName}</a></li>
                    </c:forEach>
                </ul>
            </li>
            <li><a href="#" class="menu-manager">МЕНЕДЖЕРЫ</a></li>
            <li><a href="#" class="menu-profill">ПРОФИЛЬ</a></li>
            <li><a href="#" class="menu-finans">ФИНАНСЫ</a></li>
            <li><a href="#" class="menu-help">ПОДДЕРЖКА</a></li>
        </ul>
    </aside>
    <div class="home">
        <div class="breadcrumbs">
            <a href="#">Maxiservice main</a>
        </div>
        <ul class="main">
            <c:forEach items="${servicedSites}" var="servicedSite">
                <li>
                    <p class="title"><span>${servicedSite.siteName}</span></p>

                    <div class="call">Получено звонков: <span>${servicedSite.callsReceived}</span></div>
                    <div class="mass">Получено обращений: <span>0</span></div>
                </li>
            </c:forEach>
        </ul>
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
    <div class="popup popup-signin">
        <h2 class="popup-title">ВХОД В СЕРВИС</h2>

        <form class="form form-popup">
            <input type="email" class="input-text" placeholder="E-mail" required>
            <input type="password" class="input-text" placeholder="Пароль" required>
            <a href="#" class="fl">Забыли пароль?</a>
            <a href="#" class="fr" data-popup="signup">Регистрация</a>
            <button class="button">Войти в сервис</button>
            <input type="checkbox" id="c-remember1" class="input-c"><label for="c-remember1">Запомнить меня и входить
            автоматически</label>
        </form>
    </div>
    <div class="popup popup-signin-manager">
        <h2 class="popup-title">ВХОД В СЕРВИС<span>ДЛЯ МЕНЕДЖЕРОВ</span></h2>

        <form class="form form-popup">
            <input type="email" class="input-text" placeholder="E-mail" required>
            <input type="password" class="input-text" placeholder="Пароль" required>
            <a href="#" class="fl">Забыли пароль?</a>
            <a href="#" class="fr" data-popup="signup">Регистрация</a>
            <button class="button">Войти в сервис</button>
            <input type="checkbox" id="c-remember2" class="input-c"><label for="c-remember2">Запомнить меня и входить
            автоматически</label>
        </form>
    </div>
    <div class="popup popup-signup">
        <h2 class="popup-title">РЕГИСТРАЦИЯ</h2>

        <form class="form form-popup">
            <input type="text" class="input-text" placeholder="Имя" required>
            <input type="email" class="input-text" placeholder="E-mail" required>
            <input type="password" class="input-text" placeholder="Пароль" required>

            <p>Все поля формы обязательны для заполнения.</p>
            <button class="button">Начать пользоваться сервисом</button>
        </form>
    </div>
    <div class="popup popup-signup-manager">
        <h2 class="popup-title">РЕГИСТРАЦИЯ<span>ДЛЯ МЕНЕДЖЕРОВ</span></h2>

        <form class="form form-popup">
            <input type="text" class="input-text" placeholder="Имя" required>
            <input type="email" class="input-text" placeholder="E-mail" required>
            <input type="password" class="input-text" placeholder="Пароль" required>
            <input type="tel" class="input-text" placeholder="Номер телефона" required>

            <p>Все поля формы обязательны для заполнения.</p>
            <button class="button">Начать пользоваться сервисом</button>
        </form>
    </div>
</div>

</body>
</html>