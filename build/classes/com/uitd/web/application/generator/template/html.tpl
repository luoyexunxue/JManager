<!DOCTYPE HTML>
<html lang="zh-cn">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>{{title}}</title>
    <script src="app/js/crud.js"></script>
    <script>
	{{#repeat columns}}
	{{#if columns.date}}
        function formatter_{{columns.name}}(value) { return new Date(value).format('yyyy-MM-dd'); }
	{{#if}}
	{{#if columns.datetime}}
        function formatter_{{columns.name}}(value) { return new Date(value).format('yyyy-MM-dd HH:mm:ss'); }
	{{#if}}
	{{#if columns.blob-file}}
        function formatter_{{columns.name}}(value) { return '[file]'; }
	{{#if}}
	{{#if columns.blob-img}}
        function formatter_{{columns.name}}(value) { return '<img style="height:32px;" src="data:image/jpeg;base64,' + value + '"></img>'; }
        function formatter_{{columns.name}}_input(value) { return '[image]'; }
	{{#if}}
	{{#repeat}}
        window.tableParam = {
            url: '../api/{{nameL}}/',
            columns: [
                { field: 'state', checkbox: true },
                { field: 'id', title: 'ID', halign: 'center', visible: false },
				{{#repeat columns}}
				{{#if columns.date columns.datetime columns.blob-img}}
                { field: '{{columns.name}}', title: '{{columns.desc}}', sortable: true, halign: 'left', formatter: formatter_{{columns.name}} },
				{{#else}}
                { field: '{{columns.name}}', title: '{{columns.desc}}', sortable: true, halign: 'left' },
				{{#if}}
				{{#repeat}}
                { field: 'action', title: '操作', halign: 'center', align: 'center', events: 'actionEvents', width: 64, formatter: 'actionFormatter{{actionFormatter}}' }
            ]
        };
    </script>
</head>
<body>
    <div id="main">
        <div id="toolbar">
		{{#if insert}}
            <a class="waves-effect waves-button" href="javascript:;" onclick="createAction()"><i class="zmdi zmdi-plus"></i>新增</a>
		{{#if}}
		{{#if update}}
            <a class="waves-effect waves-button" href="javascript:;" onclick="updateAction()"><i class="zmdi zmdi-edit"></i>编辑</a>
		{{#if}}
		{{#if delete}}
            <a class="waves-effect waves-button" href="javascript:;" onclick="deleteAction()"><i class="zmdi zmdi-close"></i>删除</a>
		{{#if}}
        </div>
        <table id="table"></table>
    </div>
    <div id="createDialog" class="crudDialog" hidden>
        <form>
		{{#repeat columns}}
            <div class="form-group">
                <label for="input{{columns.#}}">{{columns.desc}}</label>
                <input id="input{{columns.#}}" type="text" class="form-control" name="{{columns.name}}"{{columns.extra}} />
            </div>
		{{#repeat}}
        </form>
    </div>
</body>
</html>