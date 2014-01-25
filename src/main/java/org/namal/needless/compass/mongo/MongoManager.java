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
import org.namal.needless.compass.model.Category;
import org.namal.needless.compass.model.Coordinate;
import org.namal.needless.compass.model.House;
import org.namal.needless.compass.model.Houses;
import org.namal.needless.compass.model.Site;
import org.namal.needless.compass.model.Sites;

/**
 *
 * @author jewzaam
 */
public class MongoManager {

    private static final String COLLECTION_SITE = "site";
    private static final String COLLECTION_HOUSE = "house";
    private static final String COLLECTION_CATEGORY = "category";
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

        // and now initialize collections and ensure constraints
        { // to have short scope for collection initialization
            DBCollection coll = db.getCollection(COLLECTION_SITE);
            BasicDBObject uk = new BasicDBObject("longitude", 1)
                    .append("latitude", 1)
                    .append("owner", 1)
                    .append("unique", true);
            coll.createIndex(uk);
        }

        { // to have short scope for collection initialization
            DBCollection coll = db.getCollection(COLLECTION_HOUSE);
            BasicDBObject uk = new BasicDBObject("longitude", 1)
                    .append("latitude", 1)
                    .append("owner", 1)
                    .append("unique", true);
            coll.createIndex(uk);
        }

        { // to have short scope for collection initialization
            DBCollection coll = db.getCollection(COLLECTION_CATEGORY);
            BasicDBObject uk = new BasicDBObject("name", 1)
                    .append("unique", true);
            coll.createIndex(uk);
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

            dbSite.put("objectOwner", site.getObjectOwner());
            dbSite.put("address", site.getAddress());

            if (site.getCategories() != null && site.getCategories().length > 0) {
                BasicDBList dbCategories = new BasicDBList();
                dbCategories.addAll(Arrays.asList(site.getCategories()));
                dbSite.put("categories", dbCategories);
            }

            dbSite.put("latitude", site.getLatitude().toString());
            dbSite.put("longitude", site.getLongitude().toString());
            dbSite.put("name", site.getName());
            if (site.getRating() != null) {
                dbSite.put("rating", site.getRating().toString());
            }

            BasicDBObject query = new BasicDBObject()
                    .append("objectOwner", site.getObjectOwner())
                    .append("longitude", site.getLongitude().toString())
                    .append("latitude", site.getLatitude().toString());

            coll.update(query, dbSite, true, false);
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
                site.setObjectOwner((String) obj.get("objectOwner"));
                site.setAddress((String) obj.get("address"));
                site.setLatitude(new BigDecimal((String) obj.get("latitude")));
                site.setLongitude(new BigDecimal((String) obj.get("longitude")));
                site.setName((String) obj.get("name"));

                if (obj.get("rating") != null) {
                    site.setRating(new BigDecimal((String) obj.get("rating")));
                }

                if (obj.get("categories") instanceof Collection) {
                    List<String> categories = new ArrayList<>();
                    for (Object o : (Collection) obj.get("categories")) {
                        categories.add(o.toString());
                    }
                    site.setCategories(categories.toArray(new String[]{}));
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

    public static void createHouse(House house) {
        if (null == db) {
            initialize();
        }
        try {
            db.requestStart();
            DBCollection coll = db.getCollection(COLLECTION_HOUSE);
            BasicDBObject dbHouse = new BasicDBObject();

            dbHouse.put("objectOwner", house.getObjectOwner());
            dbHouse.put("address", house.getAddress());
            dbHouse.put("latitude", house.getLatitude().toString());
            dbHouse.put("longitude", house.getLongitude().toString());
            dbHouse.put("name", house.getName());

            if (house.getAnchors() != null && house.getAnchors().length > 0) {
                BasicDBList dbAnchors = new BasicDBList();
                for (Coordinate c : house.getAnchors()) {
                    BasicDBObject dbCoordinate = new BasicDBObject();
                    dbCoordinate.put("latitude", c.getLatitude().toString());
                    dbCoordinate.put("longitude", c.getLongitude().toString());
                    dbAnchors.add(dbCoordinate);
                }
                dbHouse.put("anchors", dbAnchors);
            }

            BasicDBObject query = new BasicDBObject()
                    .append("objectOwner", house.getObjectOwner())
                    .append("longitude", house.getLongitude().toString())
                    .append("latitude", house.getLatitude().toString());

            coll.update(query, dbHouse, true, false);
        } finally {
            db.requestDone();
        }
    }

    public static void deleteHouse(House house) {
        if (null == db) {
            initialize();
        }
        try {
            db.requestStart();
            DBCollection coll = db.getCollection(COLLECTION_HOUSE);
            BasicDBObject dbHouse = new BasicDBObject();

            BasicDBObject query = new BasicDBObject()
                    .append("objectOwner", house.getObjectOwner())
                    .append("longitude", house.getLongitude().toString())
                    .append("latitude", house.getLatitude().toString());

            coll.remove(query);
        } finally {
            db.requestDone();
        }
    }

    public static Houses loadHouses() {
        if (null == db) {
            initialize();
        }
        List<House> houseList = new ArrayList<>();
        try {
            db.requestStart();
            DBCollection coll = db.getCollection(COLLECTION_HOUSE);
            DBCursor cur = coll.find();
            // TODO add protection for large set of data
            while (cur.hasNext()) {
                House house = new House();
                DBObject obj = cur.next();
                house.setObjectOwner((String) obj.get("objectOwner"));
                house.setAddress((String) obj.get("address"));
                house.setLatitude(new BigDecimal((String) obj.get("latitude")));
                house.setLongitude(new BigDecimal((String) obj.get("longitude")));
                house.setName((String) obj.get("name"));

                if (obj.get("anchors") instanceof Collection) {
                    List<Coordinate> anchors = new ArrayList<>();
                    for (Object o : (Collection) obj.get("anchors")) {
                        DBObject dbCoordinate = (DBObject) o;
                        Coordinate c = new Coordinate();
                        c.setLatitude(new BigDecimal((String) dbCoordinate.get("latitude")));
                        c.setLongitude(new BigDecimal((String) dbCoordinate.get("longitude")));
                        anchors.add(c);
                    }
                    house.setAnchors(anchors.toArray(new Coordinate[]{}));
                }

                houseList.add(house);
            }
        } finally {
            db.requestDone();
        }

        Houses houses = new Houses();
        houses.setHouses((House[]) houseList.toArray(new House[]{}));
        return houses;
    }

    public static List<Category> loadCategories() {
        if (null == db) {
            initialize();
        }
        List<Category> categoryList = new ArrayList<>();
        try {
            db.requestStart();
            DBCollection coll = db.getCollection(COLLECTION_CATEGORY);
            DBCursor cur = coll.find();
            // TODO add protection for large set of data
            while (cur.hasNext()) {
                Category category = new Category();
                DBObject obj = cur.next();
                category.setName((String) obj.get("name"));
                category.setRateable((boolean) obj.get("rateable"));

                categoryList.add(category);
            }
        } finally {
            db.requestDone();
        }

        return categoryList;
    }
}
