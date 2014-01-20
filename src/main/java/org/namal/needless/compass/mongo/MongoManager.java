/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.namal.needless.compass.mongo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.namal.needless.compass.model.Site;
import org.namal.needless.compass.model.Sites;

/**
 *
 * @author jewzaam
 */
public class MongoManager {

    private static final String COLLECTION_SITE = "site";
    private static final String COLLECTION_HOUSE = "house";
    private static final String COLLECTION_TRIP = "trip";

    private static MongoClient client;
    private static DB db;

    private static synchronized void initialize() {
        if (null != db) {
            return;
        }
        String dbHost = System.getenv("OPENSHIFT_MONGODB_DB_HOST");
        String dbPort = System.getenv("OPENSHIFT_MONGODB_DB_PORT");
        String dbName = System.getenv("OPENSHIFT_APP_NAME");
        if (dbName == null) {
            dbName = "needlesscompass";
        }
        String user = System.getenv("OPENSHIFT_MONGODB_DB_USERNAME");
        String password = System.getenv("OPENSHIFT_MONGODB_DB_PASSWORD");

        try {
            if (dbHost != null) {
                int port = Integer.decode(dbPort);

                client = new MongoClient(dbHost, port);
                db = client.getDB(dbName);
                if (db.authenticate(user, password.toCharArray()) == false) {
                    throw new RuntimeException("Failed to authenticate against db: " + dbName);
                }
            } else {
                client = new MongoClient();
                db = client.getDB(dbName);
            }
        } catch (UnknownHostException ex) {
            throw new RuntimeException("Unable to configure mongo", ex);
        }
    }

    public static void createSite(Site site) {
        if (null == db) {
            initialize();
        }
        try {
            db.requestStart();
            DBCollection coll = db.getCollection(COLLECTION_SITE);
            BasicDBObject dbSite = new BasicDBObject();

            dbSite.put("address", site.getAddress());

            if (site.getCategories() != null && site.getCategories().length == 0) {
                BasicDBList dbCategories = new BasicDBList();
                dbCategories.addAll(Arrays.asList(site.getCategories()));
                dbSite.put("categories", dbCategories);
            }

            dbSite.put("latitude", site.getLatitude().toString());
            dbSite.put("longitude", site.getLongitude().toString());
            dbSite.put("name", site.getName());

            coll.insert(dbSite);
        } finally {
            db.requestDone();
        }
    }

    public static Sites loadSites() {
        if (null == db) {
            initialize();
        }
        List<Site> siteList = new ArrayList<>();
        try {
            db.requestStart();
            DBCollection coll = db.getCollection(COLLECTION_SITE);
            DBCursor cur = coll.find();
            // TODO add protection for large set of data
            while (cur.hasNext()) {
                Site site = new Site();
                DBObject obj = cur.next();
                site.setAddress((String) obj.get("address"));
                site.setLatitude(new BigDecimal((String) obj.get("latitude")));
                site.setLongitude(new BigDecimal((String) obj.get("longitude")));
                site.setName((String) obj.get("name"));

                if (obj.get("categories") instanceof Collection) {
                    site.setCategories((String[]) ((Collection) obj.get("categories")).toArray());
                }
                siteList.add(site);
            }
        } finally {
            db.requestDone();
        }

        Sites sites = new Sites();
        sites.setSites((Site[]) siteList.toArray(new Site[]{}));
        return sites;
    }
}
