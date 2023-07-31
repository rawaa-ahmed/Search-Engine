package MyCrawler;
import java.util.ArrayList;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import java.io.IOException;
import java.util.List;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Stack;
import java.util.concurrent.*;
import java.io.File;
import java.net.URI;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URISyntaxException;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashSet;
import java.util.Set;


public class ThreadCrawler implements Runnable {
    int MAX_DEPTH = 5;
    private static Object object1 = new Object();
    private static Object object2 = new Object();
    public static BlockingQueue<String> URLS = new LinkedBlockingDeque<>();
    private int ID;
    private static final int Max_Depth = 5;
    public static ConcurrentSkipListSet<String> VisitedURLs = new ConcurrentSkipListSet<>();

    private static WebCrawler out = new WebCrawler();
    public static Robot safe = new Robot();
    public static AddtoDB IDB = new AddtoDB();

    public static long VisitedURLS = IDB.GetSize();

    public ThreadCrawler() {


        System.out.println("Web crawler create");


    }


    @Override
    public void run() {


        while (true) {
            try {

                if (VisitedURLS > 5000) {


                    break;

                }


                String url;

                url = IDB.GetFirstURLToVisist();

                if (url == null) {
                    break;
                }


                try {
                    try {
                        url = this.normalizeUrl(url);
                    } catch (URISyntaxException e) {
                        continue;
                    }

                    //Take input from the fucking Database.


                    URL pass = new URL(url);
                    int c = 0;

                    while (!safe.robotallow(pass)) {


                        //Pop from the Database.
                        url = IDB.GetFirstURLToVisist();


                        if (c > 0) {
                            pass = new URL(url);
                        }

                        c++;
                    }

                    pass.toURI();
                    crawl(0, pass);

                    if (VisitedURLS > 5000) {
                        break;
                    }


                } catch (MalformedURLException e) {


                } catch (URISyntaxException e) {

                }
            } catch (Exception e) {

            }
        }


    }


    public String normalizeUrl(String url) throws URISyntaxException {
        if (url == null) {
            return null;
        }
        if (url.indexOf('?') != -1)
            url = url.substring(0, url.indexOf('?'));
        if (url.indexOf('#') != -1)
            url = url.substring(0, url.indexOf('#'));
        URI uri = new URI(url);
        if (!uri.isAbsolute()) {
            throw new URISyntaxException(url, "Not an absolute URL");
        }
        uri = uri.normalize();
        String path = uri.getPath();
        if (path != null) {
            path = path.replaceAll("//*/", "/");
            if (path.length() > 0 && path.charAt(path.length() - 1) == '/')
                path = path.substring(0, path.length() - 1);
        }
        return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(),
                path, uri.getQuery(), uri.getFragment()).toString();
    }


    private void crawl(int level, URL u) throws MalformedURLException {

        //  if(level<=Max_Depth)
        {
            if (VisitedURLS > 5000) {
                return;
            }
            if (level <= MAX_DEPTH) {

                String url = u.toString();

                Document doc = request(url);


                if (doc != null) {

                    {
                        for (Element link : doc.select("a[href]")) {


                            String next_link = link.absUrl("href");


                            {

                                if (next_link.indexOf(" ") == -1) {

                                    //Adding to the DB
                                    URL newlink = new URL(next_link);
                                    if (safe.robotallow(newlink)) {


                                        if (VisitedURLS > 5000) {
                                            break;
                                        }
                                        //Looping over the read links from each thread & Storing them in the DB & Crawlling over uncrawlled ones.
                                            if (IDB.VisitedContains(next_link) == false) {
                                                IDB.AddtoTovisit(url);
                                                crawl(level++, newlink);
                                            }
                                        }
                                    }

                                }
                            }


                        }
                    }


                } else {
                    return;
                }

            }
        }




    private Document request(String url) {
     //synchronized (object1)   {
        {
            try {

                Connection con = Jsoup.connect(url);
                Document doc = con.get();
                if (con.response().statusCode() == 200) {


                    {


                        if (true) {




                            VisitedURLS++;


                            IDB.Addtovisited(url); //To add to the visited Database also removes it from the ToVisit.

                        }


                    }

                    {

                    }


                    return doc;
                }

                return null;
            } catch (IOException e) {

                return null;

            }


        }
    }


}