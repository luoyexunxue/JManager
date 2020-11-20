$(function () {
    $('#iframe_home_html').load('data/home.html?' + Math.random());
    $(document).on(device.mobile() ? 'touchstart' : 'click', '#guide', function () {
        $(this).toggleClass('toggled');
        $('#sidebar').toggleClass('toggled');
    });
    $(document).on('click', '.sub-menu a', function () {
        $(this).next().slideToggle(200);
        $(this).parent().toggleClass('toggled');
    });
    $(document).on('click', '.s-profile a', function () {
        $(this).next().slideToggle(200);
        $(this).parent().toggleClass('toggled');
    });
    Waves.displayEffect();
    $('#sidebar').mCustomScrollbar({
        theme: 'minimal-dark',
        scrollInertia: 100,
        axis: 'yx',
        mouseWheel: {
            enable: true,
            axis: 'y',
            preventDefault: true
        }
    });
    $(document).on('click', '.content_tab li', function () {
        $('.content_tab li').removeClass('cur');
        $(this).addClass('cur');
        $('.iframe').removeClass('cur');
        $('#iframe_' + $(this).data('index')).addClass('cur');
        var marginLeft = ($('#tabs').css('marginLeft').replace('px', ''));
        if ($(this).position().left < marginLeft) {
            var left = $('.content_tab>ul').scrollLeft() + $(this).position().left - marginLeft;
            $('.content_tab>ul').animate({ scrollLeft: left }, 200, function () { initScrollState(); });
        }
        if (($(this).position().left + $(this).width() - marginLeft) > document.getElementById('tabs').clientWidth) {
            var left = $('.content_tab>ul').scrollLeft() + (($(this).position().left + $(this).width() - marginLeft) - document.getElementById('tabs').clientWidth);
            $('.content_tab>ul').animate({ scrollLeft: left }, 200, function () { initScrollState(); });
        }
    });
    $(document).on('click', '.tab_left>a', function () {
        $('.content_tab>ul').animate({ scrollLeft: $('.content_tab>ul').scrollLeft() - 300 }, 200, function () { initScrollState(); });
    });
    $(document).on('click', '.tab_right>a', function () {
        $('.content_tab>ul').animate({ scrollLeft: $('.content_tab>ul').scrollLeft() + 300 }, 200, function () { initScrollState(); });
    });
    new BootstrapMenu('.tabs li', {
        fetchElementData: function (item) { return item; },
        actionsGroups: [
			['close', 'refresh'],
			['closeOther', 'closeAll'],
			['closeRight', 'closeLeft']
        ],
        actions: {
            close: {
                name: '关闭',
                iconClass: 'zmdi zmdi-close',
                onClick: function (item) { closeTab($(item)); }
            },
            closeOther: {
                name: '关闭其他',
                iconClass: 'zmdi zmdi-arrow-split',
                onClick: function (item) {
                    var index = $(item).data('index');
                    $('.content_tab li').each(function () {
                        if ($(this).data('index') != index) { closeTab($(this)); }
                    });
                }
            },
            closeAll: {
                name: '关闭全部',
                iconClass: 'zmdi zmdi-swap',
                onClick: function () {
                    $('.content_tab li').each(function () { closeTab($(this)); });
                }
            },
            closeRight: {
                name: '关闭右侧所有',
                iconClass: 'zmdi zmdi-arrow-right',
                onClick: function (item) {
                    var index = $(item).data('index');
                    $($('.content_tab li').toArray().reverse()).each(function () {
                        if ($(this).data('index') != index) closeTab($(this)); else return false;
                    });
                }
            },
            closeLeft: {
                name: '关闭左侧所有',
                iconClass: 'zmdi zmdi-arrow-left',
                onClick: function (item) {
                    var index = $(item).data('index');
                    $('.content_tab li').each(function () {
                        if ($(this).data('index') != index) closeTab($(this)); else return false;
                    });
                }
            },
            refresh: {
                name: '刷新',
                iconClass: 'zmdi zmdi-refresh',
                onClick: function (item) {
                    var index = $(item).data('index');
                    var $iframe = $('#iframe_' + index).find('iframe');
                    $iframe.attr('src', $iframe.attr('src'));
                }
            }
        }
    });
});

function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]); return null;
}

function changeFrameHeight(ifm) {
    ifm.height = document.documentElement.clientHeight - 98;
}

function resizeFrameHeight() {
    $('.tab_iframe').css('height', document.documentElement.clientHeight - 98);
    $('md-tab-content').css('left', '0');
}

