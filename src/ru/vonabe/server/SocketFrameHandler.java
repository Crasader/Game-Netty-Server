package ru.vonabe.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

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
		Start.queue.addSessionToProcess(msg);
//		ReferenceCountUtil.release(msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
		cause.printStackTrace();
	}

}
