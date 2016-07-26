$(function() {
    $(window).scroll(function() {
        if ($(window).scrollTop() > 100) {
            $("#side_bar .gotop").fadeIn();
        } else {
            $("#side-bar .gotop").hide();
        }
    });
    $("#side_bar .gotop").click(function() {
        $('html,body').animate({
            'scrollTop': 0
        }, 500);
    });
    $("#side_bar .gobottom").click(function() {
        var btm = $('html,body').height();
        $('html,body').animate({
            'scrollTop': btm
        }, 500);
    })
});