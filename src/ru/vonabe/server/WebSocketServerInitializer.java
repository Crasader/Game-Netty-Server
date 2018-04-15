package ru.vonabe.server;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
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
import io.netty.util.CharsetUtil;

public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {

	private static final String WEBSOCKET_PATH = "/websocket";
	private final SslContext sslCtx;
	// ===========================================================
    // 1. define a separate thread pool to execute handlers with
    //    slow business logic. e.g database operation
    // ===========================================================
//    final public static EventExecutorGroup group = new DefaultEventExecutorGroup(1500); //thread pool of 1500

	public WebSocketServerInitializer(SslContext sslCtx) {
		this.sslCtx = sslCtx;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		   ChannelPipeline pipeline = ch.pipeline();
		   if (sslCtx != null) {
			   pipeline.addLast(sslCtx.newHandler(ch.alloc()));
		   }

//		   pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 2, 0, 2));
		   ByteToMessageDecoder preHandlerProtocol = new ByteToMessageDecoder() {
				@Override
				protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
					ByteBuf buf = in.readBytes(in.readableBytes());
					String parse = buf.toString(CharsetUtil.UTF_8);

					if(parse.contains("/websocket")){
						pipeline
							.addLast(new HttpRequestDecoder())
							.addLast(new HttpObjectAggregator(65536))
							.addLast(new HttpResponseEncoder())
							.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH))
							.addLast("business_logic", new WebSocketFrameHandler());
							pipeline.remove("protocol_verify");
							out.add(buf);
					}else if(parse.contains("GET /")){
//						if(!parse.contains("GET /favicon.ico")){
//							System.out.println("\nGET Request ************ ");
							pipeline
								.addLast(new HttpObjectAggregator(65536))
								.addLast(new HttpServerCodec())
								.addLast(new ChannelInboundHandlerAdapter(){
									@Override
									public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
										System.out.println("READ Http ************ >\n"+msg+"\nEND Http ************\n");

										String message =
												"<head>"+
												"<title>LordyServer</title>"+
												"<p> Hello World! </p>"+
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
							pipeline.remove("protocol_verify");
							out.add(buf);
//						}else{}
//						pipeline.channel().close();
					}else if(parse.contains("POST")){
						buf.clear();
						in.clear();
						System.out.println("POST Request");
						pipeline.channel().close();
					}else{
						System.out.println("Socket> "+parse);
						pipeline.remove("protocol_verify");
						pipeline
							.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()))
							.addLast(new StringDecoder(CharsetUtil.UTF_8))
							.addLast(new StringEncoder(CharsetUtil.UTF_8))
							.addLast("business_logic", new SocketFrameHandler());
						out.add(buf);
					}

				}
		   };
		   pipeline.addLast("protocol_verify", preHandlerProtocol);


	}


	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.err.println(getClass().getName()+": "+cause.getMessage());
	}

}
