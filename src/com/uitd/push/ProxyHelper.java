package com.uitd.push;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class ProxyHelper {
	private NioSocketAcceptor acceptor = null;
	private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private static Map<String, IoSession> sessions = new HashMap<String, IoSession>();

	/**
	 * 默认构造函数
	 */
	public ProxyHelper() {
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
			session.write(data);
			return true;
		} else if (data.getExpired() > 0) {
			// TODO: cache message
		}
		return false;
	}
}