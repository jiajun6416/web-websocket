package servlet;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import websocket.WebSocketUtil;

/**
 * Servlet implementation class NameConfirm
 */
@WebServlet("/NameConfirm")
public class NameConfirm extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String username = req.getParameter("username");
		if(username != null && !username.trim().equals("")) {
			Set<String> usernames = WebSocketUtil.getonlineUser();
			if(usernames != null && usernames.contains(username)) {
				resp.getWriter().write("{\"result\":\"exist\"}");
				return;
			}  else {
				req.getSession().setAttribute("username", username);
				resp.getWriter().write("{\"result\":\"success\"}");
				return;
			}
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}
