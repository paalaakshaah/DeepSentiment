
package deepSentiment;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;

import facebook4j.FacebookException;

public class WebDataClient {

	/**
	 * @param args
	 */
	public static void start_search(String query, RemoteEndpoint twisocket) throws FacebookException {
		//String searchWord="deepSentiment";

		// TODO Auto-generated method stub

		//SearchTweets tweeter = new SearchTweets(query, twisocket);
		//Thread t1 = new Thread(tweeter);
		//t1.start();
		SearchNYTimes nytimes = new SearchNYTimes(query, twisocket);
		Thread t1 = new Thread(nytimes);
		t1.run();

		SearchFacebook faceboook = new SearchFacebook(query, twisocket);
		Thread t2 = new Thread(faceboook);
		t2.start();

	}

}
