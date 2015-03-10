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
	double [] sentiment = new double[5];
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
        AccessToken aToken=new AccessToken("148313150-Udrd8TfTkqWJMxuHaRSFbHlGH50uDxzKFn4eO8dV","rlAMLATbku89QTDHbZDG2ARpsvgSVufK9uvJIkOlgIXoF");
        twitter=new TwitterFactory().getInstance();
        twitter.setOAuthConsumer("OF74Uvkp4uvZ8Awbywnb8SalP","6bjyvRrvatUQ1ZYM4j0zETfnMsfGgFOx2WP9FlhBtkuUWEqrbl");
        twitter.setOAuthAccessToken(aToken);
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
                    t = (tweet.getText());
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
 //                   System.out.println(t1);
                    long time1 = System.currentTimeMillis();                    
                    ArrayList<StanfordCoreNlpDemo.sentiment> val = StanfordCoreNlpDemo.get_sentiment(t1);
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
                    for(StanfordCoreNlpDemo.sentiment i : val)
                    {
                    	//System.out.println("in tweets" + i.value);
                    	sentiment[i.value]++;
                    	total++;
                    }
                    
                    //System.out.println(Arrays.toString(sentiment));
                    String mess = "tw: " + (sentiment[0]/total)*100 + " " + (sentiment[1]/total)*100 + " " + (sentiment[2]/total)*100 + " " + (sentiment[3]/total)*100 + " " + (sentiment[4]/total)*100; 
 //                   System.out.println(mess);
                    sess.sendString(mess);
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
	
}
