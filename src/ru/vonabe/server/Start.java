package ru.vonabe.server;

import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public class Start {

	static final boolean SSL = System.getProperty("ssl") != null;
	public static final int PORT = Integer.parseInt(System.getProperty("port", SSL ? "8443" : "8080"));
	public static SslContext sslcontext = null;

	public static void main(String[] args) throws SSLException {

		EventLoopGroup boss = new NioEventLoopGroup(1);
		EventLoopGroup worker = new NioEventLoopGroup(4);

		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap
			.group(boss, worker)
			.channel(NioServerSocketChannel.class)
//			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new WebSocketServerInitializer(sslcontext))
			.option(ChannelOption.SO_BACKLOG, 124)
	        .childOption(ChannelOption.SO_KEEPALIVE, true);

		try {
			Channel ch = bootstrap.bind(PORT).sync().channel();
			System.out.println("Open your web browser and navigate to " + (SSL? "https" : "http") + "://127.0.0.1:" + PORT + '/');
			ch.closeFuture().sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			System.out.println("finally worker group shutdown");
			boss.shutdownGracefully();
			worker.shutdownGracefully();
		}
	}

	public void enabledSSL(){
		try {
			SelfSignedCertificate ssc = new SelfSignedCertificate();
			sslcontext = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SSLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
