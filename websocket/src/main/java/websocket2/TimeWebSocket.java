package websocket2;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import websocket.HttpConfigurator;

@ServerEndpoint(value="/ws/time", configurator=HttpConfigurator.class)
public class TimeWebSocket {
	
	private static final Set<Session> sockets = Collections.synchronizedSet(new HashSet<>());
	
	private static final Logger log = LoggerFactory.getLogger(TimeWebSocket.class);
	
	/*JDK8的线程安全的格式化类*/
	private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy年/MM月/dd日 HH:mm:SS"); 
	
	@OnOpen
	public void onopen(EndpointConfig  config, Session session) {
		log.debug("TimeSocket: {} 连接成功", session);
		sockets.add(session);
	}
	


	@OnError
	public void onerro(Session session, Throwable e){
		log.debug("TimeSocket: {} 出现异常, {}", session);
		log.error(e.getMessage(), e);
		close(session);
	}
	
	@OnMessage
	public void onmessage(Session session, String message) {
		
	}

	
	@OnClose
	public void onclose(Session session) {
		log.debug("TimeSocket: {} 关闭连接", session);
/*		if(session != null && session.isOpen()) {
			try {
				session.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
	}
	
	/**
	 * 每秒发送一次时间戳
	 * @param session
	 */
	public static void sendTime() {
        if(sockets != null && sockets.size()>0) {
        	Iterator<Session> iterator = sockets.iterator();
        	while(iterator.hasNext()) {
        	//	LocalDateTime now = LocalDateTime.now();
        		String message = new Date().toString();
        		Session session = iterator.next();
        		if(session != null && session.isOpen()) {
        		//	log.debug("TimeSokcet: 给{}连接发送消息, {}", session, message); 
        			//使用异步发送
        			//iterator.next().getAsyncRemote().sendText(message);
        			try {
        				session.getBasicRemote().sendText(message);
        			} catch (IOException e) {
        				e.printStackTrace();
        			}
        		}
        	}
        }
	}
	
	private void close(Session session) {
		if(session.isOpen()) {
			try {
				session.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
