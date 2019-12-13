package {{package}}.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import {{package}}.application.common.BooleanResult;
import {{package}}.application.common.ListResult;
import {{package}}.model.{{name}};
import {{package}}.storage.{{name}}DAL;

@Component
public class {{name}}Service {
	@Autowired
	private {{name}}DAL dal;

	/**
	 * 新增
	 * 
	 * @param model
	 * @return
	 */
	public BooleanResult<String> insert({{name}} model) {
		if (dal.insert(model))
			return new BooleanResult<String>(true, "新增成功");
		return new BooleanResult<String>(false, "新增失败");
	}

	/**
	 * 修改
	 * 
	 * @param model
	 * @return
	 */
	public BooleanResult<String> update({{name}} model) {
		if (dal.update(model))
			return new BooleanResult<String>(true, "修改成功");
		return new BooleanResult<String>(false, "修改失败");
	}

	/**
	 * 删除
	 * 
	 * @param ids
	 * @return
	 */
	public BooleanResult<String> delete(String[] ids) {
		if (dal.delete(ids))
			return new BooleanResult<String>(true, "删除成功");
		return new BooleanResult<String>(false, "删除失败");
	}

	/**
	 * 选择单个
	 * 
	 * @param id
	 * @return
	 */
	public {{name}} select(String id) {
		return dal.select(id);
	}

	/**
	 * 获取列表项
	 * 
	 * @param filter
	 * @return
	 */
	public List<{{name}}> list({{name}} filter) {
		return dal.list(filter);
	}

	/**
	 * 分页列表
	 * 
	 * @param offset
	 * @param limit
	 * @param sort
	 * @param order
	 * @param search
	 * @return
	 */
	public ListResult<{{name}}> list(int offset, int limit, String sort, String order, String search) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("offset", offset);
		param.put("limit", limit);
		param.put("sort", sort);
		param.put("order", order);
		param.put("{{search}}", search);
		ListResult<{{name}}> result = new ListResult<{{name}}>();
		result.setRows(dal.page(param));
		result.setTotal(dal.page_count(param));
		return result;
	}
}