package ru.vonabe.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

//	final private Logger logger = LoggerFactory.getLogger(WebSocketFrameHandler.class);

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		System.out.println("WebSocketFrameHandler: added");
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		System.out.println("WebSocketFrameHandler: removed");
	}

	@Override
	protected void channelRead0(ChannelHandlerContext arg0, WebSocketFrame frame) throws Exception {
		if(frame instanceof TextWebSocketFrame){
			String request = ((TextWebSocketFrame)frame).text();
//			arg0.channel().writeAndFlush(new TextWebSocketFrame(request.toUpperCase(Locale.US)));
//			System.out.println("WebSocketFrameHandler channelRead0: "+request);
			Start.queue.addSessionToProcess(request);
		}else{
			String message = "unsupported frame type: " + frame.getClass().getName();
			throw new UnsupportedOperationException(message);
		}
	}

}