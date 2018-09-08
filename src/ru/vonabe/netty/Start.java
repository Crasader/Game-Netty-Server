package ru.vonabe.netty;

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
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
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
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import ru.vonabe.handler.GameLogicHandler;
import ru.vonabe.handler.PacketHandler;
import ru.vonabe.manager.MapManager;
import ru.vonabe.netty.ProtocolDetect.ProtocolDetectInterface;
import ru.vonabe.packet.PacketWriter;
import ru.vonabe.packet.PoolPacketWriter;

public class Start implements ProtocolDetectInterface {

    public static String PROTO_WEBSOCKET = "/websocket", PROTO_GET = "GET /", PROTO_POST = "POST";

    public static String HAND_PROTO_VERIFY = "protocol_verify", HAND_PROTO_BUSINESS = "business_logic";

    public static final boolean SSL = System.getProperty("ssl") != null;
    public static final int PORT = Integer.parseInt(System.getProperty("port", SSL ? "8443" : "8080"));
    public static SslContext sslcontext = null;
    public static GameLogicHandler gamelogic = null;
    public static PacketHandler packetHandler = null;

    public Start() {

	gamelogic = new GameLogicHandler(30);
	packetHandler = new PacketHandler(4);

	EventLoopGroup boss = new NioEventLoopGroup(1);
	EventLoopGroup worker = new NioEventLoopGroup(4);

	ServerBootstrap http_bootstrap = createHttpServer(boss, worker);
	ServerBootstrap socket_bootstrap = createSocketServer(boss, worker);
	ServerBootstrap websocket_bootstrap = createWebsocketServer(boss, worker);

	try {
	    Channel ch = http_bootstrap.bind(8080).sync().channel();
	    Channel ch0 = socket_bootstrap.bind(8081).sync().channel();
	    Channel ch1 = websocket_bootstrap.bind(8082).sync().channel();

	    System.out.println("Http Server " + (SSL ? "https" : "http") + "://127.0.0.1:" + 8080 + '/');

	    System.out.println("Socket Server " + (SSL ? "https" : "http") + "://127.0.0.1:" + 8081 + '/');

	    System.out.println("Websocket Server " + (SSL ? "https" : "http") + "://127.0.0.1:" + 8082 + '/');

	    MapManager.getMap("0");
	    // ResultSet set =
	    // DataBaseManager.getDB().query(String.format("select Bots.* from
	    // Bots where map='%1s';", 0));
	    // try {
	    // while (set.next()) {
	    // System.out.println(set.getString("login"));
	    // }
	    // } catch (SQLException e) {
	    // e.printStackTrace();
	    // }

	    // try {
	    // CreateBot.createBot("Bot0", 1, 0, 200f, 300f);
	    // CreateBot.createBot("Bot1", 2, 0, 300f, 300f);
	    // CreateBot.createBot("Bot2", 3, 0, 400f, 300f);
	    // CreateBot.createBot("Bot3", 4, 0, 500f, 300f);
	    // } catch (Exception e) {
	    // // TODO Auto-generated catch block
	    // e.printStackTrace();
	    // }

	    ch.closeFuture().sync();
	    ch0.closeFuture().sync();
	    ch1.closeFuture().sync();
	} catch (InterruptedException e) {
	    e.printStackTrace();
	} finally {
	    System.out.println("finally worker group shutdown");
	    boss.shutdownGracefully();
	    worker.shutdownGracefully();
	}

    }

