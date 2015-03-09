import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;

public class WebDataClient {

	/**
	 * @param args
	 */
	public static void start_search(String query, RemoteEndpoint socket) {
		String searchWord="deepSentiment";

		// TODO Auto-generated method stub
		SearchTweets tweeter = new SearchTweets(query, socket);
		Thread t = new Thread(tweeter);
		t.start();
	}

}
