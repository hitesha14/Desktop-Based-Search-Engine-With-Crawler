/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawlerlucene.google.crawler4j.multithreaded.api;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import crawlerlucene.google.crawler4j.multithreaded.constant.IndexConstants;

/**
 * This class is a helper/ core class, its a container for thread pool creation.
 * Every thread running inside this thread is controlled through the executor
 * service.User can have this class to implement thread pool for any generic
 * thread. Which can be grouped together.These threads can be of versatile. They
 * can run with different data sets.
 * 
 * @author sumit
 */
public class SimpleThreadPool {

	/*
	 * variables to control the execution of the thread pool
	 */
	private ExecutorService deamonExecutor;
	private int maxThreadPoolSize = IndexConstants.DFLT_THREAD_MAX_POOL_SIZE;
	private int coreThreadPoolSize = IndexConstants.DFLT_THREAD_CORE_POOL_SIZE;
	private boolean isShutdown = false;

	public int getMaxThreadPoolSize() {
		return maxThreadPoolSize;
	}

	public SimpleThreadPool(int pMaxPoolSize, int pMinimalPoolSize) {
		if (pMaxPoolSize > 0) {
			this.maxThreadPoolSize = pMaxPoolSize;
		}
		if (pMinimalPoolSize > 0) {
			this.coreThreadPoolSize = pMaxPoolSize;
		}
		deamonExecutor = new ThreadPoolExecutor(
				pMaxPoolSize,
				pMinimalPoolSize,
				IndexConstants.DFLT_IDEAL_THREAD_MAX_WAITING_TIME_BEFORE_DIE_IN_POOL_DAYS,
				IndexConstants.DFLT_TIME_UNIT_DAYS,
				new LinkedBlockingQueue<Runnable>(this.maxThreadPoolSize));

	}

	public SimpleThreadPool(int pMaxPoolSize) {
		if (pMaxPoolSize > 0) {
			this.maxThreadPoolSize = pMaxPoolSize;
		}
		deamonExecutor = Executors.newFixedThreadPool(this.maxThreadPoolSize);
	}

	/**
	 * This method will add one by one thread to the thread pool and start the
	 * executing, as soon as the thread pool controller decided to run them ,
	 * that depends upon the availability of the system resources.
	 * 
	 * @param workingThread
	 */
	public void execute(Runnable workingThread) {
		if (isShutdown) {
			throw new IllegalStateException(
					"Service Access Denial::The working thread is shoutdown long back, cannot request more service!");
		}
		deamonExecutor.execute(workingThread);

	}

	/**
	 * This methods causes to shutdown the current thread, it allows external
	 * module, to kill this thread.
	 */
	public void shutdown() {
		isShutdown = true;
		deamonExecutor.shutdown();
	}

	/**
	 * This method will try to cause the thread pool to shutdown quickly It acts
	 * as the system shutdown hook. not guaranteed, will try its best
	 */

	public void immediateShutdown() {
		isShutdown = true;
		deamonExecutor.shutdownNow();
	}

	public boolean isDeamonExecutorShutDown() {
		return deamonExecutor.isShutdown();
	}

	/**
	 * This will avoid the system to add more threads to the thread pool system.
	 * 
	 * @return
	 */
	public boolean canSubmitThread() {
		return isShutdown;
	}

	/**
	 * This allows the other threads to wait for completion of the shutdown of
	 * the thread pool system.
	 * 
	 * @param timieOut
	 * @param whatTimeMeans
	 * @throws InterruptedException
	 */
	public void waitForThreadPoolTermination(long timieOut,
			TimeUnit whatTimeMeans) throws InterruptedException {
		deamonExecutor.awaitTermination(timieOut, whatTimeMeans);
	}

}
