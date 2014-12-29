/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawlerlucene.google.crawler4j.multithreaded.api;

/**
 * This is a marker thread. It allows all the implementing class to expose its
 * shutdown controlling method, to control the thread execution. From outsde
 * world.
 * 
 * @author sumit
 */
public interface WorkerThread extends Runnable {

	public void shutdown();
}
