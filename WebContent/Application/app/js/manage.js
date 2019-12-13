$(function () {
    window.ticket = window.parent.ticket;
    window.pageSize = $.cookie('pageSize', Number);
    window.inputCache = {};
    Waves.displayEffect();
    $.loading = jQuery.fn.loading;
    $(window).resize(function () {
        $('#table').bootstrapTable('resetView', {
            height: $(window).height() - 20
        });
    });
    $(document).on('focus', 'input[type="text"],select', function () {
        $(this).parent().find('label').addClass('active');
    }).on('blur', 'input[type="text"],select', function () {
        if ($(this).val() == '') $(this).parent().find('label').removeClass('active');
    }).find('select').each(function () {
        if ($(this).val()) $(this).parent().find('label').addClass('active');
    });
    $('#table').bootstrapTable({
        url: tableParam.url + 'list',
        ajax: function (request) {
            if (!request.beforeSend) {
                request.beforeSend = function (XHR) {
                    XHR.setRequestHeader('Authorization', 'BasicAuth ' + window.ticket);
                };
            }
            $.ajax(request);
        },
        method: 'post',
        height: $(window).height() - 20,
        striped: true,
        search: true,
        searchOnEnterKey: true,
        showRefresh: true,
        showColumns: true,
        pagination: true,
        paginationLoop: false,
        classes: 'table table-hover table-no-bordered',
        sidePagination: 'server',
        silentSort: false,
        smartDisplay: false,
        idField: 'id',
        escape: true,
        maintainSelected: true,
        pageSize: window.pageSize == undefined ? 10 : window.pageSize,
        toolbar: '#toolbar',
        columns: tableParam.columns
    }).on('all.bs.table', function (e, name, args) {
        $('[data-toggle="tooltip"]').tooltip();
        $('[data-toggle="popover"]').popover();
    }).on('page-change.bs.table', function (e, number, size) {
        $.cookie('pageSize', '' + size);
    }).on('load-error.bs.table', function (e, status) {
        if (status == 401) $.alert('身份验证信息已失效，请重新登录');
    });
    var actionList = [
        '<a class="edit ml10" href="javascript:void(0)" data-toggle="tooltip" title="修改"><i class="glyphicon glyphicon-edit"></i></a>',
        '<a class="detail ml10" href="javascript:void(0)" data-toggle="tooltip" title="详细"><i class="glyphicon glyphicon-info-sign"></i></a>',
        '<a class="remove ml10" href="javascript:void(0)" data-toggle="tooltip" title="删除"><i class="glyphicon glyphicon-remove"></i></a>'
    ];
    window.actionFormatter1 = function () { return actionList[0] + '　' + actionList[2]; }
    window.actionFormatter2 = function () { return actionList[1] + '　' + actionList[2]; }
    window.actionFormatter3 = function () { return actionList[0]; }
    window.actionFormatter4 = function () { return actionList[1]; }
    window.actionEvents = {
        'click .edit': function (e, value, row, index) { updateAction([row]); },
        'click .remove': function (e, value, row, index) { deleteAction([row]); },
        'click .detail': function (e, value, row, index) { detailAction([row]); },
        'click .custom': function (e, value, row, index) { customAction([row]); }
    };
    window.application.refresh = function () {
        $('#table').bootstrapTable('refresh');
    };
    window.application.reload = function () {
        $('#createDialog,#exportDialog,#importDialog').find('select[url]').each(function (i, e) {
            var url = $(e).attr("url");
            var filter = {};
            if (url.indexOf('?') > 0) {
            	var params = url.substr(url.indexOf('?') + 1).split('&');
            	for (var i = 0; i < params.length; i++) {
            		var idx = params[i].indexOf('=');
            		filter[params[i].substring(0, idx)] = params[i].substring(idx + 1);
            	}
            }
            actionRequest(url, filter, function (json) {
                var html = '<option value=""></option>';
                for (var i = 0; i < json.length; i++) {
                    html += '<option value="' + json[i].id + '">' + json[i].name + '</option>';
                }
                $(e).html(html);
            });
        });
    };
    window.application.reload();
});

function actionRequest(url, obj, success, error) {
    $.ajax({
        url: url,
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(obj),
        success: success,
        beforeSend: function (XHR) {
            XHR.setRequestHeader('Authorization', 'BasicAuth ' + window.ticket);
        },
        error: function (err) {
            if (error) error(err.responseText);
            else $.alert(err.responseText);
        }
    });
}

