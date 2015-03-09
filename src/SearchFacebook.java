import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import facebook4j.Account;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Post;
import facebook4j.ResponseList;
import facebook4j.conf.Configuration;
import facebook4j.conf.ConfigurationBuilder;


public class SearchFacebook implements Runnable {
	
	String query = "FacebookQuery";
	Facebook facebook;
	
	public static String diff(String str1, String str2) {
	    int index = str1.lastIndexOf(str2);
	    if (index > -1) {
	      return str1.substring(str2.length());
	    }
	    return str1;
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
	
	public SearchFacebook(String s) throws FacebookException {
		query = s;
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
				String results = getFacebookPostes(facebook,query);
			    
				//String responce = stringToJson(results);
				
				// Create file and write to the file
				File file = new File("facebook.txt");
				if (!file.exists())
				{
					file.createNewFile();
					FileWriter fw = new FileWriter(file.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(results);
					
					bw.close();
					System.out.println("Writing complete");
				}
			} catch (FacebookException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
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
	
	public static String stringToJson(String data)
	{
		// Create JSON object
		//System.out.println("data= "+data);
		//System.out.println("********************");
		JSONObject jsonObject = JSONObject.fromObject(data);
		JSONArray message = (JSONArray) jsonObject.get("message");
		//System.out.println("Message : "+message);
		return "Done";
	}

}
