package ru.vonabe.logic;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import ru.vonabe.packet.Packet;

public class GameLogicHandler implements Runnable {

	private final BlockingQueue<Packet> sessionQueue;
	private final ExecutorService threadPool;
	private int threadPoolSize;
	private int thread_id = 0;

	public GameLogicHandler(int threadPoolSize) {
		this.threadPoolSize = threadPoolSize;
		this.threadPool = Executors.newFixedThreadPool(threadPoolSize);
		this.sessionQueue = new LinkedBlockingQueue<>();

		initThreadPool();
	}

	private void initThreadPool() {
		for (int i = 0; i < this.threadPoolSize; i++) {
			this.threadPool.execute(this);
		}
	}

	public void addSessionToProcess(Packet session) {
		if (session != null) {
			this.sessionQueue.add(session);
		}
	}

	@Override
	public void run() {
		int id = ++thread_id;
		System.out.println("Create Pool Thread - " + id);

		while (true) {
			try {
				Packet packet = this.sessionQueue.take();
				packet.handle();

//				JSONObject obj = packet.getData().getData();

//				System.out.println("ThreadID " + id + ": " + packet);

				// packet = session.getReadPacketQueue().take();

				// data = packet.getData();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
