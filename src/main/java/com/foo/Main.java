package com.foo;

import com.mongodb.MongoClient;
import dev.morphia.Morphia;

import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import dev.morphia.Datastore;
import dev.morphia.DatastoreImpl;
import dev.morphia.Morphia;

import java.net.ServerSocket;

public class Main {
    private static int mongoDBPort = 45678;

    private static MongodExecutable mongodExe;

    private static MongodProcess mongod;

    private static Datastore datastore;

    public static void main(String[] args) {
        try {
            String port = System.getProperty("mongodb.port");
            if (port != null) {
                mongoDBPort = Integer.valueOf(port);
            } else {
                ServerSocket s = new ServerSocket(0);
                mongoDBPort = s.getLocalPort();
                s.close();
            }

            IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
                    .net(new Net(mongoDBPort, Network.localhostIsIPv6())).build();

            MongodStarter runtime = MongodStarter.getDefaultInstance();

            mongodExe = runtime.prepare(mongodConfig);
            mongod = mongodExe.start();
            Morphia morphia = new Morphia();

            MongoClient mongo = new MongoClient("localhost", mongoDBPort);

            datastore = morphia.createDatastore(mongo, "cs-db-test");

            datastore.getDatabase().drop();
            System.out.println("All is good!");
        } catch (Exception e) {
            e.printStackTrace();
            if (mongod != null)
                mongod.stop();
            if (mongodExe != null)
                mongodExe.stop();
        }
    }
}
