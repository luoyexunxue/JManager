$.ajax({
    url: "data/app.json",
    dataType: 'json',
    async: false,
    success: function (data) {
        $('.sys-title').html(data.app_name);
        document.write('<script src="plugins/background-effects/' + data.background + '.js"></script>');
    }
});

$(function () {
    function login() {
        var blocking = $.alert({
            icon: 'fa fa-spinner fa-spin',
            title: '请稍等...',
            content: '正在等待服务器相应!',
            onOpenBefore: function () {
                this.buttons.ok.hide();
            }
        });
        $.ajax({
            url: '../api/user/login',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                username: $('#username').val(),
                password: $('#password').val()
            }),
            success: function (json) {
                if (json.success) {
                    if ($('#rememberMe').is(':checked')) $.cookie('ticket', json.data, { expires: 7, path: '/' });
                    var name = $('#username').val();
                    var ticket = encodeURIComponent(json.data);
                    window.location.replace('index.html?ticket=' + ticket + '&user=' + name);
                } else {
                    blocking.buttons.ok.show();
                    blocking.setIcon('');
                    blocking.setTitle(false);
                    blocking.setContent(json.message);
                    $('#password').val('');
                    $('#username').focus();
                }
            },
            error: function (err) {
                console.log(err);
                blocking.buttons.ok.show();
                blocking.setIcon('');
                blocking.setTitle(false);
                blocking.setContent('服务无响应！');
            }
        });
    }
    Waves.displayEffect();
    $('.form-control').focus(function () {
        $(this).parent().addClass('fg-toggled');
    }).blur(function () {
        $(this).parent().removeClass('fg-toggled');
    });
    $('#login-bt').click(function () { login(); });
    $('#username, #password').keypress(function (event) { if (13 == event.keyCode) login(); });
});