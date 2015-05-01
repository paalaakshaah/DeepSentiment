
package deepSentiment;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;


public class EventServlet extends WebSocketServlet
{

    public void configure(WebSocketServletFactory factory)
    {
        factory.register(EventSocket.class);
    }
}