function addTab(title, url) {
    var index = url.replace(/\./g, '_').replace(/\//g, '_').replace(/:/g, '_').replace(/\?/g, '_').replace(/,/g, '_').replace(/=/g, '_').replace(/&/g, '_').replace(/%/g, '_');
    if ($('#tab_' + index).length == 0) {
        $('.content_tab li').removeClass('cur');
        var tab = '<li id="tab_' + index + '" data-index="' + index + '" class="cur"><a class="waves-effect waves-light">' + title + '</a></li>';
        $('.content_tab>ul').append(tab);
        $('.iframe').removeClass('cur');
        var iframe = '<div id="iframe_' + index + '" class="iframe cur"><iframe class="tab_iframe" src="' + url + '" width="100%" frameborder="0" scrolling="auto" onload="changeFrameHeight(this)"></iframe></div>';
        $('.content_main').append(iframe);
        initScrollShow();
        $('.content_tab>ul').animate({ scrollLeft: document.getElementById('tabs').scrollWidth - document.getElementById('tabs').clientWidth }, 200, function () {
            initScrollState();
        });
    } else {
        $('#tab_' + index).trigger('click');
    }
    $('#guide').trigger(device.mobile() ? 'touchstart' : 'click');
}

function closeTab($item) {
    var closeable = $item.data('closeable');
    if (closeable != false) {
        if ($item.hasClass('cur')) {
            $item.prev().trigger('click');
        }
        var index = $item.data('index');
        $('#iframe_' + index).remove();
        $item.remove();
    }
    initScrollShow();
}

function initScrollShow() {
    if (document.getElementById('tabs').scrollWidth > document.getElementById('tabs').clientWidth) {
        $('.content_tab').addClass('scroll');
    } else {
        $('.content_tab').removeClass('scroll');
    }
}

function initScrollState() {
    if ($('.content_tab>ul').scrollLeft() == 0) {
        $('.tab_left>a').removeClass('active');
    } else {
        $('.tab_left>a').addClass('active');
    }
    if (($('.content_tab>ul').scrollLeft() + document.getElementById('tabs').clientWidth) >= document.getElementById('tabs').scrollWidth) {
        $('.tab_right>a').removeClass('active');
    } else {
        $('.tab_right>a').addClass('active');
    }
}

function callFrame(name, func, param) {
    var iframe = $('#iframe_' + name + '_html').find('iframe');
    if (iframe.length > 0) {
        var target = null;
        var child = iframe[0].contentWindow.application;
        eval('target = child.' + func);
        if (typeof target == 'function') {
            target(param);
            return true;
        }
    }
}

function searchResult(keyword, menus) {
    var result = [];
    for (var i = 0; i < menus.length; i++) {
        var submenus = menus[i].childer;
        if (submenus == undefined) {
            if (menus[i].title.indexOf(keyword) >= 0) result.push({ title: menus[i].title, url: menus[i].url });
        } else {
            for (var j = 0; j < submenus.length; j++) {
                var subsubmenus = submenus[j].childer;
                if (subsubmenus == undefined) {
                    if (submenus[j].title.indexOf(keyword) >= 0) result.push({ title: submenus[j].title, url: submenus[j].url });
                } else {
                    for (var k = 0; k < subsubmenus.length; k++) {
                        if (subsubmenus[k].title.indexOf(keyword) >= 0) result.push({ title: subsubmenus[k].title, url: subsubmenus[k].url });
                    }
                }
            }
        }
    }
    if ("代码生成器".indexOf(keyword) >= 0) result.push({ title: '代码生成器', url: '../Generator/index.html' });
    var html = '';
    if (result.length == 0) html = '没有找到相关的结果!';
    else result.forEach(function (e) {
        html += '<div><a href="javascript:window.searchWindow.close();addTab(\'';
        html += e.title + '\',\'' + e.url + '\');">' + e.title;
        html += '</a></div>';
    });
    window.searchWindow = $.confirm({
        title: false,
        content: '<div>' + html + '</div>',
        backgroundDismiss: true,
        columnClass: 'col-md-2 col-md-offset-5',
        buttons: { ok: { text: '确定', btnClass: 'waves-effect waves-button' } }
    });
}

function fullPage() {
    if ($.util.supportsFullScreen) {
        if ($.util.isFullScreen()) {
            $.util.cancelFullScreen();
        } else {
            $.util.requestFullScreen();
        }
    } else {
        alert("当前浏览器不支持全屏 API，请更换至最新的 Chrome/Firefox/Safari 浏览器或通过 F11 快捷键进行操作。");
    }
}

function logout() {
    $.removeCookie('ticket', { path: '/' });
    window.location.replace('../Default?logout');
}

window.ticket = getUrlParam('ticket');
window.username = getUrlParam('user');
window.onresize = function () {
    resizeFrameHeight();
    initScrollShow();
    initScrollState();
}