package com.uitd.push;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.uitd.util.Common;

public class ProxyHelper implements IoFutureListener<WriteFuture> {
	private IProxyStorage storage = null;
	private NioSocketAcceptor acceptor = null;
	private ReentrantReadWriteLock lock = null;
	private Map<String, IoSession> sessions = null;
	private Map<IoFuture, ProxyData> futures = null;

	/**
	 * 默认构造函数
	 */
	public ProxyHelper(IProxyStorage storage) {
		this.storage = storage;
		lock = new ReentrantReadWriteLock();
		sessions = new HashMap<String, IoSession>();
		futures = new ConcurrentHashMap<IoFuture, ProxyData>();
		try {
			ResourceBundle resource = ResourceBundle.getBundle("config/configure");
			int port = Integer.parseInt(resource.getString("push.port"));
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
		if (session != null && session.isConnected()) {
			session.resumeWrite();
			WriteFuture future = session.write(data);
			if (data.getExpired() > 0) {
				futures.put(future, data);
				future.addListener(this);
			}
			return true;
		} else if (data.getExpired() > 0) {
			storage.put(data);
		}
		return false;
	}

	/**
	 * 发送操作完成
	 * 
	 * @param future
	 */
	@Override
	public void operationComplete(WriteFuture future) {
		if (futures.containsKey(future)) {
			if (!future.isWritten()) {
				storage.put(futures.get(future));
			}
			futures.remove(future);
		}
	}

	/**
	 * 发送已缓存的消息
	 * 
	 * @param session
	 */
	private void sendCachedMessage(IoSession session) {
		final int limit = 100;
		String time = Common.toString(new Date(), null);
		String target = (String) session.getAttribute("id");
		while (true) {
			List<ProxyData> list = storage.get(target, time, limit);
			for (int i = 0; i < list.size(); i++) {
				ProxyData data = list.get(i);
				if (checkMessage(data)) {
					session.resumeWrite();
					WriteFuture future = session.write(data);
					futures.put(future, data);
					future.addListener(this);
				}
			}
			if (list.size() != limit) {
				break;
			}
		}
	}

	/**
	 * 检测消息是否有效
	 * 
	 * @param message
	 * @return
	 */
	private boolean checkMessage(ProxyData message) {
		long timestamp = System.currentTimeMillis();
		return timestamp < message.getExpired() * 1000 + message.getTimestamp();
	}
}