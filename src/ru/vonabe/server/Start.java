package ru.vonabe.server;

import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.CharsetUtil;
import ru.vonabe.server.ProtocolDetect.ProtocolDetectInterface;

public class Start implements ProtocolDetectInterface{

	public static String PROTO_WEBSOCKET = "/websocket", PROTO_GET = "GET /", PROTO_POST = "POST";

	public static String HAND_PROTO_VERIFY = "protocol_verify", HAND_PROTO_BUSINESS = "business_logic";

	public static final boolean SSL = System.getProperty("ssl") != null;
	public static final int PORT = Integer.parseInt(System.getProperty("port", SSL ? "8443" : "8080"));
	public static SslContext sslcontext = null;
	public static ReadQueueHandler queue = null;

	public Start(){

		queue = new ReadQueueHandler(4);

		EventLoopGroup boss = new NioEventLoopGroup(1);
		EventLoopGroup worker = new NioEventLoopGroup(4);

		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap
			.group(boss, worker)
			.channel(NioServerSocketChannel.class)
//			.handler(new LoggingHandler(LogLevel.INFO))
//			.childHandler(new WebSocketServerInitializer(sslcontext))
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(HAND_PROTO_VERIFY, protocolDetect());
				}
			})
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

	@Override
	public void websocket(ChannelPipeline pipeline) {
		if(pipeline!=null){
			pipeline
				.addLast(new HttpRequestDecoder())
				.addLast(new HttpObjectAggregator(65536))
				.addLast(new HttpResponseEncoder())
				.addLast(new WebSocketServerProtocolHandler(PROTO_WEBSOCKET))
				.addLast(HAND_PROTO_BUSINESS, new WebSocketFrameHandler());
			pipeline.remove(HAND_PROTO_VERIFY);
		}else{
			System.out.println("websocket pipline null");
		}
	}

	@Override
	public void socket(ChannelPipeline pipeline) {
		if(pipeline!=null){
			pipeline
				.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()))
				.addLast(new StringDecoder(CharsetUtil.UTF_8))
				.addLast(new StringEncoder(CharsetUtil.UTF_8))
				.addLast(HAND_PROTO_BUSINESS, new SocketFrameHandler());
			pipeline.remove(HAND_PROTO_VERIFY);
		}else{
			System.out.println("socket pipline null");
		}
	}

	@Override
	public void http_get(ChannelPipeline pipeline) {
		if(pipeline!=null){
			pipeline
			.addLast(new HttpObjectAggregator(65536))
			.addLast(new HttpServerCodec())
			.addLast(new ChannelInboundHandlerAdapter(){
				@Override
				public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
					String message =
							"<head>"+
							"<title>GameServer</title>"+
							"<p> Hello Player! </p>"+
							"</head>";

					byte[] bytes = message.getBytes();
					ByteBuf bufsend = Unpooled.wrappedBuffer(bytes);

					FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, bufsend);
					response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
					response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

					pipeline.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
				}

				@Override
				public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
					System.out.println("remove get");
				}

			});
			pipeline.remove(HAND_PROTO_VERIFY);
		}else{
			System.out.println("http_get pipline null");
		}
	}

	@Override
	public void http_post(ChannelPipeline pipeline) {
		// TODO Auto-generated method stub

	}


	private ChannelHandler protocolDetect(){
		return new ProtocolDetect(this);
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

	public static void main(String[] args) throws SSLException {
		new Start();
	}




}
