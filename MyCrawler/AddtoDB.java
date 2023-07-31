package MyCrawler;


import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.model.Filters;


public class AddtoDB {

    public static MongoClient Client = new MongoClient("localhost", 27017);


    public long GetSize() {
        MongoDatabase T = Client.getDatabase("universe");
        MongoCollection<Document> coll = T.getCollection("Vurls");
        return coll.countDocuments();

    }

    //It returns the url from the uh.. first link to visit.
    synchronized public String GetFirstURLToVisist() {


        MongoDatabase KO = Client.getDatabase("InputURLs");


        MongoCollection<Document> collection = KO.getCollection("urls");

        System.out.println(collection.find().first());

        Document mydoc = collection.findOneAndDelete(new Document());

        while (mydoc == null) {
            mydoc = collection.findOneAndDelete(new Document());

        }

        return mydoc.get("url").toString();


    }


    public boolean VisitedContains(String url) {


        DB K = Client.getDB("InputURLs");
        DBCollection coll = K.getCollection("Vurls");
        BasicDBObject query = new BasicDBObject();
        query.put("url", url);
        if (coll.getCount() > 0) {

            DBCursor cursor = coll.find(query);
            return cursor.hasNext();


        } else {

            return false;
        }
    }


    public void AddtoTovisit(String url) {


        MongoDatabase KO = Client.getDatabase("InputURLs");


        MongoCollection<Document> collection = KO.getCollection("urls");


        KO.getCollection("urls").insertOne(new Document("url", url));




    }

     synchronized public void Addtovisited(String url) {

              DB K = Client.getDB("InputURLs");  //Seeing if it exists previously before adding.
              DBCollection coll = K.getCollection("Vurls");
              BasicDBObject query = new BasicDBObject();
              query.put("url", url);
             DBCursor cursor = coll.find(query);



        if (!cursor.hasNext()) {


            MongoDatabase KO = Client.getDatabase("InputURLs"); //change


            MongoCollection<Document> coll2 = KO.getCollection("urls");



            BasicDBObject query2 = new BasicDBObject("url", url);


            coll2.findOneAndDelete(query); //Deleting from the TOVisit before adding to visited.


            MongoCollection<Document> collection = KO.getCollection("Vurls"); //change


            collection.insertOne(new Document("url", url));

            System.out.println("ADDED TO VISITED  " + GetSize()+"   "   +  url);



        }


    }


}












