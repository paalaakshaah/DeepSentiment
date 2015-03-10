import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import facebook4j.FacebookException;

public class EventSocket extends WebSocketAdapter
{
	Session mysess;
    
    public void onWebSocketConnect(Session sess)
    {
        super.onWebSocketConnect(sess);
        mysess = sess;
//	sess.getRemote().sendString("wassssssssssssssssssup");
        System.out.println("Socket Connected: " + sess);
    }
    
    public void sendmessage(Session sess, String mess) throws IOException
    {
			sess.getRemote().sendString(mess);
			System.out.println(mess);
    }
    
    
    public void onWebSocketText(String message)
    {
        super.onWebSocketText(message);
        System.out.println("Received TEXT message: " + message);
        try {
			WebDataClient.start_search(message, mysess.getRemote());
		} catch (FacebookException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    public void onWebSocketClose(int statusCode, String reason)
    {
        super.onWebSocketClose(statusCode,reason);
        System.out.println("Socket Closed: [" + statusCode + "] " + reason);
    }
    
    
    public void onWebSocketError(Throwable cause)
    {
        super.onWebSocketError(cause);
        cause.printStackTrace(System.err);
    }
}
