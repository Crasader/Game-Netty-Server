package ru.vonabe.handler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import ru.vonabe.packet.Packet;

public class PacketHandler implements Runnable {

    private final BlockingQueue<Packet> sessionQueue;
    private final ExecutorService threadPool;
    private int threadPoolSize;
    private int thread_id = 0;

    // private static PacketHandler instance = null;

    public PacketHandler(int threadPoolSize) {
	this.threadPoolSize = threadPoolSize;
	this.threadPool = Executors.newFixedThreadPool(threadPoolSize);
	this.sessionQueue = new LinkedBlockingQueue<>();

	initThreadPool();
    }

    // public void setThreadPoolSize(int threadPoolSize) {
    // this.threadPoolSize = threadPoolSize;
    // }

    // public static PacketHandler getInstance() {
    // return (instance == null) ? instance = new PacketHandler(thread_id) :
    // instance;
    // }

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
	    Packet packet;
	    try {
		packet = this.sessionQueue.take();
		packet.handler();
	    } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }

}
