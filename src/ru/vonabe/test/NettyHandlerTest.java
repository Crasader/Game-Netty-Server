package ru.vonabe.test;

import java.io.File;
import java.nio.file.Files;

import ru.vonabe.handler.GameLogicHandler;
import ru.vonabe.handler.PacketHandler;
import ru.vonabe.manager.Client;
import ru.vonabe.manager.ClientManager;
import ru.vonabe.manager.PacketManager;
import ru.vonabe.packet.FastData;
import ru.vonabe.packet.Packet;

public class NettyHandlerTest {

    private PacketHandler packetHandler = new PacketHandler(4);
    private GameLogicHandler gameLogicHandler = new GameLogicHandler(30);

    public NettyHandlerTest() {

	String[] packets = { "{\"action\":\"auto\",\"data\":{\"login\":\"vonabe\",\"password\":\"qwerty\"}}",
		"{\"action\":\"reg\",\"data\":{\"login\":\"vonabe\",\"password\":\"qwerty\",\"email\":\"vonabe.dev@gmail.com\"}}",
		"{\"action\":\"verify\",\"data\":{\"code\":\"vonabe\"}}",
		"{\"action\":\"move\",\"data\":{\"move\":\"BOT\"}}" };

	Client client = new Client(null);
	ClientManager.registerClient(client);

	FastData fastData = new FastData(client.uuid, packets[1]);
	createPacket(fastData);

	try {
	    Thread.sleep(100);
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	fastData = new FastData(client.uuid, packets[3]);
	createPacket(fastData);

    }

    public void createPacket(FastData fastData) {

	Packet packet = PacketManager.getPacket(fastData.getPacketType());
	packet.setData(fastData);

	this.packetHandler.addSessionToProcess(packet);
    }

    public static void main(String[] args) {
	// new NettyHandlerTest();
	// File dir = new File("C:\\Users\\vonabe\\Desktop\\звуки");
	// File[] files = dir.listFiles();
	// for (File file : files) {
	// if (!file.getName().endsWith(".wav")) {
	// System.out.println(file.renameTo(new File(file.getAbsolutePath() +
	// ".wav")));
	// }
	// }
    }

}
