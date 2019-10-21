document.write([
    '<script src="plugins/jquery.1.12.4.min.js"></script>',
    '<script src="plugins/bootstrap-3.3.0/js/bootstrap.min.js"></script>',
    '<script src="plugins/bootstrap-table-1.11.0/bootstrap-table.min.js"></script>',
    '<script src="plugins/bootstrap-table-1.11.0/locale/bootstrap-table-zh-CN.min.js"></script>',
    '<script src="plugins/waves-0.7.5/waves.min.js"></script>',
    '<script src="plugins/jquery-confirm/jquery-confirm.min.js"></script>',
    '<script src="plugins/jquery.cookie.js"></script>',
    '<script src="plugins/laydate/laydate.js"></script>',
    '<script src="plugins/common.js"></script>',
    '<script src="app/js/manage.js"></script>',
    '<link href="plugins/bootstrap-3.3.0/css/bootstrap.min.css" rel="stylesheet"/>',
    '<link href="plugins/material-design-iconic-font-2.2.0/css/material-design-iconic-font.min.css" rel="stylesheet"/>',
    '<link href="plugins/bootstrap-table-1.11.0/bootstrap-table.min.css" rel="stylesheet"/>',
    '<link href="plugins/font-awesome-4.7.0/css/font-awesome.min.css" rel="stylesheet"/>',
    '<link href="plugins/waves-0.7.5/waves.min.css" rel="stylesheet"/>',
    '<link href="plugins/jquery-confirm/jquery-confirm.min.css" rel="stylesheet"/>',
    '<link href="app/css/manage.css" rel="stylesheet" />'
].join('\r\n'));

window.application = {};
window.application.events = [];
window.application.call = parent.callFrame;
window.application.register = function (event, callback) {
    var evt = event.split(',');
    for (var i = 0; i < evt.length; i++) {
        if (window.application.events[evt[i]] == undefined) {
            window.application.events[evt[i]] = [callback];
        } else {
            window.application.events[evt[i]].push(callback);
        }
    }
};
window.application.onevent = function (event, param) {
    if (window.application.events[event] == undefined) return;
    for (var i = 0; i < window.application.events[event].length; i++) {
        window.application.events[event][i](param);
    }
};