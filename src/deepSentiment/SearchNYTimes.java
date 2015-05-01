
package deepSentiment;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import javax.xml.parsers.ParserConfigurationException;

/*TODO
 * 1. GET OWN AUTH ID
 * 2. TRACK BY KEYWORD IN ARTICLE AND CHECK IN CORRESPONDING COMMENT
 * 3. PASS KEY THROUGH CONSTRUCTER
 * 4. INTEGRATE WITH MAIN CODE
 */


 class Result {
	String[] commentSentences;
	String[] urls;

	public Result(String[] a, String[] b) {
		this.urls = a;
		this.commentSentences = b;
	}

}

//http://api.nytimes.com/svc/community/v2/comments/by-date/
//http://api.nytimes.com/svc/community/v2/comments/by-date/20101212.json?&offset=1&api-key=cd6479409e9d2849d2c8f0246ab895ea:10:68731810
//http://api.nytimes.com/svc/community/v2/comments/by-date/20101212.json?api-key=8c5b6144d7eb91d5acc87de2521d449b:8:58236592
public class SearchNYTimes implements Runnable {

	static String keyword = "obama";

	RemoteEndpoint sess;
	static String accessKey = "65117e92644a014b739dd7a1909dc720:4:70127886";//"8c5b6144d7eb91d5acc87de2521d449b:8:58236592";

	public SearchNYTimes(String query, RemoteEndpoint webs) {
		keyword = query;
		sess = webs;
	}

	public static String makeURL(String date) {
		// check about offset
		return "http://api.nytimes.com/svc/community/v2/comments/by-date/"+date+".json?api-key="+accessKey;
	}

	public static Result processPara(String x1) {
		String x = null;
		//System.out.println("------------"+x);
		final Matcher matcher = Pattern.compile("articleURL.*").matcher(x1);
		//final Matcher matcher = Pattern.compile("commentBody.*commentTitle").matcher(x);
		if (matcher.find()) {
			// sets[i] = sets[i].substring(matcher.end()).trim();

			// System.out.println(matcher.group(0));
			x = matcher.group(0);
			x = x.substring(13, x.length()-1);

			// rectify
			// x = x.replace("<br \/><br \/>", " ");
			x = x.replace("<br", " ");
			x = x.replace("/>", " ");
			x = x.replace("\\", " ");
		}
		// System.out.println("came inside function"+x);
		String res1[] = x.split("(?<=[.!?])\\s* ");

		////////////////////////////////////////////////////////
		final Matcher matcher2 = Pattern.compile("commentBody.*commentTitle")
				.matcher(x1);
		if (matcher2.find()) {
			// sets[i] = sets[i].substring(matcher.end()).trim();

			// System.out.println(matcher.group(0));
			x = matcher2.group(0);
			x = x.substring(14, x.length() - 15);

			// rectify
			// x = x.replace("<br \/><br \/>", " ");
			x = x.replace("<br", " ");
			x = x.replace("/>", " ");
			x = x.replace("\\", " ");
		}
		// System.out.println("came inside function"+x);
		String[] res2 = x.split("(?<=[.!?])\\s* ");

		return new Result(res1, res2);

	}

	public static String removeUrlAndAhref(String x) {

		x = x.replaceAll("<a href=(.+?)< /a>", "");
		return x.replaceAll("https?://\\S+\\s?", "");
	}

	public void run() {


		InputStream is = null;

		Calendar now = Calendar.getInstance();
		Integer year = now.get(Calendar.YEAR);
		Integer month = now.get(Calendar.MONTH); // Note: zero based
		Integer day = now.get(Calendar.DAY_OF_MONTH);

		try {
			while (true) {
				String monthString = (month < 10) ? (String) ('0' + month
						.toString()) : (String) month.toString();
				String dayString = (day < 10) ? (String) ('0' + day.toString())
						: (String) day.toString();
				String dateString = year + monthString + dayString;
				URL url = new URL(makeURL(dateString));
				// System.out.println(dateString);
				is = url.openStream();
				int ch;
				StringBuilder returnedText = new StringBuilder();
				while ((ch = is.read()) != -1) {
					returnedText.append((char) ch);
				}
				String[] sets = returnedText.toString().split("}");

				// System.out.println(returnedText);

				for (int i = 0; i < sets.length; i++) {
					if (sets[i].contains(keyword)) {
						Result res = processPara(sets[i]);
						String[] ans = res.urls;
					   // System.out.println("came here"+ans[0]);
						for (int j = 0; j < ans.length; j++) {
							// ans[j]= removeUrl(ans[j]);

						//	setOfRelevantResults.add(ans[j]);
						//	System.out.println("ans[j]="+ans[j]);

						    String[] splits = ans[j].split("/");

							int len = splits[splits.length-1].length();
							//get the last bit of string
						    String x = splits[splits.length-1].substring(0, len-5);

								if(x.contains("obama")) {
							//		System.out.println("output ="+x);
									//process the comment and send to NLP
									String[] ansComments = res.commentSentences;
									for (int k = 0; k < ansComments.length; k++) {

										ansComments[k] = removeUrlAndAhref(ansComments[k]);
										System.out.println("sentence ="+ansComments[k]);

										// send for NLP processing
//////////////////////////////////////////////////////////////////////////////////////////////////
										PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("searchNYT.out")));
										long time1 = System.currentTimeMillis();
										ArrayList<StanfordCoreNlpDemo.sentiment> val = null;
										try {
											val = StanfordCoreNlpDemo.get_sentiment(ansComments[k]);
										} catch (ParserConfigurationException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										long time2 = System.currentTimeMillis();
										out.println("nlp call time"+(time2-time1) + " length: " + ansComments[k].length());
						                out.flush();


						                double [] sentiment  = new double[5];
						                for(StanfordCoreNlpDemo.sentiment n : val)
						                {
						                	//System.out.println("in fb" + i.value);
						                	sentiment[n.value]++;

						                	message_sender.values sending_values = new message_sender.values();
				                			sending_values.sentiment = sentiment;
				                			message_sender.send(sending_values, sess);
											//Thread.sleep(100);
						                }


									}

								}
						}



					}
				}
			/*	for (int j = 0; j < setOfRelevantResults.size(); j++) {
					// process the sentence here
					// System.out.println("/////////////////////////////////////");
					 System.out.println(setOfRelevantResults.get(j));
				} */
				day--;
				if (day == 0) {
					month--;
					if (month == 0) {
						year--;
						month = 12;
						day = 31;
					} else {
						switch (month) {
						case 1:
						case 3:
						case 5:
						case 7:
						case 8:
						case 10:
						case 12:
							day = 31;
							break;
						case 4:
						case 6:
						case 9:
						case 11:
							day = 30;
							break;
						case 2:
							day = (year % 4 == 0) ? 29 : 28;
							break;
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Thread.currentThread().interrupt();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
