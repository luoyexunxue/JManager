<%@ page language="java" import="java.util.*,com.uitd.web.model.*" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
String lblDateTime = (String)request.getAttribute("lblDateTime");
String lblUsername = (String)request.getAttribute("lblUsername");
String lblName = (String)request.getAttribute("lblName");
String imgAvatar = (String)request.getAttribute("imgAvatar");
String lblTicket = (String)request.getAttribute("lblTicket");
@SuppressWarnings("unchecked")
List<Login> loginRecord = (List<Login>)request.getAttribute("loginRecord");
Boolean lblHasMore = (Boolean)request.getAttribute("lblHasMore");
%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="Application/plugins/bootstrap-3.3.0/css/bootstrap.min.css" rel="stylesheet" />
    <link href="Application/plugins/material-design-iconic-font-2.2.0/css/material-design-iconic-font.min.css" rel="stylesheet"/>
    <link href="Application/plugins/jquery-confirm/jquery-confirm.min.css" rel="stylesheet" />
    <link href="Application/plugins/waves-0.7.5/waves.min.css" rel="stylesheet"/>
    <link href="Application/app/css/manage.css" rel="stylesheet" />
    <script src="Application/plugins/jquery.1.12.4.min.js"></script>
    <script src="Application/plugins/jquery-confirm/jquery-confirm.min.js"></script>
    <script src="Application/plugins/jquery.cookie.js"></script>
    <script src="Application/plugins/waves-0.7.5/waves.min.js"></script>
    <title>用户信息</title>
    <script>
        $(function () {
            Waves.displayEffect();
            $(document).on('focus', 'input[type="password"]', function () {
                $(this).parent().find('label').addClass('active');
            }).on('blur', 'input[type="password"]', function () {
                if ($(this).val() == '') $(this).parent().find('label').removeClass('active');
            }).on('change', 'input[type="file"]', function (evt) {
                var reader = new FileReader();
                reader.onload = function (file) {
                    var model = {
                        username: $('#lblUsername').text(),
                        avatar: file.target.result
                    };
                    $.ajax({
                        url: 'api/user/avatar',
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify(model),
                        success: function (json) {
                            if (json.success) window.location.reload();
                            else $.alert({ content: json.message, title: false, animateFromElement: false });
                        },
                        error: function (err) {
                            $.alert({ content: err.responseText, title: false, animateFromElement: false });
                        },
                        beforeSend: function (XHR) {
                            XHR.setRequestHeader('Authorization', 'BasicAuth ' + $('#lblTicket').html());
                        }
                    });
                };
                reader.readAsDataURL(evt.target.files[0]);
            });
        });
        function changePassword() {
            $.confirm({
                title: '修改密码',
                content: $('#changeDialog').html(),
                buttons: {
                    confirm: {
                        text: '确认',
                        btnClass: 'waves-effect waves-button',
                        action: function () {
                            var model = {
                                username: $('#lblUsername').text(),
                                password: this.$content.find('#password').val(),
                                newpwd: this.$content.find('#newpwd').val()
                            };
                            $.ajax({
                                url: 'api/user/change',
                                type: 'POST',
                                contentType: 'application/json',
                                data: JSON.stringify(model),
                                success: function (json) {
                                    $.alert({ content: json.message, title: false, animateFromElement: false, backgroundDismiss: true });
                                    if (json.success) {
                                        parent.ticket = json.data;
                                        if ($.cookie('ticket')) $.cookie('ticket', json.data, { expires: 7, path: '/' });
                                    }
                                },
                                error: function (err) {
                                    $.alert({ content: err.responseText, title: false, animateFromElement: false });
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
    </script>
</head>
<body style="padding:15px 30px;">
    <form id="form1">
        <div>
            <p><h4>您好，今天是 <i style="color:#c00"><span id="lblDateTime">${lblDateTime }</span></i></h4><p /><br />
            <c:if test="${lblUsername!=null && lblUsername!=''}">
            <table>
                <tr>
                    <td rowspan="3" style="width:80px;">
                        <label for="upload" style="cursor:pointer;">
                            <img id="imgAvatar" Width="64px" Height="64px" src="${imgAvatar }" />
                        </label>
                    </td>
                    <td><b>登录账户</b>：<span id="lblUsername">${lblUsername }</span></td>
                </tr>
                <tr><td><b>用户姓名</b>：<span id="lblName">${lblName }</span></td></tr>
                <tr><td><b>修改密码</b>：<a href="javascript:changePassword();"><i class="zmdi zmdi-edit"></i>修改</a></td></tr>
            </table>
            <br /><br />
            <span id="lblTicket" style="display:none;" >${lblTicket }</span>
            <input type="file" id="upload" accept="image/*" style="display:none;" />
            <p>历史登录记录</p>
            <ul>
            	<c:forEach var="item" items="${loginRecord}" >
            		<c:if test="${item.success}"><li style="color:#827f02;">${item.time } 在${item.address }登录成功 【<i>${item.ip }</i>】 ${item.platform }</li></c:if>
            		<c:if test="${!item.success}"><li style="color:#ff2323;">${item.time } 在${item.address }登录失败 【<i>${item.ip }</i>】 ${item.platform }</li></c:if>
            	</c:forEach>
            	<c:if test="${lblHasMore }"><span style="color:#acacac;">------仅显示最近10条记录------</span></c:if>
            </ul>
            </c:if>
        </div>
    </form>
    <div id="changeDialog" class="crudDialog" hidden>
        <form>
            <div class="form-group">
                <label for="password">原密码</label>
                <input id="password" type="password" class="form-control" />
            </div>
            <div class="form-group">
                <label for="newpwd">新密码</label>
                <input id="newpwd" type="password" class="form-control" />
            </div>
        </form>
    </div>
</body>
</html>