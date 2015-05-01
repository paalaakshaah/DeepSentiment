
package deepSentiment;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.*;
import twitter4j.auth.AccessToken;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;


public class SearchTweets implements Runnable{
	Twitter twitter;
	String searchWord;
	RemoteEndpoint sess;
	int total = 1;
	ArrayList<double[]> mapPoints = new ArrayList<double[]>();
	double [] sentiment = new double[5];
	int count = 0;

	public SearchTweets(String query, RemoteEndpoint webs) {
		searchWord = query;
		sess = webs;
		setAuth();
	}

	public void run() {
		try {
			search(searchWord);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setAuth() {
		//setupProperties();
		 long k1 = System.currentTimeMillis();

        AccessToken aToken=new AccessToken("148313150-TvnyzcwBpu5bokeHgdaIBQme3VYQPGfPREceZqCY","m9RCseEaj14sn0srg5MbbiQB0HTqy5l9AGNXT11b6w34P");
        twitter=new TwitterFactory().getInstance();
        twitter.setOAuthConsumer("J8hPaid7DL4guQRo5U4xXZVcJ","NzWKkaeuf8fPRzXG8zDS9pXgcNhFSg03RRZBz4LMaXb0f0iFNb");
        twitter.setOAuthAccessToken(aToken);
        long k2 = System.currentTimeMillis();
        System.out.println("twitter oauth:" + (k2-k1));
	}
	/**
     * Usage: java twitter4j.examples.search.SearchTweets [query]
     *
     * @param args search query
	 * @throws IOException
     */
    public void search(String word) throws IOException {
        if (word == null) {
            System.out.println("java twitter4j.examples.search.SearchTweets [query]");
            System.exit(-1);
        }

        String t = "";
        String t1 = "";
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("searchtweets.out")));

        //Twitter twitter = new TwitterFactory().getInstance();
        try {
            Query query = new Query(word);
            QueryResult result;
            do {
                result = twitter.search(query);

                List<Status> tweets = result.getTweets();
                for (Status tweet : tweets) {
                	System.out.println(tweet);
                	//System.out.println(loc);
                	//System.out.println(tweet.toString());
                    t = (tweet.getText());
                    GeoLocation loc = tweet.getGeoLocation();
                    double lat;
                    double lng;

                    if(loc != null) {
                    	lat = loc.getLatitude();
                    	lng = loc.getLongitude();
                    }

                    //t1 = removeUrl(t);
                    int find1 = t.indexOf("http");
                    if(find1!=-1){
                    if(find1 == 0)
                    	continue;
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

                    long time1 = System.currentTimeMillis();
                    ArrayList<StanfordCoreNlpDemo.sentiment> val = StanfordCoreNlpDemo.get_sentiment(t1);
                    /*double[] mapP = new double[3];
                    mapP[0] = lat;
                    mapP[1] = lng;*/
                    long time2 = System.currentTimeMillis();
                    out.println("nlp call time"+(time2-time1) + " length: " + t1.length());
                    out.flush();

                    if(total > 100)
                    {
                    	total = 1;
                    	sentiment[0] = 0;
                    	sentiment[1] = 0;
                    	sentiment[2] = 0;
                    	sentiment[3] = 0;
                    	sentiment[4] = 0;
                    }
                    System.out.println(t1);

                    String msg = "twmap: ";

                    for(StanfordCoreNlpDemo.sentiment i : val)
                    {
                    	//System.out.println("in tweets" + i.value);
                    	sentiment[i.value]++;
                    	total++;
                    	lat = getlatitude();
                        lng = getlongitude();
                        count++;
                    	msg= msg + lat + " " + lng + " " + i.value + " ";
                    	System.out.print(i.value + "  ");
                    }

                    sess.sendString(msg);
                    //System.out.println(Arrays.toString(sentiment));
                    String mess = "tw: " + (sentiment[0]/total)*100 + " " + (sentiment[1]/total)*100 + " " + (sentiment[2]/total)*100 + " " + (sentiment[3]/total)*100 + " " + (sentiment[4]/total)*100;
                    //System.out.println(mess);
                    sess.sendString(mess);
                    mapPoints.clear();
                }
            } while((query = result.nextQuery()) != null);
            System.exit(0);
        } catch (Exception te) {

            te.printStackTrace();
            System.out.println(t);
            System.out.println(t1);
            System.out.println("Failed to search tweets: " + te.getMessage());
            //System.exit(-1);
            Thread.currentThread().interrupt();
            return;
        }
    }

    public String removeUrl(String commentstr)
    {
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(commentstr);
        int i = 0;
        while (m.find()) {
            commentstr = commentstr.replaceAll(m.group(i),"").trim();
            i++;
        }
        return commentstr;
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