function dialogContent(content, options, row) {
    if (options.datetimePicker) {
        content.find('input[validate*="date:true"]').each(function (i, e) {
            laydate.render({
                elem: e, type: 'date', done: function (v) {
                    if (v == '') $(e).prev().removeClass('active');
                    else $(e).prev().addClass('active');
                    $(e).trigger('input');
                }
            });
        });
        content.find('input[validate*="datetime:true"]').each(function (i, e) {
            laydate.render({
                elem: e, type: 'datetime', done: function (v) {
                    if (v == '') $(e).prev().removeClass('active');
                    else $(e).prev().addClass('active');
                    $(e).trigger('input');
                }
            });
        });
    }
    if (options.uploadFile) {
        content.find('input[file]').each(function (i, e) {
            var fileInput = $(e);
            fileInput.after('<input class="form-control upload-file" type="file" accept="' + $(e).attr('file') + '" />');
            fileInput.next().unbind().bind('change', function (evt) {
                fileInput.val(evt.target.files[0].name);
                fileInput.prev().addClass('active');
                var reader = new FileReader();
                reader.onload = function (f) {
                    fileInput.data("attach", f.target.result);
                };
                reader.readAsDataURL(evt.target.files[0]);
            });
        });
    }
    if (options.formatInput) {
        content.find('input,select').each(function (i, e) {
            var value = row[$(e).attr('name')];
            if (value != undefined && value !== '') {
                $(e).data("attach", value);
                $(e).prev().addClass('active');
                if ($(e).attr("url")) {
                    $(e).val(value.id == undefined ? value : value.id);
                } else {
                    var formatter = $(e).attr('formatter');
                    $(e).val(formatter ? eval(formatter + '(value)') : value);
                }
            }
        });
    }
    if (options.loadDetail) {
        content.find('div>div[url]').each(function (i, e) {
            var param = {};
            var paramArray = [];
            var dataRow = row;
            if ($(e).attr("param")) paramArray = $(e).attr("param").replace(/^\{|\}$/gm, '').split(',');
            for (var i = 0; i < paramArray.length; i++) {
                var kv = paramArray[i].split(':');
                param[kv[0]] = eval('dataRow.' + kv[1]);
            }
            actionRequest($(e).attr("url"), param, function (json) {
                $(e).prev().addClass("active");
                var formatter = $(e).attr('formatter');
                $(e).html(formatter ? eval(formatter + '(json)') : JSON.stringify(json));
            });
        });
    }
    if (options.loadDefault) {
        content.find('input,select').each(function (i, e) {
            var value = window.inputCache[$(e).attr('name')];
            if (value != undefined && value !== '') {
                $(e).data("attach", value);
                $(e).prev().addClass('active');
                var formatter = $(e).attr('formatter');
                $(e).val(formatter ? eval(formatter + '(value)') : value);
            }
        });
    }
}

function createAction() {
    $.confirm({
        title: '新增',
        content: $('#createDialog').html(),
        columnClass: 'col-md-6 col-md-offset-3',
        buttons: {
            confirm: {
                text: '确认',
                btnClass: 'waves-effect waves-button',
                action: function () {
                    var model = {};
                    if (!this.$content.validate()) {
                        this.highlight();
                        return false;
                    }
                    this.$content.find('input,select').each(function (i, e) {
                        var inputVal = $(e).attr("file") ? $(e).data("attach") : $(e).val();
                        var defValue = $(e).attr("default");
                        model[$(e).attr("name")] = inputVal ? inputVal : (defValue ? defValue : '');
                        if ($(e).attr('keep') != undefined) {
                            window.inputCache[$(e).attr("name")] = model[$(e).attr("name")];
                        }
                    });
                    actionRequest(tableParam.url + 'insert', model, function (json) {
                        if (!json.success) {
                            $.alert(json.message);
                        } else {
                            application.onevent('insert', model);
                            $('#table').bootstrapTable('refresh');
                        }
                    });
                }
            },
            cancel: {
                text: '取消',
                btnClass: 'waves-effect waves-button'
            }
        },
        onContentReady: function () {
            dialogContent(this.$content, { datetimePicker: true, uploadFile: true, loadDefault: true });
            this.$content.find('input[validate*="required:true"]').prev().addClass('required');
        }
    });
}

function updateAction(rows) {
    rows = rows ? rows : $('#table').bootstrapTable('getSelections');
    if (rows.length != 1) {
        $.confirm({
            title: false,
            content: '请选择一条记录！',
            autoClose: 'cancel|3000',
            backgroundDismiss: true,
            buttons: {
                cancel: {
                    text: '取消',
                    btnClass: 'waves-effect waves-button'
                }
            }
        });
    } else {
        $.confirm({
            title: '修改',
            content: $('#createDialog').html(),
            columnClass: 'col-md-6 col-md-offset-3',
            buttons: {
                confirm: {
                    text: '确认',
                    btnClass: 'waves-effect waves-button',
                    action: function () {
                        var model = { id: rows[0].id };
                        if (!this.$content.validate()) {
                            this.highlight();
                            return false;
                        }
                        this.$content.find('input,select').each(function (i, e) {
                            model[$(e).attr("name")] = $(e).attr("file") ? $(e).data("attach") : $(e).val();
                        });
                        actionRequest(tableParam.url + 'update', model, function (json) {
                            if (!json.success) {
                                $.alert(json.message);
                            } else {
                                application.onevent('update', model);
                                $('#table').bootstrapTable('refresh');
                            }
                        });
                    }
                },
                cancel: {
                    text: '取消',
                    btnClass: 'waves-effect waves-button'
                }
            },
            onContentReady: function () {
                dialogContent(this.$content, { datetimePicker: true, uploadFile: true, formatInput: true }, rows[0]);
                this.$content.find('input[validate*="required:true"]').prev().addClass('required');
            }
        });
    }
}

