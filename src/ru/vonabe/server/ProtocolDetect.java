package ru.vonabe.server;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

public class ProtocolDetect extends ByteToMessageDecoder {

	final ProtocolDetectInterface detect;

	public ProtocolDetect(ProtocolDetectInterface interf) {
		this.detect = interf;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		ByteBuf buf = in.readBytes(in.readableBytes());
		String parse = buf.toString(CharsetUtil.UTF_8);

		if(parse.contains(Start.PROTO_WEBSOCKET)){
			this.detect.websocket(ctx.pipeline());
		}else if(parse.contains(Start.PROTO_GET)){
			this.detect.http_get(ctx.pipeline());
		}else if(parse.contains(Start.PROTO_POST)){
			this.detect.http_post(ctx.pipeline());
		}else{
			this.detect.socket(ctx.pipeline());
		}
		out.add(buf);
	}

	interface ProtocolDetectInterface {
		void websocket(ChannelPipeline pipeline);
		void http_get(ChannelPipeline pipeline);
		void http_post(ChannelPipeline pipeline);
		void socket(ChannelPipeline pipeline);
	}

}
