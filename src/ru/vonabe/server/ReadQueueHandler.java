package ru.vonabe.server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ReadQueueHandler implements Runnable {

	private final BlockingQueue<Object> sessionQueue;
	private final ExecutorService threadPool;
	private int threadPoolSize;
	private int thread_id = 0;

	public ReadQueueHandler(int threadPoolSize) {
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

	// ���������� ����� � ������� �� ���������
	public void addSessionToProcess(Object session) {
		if (session != null) {
			this.sessionQueue.add(session);
		}
	}

	@Override
	public void run() {
		int id = ++thread_id;
		System.out.println("Create Pool Thread - "+id);

		while (true) {
			// �������� ��������� ������ ��� ���������
			try {
				Object session = this.sessionQueue.take();

				System.out.println("ThreadID "+id+": "+session);

				// ����� ���������� ��������� ������� ���������
				// �������� �����
				// packet = session.getReadPacketQueue().take();

				// ����� �������� � ������������ ������ �� ����
				// data = packet.getData();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