function detailAction(rows) {
    rows = rows ? rows : $('#table').bootstrapTable('getSelections');
    if (rows.length != 1) {
        $.confirm({
            title: false,
            content: '请选择一条记录！',
            autoClose: 'cancel|3000',
            backgroundDismiss: true,
            buttons: {
                cancel: {
                    text: '取消',
                    btnClass: 'waves-effect waves-button'
                }
            }
        });
    } else {
        $.confirm({
            title: false,
            content: $('#createDialog').html(),
            columnClass: 'col-md-6 col-md-offset-3',
            buttons: {
                cancel: {
                    text: '关闭',
                    btnClass: 'waves-effect waves-button'
                }
            },
            onContentReady: function () {
                dialogContent(this.$content, { formatInput: true, loadDetail: true }, rows[0]);
            }
        });
    }
}

function deleteAction(rows) {
    rows = rows ? rows : $('#table').bootstrapTable('getSelections');
    if (rows.length == 0) {
        $.confirm({
            title: false,
            content: '请至少选择一条记录！',
            autoClose: 'cancel|3000',
            backgroundDismiss: true,
            buttons: {
                cancel: {
                    text: '取消',
                    btnClass: 'waves-effect waves-button'
                }
            }
        });
    } else {
        $.confirm({
            title: false,
            content: '确认删除这' + rows.length + '条数据吗？',
            buttons: {
                confirm: {
                    text: '确认',
                    btnClass: 'waves-effect waves-button',
                    action: function () {
                        var ids = [];
                        for (var i in rows) ids.push(rows[i].id);
                        actionRequest(tableParam.url + 'delete', ids, function (json) {
                            if (!json.success) {
                                $.alert(json.message);
                            } else {
                                application.onevent('delete', ids);
                                $('#table').bootstrapTable('refresh');
                            }
                        });
                    }
                },
                cancel: {
                    text: '取消',
                    btnClass: 'waves-effect waves-button'
                }
            }
        });
    }
}

function importAction() {
    $.confirm({
        title: '导入',
        content: $('#importDialog').html(),
        columnClass: 'col-md-6 col-md-offset-3',
        buttons: {
            confirm: {
                text: '确认',
                btnClass: 'waves-effect waves-button',
                action: function () {
                    var model = {};
                    if (!this.$content.validate()) {
                        this.highlight();
                        return false;
                    }
                    this.$content.find('input,select').each(function (i, e) {
                        var inputVal = $(e).attr("file") ? $(e).data("attach") : $(e).val();
                        var defValue = $(e).attr("default");
                        model[$(e).attr("name")] = inputVal ? inputVal : (defValue ? defValue : '');
                    });
                    actionRequest(tableParam.url + 'import', model, function (json) {
                        if (!json.success) {
                            $.alert(json.message);
                        } else {
                            application.onevent('import', model);
                            $('#table').bootstrapTable('refresh');
                        }
                    });
                }
            },
            cancel: {
                text: '取消',
                btnClass: 'waves-effect waves-button'
            }
        },
        onContentReady: function () {
            dialogContent(this.$content, { datetimePicker: true, uploadFile: true });
            this.$content.find('input[validate*="required:true"]').prev().addClass('required');
        }
    });
}

function exportAction() {
    var content = $('#exportDialog').html();
    $.confirm({
        title: '导出',
        content: content != undefined ? content : "是否导出所有数据？",
        columnClass: 'col-md-6 col-md-offset-3',
        buttons: {
            confirm: {
                text: '确认',
                btnClass: 'waves-effect waves-button',
                action: function () {
                    var model = { auth: window.ticket };
                    if (!this.$content.validate()) {
                        this.highlight();
                        return false;
                    }
                    this.$content.find('input,select').each(function (i, e) {
                        model[$(e).attr("name")] = $(e).val();
                    });
                    var param = encodeURIComponent(JSON.stringify(model));
                    window.open('/Default.ashx?method=export&param=' + param);
                }
            },
            cancel: {
                text: '取消',
                btnClass: 'waves-effect waves-button'
            }
        },
        onContentReady: function () {
            dialogContent(this.$content, { datetimePicker: true });
            this.$content.find('input[validate*="required:true"]').prev().addClass('required');
        }
    });
}