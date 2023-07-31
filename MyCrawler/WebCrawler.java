package MyCrawler;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.*;
import java.util.Stack;
import java.util.HashSet;
import java.util.Set;
import java.net.URL;
public class WebCrawler {
    private List<Thread> threads;
    public static int count=0;
    public static File file;
  public static FileWriter fw;

    public static PrintWriter pw;
    public static File file2;
    public static FileWriter fw2;

    public static PrintWriter pw2;




private static int hash_count=0;
    public WebCrawler()
    {
        try {

            file=new File("Visited.txt");
            fw = new FileWriter(file);
            pw=new PrintWriter(fw);
            file2=new File("ToVisit.txt");
            fw2 = new FileWriter(file2);
            pw2=new PrintWriter(fw2);


        }catch(IOException e)
        {

        }


    }



    public static void increase_count()
    {
        count++;

    }

    public static int get_count()
    {
        return count;

    }




public void close_output()
{

    pw.close();

}
    public void close_TO_Visit()
    {

        pw2.close();

    }


    public  void log(String str) {
        synchronized (pw) {
            pw.println( str);
            pw.flush();
        }
    }
    public  void log_TOVisit(String str) {
        synchronized (pw2) {
            pw2.println( str);
            pw2.flush();
        }
    }

public void thread_running(int no_thread)
{
threads=new ArrayList<>();

for(int i=0;i<no_thread;i++)
{
threads.add(new Thread(new ThreadCrawler()));
threads.get(i).setName("thread no"+String.valueOf(i));

threads.get(i).start();

}



}
    public void thread_finish()
    {

        for(Thread thread :threads)
        {
            try
            {
                thread.join();

            }
            catch (InterruptedException e)
            {

                e.printStackTrace();

            }

        }


    }






}
