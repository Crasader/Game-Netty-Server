package ru.vonabe.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import ru.vonabe.manager.Client;
import ru.vonabe.manager.ClientManager;
import ru.vonabe.manager.PacketManager;
import ru.vonabe.manager.VerificationManager;
import ru.vonabe.packet.FastData;
import ru.vonabe.packet.Packet;

public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    // final private Logger logger =
    // LoggerFactory.getLogger(WebSocketFrameHandler.class);

    private Client client = null;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
	System.out.println("WebSocketFrameHandler: added");
	if (this.client == null) {
	    this.client = new Client(ctx);
	    System.out.println("registration client: " + ClientManager.registerClient(this.client));
	    VerificationManager.onVerify(this.client.uuid);
	    // ctx.write(new TextWebSocketFrame("жопа".toUpperCase(Locale.US)));
	    // ctx.flush();
	} else {
	    // super.userEventTriggered(ctx, evt);
	}
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
	System.out.println("WebSocketFrameHandler: removed");
	if (this.client != null) {
	    this.client.close();
	    ClientManager.unregisterClient(this.client);
	    this.client = null;
	}
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
	if (frame instanceof TextWebSocketFrame) {
	    String request = ((TextWebSocketFrame) frame).text();
	    // ctx.channel().writeAndFlush(new
	    // TextWebSocketFrame(request.toUpperCase(Locale.US)));
	    // System.out.println("WebSocketFrameHandler channelRead0:" + ctx);
	    // PacketWriter packetWriter = new PacketWriter();
	    // JSONObject data = packetWriter.getData();
	    // data.put("login", "fsdf");
	    // data.put("x", 12);
	    // data.put("y", 323);
	    // data.put("id_map", 0);
	    // packetWriter.getObject().put("action", "auto");
	    // packetWriter.getObject().put("data", data);
	    // client.write(packetWriter);
	    // packetWriter.clear();

	    FastData fast = new FastData(client.uuid, request);
	    if (fast.valid()) {
		Packet packet = PacketManager.getPacket(fast.getPacketType());
		packet.setData(fast);

		Start.packetHandler.addSessionToProcess(packet);
	    } else {
		System.out.println("error parse message");
	    }
	    // System.out.println("read WebSocket: " + request);
	} else {
	    String message = "unsupported frame type: " + frame.getClass().getName();
	    throw new UnsupportedOperationException(message);
	}
	// ReferenceCountUtil.release(frame);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	ctx.close();
	cause.printStackTrace();
	ClientManager.unregisterClient(this.client);
	// this.client.close();
    }

}
