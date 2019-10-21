package com.uitd.web.service;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.uitd.util.Common;
import com.uitd.web.application.WebApiAuthFilter;
import com.uitd.web.application.common.AddrHelper;
import com.uitd.web.application.common.BooleanResult;
import com.uitd.web.application.common.IPRegion;
import com.uitd.web.application.common.ListResult;
import com.uitd.web.model.Login;
import com.uitd.web.model.User;
import com.uitd.web.storage.UserDAL;

@Component
public class UserService {
	@Autowired
	private UserDAL dal;

	/**
	 * 获取用户
	 * 
	 * @param id
	 * @return
	 */
	public User item(String id) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", id);
		param.put("username", null);
		return dal.item(param);
	}

	/**
	 * 登录验证
	 * 
	 * @param username
	 * @param password
	 * @param login
	 * @return
	 */
	public BooleanResult<String> login(String username, String password, Login login) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", null);
		param.put("username", username);
		User model = dal.item(param);
		if (model == null) {
			return new BooleanResult<String>(false, "用户不存在");
		}
		BooleanResult<String> result = null;
		ListResult<Login> history = history(model.getId(), 3);
		if (history.getTotal() >= 3 && history.getRows().get(0).isSuccess() == false
				&& history.getRows().get(1).isSuccess() == false && history.getRows().get(2).isSuccess() == false
				&& new Date().getTime() - 60000 < Common.getDate(history.getRows().get(0).getTime(), null).getTime()) {
			return new BooleanResult<String>(false, "您已多次输入错误密码，请一分钟之后再试！");
		}
		IPRegion region = AddrHelper.getRegion(login.getIp());
		login.setUser(model);
		login.setAddress(region != null ? region.getAddress() : (login.getIp().equals("::1") ? "本机" : "未知区域"));
		login.setIsp(region != null ? region.getIsp() : (login.getIp().equals("::1") ? "本机" : "未知"));
		String encrypt = Common.sha256(password);
		if (model.getPassword().equals(encrypt)) {
			result = new BooleanResult<String>(true, "登录成功");
			result.setData(WebApiAuthFilter.encryptString(model.getId() + "&" + model.getPassword() + "&"
					+ Common.toString(new Date(), "yyyy-MM-dd HH:mm:ss")));
			WebApiAuthFilter.updateAuthorize(model.getId(), model.getPassword());
		} else {
			result = new BooleanResult<String>(false, "用户名或密码错误");
		}
		login.setSuccess(result.isSuccess());
		dal.insertHistory(login);
		return result;
	}

	/**
	 * 修改密码
	 * 
	 * @param username
	 * @param password
	 * @param newpwd
	 * @return
	 */
	public BooleanResult<String> change(String username, String password, String newpwd) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", null);
		param.put("username", username);
		User model = dal.item(param);
		if (model == null)
			return new BooleanResult<String>(false, "用户不存在");
		String encrypt = Common.sha256(password);
		if (!model.getPassword().equals(encrypt))
			return new BooleanResult<String>(false, "用户名或密码错误");
		model.setPassword(Common.sha256(newpwd));
		if (dal.update(model) > 0) {
			BooleanResult<String> result = new BooleanResult<String>(true, "修改成功，请重新登录");
			result.setData(WebApiAuthFilter.encryptString(
					model.getId() + "&" + model.getPassword() + "&" + Common.toString(new Date(), null)));
			WebApiAuthFilter.updateAuthorize(model.getId(), model.getPassword());
			return result;
		}
		return new BooleanResult<String>(false, "修改失败");
	}

	/**
	 * 修改头像
	 * 
	 * @param username
	 * @param avatar
	 * @return
	 */
	public BooleanResult<String> avatar(String username, String avatar) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", null);
		param.put("username", username);
		User model = dal.item(param);
		if (model == null)
			return new BooleanResult<String>(false, "用户不存在");
		try {
			byte[] imageData = Base64.getDecoder().decode(avatar.split(",", -1)[1]);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			stream.write(imageData, 0, imageData.length);
			Image image = ImageIO.read(new ByteArrayInputStream(stream.toByteArray()));
			Image photo = new BufferedImage(64, 64, BufferedImage.TYPE_4BYTE_ABGR);
			Graphics g = photo.getGraphics();
			g.drawImage(image, 0, 0, 64, 64, null);
			stream.reset();
			ImageIO.write((RenderedImage) photo, "png", stream);
			model.setAvatar(stream.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (dal.update(model) > 0)
			return new BooleanResult<String>(true, "修改成功");
		return new BooleanResult<String>(false, "修改失败");
	}

	/**
	 * 查询登录历史
	 * 
	 * @param id
	 * @param limit
	 * @return
	 */
	public ListResult<Login> history(String id, int limit) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", id);
		param.put("limit", limit);
		ListResult<Login> result = new ListResult<Login>();
		result.setRows(dal.pageHistory(param));
		result.setTotal(dal.pageHistory_count(param));
		return result;
	}
}