package ru.vonabe.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class ProtocolDetect extends SimpleChannelInboundHandler<Object> {

    final ProtocolDetectInterface detect;

    public ProtocolDetect(ProtocolDetectInterface interf) {
	this.detect = interf;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
	System.out.println("ProtocolDetect Added - ");
	// ctx.pipeline().fireChannelRead("bebebe");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
	// TODO Auto-generated method stub
	if (msg instanceof FullHttpRequest) {
	    System.out.println("read protocol detect http\n" + msg);
	} else if (msg instanceof WebSocketFrame) {
	    System.out.println("read protocol detect websocket\n" + msg);
	} else {
	    System.out.println("read protocol detect socket \n" + msg);
	}
    }
    // @Override
    // protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object>
    // out) throws Exception {
    //
    // System.out.println("ProtocolDetect - " + in);
    //
    // ByteBuf buf = in.readBytes(in.readableBytes());
    // String parse = buf.toString(CharsetUtil.UTF_8);
    //
    // if (parse.contains(Start.PROTO_WEBSOCKET)) {
    // this.detect.websocket(ctx.pipeline());
    // } else if (parse.contains(Start.PROTO_GET)) {
    // this.detect.http_get(ctx.pipeline());
    // } else if (parse.contains(Start.PROTO_POST)) {
    // this.detect.http_post(ctx.pipeline());
    // } else {
    // this.detect.socket(ctx.pipeline());
    // }
    // out.add(buf);
    // }

    interface ProtocolDetectInterface {
	void websocket(ChannelPipeline pipeline);

	void socket(ChannelPipeline pipeline);

	void http_get(ChannelPipeline pipeline);

	void http_post(ChannelPipeline pipeline);
    }

}
