/*						Popup
 -----------------------------------------------------------*/
$('[data-popup]').click(function () {
    popupOpen($(this).attr('data-popup'))
});
$(document).keyup(function (d) {
    if (d.keyCode == 27) {
        popupClose();
    }
});
$(document).on('click', '.popup-close,.layout-overlay', function () {
    popupClose()
});
function popupOpen(popupName) {
    var shift = $(window).width();
    $('body').addClass('lock');
    shift -= $(window).width();
    $('body').css({'margin-left': shift});
    $('.popup').hide();
    $('.layout,.popup-' + popupName).fadeIn();
    return false;
}
function popupClose() {
    $('body').removeClass('lock');
    $('body').css({'margin-left': 0});
    $('.layout,.popup').fadeOut();
    return false;
}

/*						Inputmask
 -----------------------------------------------------------*/
$(document).ready(function () {

    $.ajax({
        url: '//freegeoip.net/json/',
        type: 'POST',
        dataType: 'jsonp',
        success: function (location) {

            var countryCode = location.country_code;

            if (countryCode == "RU")
                $('input[type="tel"]').inputmask("mask", {"mask": "+79999999999"});
            else if (countryCode == "UA")
                $('input[type="tel"]').inputmask("mask", {"mask": "+380999999999"});
            else if (countryCode == "BY")
                $('input[type="tel"]').inputmask("mask", {"mask": "+375999999999"});
        }});
});
/*						Tabs
 -----------------------------------------------------------*/
$('.type-call > li').click(function () {
    $(this).addClass('active').siblings().removeClass('active');
    $('.calls > ul > li').eq($(this).index()).addClass('active').siblings().removeClass('active');
});

/*						Phone Button
 -----------------------------------------------------------*/

var phoneButton = $('.phone-button div');

function togglePhoneButton() {
    phoneButton.addClass('active').siblings().removeClass('active');
    phoneButton.parent().toggleClass('active');
}

phoneButton.click(togglePhoneButton);

/*						Switch Button
 -----------------------------------------------------------*/

function toggleSwitchButton() {
    $(".switch-active").toggleClass("active");
    $(".switch-not-active").toggleClass("active");
}

$('.icon-switch-not-active, .icon-switch-active').click(toggleSwitchButton);

/*						Phone
 -----------------------------------------------------------*/
$('.numbers li:not(.green,.red,.star-sharp),.star,.sharp').click(function () {
    var numb = $(this).html();
    var phone = $('.phone-number');
    var newNumb = phone.val() + numb;
    phone.val(newNumb);
});


/*						Calls Scroll
 -----------------------------------------------------------*/
$(function () {
    $('.calls li').perfectScrollbar();
});

