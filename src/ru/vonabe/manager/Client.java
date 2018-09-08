package ru.vonabe.manager;

import java.util.UUID;

import org.json.simple.JSONObject;

import io.netty.channel.ChannelHandlerContext;
import ru.vonabe.handler.Renderer;
import ru.vonabe.packet.PacketWriter;
import ru.vonabe.packet.PoolPacketWriter;
import ru.vonabe.player.Player;

public class Client implements Renderer {

    final public String uuid = UUID.randomUUID().toString();
    public boolean auto = false, verify = false;
    public Player player = null;

    private ChannelHandlerContext ctx = null;
    private int verify_attemp = 0;

    public Client(ChannelHandlerContext channel) {
	ctx = channel;
	ClientManager.registerClient(this);
	VerificationManager.onVerify(uuid);
    }

    public void verification(boolean verify) {
	this.verify = verify;
	if (!this.verify)
	    this.verify_attemp++;
	if (this.verify_attemp >= 2)
	    this.close("error", "Исчерпаны попытки верификации.");
	System.out.println("verification success");
    }

    public void setAuto(boolean auto, String name) {
	if (!verify)
	    return;
	if (auto) {

	    if (BattleManager.contains(name)) {
		this.player = BattleManager.getPlayer(name);
		this.player.setClient(this);
	    } else {
		this.player = new Player(name, this);
		this.player.init();
	    }

	    boolean reg_map = MapManager.registration(uuid, String.valueOf(player.getMap()));

	    PacketWriter writer_map = MapManager.getMap(String.valueOf(player.getMap())).getMapInfo();

	    PacketWriter writer = PoolPacketWriter.getWriter();
	    JSONObject data = writer.getData();
	    data.put("login", name);
	    data.put("x", this.player.getPosition().x);
	    data.put("y", this.player.getPosition().y);
	    data.put("id_map", this.player.getMap());
	    data.put("mylord", this.player.getMyinfo());
	    data.put("map_state", writer_map.getData());

	    // JSONArray players = new JSONArray();
	    // Collection<Client> clients =
	    // MapManager.getMap(String.valueOf(player.getMap())).getClients();
	    // Iterator<Client> it_clients = clients.iterator();
	    // while (it_clients.hasNext()) {
	    // Client client = it_clients.next();
	    // players.add(client.player.getPacketPublic());
	    // }
	    // data.put("players", players);

	    writer.getObject().put("action", "auto");
	    writer.getObject().put("data", data);

	    write(writer);

	}
	this.auto = auto;
    }

    public String getIP() {
	return ctx.channel().remoteAddress().toString();
    }

    public void close(String type, String message) {

	PacketWriter writer = PoolPacketWriter.getWriter();

	JSONObject object = writer.getObject();
	JSONObject data = writer.getData();
	data.put("message", message);
	object.put("action", type);
	object.put("data", data);

	this.write(writer);

	this.close();
	// packetWriter.clear();
    }

    public void close() {
	if (this.player != null)
	    this.player.save();
	MapManager.unregistration(uuid);
	ClientManager.unregisterClient(this);
	this.ctx.disconnect();
	this.ctx.close();
    }

    public void write(PacketWriter object) {
	this.ctx.writeAndFlush(object);
    }

    @Override
    public void render(float delta) {
	if (auto) {
	    if (player.isBattle()) {

	    }
	}
    }

}
