package listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import websocket2.SecondWebSocketServer;

public class StartSocketListener implements ServletContextListener{

	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		 //初始化一个websocket服务, 端口是6666
		SecondWebSocketServer socketServer = new SecondWebSocketServer(5678);
		//必须调用start, 才是启动
		socketServer.start();
		new Thread(new Runnable() {
			public void run() {
				socketServer.doSendMessage();
			}
		});
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}

}
