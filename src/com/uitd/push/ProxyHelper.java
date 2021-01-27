package com.uitd.push;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class ProxyHelper implements IoFutureListener<WriteFuture> {
	private IProxyStorage storage = null;
	private NioSocketAcceptor acceptor = null;
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private Map<String, IoSession> sessions = new HashMap<String, IoSession>();
	private Map<IoFuture, String> futures = new ConcurrentHashMap<IoFuture, String>();

	/**
	 * 默认构造函数
	 */
	public ProxyHelper(IProxyStorage storage) {
		this.storage = storage;
		ResourceBundle resource = ResourceBundle.getBundle("config/configure");
		int port = Integer.parseInt(resource.getString("push.port"));
		try {
			acceptor = new NioSocketAcceptor();
			acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ProxyDataCodecFactory()));
			acceptor.setHandler(new ProxyDataHandler(this));
			acceptor.bind(new InetSocketAddress(port));
			System.out.println(String.format("Proxy listen started (port:%d)...", port));
		} catch (Exception e) {
			e.printStackTrace();
			acceptor = null;
		}
	}

	/**
	 * 客户端上线
	 * 
	 * @param session
	 */
	public void online(IoSession session) {
		String id = (String) session.getAttribute("id");
		if (id != null) {
			lock.writeLock().lock();
			IoSession older = sessions.get(id);
			sessions.put(id, session);
			lock.writeLock().unlock();
			if (older != null) {
				ProxyData message = new ProxyData();
				message.setId(0);
				message.setCommand(Command.OFFLINE);
				message.setTimestamp(System.currentTimeMillis());
				message.setControl(0);
				message.setExpired(0);
				message.setSource("0");
				message.setTarget(id);
				message.setData("{}");
				older.resumeWrite();
				older.write(message);
				older.closeOnFlush();
			}
			sendCachedMessage(session);
			System.out.println(String.format("client %s connected", id));
		}
	}

	/**
	 * 客户端下线
	 * 
	 * @param session
	 */
	public void offline(IoSession session) {
		String id = (String) session.getAttribute("id");
		if (id != null) {
			if (sessions.get(id) == session) {
				lock.writeLock().lock();
				sessions.remove(id);
				lock.writeLock().unlock();
				System.out.println(String.format("client %s disconnected", id));
			} else {
				System.out.println(String.format("client %s previous connection closed", id));
			}
		}
	}

	/**
	 * 发送消息
	 * 
	 * @param data
	 * @return
	 */
	public boolean sendMessage(ProxyData data) {
		lock.readLock().lock();
		IoSession session = sessions.get(data.getTarget());
		lock.readLock().unlock();
		String id = null;
		if (data.getExpired() > 0) {
			id = UUID.randomUUID().toString().replace("-", "");
			storage.insert(id, data);
		}
		if (session != null && session.isConnected()) {
			session.resumeWrite();
			WriteFuture future = session.write(data);
			futures.put(future, id);
			future.addListener(this);
			return true;
		}
		return false;
	}

	/**
	 * 发送已缓存的消息
	 * 
	 * @param session
	 */
	private void sendCachedMessage(IoSession session) {
		long timestamp = System.currentTimeMillis();
		List<String> invalid = new ArrayList<String>();
		String id = (String) session.getAttribute("id");
		List<Pair<String, ProxyData>> list = storage.list(id, 100);
		for (int i = 0; i < list.size(); i++) {
			ProxyData message = list.get(i).getValue();
			if (!checkMessage(timestamp, message)) {
				invalid.add(list.get(i).getKey());
				continue;
			}
			session.resumeWrite();
			WriteFuture future = session.write(message);
			futures.put(future, list.get(i).getKey());
			future.addListener(this);
		}
		while (list.size() == 100) {
			list = storage.list(id, 100);
			for (int i = 0; i < list.size(); i++) {
				ProxyData message = list.get(i).getValue();
				if (!checkMessage(timestamp, message)) {
					invalid.add(list.get(i).getKey());
					continue;
				}
				session.resumeWrite();
				WriteFuture future = session.write(message);
				futures.put(future, list.get(i).getKey());
				future.addListener(this);
			}
		}
		storage.delete(invalid.toArray(new String[0]));
	}

	/**
	 * 检测消息是否合理
	 * 
	 * @param timestamp
	 * @param message
	 * @return
	 */
	private boolean checkMessage(long timestamp, ProxyData message) {
		return timestamp < message.getExpired() * 1000 + message.getTimestamp();
	}

	/**
	 * 发送操作完成
	 */
	@Override
	public void operationComplete(WriteFuture future) {
		if (future.isWritten() && futures.containsKey(future)) {
			String id = futures.get(future);
			if (id != null) {
				storage.delete(id);
			}
		}
	}
}