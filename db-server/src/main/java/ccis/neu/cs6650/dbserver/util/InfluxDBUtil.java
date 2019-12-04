package ccis.neu.cs6650.dbserver.util;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class InfluxDBUtil {


    private static InfluxDB influxDB;
    private static String retentionPolicy = "autogen";

    static {
        influxDB = influxDbBuild();
    }

    /**
     * build influxdb
     *
     * @return
     */
    private static InfluxDB influxDbBuild() {
        try {
            if (influxDB == null) {
                InputStream is = InfluxDBUtil.class.getClassLoader().getResourceAsStream("InfluxDB.properties");
                Properties props = new Properties();
                props.load(is);
                influxDB = InfluxDBFactory.connect(props.getProperty("openurl"), props.getProperty("username"), props.getProperty("password"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return influxDB;
    }


    /**
     * query
     *
     * @param command
     *
     * @return
     */
    public static QueryResult query(String database, String command) {
        QueryResult res =  influxDB.query(new Query(command, database));
        close();
        return res;
    }

    /**
     * insert
     *
     * @param measurement
     *
     * @param tags
     *
     * @param fields
     *
     */
    public static void insert(String database, String measurement, Map<String, String> tags, Map<String, Object> fields, long time,
                       TimeUnit timeUnit) {
        Point point = Point.measurement(measurement).tag(tags).fields(fields).time(time, timeUnit).build();
        insert(database, point);
    }

    /**
     * insert by point
     * @param database
     * @param point
     */
    public static void insert(String database, Point point) {
        influxDB.write(database, retentionPolicy,point);
        close();
    }

    /**
     * bulk insert
     *
     * @param batchPoints
     */
    public static void batchInsert(BatchPoints batchPoints) {
        influxDB.write(batchPoints);
        close();
    }


    /**
     * close db
     */
    public static void close() {
        influxDB.close();
    }


}
