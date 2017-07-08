package websocket;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @描述：chart socket, 必须多例 
 * 	
 * 可以注入参数: Session session, EndpointConfig  config
 * 	
 * @author jiajun
 * @date 2017年6月24日下午5:46:59
 */
@ServerEndpoint(value="/ws/chart", configurator=HttpConfigurator.class)
public class FirstWebSocketServer {
	
	private static Logger log = LoggerFactory.getLogger(FirstWebSocketServer.class);
	
	private String username;
	ObjectMapper mapper  = new ObjectMapper();
	
	@OnOpen
	public void onopen(EndpointConfig  config, Session session) {
		//之前在自定义的 config中注入了httpsession现在可以获取了
		HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
		username = (String) httpSession.getAttribute("username");
		if(null != username && !"".equals(username)) {
			log.debug(username+"和服务端握手成功");
			WebSocketUtil.addSocket(username, session);
			//给当前用户, 发送用户列表
			Map<String, Object> result = new HashMap<>();
			result.put("type", "get_online_user");
			result.put("list", WebSocketUtil.getonlineUser());
			try {
				String message = mapper.writeValueAsString(result);
				WebSocketUtil.send(message, session);
				log.debug("发送在线用户给: "+ username);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			
			//给其他用户更新在线列表
			String joinMsg = "{\"type\":\"user_join\", \"user\":\""+username+"\"}";
			WebSocketUtil.sendToOthers(username, joinMsg);
			log.debug("系统: 更新在线列表信息{}", joinMsg);
			
			//给所有发送上线信息
			result = new HashMap<>();
			result.put("type", "message");
			result.put("from", "[系统]");
			result.put("content", username+"上线了");
			result.put("timestamp", new Date().getTime());
			try {
				joinMsg = mapper.writeValueAsString(result);
				WebSocketUtil.sendToAll(joinMsg);
				log.debug("系统消息: "+ joinMsg);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		} else {
			log.debug("用户未登录!");
			onclose();
		}
	}
	

	@OnError
	public void onerro(Session session, Throwable error){
		log.error(error.getMessage(), error);
	}
	
	@OnMessage
	/**
	 * @param session
	 * @param content
	 */
	public void onmessage(Session session, String content) {
		Map<String, Object> result = new HashMap<>();
		result.put("from", username);
		result.put("content", content);
		result.put("type", "message");
		result.put("timestamp", new Date().getTime());
		try {
			String message = mapper.writeValueAsString(result);
			WebSocketUtil.sendToAll(message);
			log.debug(username+"给所以人发送: " + content);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	@OnClose
	public void onclose() {
		if(username != null) {
			Map<String, Object> result = new HashMap<>();
			result.put("type", "user_leave");
			result.put("user", username);
			try {
				//移除列表中的用户
				String message = mapper.writeValueAsString(result);
				//必须排除自己的session, 因为此时已经关闭连接了
				WebSocketUtil.sendToOthers(username, message);
				log.debug("发送消息给所有人, {}关闭连接下线", username);			
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} finally {
				WebSocketUtil.removeSocket(username);
			}
			//向其他人发送下线信息
			result = new HashMap<>();
			result.put("type", "message");
			result.put("from", "[系统]");
			result.put("content", username+"下线了");
			result.put("timestamp", new Date().getTime());
			try {
				String leaveLine = mapper.writeValueAsString(result);
				WebSocketUtil.sendToAll(leaveLine);
				log.debug("系统消息: {}", leaveLine);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		} else {
			log.debug("关闭无效连接");
		}
	}
}
