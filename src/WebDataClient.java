import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;

public class WebDataClient {

	/**
	 * @param args
	 */
	public static void start_search(String query, RemoteEndpoint twisocket, RemoteEndpoint fbsocket) throws FacebookException {
		String searchWord="deepSentiment";

		// TODO Auto-generated method stub
		SearchTweets tweeter = new SearchTweets(query, twisocket);
		Thread t1 = new Thread(tweeter);
		t1.start();
		
		SearchFacebook faceboook = new SearchFacebook(query, fbsocket);
		Thread t2 = new Thread(faceboook);
		t2.start();
	}

}
