package MyCrawler;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCursor;
//import org.bson.Document;
import java.io.FileReader;
import java.io.BufferedReader;

import com.mongodb.client.MongoClients;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.regex.Pattern;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.ArrayList;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;


import java.util.Arrays;
import com.mongodb.Block;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;
//import org.bson.Document;
//import org.bson.codecs.JsonObjectCodecProvider;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
class Word{
	String word;
	float tf;
	int ferq=0;
	public Word(String w)
	{
		word=w;
	}
}
class index{    //class containing the word, importance,idf,tf and docs
	String word;
	
	Vector<Doc> inv = new Vector<Doc>();
	float idf;
	
public	index(String w)
	{
		word=w;
		
		idf = (float)(5000.0/(float)inv.size());
	}
float calcidf()
{
	idf = (float)(5000.0 / (float)inv.size());
	return idf;
}

}
 
class Doc{  //class doc contains the html documents,urls and vector of indeces 
	Vector<Word>words=new Vector<Word>();
	Document doc;
	String url;
	Doc(Document doc,String u)
	{
		this.doc=doc;
		url=u;
	}
}
 class indexer0 {             //class indexer0 conatain all data members and methods used to index words and documents
	Vector<Doc>docs=new Vector<Doc>(); // all docs in my database
	Vector<String>stopwords=new Vector<String>();     //all stop words in database
	boolean finished=false;                           //flag to indicate that there is no more documents
    int n=0;      
    Vector<index>indexed=new Vector<index>();   
   int v=0;//index used to keep up with the size of vector docs
//database variables to create and insert in database	
    MongoClient mongoClient;                        
	MongoDatabase db;
	MongoCollection<org.bson.Document> docu,words,stops,indx,history;
	Vector<String>stems=new Vector<String>();
//constructor creates database if it does not exist and initialze collections
	public  indexer0()
	{	
		mongoClient = MongoClients.create("mongodb://localhost:27017/universe");
	    db = mongoClient.getDatabase("universe");
	    docu=db.getCollection("docs");
	    words=db.getCollection("words");
		stops=db.getCollection("stopwords");
		indx=db.getCollection("index");
		history=db.getCollection("history");
		org.bson.Document h=new org.bson.Document("history",stems);
        history.insertOne(h);
	//	System.out.println(stops==null);	
	}
	@SuppressWarnings("unchecked")
	public int getm(String S)
	{
		int m=0,ind=0;
		while(!S.isEmpty() && !S.matches("(?i)^[aeiouy].*$"))
		{
			if(S.length()==1)
				return m;
			S=S.substring(1);
		}
		while(!S.isEmpty() && (S.matches("^.*[aeiou]$") || S.matches("^.*[qwrtpsdfghjklzxcvbnm]y$")))
			{
			if(S.length()==1)
				return m;
			S=S.substring(0, S.length()-1);
			}
		while(!S.isEmpty()) {
			ind=0;
			while(S.matches("(?i)^[aeiouy].*$"))
			{
				if(S.length()==1)
					return m;
				S=S.substring(1);
				ind=0;
			}
			while(!S.matches("(?i)^[aeiouy].*$"))
			{
				if(S.length()==1)
				{
					m++;
					return m;
				}
				S=S.substring(1);
				ind=1;
			}
			if(ind==1)
				m++;
		}
		return m;
	}
	
	public String stem(String s)
	{
		s = s.toLowerCase();
		int m, suc=0;
		m=getm(s);
		if(s.endsWith("sses"))
			s=s.substring(0, s.lastIndexOf("es"));
		else if(s.endsWith("ies"))
			s=s.substring(0, s.lastIndexOf("es"));
		else if(s.endsWith("ss"));
		else if(s.endsWith("s"))
			s=s.substring(0, s.lastIndexOf("s"));
			
		if(s.endsWith("eed") && getm(s.substring(0, s.lastIndexOf("eed")))>0)
			s=s.substring(0, s.lastIndexOf("d"));
		else if(s.endsWith("ed")&& Pattern.compile("[aeiouy]").matcher(s.substring(0, s.lastIndexOf("ed"))).find())
			{
			s=s.substring(0, s.lastIndexOf("ed"));
			suc=1;
			}
		else if(s.endsWith("ing") && Pattern.compile("[aeiouy]").matcher(s.substring(0, s.lastIndexOf("ing"))).find() )
			{
			s=s.substring(0, s.lastIndexOf("ing"));
			suc=1;
			}
		if(suc==1)
		{
			if(s.endsWith("at"))
				s=s.concat("e");
			if(s.endsWith("bl"))
				s=s.concat("e");
			if(s.endsWith("iz"))
				s=s.concat("e");
			if(s.charAt(s.length()-1)==s.charAt(s.length()-2) && 
					s.charAt(s.length()-1)!='l' && s.charAt(s.length()-1)!='s'&& s.charAt(s.length()-1)!='z')
				s=s.substring(0, s.length()-1);
			m=getm(s);
			if(s.length()>2)
			if(m==1 && !("aeiouy".indexOf(s.charAt(s.length()-2)) != -1  && "aeiouy".indexOf(s.charAt(s.length()-1)) == -1  &&  "aeiouy".indexOf(s.charAt(s.length()-3)) == -1))
				s=s.concat("e");
		}
		if(s.endsWith("y") && Pattern.compile("[aeiou]").matcher(s.substring(0, s.lastIndexOf("y"))).find())
			s=s.substring(0, s.length()-1).concat("i");
		if(s.length()>1)
		switch(s.charAt(s.length()-2)) {
		case 'a':
			if(s.endsWith("ational") && getm(s.substring(0, s.lastIndexOf("ational")))>0)
				s=s.substring(0, s.lastIndexOf("ational")).concat("ate");
			else if(s.endsWith("tional") && getm(s.substring(0, s.lastIndexOf("tional")))>0)
				s=s.substring(0, s.lastIndexOf("tional")).concat("tion");
			break;
		case 'c':
			if(s.endsWith("enci") && getm(s.substring(0, s.lastIndexOf("enci")))>0)
				s=s.substring(0, s.lastIndexOf("enci")).concat("ence");
			else if(s.endsWith("anci") && getm(s.substring(0, s.lastIndexOf("anci")))>0)
				s=s.substring(0, s.lastIndexOf("anci")).concat("ance");
			break;
		case 'e':
			if(s.endsWith("izer") && getm(s.substring(0, s.lastIndexOf("izer")))>0)
				s=s.substring(0, s.lastIndexOf("izer")).concat("ize");
			break;
		case 'l':
			if(s.endsWith("abli") && getm(s.substring(0, s.lastIndexOf("abli")))>0)
				s=s.substring(0, s.lastIndexOf("abli")).concat("able");
			else if(s.endsWith("alli") && getm(s.substring(0, s.lastIndexOf("alli")))>0)
				s=s.substring(0, s.lastIndexOf("alli")).concat("al");
			else if(s.endsWith("entli") && getm(s.substring(0, s.lastIndexOf("entli")))>0)
				s=s.substring(0, s.lastIndexOf("entli")).concat("ent");
			else if(s.endsWith("eli") && getm(s.substring(0, s.lastIndexOf("eli")))>0)
				s=s.substring(0, s.lastIndexOf("eli")).concat("e");
			else if(s.endsWith("ousli") && getm(s.substring(0, s.lastIndexOf("ousli")))>0)
				s=s.substring(0, s.lastIndexOf("ousli")).concat("ous");
			break;
		case 'o':
			if(s.endsWith("ization") && getm(s.substring(0, s.lastIndexOf("ization")))>0)
				s=s.substring(0, s.lastIndexOf("ization")).concat("ize");
			else if(s.endsWith("ation") && getm(s.substring(0, s.lastIndexOf("ation")))>0)
				s=s.substring(0, s.lastIndexOf("ation")).concat("ate");
			else if(s.endsWith("ator") && getm(s.substring(0, s.lastIndexOf("ator")))>0)
				s=s.substring(0, s.lastIndexOf("ator")).concat("ate");
			break;
		case 's':
			if(s.endsWith("alism") && getm(s.substring(0, s.lastIndexOf("alism")))>0)
				s=s.substring(0, s.lastIndexOf("alism")).concat("al");
			else if(s.endsWith("iveness") && getm(s.substring(0, s.lastIndexOf("iveness")))>0)
				s=s.substring(0, s.lastIndexOf("iveness")).concat("ive");
			else if(s.endsWith("fulness") && getm(s.substring(0, s.lastIndexOf("fulness")))>0)
				s=s.substring(0, s.lastIndexOf("fulness")).concat("ful");
			else if(s.endsWith("ousness") && getm(s.substring(0, s.lastIndexOf("ousness")))>0)
				s=s.substring(0, s.lastIndexOf("ousness")).concat("ous");
			break;
		case 't':
			if(s.endsWith("aliti") && getm(s.substring(0, s.lastIndexOf("aliti")))>0)
				s=s.substring(0, s.lastIndexOf("aliti")).concat("al");
			else if(s.endsWith("iviti") && getm(s.substring(0, s.lastIndexOf("iviti")))>0)
				s=s.substring(0, s.lastIndexOf("iviti")).concat("ive");
			else if(s.endsWith("biliti") && getm(s.substring(0, s.lastIndexOf("biliti")))>0)
				s=s.substring(0, s.lastIndexOf("biliti")).concat("ble");
			break;
		}
		if(s.endsWith("icate") && getm(s.substring(0, s.lastIndexOf("icate")))>0)
			s=s.substring(0, s.lastIndexOf("icate")).concat("ic");
		else if(s.endsWith("ative") && getm(s.substring(0, s.lastIndexOf("ative")))>0)
			s=s.substring(0, s.lastIndexOf("ative"));
		else if(s.endsWith("alize") && getm(s.substring(0, s.lastIndexOf("alize")))>0)
			s=s.substring(0, s.lastIndexOf("ize"));
		else if(s.endsWith("iciti") && getm(s.substring(0, s.lastIndexOf("iciti")))>0)
			s=s.substring(0, s.lastIndexOf("iti"));
		else if(s.endsWith("ical") && getm(s.substring(0, s.lastIndexOf("ical")))>0)
			s=s.substring(0, s.lastIndexOf("al"));
		else if(s.endsWith("ful") && getm(s.substring(0, s.lastIndexOf("ful")))>0)
			s=s.substring(0, s.lastIndexOf("ful"));
		else if(s.endsWith("ness") && getm(s.substring(0, s.lastIndexOf("ness")))>0)
			s=s.substring(0, s.lastIndexOf("ness"));
		
		if(s.endsWith("al") && getm(s.substring(0, s.lastIndexOf("al")))>1)
			s=s.substring(0, s.lastIndexOf("al"));
		else if(s.endsWith("ance") && getm(s.substring(0, s.lastIndexOf("ance")))>1)
			s=s.substring(0, s.lastIndexOf("ance"));
		else if(s.endsWith("ence") && getm(s.substring(0, s.lastIndexOf("ence")))>1)
			s=s.substring(0, s.lastIndexOf("ence"));
		else if(s.endsWith("er") && getm(s.substring(0, s.lastIndexOf("er")))>1)
			s=s.substring(0, s.lastIndexOf("er"));
		else if(s.endsWith("ic") && getm(s.substring(0, s.lastIndexOf("ic")))>1)
			s=s.substring(0, s.lastIndexOf("ic"));
		else if(s.endsWith("able") && getm(s.substring(0, s.lastIndexOf("able")))>1)
			s=s.substring(0, s.lastIndexOf("able"));
		else if(s.endsWith("ible") && getm(s.substring(0, s.lastIndexOf("ible")))>1)
			s=s.substring(0, s.lastIndexOf("ible"));
		else if(s.endsWith("ant") && getm(s.substring(0, s.lastIndexOf("ant")))>1)
			s=s.substring(0, s.lastIndexOf("ant"));
		else if(s.endsWith("ement") && getm(s.substring(0, s.lastIndexOf("ement")))>1)
			s=s.substring(0, s.lastIndexOf("ement"));
		else if(s.endsWith("ment") && getm(s.substring(0, s.lastIndexOf("ment")))>1)
			s=s.substring(0, s.lastIndexOf("ment"));
		else if(s.endsWith("ent") && getm(s.substring(0, s.lastIndexOf("ent")))>1)
			s=s.substring(0, s.lastIndexOf("ent"));
		else if(s.endsWith("sion") && getm(s.substring(0, s.lastIndexOf("ion")))>1)
			s=s.substring(0, s.lastIndexOf("sion"));
		else if(s.endsWith("tion") && getm(s.substring(0, s.lastIndexOf("ion")))>1)
			s=s.substring(0, s.lastIndexOf("tion"));
		else if(s.endsWith("ou") && getm(s.substring(0, s.lastIndexOf("ou")))>1)
			s=s.substring(0, s.lastIndexOf("ou"));
		else if(s.endsWith("ism") && getm(s.substring(0, s.lastIndexOf("ism")))>1)
			s=s.substring(0, s.lastIndexOf("ism"));
		else if(s.endsWith("ate") && getm(s.substring(0, s.lastIndexOf("ate")))>1)
			s=s.substring(0, s.lastIndexOf("ate"));
		else if(s.endsWith("iti") && getm(s.substring(0, s.lastIndexOf("iti")))>1)
			s=s.substring(0, s.lastIndexOf("iti"));
		else if(s.endsWith("ous") && getm(s.substring(0, s.lastIndexOf("ous")))>1)
			s=s.substring(0, s.lastIndexOf("ous"));
		else if(s.endsWith("ive") && getm(s.substring(0, s.lastIndexOf("ive")))>1)
			s=s.substring(0, s.lastIndexOf("ive"));
		else if(s.endsWith("ize") && getm(s.substring(0, s.lastIndexOf("ize")))>1)
			s=s.substring(0, s.lastIndexOf("ize"));
		
		if(s.endsWith("e") && getm(s.substring(0, s.lastIndexOf("e")))>1)
			s=s.substring(0, s.lastIndexOf("e"));
		
		if(s.length()>3)
		if(s.endsWith("e") && getm(s.substring(0, s.lastIndexOf("e")))==1 && !("aeiouy".indexOf(s.charAt(s.length()-3)) != -1  && "aeiouy".indexOf(s.charAt(s.length()-2)) == -1  &&  "aeiouy".indexOf(s.charAt(s.length()-4)) == -1))
		{
			s=s.substring(0, s.lastIndexOf("e"));
			
		}
			
		m= getm(s);
		if(m>1 && s.endsWith("ll"))
			s=s.substring(0, s.lastIndexOf("l"));
		return s;
	}
	
@SuppressWarnings("unchecked")
public void getStopWords(String path)
{
if(stops.countDocuments()==0)
{
	String str="";
		try {
			Scanner read=new Scanner(new File(path));
			while(read.hasNext())
			{
				str+=read.next()+" ";
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stopwords.addAll(Arrays.asList(str.split("[^a-zA-Z]+")));
		org.bson.Document w=new org.bson.Document("words",stopwords);
		stops.insertOne(w);
}
else
{
	org.bson.Document  doc=stops.find().first();

	stopwords.addAll((ArrayList<String>)doc.get("words"));
}

}
public synchronized void  getDocs(String urls)
	{
	//	System.out.println("urls.txt");	
	if(docu.countDocuments()==0)
	{	try {
			Scanner read=new Scanner(new File(urls));
			while(read.hasNext())
			{
				try {
					String url=read.next();
					Document doc= Jsoup.connect(url).get();
					
					docs.add(new Doc(doc,url));
				this.notifyAll();
					System.out.println(url);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			read.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finished=true;
		System.out.println(Thread.currentThread().getName()+" finished");
	}
	else
		finished=true;
	}
public boolean notStop(String str)
	{
		int z=stopwords.size();
		str=str.toLowerCase();
		for(int i=0;i<z;i++)
			if(str.equals(stopwords.get(i)))
					return false;
		return true;
	}
public int ifheader(String h)
{
	int i=0;
	String header="h";
	
	for(i=1;i<6;i++)
	{
		if(h.equals(header+i))
			return i;
	}
	i=0;
	return i;
}
public  void indexing(int i)
	{
	Doc doc=docs.get(i);
	String txt[]=doc.doc.text().split("[^a-zA-Z]+");      
	String u=docs.get(i).url;
	
	org.bson.Document d=new org.bson.Document("_id",u);
	Vector<String>ids=new Vector<String>();

	int k=0;
	for(String t:txt)
	{
	
		t=t.toLowerCase();
		if(notStop(t))
		{//	System.out.println(t+" "+u);
			t=stem(t);
	if(t!=""&&stems.contains(t)==false)
	{	
	stems.add(t);
	}
	
	if(ids.contains(t)==false)
	{
	doc.words.add(new Word(t));
	doc.words.get(k).ferq++;
	k++;
	ids.add(t);
	}
	else
	{
		
	int l=ids.indexOf(t);
//	System.out.println(l);
	doc.words.get(l).ferq++;
	//System.out.println("k:"+k);
	
	}
		
		}
	}
d.append("words", ids);
	
	docu.insertOne(d);
	}
	public void ProcessDocs()
	{
		while(true)
		{
			int i=0;
			synchronized(this)
			{
				if(n<docs.size())
				{
					i=n;
					n++;
				}
				else
				{
					if(finished)
						break;
					try {
					//	this.notifyAll();
						this.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
				}
				System.out.println(Thread.currentThread().getName()+" indexes doc:"+i);	
			}
			indexing(i);
		}
		System.out.println(Thread.currentThread().getName()+" finished");
	}
	public void inv_indexing(int i)
	{
		String t=stems.get(i);
	    index in=new index(t);
		Vector<String> urls=new Vector<String>();
		
		for(int l=0;l<docs.size();l++)
		{
			float tf=0;			
			int z=docs.get(l).words.size();
			org.bson.Document w=new org.bson.Document("_id",t).append("docs", urls);

			for(int k=0;k<z;k++)
			{
			 if(docs.get(l).words.get(k).word.equals(t))
			 {
				 in.inv.add(docs.get(l));
				 urls.add(docs.get(l).url);
				 tf=(float)(docs.get(l).words.get(k).ferq*1.0/z);
				 docs.get(l).words.get(k).tf=tf;
			org.bson.Document m=new org.bson.Document("word",t).append("doc",docs.get(l).url).append("tf", tf);
            indx.insertOne(m);
			 }
			 
			}
		}
		in.idf=(float)docs.size()/urls.size();
		org.bson.Document w=new org.bson.Document("_id",t).append("docs", urls).append("idf", (float)docs.size()/urls.size());
		words.insertOne(w);
		indexed.add(in);
	}
public void inverted_indexing()
{
	
	while(true)
	{
		int i=0;
		synchronized(this)
		{
			if(v<stems.size())
			{
				i=v;
			    v++;
			}
			else
			{
				break;
			}
			System.out.println(Thread.currentThread().getName()+" inv_indexes word:"+i);	
		}
		inv_indexing(i);
	}

}

}
class Indexer implements Runnable {
	private indexer0 ind;
	String urls;
	boolean invert_index=false;
	Indexer(String stps,String u)
	{
	ind=new indexer0();
	ind.getStopWords(stps);
	urls=u;
	}
	public void run()
	{
		if(Thread.currentThread().getName().equals("invert"))
		{
			//while(!invert_index);
			
			ind.inverted_indexing();
		}
		else if(Thread.currentThread().getName().equals("getDocs"))
			ind.getDocs(urls);
		else
		{
			ind.ProcessDocs();
		}
		}
	public void closeDatabase()
	{
	ind.mongoClient.close();	
	}	
	}

public class database {
	public database() {
		// TODO Auto-generated constructor stub
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*    	Scanner input=new Scanner(System.in);

  System.out.println("Please Enter The number of Thread");
  int no_thread=input.nextInt();//take number of thread

  WebCrawler crawler=new WebCrawler();
  
          input.close();
  crawler.thread_running(no_thread);
  crawler.thread_finish();
*/

  
  
		
		
			Indexer indx=new Indexer("stopwords.txt","urls.txt");
			
			Thread th1=new Thread(indx);
			Thread ths[]=new Thread[100];
			th1.setName("getDocs");
			th1.start();
			for(int i=1;i<=50;i++)
			{
				ths[i-1]=new Thread(indx);
				ths[i-1].setName("indexer-"+i);
				ths[i-1].start();
				System.out.println(ths[i-1].getName());
			} 
			for(int i=1;i<=50;i++)
			{
				
				try {
					ths[i-1].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
			
			indx.invert_index=true;
			if(indx.invert_index)
			for(int i=51;i<=100;i++)
			{
				ths[i-1]=new Thread(indx);
				ths[i-1].setName("invert");
				ths[i-1].start();
				System.out.println(ths[i-1].getName()+i);
			} 
			try {
				th1.join();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			for(int i=51;i<=100;i++)
			{
				
				try {
					ths[i-1].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
			
		indx.closeDatabase();		
			
	}
}

