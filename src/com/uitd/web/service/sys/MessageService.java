package com.uitd.web.service.sys;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.uitd.push.IProxyStorage;
import com.uitd.push.ProxyData;
import com.uitd.util.Common;
import com.uitd.web.model.sys.Message;
import com.uitd.web.storage.sys.MessageDAL;

@Component
public class MessageService implements IProxyStorage {
	@Autowired
	private MessageDAL dal;

	/**
	 * 新增
	 */
	@Override
	public void put(ProxyData data) {
		try {
			Message model = new Message();
			model.setId(UUID.randomUUID().toString().replace("-", ""));
			model.setCreatetime(Common.toString(new Date(), null));
			model.setMessage_id(data.getId());
			model.setMessage_command(data.getCommand());
			model.setMessage_control(data.getControl());
			model.setMessage_expired(data.getExpired());
			model.setMessage_timestamp(data.getTimestamp());
			model.setMessage_source(data.getSource());
			model.setMessage_target(data.getTarget());
			model.setMessage_data(data.getData().getBytes("utf-8"));
			dal.insert(model);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 获取
	 */
	@Override
	public List<ProxyData> get(String id, String time, int limit) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("createtime", time);
		param.put("limit", limit);
		param.put("message_target", id);
		List<String> deleted = new ArrayList<String>();
		List<ProxyData> result = new ArrayList<ProxyData>();
		List<Message> list = dal.list(param);
		for (int i = 0; i < list.size(); i++) {
			try {
				Message item = list.get(i);
				ProxyData data = new ProxyData();
				data.setId(item.getMessage_id());
				data.setCommand(item.getMessage_command());
				data.setControl(item.getMessage_control());
				data.setExpired(item.getMessage_expired());
				data.setTimestamp(item.getMessage_timestamp());
				data.setSource(item.getMessage_source());
				data.setTarget(item.getMessage_target());
				data.setData(new String(item.getMessage_data(), "utf-8"));
				deleted.add(item.getId());
				result.add(data);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		if (deleted.size() > 0) {
			dal.delete(deleted.toArray(new String[0]));
		}
		return result;
	}
}