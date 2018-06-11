package ru.vonabe.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.vonabe.packet.FastPacket;
import ru.vonabe.packet.Packet;
import ru.vonabe.packet.PacketManager;

public class SocketFrameHandler extends SimpleChannelInboundHandler<String> {

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		System.out.println("SocketFrameHandler: added");
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		System.out.println("SocketFrameHandler: removed");
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
//		System.out.println("SocketFrameHandler channelRead: *******\n"+msg+"\n*******");
//		String message = ((ByteBuf)msg).toString();
//		System.out.println("SocketFrameHandler channelRead: "+msg);

		FastPacket fast = new FastPacket(msg);
		Packet packet = PacketManager.getPacket(fast.getAction());
		packet.setChannel(ctx);
		packet.setData(fast);

		Start.gamelogic.addSessionToProcess(packet);

//		ReferenceCountUtil.release(msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
		cause.printStackTrace();
	}

}
