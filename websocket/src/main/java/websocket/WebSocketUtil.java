package websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

/**
 * @描述：socket工具类
 * @author jiajun
 * @date 2017年6月24日下午5:09:43
 */
public class WebSocketUtil {
	
	//key是name, value是连接的socket
	private static Map<String, Session> socketPool = new ConcurrentHashMap<>();

	
	/**
	 * 获得在线人数
	 * @return
	 */
	public static int getOnlineNum(){
		if(socketPool != null ) {
			return socketPool.size();
		} else {
			return 0;
		}
	}
	
	public static Set<String> getonlineUser() {
		if(getOnlineNum() > 0) {
			return socketPool.keySet();
		} else {
			return null;
		}
	}
	
	
	/**
	 * 加入
	 * @param username
	 * @param session
	 */
	public static void addSocket(String username, Session session) {
		socketPool.put(username, session);
	}
	
	/**
	 * 离开
	 * @param username
	 */
	public static void removeSocket(String username) {
		if(socketPool != null && socketPool.size()>0) {
			socketPool.remove(username);
		}
	}
	
	/**
	 * 发送消息给其他人
	 * @param username
	 * @param message
	 */
	public static void sendToOthers(String username, String message) {
		if(username != null && !username.trim().equals("") && message!=null) {
			if(socketPool != null && socketPool.size()>0) {
				Set<String> keySet = socketPool.keySet();
				for (String key : keySet) {
					if(!username.equals(key)) {
						try {
							socketPool.get(key).getBasicRemote().sendText(message);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	/**
	 * 发送消息给所有人包括自己
	 * @param message
	 */
	public static void sendToAll(String message) {
		if(socketPool != null && socketPool.size()>0) {
			Set<String> keySet = socketPool.keySet();
			for (String key : keySet) {
				try {
					socketPool.get(key).getBasicRemote().sendText(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 发送消息给指定人
	 * @param username
	 * @param message
	 */
	public static void send(String username, String message) {
		if(username != null && !username.trim().equals("") && message!=null) {
			if(socketPool != null && socketPool.size()>0) {
				Session session = socketPool.get(username);
				if(session != null) {
					try {
						session.getBasicRemote().sendText(message);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	
	/**
	 * 发送给指定session
	 * @param username
	 * @param message
	 * @param session
	 */
	public static void send(String message, Session session) {
		if(message!=null&&session != null) {
			try {
				session.getBasicRemote().sendText(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
