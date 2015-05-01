/*
    Simple Reddit Scraper
    Copyright (C) 2012-2013, Gary Paduana, gary.paduana@gmail.com
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.reddit;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.reddit.domain.CommentThread;
import com.reddit.util.ApiUtil;

public class RedditScraperBot implements Runnable {

	public static final int MINIMUM_TIME_BETWEEN_REQUESTS_IN_MS = 10000;
	public static final boolean DO_NOT_RECURSE_COMMENT_TREE = false;
	public static final boolean RETRIEVE_ALL_COMMENTS = true;
	public static String keyword = "obama";
	RemoteEndpoint sess;
	
	public RedditScraperBot(String query, RemoteEndpoint webs) {
		keyword = query;
		sess = webs;
	} 
	
	public void run() {

		ApiUtil apiUtil = new ApiUtil(MINIMUM_TIME_BETWEEN_REQUESTS_IN_MS, sess);
		
		List<String> subreddits = Arrays.asList("funny");
		
		for(String subreddit : subreddits){
			System.out.println("########################################");
			System.out.println("#######  Begin parsing for: " + subreddit);
			System.out.println("########################################");
			
			// Return 50 threads from the front page of this subreddit
			JSONObject redditObject;
			try {
				redditObject = new JSONObject(apiUtil.getPage("http://www.reddit.com/search.json?q="+keyword));
						
			JSONArray children = redditObject.getJSONObject("data").getJSONArray("children");
			for(int i = 0; i < children.length(); i++){
				try{
					String threadId = children.getJSONObject(i).getJSONObject("data").getString("id");

					// Request up to 500 comments for this thread
					JSONArray jsonArray = new JSONArray(apiUtil.getPage("http://www.reddit.com/comments/" + threadId + ".json"));
					
					// Change 2nd argument to RETRIEVE_ALL_COMMENTS if you want to fully construct this
					// commentThread object.  Warning: it can take some time since we respect the 3 seconds
					// between requests API rule and a large thread requires many requests.
					CommentThread commentThread = apiUtil.parseJSON(jsonArray, DO_NOT_RECURSE_COMMENT_TREE);
					
					// Do something with the comment thread object
					// Displaying meta data for lack of a better objective 
				/*	System.out.println("Author: " + commentThread.getAuthor() + ", Title: " + commentThread.getTitle() +
						", Upvotes: " + commentThread.getUpvotes() + ", Downvotes: " + commentThread.getDownvotes() + 
						", comments retrieved: " + commentThread.getComments().size()); */
				}
				catch(IOException ioException){
					ioException.printStackTrace();
				}
				catch(JSONException jsonException){
					jsonException.printStackTrace();
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		apiUtil.getTimer().cancel();
	}
	}
}