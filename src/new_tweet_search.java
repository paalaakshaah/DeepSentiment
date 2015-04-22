import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;


public class new_tweet_search{
	private static final String CONSUMER_KEY = "1yBVYYLzYbpzryPvJs7ZHQ0rK";
	private static final String CONSUMER_SECRET = "B7FwgGxC3BEj4ZBmCHZViKwoUpHLrVPVSOZoyqhu3ZDUkiJn3m";
	private static final String ACCESS_TOKEN = "148313150-9GmIKwMFm4jLlHRM6Rk7qr4hPAAL4EScpQek21bH";
	private static final String ACCESS_TOKEN_SECRET = "nal1eGTsjRgSZQm1oUuNXQKbibYbeyLQf17grdoNfXlCL";
	private int pnum = 0;
	double [] sentiment = new double[5];
	HashMap<String, RemoteEndpoint> socket_list = new HashMap<String, RemoteEndpoint>();
	public static TwitterStream twitterStream = null;
	Vector<String> queries = new Vector<String>();
    public void streamData(String curr_query, RemoteEndpoint sess){
    	queries.add(curr_query);
    	 socket_list.put(curr_query, sess);
    	try {
	        // sample() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
	        //twitterStream.sample();
	        twitterStream.cleanUp();
	        FilterQuery query = new FilterQuery();
	        if(queries.size() != 0)
	        {
		        String [] track = new String[queries.size()];
		        queries.toArray(track);
		       
		        query.track(track);
		        String[] lang = {"en"};
		        query.language(lang);
		        twitterStream.filter(query);
	        }
	        //twitterStream.sample();
	        
    	}
    	catch (Exception e) {
    		System.out.println(e.getMessage());
    		twitterStream.shutdown();
    	}
    }

	public void initialize() {
		// TODO Auto-generated method stub
		ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true).setOAuthConsumerKey(CONSUMER_KEY)
                .setOAuthConsumerSecret(CONSUMER_SECRET)
                .setOAuthAccessToken(ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET)
                .setJSONStoreEnabled(true);
        
        twitterStream = new TwitterStreamFactory(cb.build()).getInstance();

        try{
        	StatusListener listener;   
            listener = new StatusListener(){
                public void onStatus(Status status) {
                	String t1 = "";
                	String t = status.getText();
                	System.out.println(t);
                	int find1 = t.indexOf("http");
                    if(find1!=-1){
	                    if(find1 == 0)
	                    	return;
	                    int find2 = t.indexOf(' ', find1);
	                    if(find2 == -1)
	                    	find2 = t.length()-1;
	                    
	                    t1 = t.substring(0, find1-1); //+ t.substring(find2, t.length()-1);
                    }
                    else
                    {
                    	t1 = t;
                    }
                    t1 = t1.replaceAll("#[A-Za-z]+","");
                    t1 = t1.replaceAll("@[A-Za-z]+","");
                	//long userID = status.getId();
                	
                	/*GeoLocation loc = tweet.getGeoLocation();
                    double lat = usleft[1] + Math.random()*(usright[1]-usleft[1]);
                    double lng = usleft[0] + Math.random()*(usright[0]-usleft[0]);
                    if(loc != null)
                    {
                    	lat = loc.getLatitude();
                    	lng = loc.getLongitude();
                    }*/
                	
                	ArrayList<StanfordCoreNlpDemo.sentiment> val = null;
                	try {
						val = StanfordCoreNlpDemo.get_sentiment(t1);
					
                	if(pnum > 100)
                    {
                    	pnum = 1;
                    	sentiment[0] = 0;
                    	sentiment[1] = 0;
                    	sentiment[2] = 0;
                    	sentiment[3] = 0;
                    	sentiment[4] = 0;
                    }
                	
                	for(StanfordCoreNlpDemo.sentiment i : val)
                    {
                    	//System.out.println("in tweets" + i.value);
                    	sentiment[i.value]++;
                    	pnum++;
                    	/*mapP[2] = i.value;
                    	mapPoints.add(mapP);*/
                    }
                	
                	if(val != null)
                	{
	                	for(String a : queries)
	                	{
	                		
	                		if(org.apache.commons.lang3.StringUtils.containsIgnoreCase(t1, a))
	                		{
	                			RemoteEndpoint rp = socket_list.get(a);
	                			String mess = "tw: " + (sentiment[0]/pnum)*100 + " " + (sentiment[1]/pnum)*100 + " " + (sentiment[2]/pnum)*100 + " " + (sentiment[3]/pnum)*100 + " " + (sentiment[4]/pnum)*100;
	                			try{
	                			rp.sendString(mess);
	                			}catch(Exception e)
	                			{
	                				queries.remove(a);
	                				socket_list.remove(a);
	                			}
	                		}
	                			
	                	}
                	}
                	} catch (IOException e) {
						// TODO Auto-generated catch block
                		
                		e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						
						
						e.printStackTrace();
					}	
                   
                }
                public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
                public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
                public void onException(Exception ex) {
                    ex.printStackTrace();
                }
    			public void onScrubGeo(long arg0, long arg1) {
    				// TODO Auto-generated method stub
    				
    			}
    			public void onStallWarning(StallWarning arg0) {
    				// TODO Auto-generated method stub
    				
    			}
                
            };
            twitterStream.addListener(listener);
        }catch(Exception e){
        	System.out.println(e.getMessage());
    		twitterStream.shutdown();
        }
        
		
	}
}