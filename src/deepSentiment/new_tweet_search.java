
package deepSentiment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import twitter4j.FilterQuery;
import twitter4j.GeoLocation;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;


public class new_tweet_search{
	private static final String CONSUMER_KEY = "V69tHiEZ3qYVb6ERGIkbLkOdt";
	private static final String CONSUMER_SECRET = "TX8luyOFRj36Tb0XP1XozerwdWeWZxU2uBVGrb4y7GAusV1Rs6";
	private static final String ACCESS_TOKEN = "148313150-Udrd8TfTkqWJMxuHaRSFbHlGH50uDxzKFn4eO8dV";
	private static final String ACCESS_TOKEN_SECRET = "rlAMLATbku89QTDHbZDG2ARpsvgSVufK9uvJIkOlgIXoF";
	private int pnum = 0;
	int count;
	double lat, lng;

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
                	GeoLocation loc = status.getGeoLocation();

                    if(loc != null) {
                    	lat = loc.getLatitude();
                    	lng = loc.getLongitude();
                    }
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



                	String msg = "twmap: ";
                	double [] sentiment = new double[5];
                	for(StanfordCoreNlpDemo.sentiment i : val)
                    {
                    	//System.out.println("in tweets" + i.value);
                    	sentiment[i.value]++;
                    	pnum++;
                    	lat = getlatitude();
                        lng = getlongitude();
                        count++;
                    	msg= msg + lat + " " + lng + " " + i.value + " ";
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
	                			message_sender.values sending_values = new message_sender.values();
	                			sending_values.sentiment = sentiment;
	                			try{
	                			rp.sendString(msg);
	                			message_sender.send(sending_values, rp);
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

	double getlatitude() {
        if(count%100<17){ //US
            return 27 + Math.random()*(20);
        } else if(count%100<29) { //Canada
            return 47 + Math.random()*(20);
        } else if(count%100<38) { //SAm1
            return -15 + Math.random()*(15);
        } else if(count%100<44) { //SAm2
            return -45 + Math.random()*(20);
        } else if(count%100<55) { //NAf
            return 8 + Math.random()*(22);
        } else if(count%100<61) { //SAf
            return -30 + Math.random()*(35);
        } else if(count%100<64) { //Russia
            return 45 + Math.random()*(20);
        } else if(count%100<68) { //SAs
            return 23 + Math.random()*(20);
        } else if(count%100<81) { //Europe
            return 45 + Math.random()*(15);
        } else if(count%100<90) { //Alaska
            return 60 + Math.random()*(10);
        } else if(count%100<92) { //Greenland
            return 70 + Math.random()*(15);
        } else { //Aus
            return -32 + Math.random()*(12);
        }
    }

    double getlongitude() {
        if(count%100<17){ //US
            return -123 + Math.random()*(40);
        } else if(count%100<29) { //Canada
            return -120 + Math.random()*(60);
        } else if(count%100<38) { //SAm1
            return -75 + Math.random()*(38);
        } else if(count%100<44) { //SAm2
            return -67 + Math.random()*(10);
        } else if(count%100<55) { //NAf
            return -15 + Math.random()*(60);
        } else if(count%100<61) { //SAf
            return 15 + Math.random()*(20);
        } else if(count%100<64) { //Russia
            return 30 + Math.random()*(105);
        } else if(count%100<68) { //SAs
            return 45 + Math.random()*(80);
        } else if(count%100<81) { //Europe
            return 0 + Math.random()*(45);
        } else if(count%100<90) { //Alaska
            return -164 + Math.random()*(45);
        } else if(count%100<92) { //Greenland
            return -30 + Math.random()*(10);
        } else { //Aus
            return 120 + Math.random()*(30);
        }
    }
}
