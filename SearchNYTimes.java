import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*TODO
 * 1. GET OWN AUTH ID
 * 2. TRACK BY KEYWORD IN ARTICLE AND CHECK IN CORRESPONDING COMMENT
 * 3. PASS KEY THROUGH CONSTRUCTER
 * 4. INTEGRATE WITH MAIN CODE
 */


//http://api.nytimes.com/svc/community/v2/comments/by-date/
//http://api.nytimes.com/svc/community/v2/comments/by-date/20101212.json?&offset=1&api-key=cd6479409e9d2849d2c8f0246ab895ea:10:68731810
//http://api.nytimes.com/svc/community/v2/comments/by-date/20101212.json?api-key=8c5b6144d7eb91d5acc87de2521d449b:8:58236592
public class SearchNYTimes {

	public static String makeURL(String date) {
		// check about offset
		return "http://api.nytimes.com/svc/community/v2/comments/by-date/"+date+".json?api-key=8c5b6144d7eb91d5acc87de2521d449b:8:58236592";
	}

	public static String[] processPara(String x) {
		final Matcher matcher = Pattern.compile("commentBody.*commentTitle")
				.matcher(x);
		if (matcher.find()) {
			// sets[i] = sets[i].substring(matcher.end()).trim();

			// System.out.println(matcher.group(0));
			x = matcher.group(0);
			x = x.substring(14, x.length() - 15);

			// rectify
			// x = x.replace("<br \/><br \/>", " ");
			x = x.replace("<br", " ");
			x = x.replace("/>", " ");
			x = x.replace("\\", " ");
		}
		// System.out.println("came inside function"+x);
		return x.split("(?<=[.!?])\\s* ");

	}

	public static String removeUrlAndAhref(String x) {

		x = x.replaceAll("<a href=(.+?)< /a>", "");
		return x.replaceAll("https?://\\S+\\s?", "");
	}

	public static void main(String[] args) {

		String keyword = "obama";

		InputStream is = null;
		ArrayList<String> setOfRelevantResults = new ArrayList<String>();
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
						String[] ans = processPara(sets[i]);
						// System.out.println("came here"+ans[0]);
						for (int j = 0; j < ans.length; j++) {
							// ans[j]= removeUrl(ans[j]);
							ans[j] = removeUrlAndAhref(ans[j]);
							setOfRelevantResults.add(ans[j]);
						}
					}
				}
				for (int j = 0; j < setOfRelevantResults.size(); j++) {
					// process the sentence here
					// System.out.println("/////////////////////////////////////");
					 System.out.println(setOfRelevantResults.get(j));
				}
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
