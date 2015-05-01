
package deepSentiment;
import java.io.*;
import java.util.*;


import javax.xml.parsers.ParserConfigurationException;



import edu.stanford.nlp.io.*;

import edu.stanford.nlp.pipeline.*;


/** This class demonstrates building and using a Stanford CoreNLP pipeline. */
public class StanfordCoreNlpDemo {

	public static class sentiment
	{
		int sentence_id;
		int value;
		int pon;
	}

  /** Usage: java -cp "*" StanfordCoreNlpDemo [inputFile [outputTextFile [outputXmlFile]]]
 * @throws ParserConfigurationException */
  public static ArrayList<sentiment> get_sentiment(String t1) throws IOException, ParserConfigurationException {
    // set up optional output files


	  PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("bigdata.out")));
	  out.println("checking string input: " + t1);

    PrintWriter xmlOut = null;

      xmlOut = new PrintWriter("myxml");


    // Create a CoreNLP pipeline. This line just builds the default pipeline.
    // In comments we show how you can build a particular pipeline
     Properties props = new Properties();
     props.put("annotators", "tokenize, ssplit, pos, parse, depparse, sentiment");
     props.put("ner.model", "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
     props.put("ner.applyNumericClassifiers", "false");
     StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    //StanfordCoreNLP pipeline = new StanfordCoreNLP();

    // Initialize an Annotation with some text to be annotated. The text is the argument to the constructor.
    Annotation annotation;

      annotation = new Annotation(t1);


    // run all the selected Annotators on this text
    pipeline.annotate(annotation);

    // print the results to file(s)
    //pipeline.prettyPrint(annotation, out);

    StringWriter xmlout = new StringWriter();
    if (true) {
      pipeline.xmlPrint(annotation, xmlout);
    }
   // System.out.println("got xml output");
    String mystring = xmlout.toString();
    out.println(mystring);
    out.flush();
    int found = 0;
    String str = "sentence id";
    ArrayList<sentiment> sent_list = new ArrayList<sentiment>();
    int i1 = 13, i2 = 14, j1 = 32, j2 = 33;
    while(found != -1)
    {
    	found = mystring.indexOf(str, found+1);
 //   	System.out.println("found value: " + found);

    	if(found == -1)
    		break;
    	sentiment n = new sentiment();
    	if(sent_list.size()!=0){
    	if((sent_list.get(sent_list.size()-1).sentence_id==9)||(sent_list.get(sent_list.size()-1).sentence_id==999))
    	{i2++;	j1++; j2++;}
    	}

    	n.sentence_id = Integer.parseInt(mystring.substring(found+i1, found+i2));
    	n.value = Integer.parseInt(mystring.substring(found+j1, found+j2));
//    	System.out.println("senti : "+n.value);

    	if(mystring.charAt(found+46)=='N')
    		n.pon = -1;
    	else
    		n.pon = 1;
    	sent_list.add(n);
    }
 //   System.out.println("******************");
//    IOUtils.closeIgnoringExceptions(out);
//    IOUtils.closeIgnoringExceptions(xmlOut);
    return sent_list;

  }

}
