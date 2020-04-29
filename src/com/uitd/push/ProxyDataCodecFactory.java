package com.uitd.push;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class ProxyDataCodecFactory implements ProtocolCodecFactory {
	private ProtocolEncoder encoder;
	private ProtocolDecoder decoder;

	public ProxyDataCodecFactory() {
		encoder = new DataRequestEncoder();
		decoder = new DataRequestDecoder();
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder;
	}

	private class DataRequestEncoder extends ProtocolEncoderAdapter {
		@Override
		public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
			ProxyData request = (ProxyData) message;
			byte[] source = request.getSource().getBytes("utf-8");
			byte[] target = request.getTarget().getBytes("utf-8");
			byte[] data = request.getData().getBytes("utf-8");
			int length = data.length + 88;
			IoBuffer buffer = IoBuffer.allocate(length + 5, false);
			buffer.put((byte) 0xAA);
			buffer.putInt(length);
			buffer.putInt(request.getId());
			buffer.putInt(request.getCommand());
			buffer.putInt(request.getControl());
			buffer.putInt(request.getExpired());
			buffer.putLong(request.getTimestamp());
			buffer.put(source, 0, Math.min(32, source.length));
			for (int i = source.length; i < 32; i++) {
				buffer.put((byte) 0x00);
			}
			buffer.put(target, 0, Math.min(32, target.length));
			for (int i = target.length; i < 32; i++) {
				buffer.put((byte) 0x00);
			}
			buffer.put(data);
			buffer.flip();
			out.write(buffer);
		}
	}

	private class DataRequestDecoder extends CumulativeProtocolDecoder {
		@Override
		public boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
			while (in.remaining() >= 5 && in.get() != (byte) 0xAA) {
			}
			if (in.remaining() < 4) {
				return false;
			}
			int position = in.position();
			int length = in.getInt();
			if (length > in.remaining()) {
				in.position(position);
				return false;
			}
			if (length < 88) {
				in.clear();
				return false;
			}
			byte[] source = new byte[32];
			byte[] target = new byte[32];
			byte[] data = new byte[length - 88];
			ProxyData request = new ProxyData();
			request.setId(in.getInt());
			request.setCommand(in.getInt());
			request.setControl(in.getInt());
			request.setExpired(in.getInt());
			request.setTimestamp(in.getLong());
			in.get(source);
			in.get(target);
			in.get(data);
			request.setSource(new String(source, "utf-8").trim());
			request.setTarget(new String(target, "utf-8").trim());
			request.setData(new String(data, "utf-8"));
			out.write(request);
			return true;
		}
	}
}