import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;


import facebook4j.Account;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Post;
import facebook4j.ResponseList;
import facebook4j.conf.Configuration;
import facebook4j.conf.ConfigurationBuilder;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;

public class SearchFacebook implements Runnable {
	
	String searchWord = "FacebookQuery";
	Facebook facebook;
	RemoteEndpoint sess;
	int total = 1;
	double [] sentiment = new double[5];
	
	public SearchFacebook(String query, RemoteEndpoint webs) throws FacebookException {
		searchWord = query;
		sess = webs;
		setAuth();
	}
		
	public void setAuth() throws FacebookException {
		// Make the configuration builder
				ConfigurationBuilder confBuilder = new ConfigurationBuilder();
				confBuilder.setDebugEnabled(true);

				// Set application id, secret key and access token
		        confBuilder.setOAuthAppId("611319842300889");
		        confBuilder.setOAuthAppSecret("23fe3a8d413591cc6ff3009ce9c51d36");
		        //confBuilder.setOAuthAccessToken("kjdbfhewk");
		        
		        // Set permission
		        confBuilder.setOAuthPermissions("email,publish_stream, id, name, first_name, last_name, generic");
		        confBuilder.setUseSSL(true);
		        confBuilder.setJSONStoreEnabled(true);

		        // Create configuration object
		        Configuration configuration = confBuilder.build();

		        // Create facebook instance
		        FacebookFactory ff = new FacebookFactory(configuration);
		        facebook = ff.getInstance();
		        facebook.setOAuthAccessToken(facebook.getOAuthAppAccessToken());
	}
	
	public void run() {	
		while(true) {
			try {
		
				// Get facebook posts
				//String query = "cricket";
				String results = getFacebookPostes(facebook,searchWord);
			    
				//String responce = stringToJson(results);
				
				// Create file and write to the file
				/*File file = new File("facebook.txt");
				if (!file.exists())
				{
					file.createNewFile();
					FileWriter fw = new FileWriter(file.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(results);
					
					bw.close();
					System.out.println("Writing complete");
				}*/
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("searchfb.out")));
				long time1 = System.currentTimeMillis();    
				ArrayList<StanfordCoreNlpDemo.sentiment> val = StanfordCoreNlpDemo.get_sentiment(results);
				long time2 = System.currentTimeMillis();
				out.println("nlp call time"+(time2-time1) + " length: " + results.length());
                out.flush();
                
                
                if(total > 100)
                {
                	total = 1;
                	for(int i=0; i<5; i++) {
                		sentiment[i] = 0;
                	}
                }
                for(StanfordCoreNlpDemo.sentiment i : val)
                {
                	//System.out.println("in fb" + i.value);
                	sentiment[i.value]++;
                	total++;
                	String mess = "fb: " + (sentiment[0]/total)*100 + " " + (sentiment[1]/total)*100 + " " + (sentiment[2]/total)*100 + " " + (sentiment[3]/total)*100 + " " + (sentiment[4]/total)*100; 
                    sess.sendString(mess);
					Thread.sleep(100);
                }
                
                //System.out.println(Arrays.toString(sentiment));
                
				
			} catch (FacebookException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
			// TODO Auto-generated catch block
				System.out.println("in IO exception");
				
			e.printStackTrace();
			Thread.currentThread().interrupt();
            return;
		} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
	
	public static String getFacebookPostes(Facebook facebook, String query) throws FacebookException {
		// Get posts for a particular search
		ResponseList<Post> results =  facebook.getPosts(query);
		StringBuffer t1=new StringBuffer();
		String SubStr1 = new String("message='");
		
		for (int i = 0; i < results.size(); i++) {
			String temps = results.get(i).toString();
			
			String temp3 = temps.substring(temps.indexOf(SubStr1));
			//System.out.println(temp3);
			temp3 = temp3.substring(9,temp3.indexOf("',"));
			temp3 = removeUrl(temp3);			
			t1.append(temp3);	
		}
		//System.out.println(t1);
		return t1.toString();
	}
	
	public static String removeUrl(String commentstr)
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
