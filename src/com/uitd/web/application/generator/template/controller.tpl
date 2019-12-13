package {{package}}.controller;

{{#if haveBase64}}
import java.util.Base64;
{{#if}}
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

{{#if haveStringUtils}}
import org.apache.commons.lang3.StringUtils;
{{#if}}
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import {{package}}.application.WebApiAuth;
import com.uitd.web.application.WebApiController;
import {{package}}.application.common.BooleanResult;
import {{package}}.application.common.ListResult;
import {{package}}.model.{{name}};
import {{package}}.service.{{name}}Service;

@WebApiAuth
@Component
@Path("{{nameL}}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class {{name}}Controller extends WebApiController {
	@Autowired
	private {{name}}Service service;

	/**
	 * 列表
	 * 
	 * @param param
	 * @return
	 */
	@POST
	@Path("list")
	public ListResult<{{name}}> list(@RequestBody Map<String, Object> param) {
		int offset = (int) param.get("offset");
		int limit = (int) param.get("limit");
		String sort = (String) param.get("sort");
		String order = (String) param.get("order");
		String search = (String) param.get("search");
		return service.list(offset, limit, sort, order, search);
	}
	{{#if insert}}

	/**
	 * 新增
	 * 
	 * @param param
	 * @return
	 */
	@POST
	@Path("insert")
	public BooleanResult<String> insert(@RequestBody Map<String, Object> param) {
		{{name}} model = new {{name}}();
		model.setId(UUID.randomUUID().toString().replace("-", ""));
		{{#repeat columns}}
		{{#if columns.isBlob}}
		String {{columns.name}} = (String) param.get("{{columns.name}}");
		String[] {{columns.name}}_data = StringUtils.isEmpty({{columns.name}}) ? null : {{columns.name}}.split(",", -1);
		model.set{{columns.nameU}}({{columns.name}}_data != null ? Base64.getDecoder().decode({{columns.name}}_data[{{columns.name}}_data.length - 1]) : null);
		{{#else}}
		{{#if columns.isInt}}
		model.set{{columns.nameU}}(Integer.parseInt((String) param.get("{{columns.name}}")));
		{{#if}}
		{{#if columns.isFloat}}
		model.set{{columns.nameU}}(Float.parseFloat((String) param.get("{{columns.name}}")));
		{{#if}}
		{{#if columns.isString}}
		model.set{{columns.nameU}}((String) param.get("{{columns.name}}"));
		{{#if}}
		{{#if}}
		{{#repeat}}
		return service.insert(model);
	}
	{{#if}}
	{{#if update}}

	/**
	 * 修改
	 * 
	 * @param param
	 * @return
	 */
	@POST
	@Path("update")
	public BooleanResult<String> update(@RequestBody Map<String, Object> param) {
		{{name}} model = new {{name}}();
		model.setId((String) param.get("id"));
		{{#repeat columns}}
		{{#if columns.isBlob}}
		String {{columns.name}} = (String) param.get("{{columns.name}}");
		String[] {{columns.name}}_data = StringUtils.isEmpty({{columns.name}}) ? null : {{columns.name}}.split(",", -1);
		model.set{{columns.nameU}}({{columns.name}}_data != null ? Base64.getDecoder().decode({{columns.name}}_data[{{columns.name}}_data.length - 1]) : null);
		{{#else}}
		{{#if columns.isInt}}
		model.set{{columns.nameU}}(Integer.parseInt((String) param.get("{{columns.name}}")));
		{{#if}}
		{{#if columns.isFloat}}
		model.set{{columns.nameU}}(Float.parseFloat((String) param.get("{{columns.name}}")));
		{{#if}}
		{{#if columns.isString}}
		model.set{{columns.nameU}}((String) param.get("{{columns.name}}"));
		{{#if}}
		{{#if}}
		{{#repeat}}
		return service.update(model);
	}
	{{#if}}
	{{#if delete}}

	/**
	 * 删除
	 * 
	 * @param param
	 * @return
	 */
	@POST
	@Path("delete")
	public BooleanResult<String> delete(@RequestBody List<String> param) {
		int count = param.size();
		String[] ids = new String[count];
		for (int i = 0; i < count; i++) {
			ids[i] = param.get(i);
		}
		return service.delete(ids);
	}
	{{#if}}
	{{#if table}}

	/**
	 * 列表项
	 * 
	 * @param param
	 * @return
	 */
	@POST
	@Path("table")
	public List<{{name}}> table(@RequestBody {{name}} param) {
		return service.list(param);
	}
	{{#if}}
}