/*
 * Copyright (C) 2014 Naveen Malik
 *
 * Carnation Trout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Carnation Trout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Carnation Trout.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jewzaam.needless.compass;

import com.mongodb.Mongo;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.jewzaam.mongo.Configuration;
import org.jewzaam.mongo.MongoCRUD;

/**
 * Base class for any test that wants to use the embedded monto database flapdoodle.
 *
 * @author jewzaam
 */
public abstract class AbstractMongoTest {

    // Copied from  https://github.com/tommysdk/showcase/blob/master/mongo-in-mem/src/test/java/tommysdk/showcase/mongo/TestInMemoryMongo.java
    private static final String MONGO_HOST = "localhost";
    private static final int MONGO_PORT = 27777;
    private static final String IN_MEM_CONNECTION_URL = MONGO_HOST + ":" + MONGO_PORT;

    private static final String DB_NAME = "needlesscompasstest";

    protected static MongodExecutable mongodExe;
    protected static MongodProcess mongod;
    protected static Mongo mongo;
    protected static MongoCRUD crud;

    @BeforeClass
    public static void setupClass() throws Exception {
        MongodStarter runtime = MongodStarter.getDefaultInstance();
        mongodExe = runtime.prepare(new MongodConfig(de.flapdoodle.embed.mongo.distribution.Version.V2_4_3, MONGO_PORT, false));
        mongod = mongodExe.start();
        mongo = new Mongo(IN_MEM_CONNECTION_URL);

        Configuration config = new Configuration();
        config.setName(DB_NAME);
        config.setHost(MONGO_HOST);
        config.setPort(String.valueOf(MONGO_PORT));
        crud = new MongoCRUD(config);
    }

    @AfterClass
    public static void teardownClass() throws Exception {
        if (mongod != null) {
//            mongo.dropDatabase(DB_NAME);
            mongod.stop();
            mongodExe.stop();
        }
        crud = null;
    }
}
