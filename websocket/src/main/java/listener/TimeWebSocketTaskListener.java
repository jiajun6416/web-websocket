package listener;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import websocket2.TimeWebSocket;

/**
 * @描述：JDK的定时任务
 * @author jiajun
 * @date 2017年6月25日上午10:42:02
 */
public class TimeWebSocketTaskListener implements ServletContextListener{
	
	private static ScheduledThreadPoolExecutor executor;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		//创建最多开启5个线程去执行
		executor = new ScheduledThreadPoolExecutor(5);
		//创建定时任务
		TimeWebSocketTask task = new TimeWebSocketTask();
		//演示2秒后, 每1秒执行一次
		executor.scheduleWithFixedDelay(task, 2, 1, TimeUnit.SECONDS);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if(executor != null) {
			executor.shutdown();
		}
	}
	

	/**
	 * @描述：定时任务类
	 * @author jiajun
	 * @date 2017年6月25日上午10:54:00
	 */
	private class TimeWebSocketTask implements Runnable {
		@Override
		public void run() {
			TimeWebSocket.sendTime();
		}
	}

}
