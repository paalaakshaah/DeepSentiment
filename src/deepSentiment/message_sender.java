
package deepSentiment;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
public class message_sender {

	/**
	 * @param args
	 */
	public static class values
	{
		double [] sentiment;
	}
	public static double [] total_sentiment = new double[5];
	public static Queue<values> a = new LinkedList<values>() ;
	public synchronized static void send(values sending_values, RemoteEndpoint twisocket) throws IOException {
		// TODO Auto-generated method stub
		for(int i = 0; i < 5; i++)
			total_sentiment[i] += sending_values.sentiment[i];
		a.add(sending_values);
		if(a.size() > 100)
		{
			values b = a.remove();
			for(int i = 0; i < 5; i++)
				total_sentiment[i] = total_sentiment[i] - b.sentiment[i];
		}
		String sending_message = "tw: " + (total_sentiment[0]/a.size())*100 + " " + (total_sentiment[1]/a.size())*100 + " " + (total_sentiment[2]/a.size())*100 + " " + (total_sentiment[3]/a.size())*100 + " " + (total_sentiment[4]/a.size())*100;
		twisocket.sendString(sending_message);
	}

}
