package {{package}}.storage;

import java.util.List;
import java.util.Map;

import {{package}}.model.{{name}};

public interface {{name}}DAL {
	/**
	 * 新增
	 * 
	 * @param model
	 * @return
	 */
	public boolean insert({{name}} model);

	/**
	 * 修改
	 * 
	 * @param model
	 * @return
	 */
	public boolean update({{name}} model);

	/**
	 * 删除
	 * 
	 * @param model
	 * @return
	 */
	public boolean delete(String[] ids);

	/**
	 * 选择单个
	 * 
	 * @param id
	 * @return
	 */
	public {{name}} select(String id);

	/**
	 * 获取列表项
	 * 
	 * @param filter
	 * @return
	 */
	public List<{{name}}> list({{name}} filter);

	/**
	 * 分页获取列表
	 * 
	 * @param param
	 * @return
	 */
	public List<{{name}}> page(Map<String, Object> param);

	/**
	 * 分页获取列表计数
	 * 
	 * @param param
	 * @return
	 */
	public int page_count(Map<String, Object> param);
}