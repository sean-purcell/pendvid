package nutella.pendvid;

import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AsyncLoader extends Thread {
	private int numFrames;
	private ConcurrentLinkedQueue<BufferedImage> imgq;
	private String dir;
	
	private volatile boolean stop;
	
	public AsyncLoader(int numFrames, ConcurrentLinkedQueue<BufferedImage> imgq, String dir) {
		this.numFrames = numFrames;
		this.imgq = imgq;
		this.dir = dir;
		this.stop = false;
	}
	
	@Override
	public void run() {
		int i = 0;
		System.out.println("asynchronous loader started");
		while(i < numFrames && !this.stop) {
			Thread.interrupted();
			try{
				Thread.sleep(1000);
			} catch(InterruptedException e) {
				Thread.currentThread().interrupt();
				System.out.println("loader interrupted");
			}
			while(imgq.size() < 10 && i < numFrames) {
				imgq.add(Util.getImg(dir, i));
				System.out.println("loaded frame " + i);
				i++;
			}
		}
	}
	
	public void done() {
		this.stop = true;
	}
}
