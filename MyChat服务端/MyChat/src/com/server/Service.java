package com.server;

import java.io.Serializable;
import java.net.InetSocketAddress;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class Service {

	public void start() {
		IoAcceptor acceptor = new NioSocketAcceptor();
		// 添加日志过滤器
		acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
		acceptor.setHandler(new DemoServerHandler());
		acceptor.getSessionConfig().setReadBufferSize(2048);
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
		try {
			acceptor.bind(new InetSocketAddress(9898));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class DemoServerHandler extends IoHandlerAdapter implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		// 服务器与客户端创建连接
		@Override
		public void sessionCreated(IoSession session) throws Exception {
			System.out.println("服务器与客户端创建连接...");
			super.sessionCreated(session);
		}

		// 消息的接收处理
		@Override
		public void messageReceived(IoSession session, Object message) throws Exception {

		}

		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
			System.out.println(cause.toString());
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception {
			System.out.println("sessionClosed");
		}

		@Override
		public void messageSent(IoSession session, Object message) throws Exception {
			System.out.println("messageSent:" + message.toString());
		}

	}

	public static void main(String[] a) {
		new Service().start();
	}
}
