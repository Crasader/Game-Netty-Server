package ru.vonabe.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.vonabe.manager.Client;
import ru.vonabe.manager.ClientManager;
import ru.vonabe.manager.PacketManager;
import ru.vonabe.packet.FastData;
import ru.vonabe.packet.Packet;

public class SocketFrameHandler extends SimpleChannelInboundHandler<String> {

    private Client client = null;

    // @Override
    // public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
    // throws Exception {
    // System.out.println("SocketFrameHandler: triggered");
    // if (this.client == null) {
    // this.client = new Client(ctx);
    // }
    // }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
	System.out.println("SocketFrameHandler: added");
	if (this.client == null) {
	    this.client = new Client(ctx);
	    // ClientManager.registerClient(this.client);
	    // VerificationManager.onVerify(this.client.uuid);
	    System.out.println(ctx.channel().remoteAddress().toString());
	}
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
	System.out.println("SocketFrameHandler: removed");
	if (this.client != null) {
	    this.client.close();
	    this.client = null;
	}
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
	// System.out.println("SocketFrameHandler channelRead:
	// *******\n"+msg+"\n*******");
	// String message = ((ByteBuf)msg).toString();
	// System.out.println("SocketFrameHandler channelRead: "+msg);
	// System.out.println(msg);
	FastData fast = new FastData(client.uuid, msg);
	if (fast.valid()) {
	    Packet packet = PacketManager.getPacket(fast.getPacketType());
	    packet.setData(fast);

	    Start.packetHandler.addSessionToProcess(packet);
	} else {
	    System.out.println("error parse message: " + msg);
	}
	// System.out.println("msg: " + msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	ctx.close();
	// cause.printStackTrace();
	client.close();
	ClientManager.unregisterClient(this.client);
	client = null;
    }

}