    private ServerBootstrap createHttpServer(EventLoopGroup... boss) {
	ServerBootstrap bootstrap = new ServerBootstrap();
	bootstrap.group(boss[0], boss[1]).channel(NioServerSocketChannel.class)
		// .handler(new LoggingHandler(LogLevel.INFO))
		// .childHandler(new WebSocketServerInitializer(sslcontext))
		.childHandler(new ChannelInitializer<SocketChannel>() {
		    @Override
		    protected void initChannel(SocketChannel ch) throws Exception {
			ChannelPipeline pipeline = ch.pipeline();
			pipeline.addLast(new HttpObjectAggregator(65536)).addLast(new HttpServerCodec()).addLast(new ChannelInboundHandlerAdapter() {
			    @Override
			    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				System.out.println("read http: " + msg);

				String message = "<head>" + "<title>GameServer</title>"
					+ "<p> Скачать клиент можно по адресу: <a href=\"https://play.google.com/store/apps/details?id=ru.vonabe.customhotkeys\">https://play.google.com</a> </p>"
					+ "</head>";

				byte[] bytes = message.getBytes("UTF8");
				ByteBuf bufsend = Unpooled.wrappedBuffer(bytes);

				FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, bufsend);
				response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
				response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

				pipeline.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
				ReferenceCountUtil.release(msg);
			    }

			    @Override
			    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
				System.out.println("remove get");
			    }

			});
			// pipeline.remove(HAND_PROTO_VERIFY);
		    }
		}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
	return bootstrap;
    }

    private ServerBootstrap createWebsocketServer(EventLoopGroup... boss) {
	ServerBootstrap bootstrap = new ServerBootstrap();
	bootstrap.group(boss[0], boss[1]).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
	    @Override
	    protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new HttpRequestDecoder()).addLast(new HttpObjectAggregator(65536)).addLast(new HttpResponseEncoder())
			.addLast(new ChannelOutboundHandlerAdapter() {
			    @Override
			    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
				// TODO Auto-generated method stub
				if (msg instanceof PacketWriter) {
				    super.write(ctx, new TextWebSocketFrame(msg.toString()), promise);
				    PoolPacketWriter.free((PacketWriter) msg);
				} else {
				    super.write(ctx, msg, promise);
				}
			    }
			}).addLast(new WebSocketServerProtocolHandler(PROTO_WEBSOCKET)).addLast(HAND_PROTO_BUSINESS, new WebSocketFrameHandler());
		// pipeline.remove(HAND_PROTO_VERIFY);
	    }
	}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
	return bootstrap;
    }

    private ServerBootstrap createSocketServer(EventLoopGroup... boss) {
	ServerBootstrap bootstrap = new ServerBootstrap();
	bootstrap.group(boss[0], boss[1]).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
	    @Override
	    protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter())).addLast(new StringDecoder(CharsetUtil.UTF_8))
			.addLast(new StringEncoder(CharsetUtil.UTF_8)).addLast(new ChannelOutboundHandlerAdapter() {
			    @Override
			    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
				if (msg instanceof PacketWriter) {
				    super.write(ctx, msg.toString() + "\r\n", promise);
				    PoolPacketWriter.free((PacketWriter) msg);
				} else {
				    super.write(ctx, msg, promise);
				}

			    }
			}).addLast(HAND_PROTO_BUSINESS, new SocketFrameHandler());
		// pipeline.remove(HAND_PROTO_VERIFY);
	    }
	}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
	return bootstrap;
    }

    private ChannelHandler protocolDetect() {
	return new ProtocolDetect(this);
    }

    @Override
    public void websocket(ChannelPipeline pipeline) {
	if (pipeline != null) {
	    pipeline.addLast(new HttpRequestDecoder()).addLast(new HttpObjectAggregator(65536)).addLast(new HttpResponseEncoder())
		    .addLast(new WebSocketServerProtocolHandler(PROTO_WEBSOCKET)).addLast(HAND_PROTO_BUSINESS, new WebSocketFrameHandler());
	    pipeline.remove(HAND_PROTO_VERIFY);
	} else {
	    System.out.println("websocket pipline null");
	}
    }

    @Override
    public void socket(ChannelPipeline pipeline) {
	if (pipeline != null) {
	    pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter())).addLast(new StringDecoder(CharsetUtil.UTF_8))
		    .addLast(new StringEncoder(CharsetUtil.UTF_8)).addLast(HAND_PROTO_BUSINESS, new SocketFrameHandler());
	    pipeline.remove(HAND_PROTO_VERIFY);
	} else {
	    System.out.println("socket pipline null");
	}
    }

    @Override
    public void http_get(ChannelPipeline pipeline) {
	if (pipeline != null) {
	    pipeline.addLast(new HttpObjectAggregator(65536)).addLast(new HttpServerCodec()).addLast(new ChannelInboundHandlerAdapter() {
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		    String message = "<head>" + "<title>GameServer</title>"
			    + "<p> Скачать клиент можно по адресу: <a href=\"https://play.google.com/store/apps/details?id=ru.vonabe.customhotkeys\">https://play.google.com</a> </p>"
			    + "</head>";

		    byte[] bytes = message.getBytes("UTF8");
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
	} else {
	    System.out.println("http_get pipline null");
	}
    }

    @Override
    public void http_post(ChannelPipeline pipeline) {
	// TODO Auto-generated method stub

    }

    public void enabledSSL() {
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
