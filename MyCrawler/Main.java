package MyCrawler;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.List;



import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;

public class Main {
	
	
//Mongo code.



	
	
	public List<String>urls=new ArrayList<>();
   



    public static void main(String args[])
    {



    	
    	Scanner input=new Scanner(System.in);
      /* String file_name=input.next();

             
        try
        {
        
    		

            //reading seeds



BufferedReader seeds=new BufferedReader(new FileReader(file_name+".txt"));

            String input_from_file;

            while ((input_from_file = seeds.readLine()) != null) {

                if(!ThreadCrawler.VisitedURLs.contains(input_from_file))

                ThreadCrawler.URLS.add(input_from_file);

                }


            seeds.close();

        }
        catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        catch(Exception e)
        {
            return;

        }
*/

//urls.add("https://www.geeksforgeeks.org/stack-class-in-java/");
//urls.add("https://codeforces.com/");
//urls.add("https://www.w3schools.com/java/java_user_input.asp");
//urls.add("https://www.codota.com/code/java/methods/java.net.URL/%3Cinit%3E");

System.out.println("Please Enter The number of Thread");
int no_thread=input.nextInt();//take number of thread


//urls.add("https://codeforces.com/");

WebCrawler crawler=new WebCrawler();

//crawler.begin(no_thread);
        input.close();
crawler.thread_running(no_thread);
crawler.thread_finish();



    }
}
