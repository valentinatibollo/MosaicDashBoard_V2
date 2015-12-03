package it.fsm.mosaic.mongodb;


import it.fsm.mosaic.model.CacheMongoObject;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

public class MongoDbUtil {
	private MongoClient mc;
	private CacheMongoObject cacheMOResult;
	private DB mosaicDB;
	private DBCollection DBcollection;
	
	public MongoDbUtil(String server, String serverPort, String dbname, String collection){
		try {
			mc = new MongoClient(server, Integer.parseInt(serverPort));
			cacheMOResult = new CacheMongoObject();
			mosaicDB = mc.getDB(dbname);
			DBcollection = mosaicDB.getCollection(collection);
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 
	
	
	public CacheMongoObject insertObj(String obj){
		try {
			DBObject bson = ( DBObject ) JSON.parse(obj);
			//adding MD5 hash as root attribute
			byte[] bytesOfMessage = obj.getBytes("UTF-8");

			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] thedigest = md.digest(bytesOfMessage);
			
			bson.put("jsonMD5", new String(thedigest));
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss.SSS");	
			
			bson.put("request_time", df.format(new java.util.Date()));
			
			BasicDBObject whereQuery = new BasicDBObject();
			System.out.println("MONGO DB insert obj "+new String(thedigest));
			whereQuery.put("jsonMD5", new String(thedigest));
			//whereQuery.put("concept", "LOC");
			DBCursor cursor = DBcollection.find(whereQuery);
			boolean found=false;
			while(cursor.hasNext()) {
			    found=true;
			    DBObject o = cursor.next();
			    cacheMOResult.setMongoObj(o);
              //  String results = o.get("results").toString() ; 
                if(o.get("results") !=null){
                	cacheMOResult.setStatus(2);
                }else{
                	cacheMOResult.setStatus(1);
                }
			}
			if(!found){
				DBcollection.insert(bson);
				System.out.println("MONGO DB insert obj");
				cacheMOResult.setMongoObj(bson);
				cacheMOResult.setStatus(0);
			}
			
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} finally{
			if(mc!=null){
				//TO DO
		//		mc.close();
			}
		}
		
		return cacheMOResult;
	}
	
	public void updateCacheObj(String results){
		
		BasicDBObject updateObj = new BasicDBObject();
		updateObj.append("$set", new BasicDBObject().append("results", JSON.parse(results)));
		
		DBcollection.update(cacheMOResult.getMongoObj(), updateObj);
		System.out.println("OBJ updated");
	
	}
}
