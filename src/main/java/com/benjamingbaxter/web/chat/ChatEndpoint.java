package com.benjamingbaxter.web.chat;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/chat")
public class ChatEndpoint {
	
	@OnOpen
	public void onOpen(Session session) {
		System.out.println("Chat session opened (id: " + session.getId() + ")");
		try {
			session.getBasicRemote().sendText("Connecting...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@OnClose
	public void onClose(Session session) {
		System.out.println("Chat session closed (id: " + session.getId() + ")");
	}
	
	@OnMessage
	public void onMessage(String message, Session session) {
		if(message.equals("Ping")) {
			try {
				session.getBasicRemote().sendText(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if( message.startsWith("register=") ) {
			String username = message.substring("register=".length());
			System.out.println("Registering user: " + username + " for session " + session.getId());
			try {				
				SessionHandler.getInstance().addSession(username, session);
			} catch( Exception e ) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		} else {
			sendChat(message, session);
		}
	}

	private void sendChat(String message, Session session) {
		String[] info = message.split("\\|");
		if( info.length == 3 ) {
			String from = info[0];
			String to = info[1];
			String text = info[2];
			System.out.println("Sending '" + text + "' to " + to + " from " + from);
			
			sendMessage(message, SessionHandler.getInstance().getSession(to));
			sendMessage(message, SessionHandler.getInstance().getSession(from));
		}
	}

	private void sendMessage(String message, Session s) {
		if( s != null ) {
			System.out.println("Sending '" + message + " to session " + s.getId());
			s.getAsyncRemote().sendText(message);
		}
	}

}