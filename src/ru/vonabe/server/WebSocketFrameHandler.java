package ru.vonabe.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import ru.vonabe.client.Client;
import ru.vonabe.client.ClientManager;
import ru.vonabe.packet.FastPacket;
import ru.vonabe.packet.Packet;
import ru.vonabe.packet.PacketManager;

public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

//	final private Logger logger = LoggerFactory.getLogger(WebSocketFrameHandler.class);

	private Client client = null;

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		System.out.println("WebSocketFrameHandler: added");
		if(this.client == null){
			this.client = new Client(ctx);
			ClientManager.registerClient(this.client);
		}
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		System.out.println("WebSocketFrameHandler: removed");
		if(this.client != null){
			this.client.close();
			this.client = null;
			ClientManager.unregisterClient(this.client);
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
		if(frame instanceof TextWebSocketFrame){
			String request = ((TextWebSocketFrame)frame).text();
//			arg0.channel().writeAndFlush(new TextWebSocketFrame(request.toUpperCase(Locale.US)));
//			System.out.println("WebSocketFrameHandler channelRead0: "+request);

			FastPacket fast = new FastPacket(request);
			Packet packet = PacketManager.getPacket(fast.getAction());
			packet.setChannel(ctx);
			packet.setData(fast);
			packet.setClient(this.client);

			Start.gamelogic.addSessionToProcess(packet);

		}else{
			String message = "unsupported frame type: " + frame.getClass().getName();
			throw new UnsupportedOperationException(message);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
		cause.printStackTrace();
		ClientManager.unregisterClient(this.client);
//		this.client.close();
	}

}
