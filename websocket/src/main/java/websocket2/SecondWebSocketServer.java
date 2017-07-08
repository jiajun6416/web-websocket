package websocket2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @描述：自己单间websocket服务端, 可以自定义路径和端口
 * @author jiajun
 * @date 2017年6月25日上午12:15:15
 */
public class SecondWebSocketServer extends WebSocketServer{
	
	private static Logger log = LoggerFactory.getLogger(SecondWebSocketServer.class);
	
	private static Set<WebSocket> pool = Collections.synchronizedSet(new HashSet<>());
	
	public SecondWebSocketServer(int port) {
		//重写构造器的另外一种方式
		super(new InetSocketAddress(port));
	}
	public SecondWebSocketServer(InetSocketAddress address) {
		super(address);
	}
	
	
	@Override
	public void onOpen(WebSocket socket, ClientHandshake handshake) {
		log.debug("WEBSOCKET: {} 连接", socket.hashCode());
		pool.add(socket);
	}

	@Override
	public void onMessage(WebSocket socket, String message) {
	}
	
	@Override
	public void onClose( WebSocket socket, int code, String reason, boolean remote ) {
		if(pool!= null && pool.size()>0) {
			pool.remove(socket);
		}
		socket.close();
		log.debug("WEBSOCKET: {} 关闭连接", socket.hashCode());
	}

	@Override
	public void onError(WebSocket socket, Exception ex) {
		log.debug("WEBSOCKET: {} 出现异常", socket.hashCode());
		log.debug(ex.getMessage(), ex);
	}
	
	public void doSendMessage() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH24:mm:SS");
		while(true) {
			Date date = new Date();
			if(pool != null && pool.size() > 0) {
				for (WebSocket socket : pool) {
					socket.send(format.format(date));
				}
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main( String[] args ) throws InterruptedException , IOException {
		WebSocketImpl.DEBUG = false;
		int port = 6666; //端口
		SecondWebSocketServer s = new SecondWebSocketServer(port);
		//必须调用启动
		s.start();
		System.out.println( "服务器的端口" + s.getPort() );
	}
}
