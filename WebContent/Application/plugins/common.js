String.prototype.replaceAll = function (FindText, RepText) {
    return this.replace(new RegExp(FindText, "g"), RepText);
}

Date.prototype.format = function (format) {
    if (isNaN(this.getTime())) return '--';
    var o = {
        "M+": this.getMonth() + 1,
        "d+": this.getDate(),
        "H+": this.getHours(),
        "m+": this.getMinutes(),
        "s+": this.getSeconds(),
        "q+": Math.floor((this.getMonth() + 3) / 3),
        "S": this.getMilliseconds()
    }
    if (/(y+)/.test(format)) {
        format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(format)) {
            var RepText = RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length);
            format = format.replace(RegExp.$1, RepText);
        }
    }
    return format;
}

Array.prototype.indexOf = function (val) {
	if (val instanceof Function) {
		for (var i = 0; i < this.length; i++) {
			if (val(this[i], i)) return i;
		}
	}
    for (var i = 0; i < this.length; i++) {
        if (this[i] == val) return i;
    }
    return -1;
}

Array.prototype.remove = function (val) {
	if (val instanceof Function) {
		for (var i = 0; i < this.length; i++) {
			if (val(this[i], i)) {
				this.splice(i, 1);
				return i;
			}
		}
	}
	var index = this.indexOf(val);
    if (index > -1) this.splice(index, 1);
    return index;
}

jQuery.fn.loading = function (options) {
    var defaults = { title: '正在加载，请稍后...', block: true };
    var settings = $.extend(defaults, options);
    if (settings.block) {
        if (window.blockingDialog) {
            window.blockingDialog.count += 1;
            return;
        }
        window.blockingDialog = $.alert({
            icon: 'fa fa-spinner fa-spin',
            title: settings.title,
            animateFromElement: false,
            content: '<div></div>',
            onOpenBefore: function () {
                this.buttons.ok.hide();
                this.$el.attr('style', 'z-index:999999');
            }
        });
        window.blockingDialog.count = 1;
    } else if (window.blockingDialog) {
        if (--window.blockingDialog == 0) {
            window.blockingDialog.close();
            window.blockingDialog = null;
        }
    }
}

jQuery.fn.validate = function (options) {
    var success = true;
    var item_success = true;
    var defaults = {
        onerror: function (e, msg) {
            $(e).addClass('input-error');
            $(e).prev().append('<span class="title-error">[' + msg + ']</span>');
            success = false;
            item_success = false;
        }
    };
    var oncheck = function (e) {
        var rule = $(e).attr("validate");
        if (rule) {
            var input = $(e).val();
            var rules = rule.split(';');
            $(e).removeClass('input-error');
            $(e).prev().find('.title-error').remove();
            for (var i = 0; i < rules.length; i++) {
                if (rules[i] == '') continue;
                item_success = true;
                var item = rules[i].split(':');
                switch (item[0]) {
                    case 'required': if (item[1] == 'true' && input.length == 0) onerror(e, '必填项'); break;
                    case 'tel': if (item[1] == 'true' && input.length > 0 && !(/^1[34578]\d{9}$/.test(input))) onerror(e, '手机号格式不正确'); break;
                    case 'email': if (item[1] == 'true' && input.length > 0 && !(/^\s*([A-Za-z0-9_-]+(\.\w+)*@(\w+\.)+\w{2,5})\s*$/.test(input))) onerror(e, '邮件地址格式不正确'); break;
                    case 'cardId': if (item[1] == 'true' && input.length > 0 && !(/^\d{17}([0-9]|X)$/.test(input))) onerror(e, '身份证号格式不正确'); break;
                    case 'date': if (item[1] == 'true' && input.length > 0 && !(/^\d{4}-((0[1-9])|(1[012]))-((0[1-9])|([12][0-9])|(3[01]))$/.test(input))) onerror(e, '日期格式不正确'); break;
                    case 'datetime': if (item[1] == 'true' && input.length > 0 && !(/^\d{4}-((0[1-9])|(1[012]))-((0[1-9])|([12][0-9])|(3[01]))\s(([01][0-9])|2[0-3]):([0-5][0-9]):([0-5][0-9])$/.test(input))) onerror(e, '时间格式不正确'); break;
                    case 'decimal': if (item[1] == 'true' && input.length > 0 && !(/^-?[0-9]+([.][0-9]*)?$/.test(input))) onerror(e, '不是有效的数字'); break;
                    case 'integer': if (item[1] == 'true' && input.length > 0 && !(/^-?[0-9]+[0-9]*$/.test(input))) onerror(e, '不是有效的整数'); break;
                    case 'url': if (item[1] == 'true' && input.length > 0 && !(/^https?:\/\/[\w\-_]+(\.[\w\-_]+)/.test(input))) onerror(e, 'URL地址不正确'); break;
                    case 'minlength': if (input.length < parseInt(item[1])) onerror(e, '最少需输入' + item[1] + '个字符'); break;
                    case 'maxlength': if (input.length > parseInt(item[1])) onerror(e, '最多可以输入' + item[1] + '个字符'); break;
                    case 'min': if (input.length > 0 && parseFloat(input) < parseFloat(item[1])) onerror(e, '输入值小于' + item[1]); break;
                    case 'max': if (input.length > 0 && parseFloat(input) > parseFloat(item[1])) onerror(e, '输入值大于' + item[1]); break;
                }
                if (!item_success) {
                    if (!$(e).data('__check')) {
                        if ($(e).is('select')) $(e).on('change', function (evt) { oncheck(evt.target); });
                        else if ($(e).is('input,textarea')) $(e).on('input', function (evt) { oncheck(evt.target); });
                        $(e).data('__check', true);
                    }
                    break;
                }
            }
        }
    };
	var settings = $.extend(defaults, options);
    var onerror = settings.onerror;
    this.find('input,select,textarea').each(function (i, e) { oncheck(e); });
    return success;
}

function guid() {
    return 'xxxxxxxxxxxx4xxxyxxxxxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random() * 16 | 0;
        var v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}