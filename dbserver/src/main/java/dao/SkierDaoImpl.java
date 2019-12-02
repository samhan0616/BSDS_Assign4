package dao;

import entity.Skier;
import entity.StatisPojo;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;
import util.InfluxDBUtil;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author create by Xiao Han 10/22/19
 * @version 1.0
 * @since jdk 1.8
 */
public class SkierDaoImpl implements ISkierDao {

  public static final int[] DAYS = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
  public static final String DATABASE = "Upic_Resort";
  public static final String MEASUREMENT = "skiers";
  public static final String RESORT_ID = "RESORT_ID";
  public static final String SKIER_ID = "SKIER_ID";
  public static final int START_HOUR = 9;
  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");


  public Calendar cal = Calendar.getInstance();

  @Override
  public void createSkierHistory(Skier skier) {
    Point point = getPoint(skier);
    InfluxDBUtil.insert(DATABASE, point);
  }

  @Override
  public void createSkierHistoryBatch(List<Skier> skiers) {
    BatchPoints batchPoints = BatchPoints.database(DATABASE).consistency(InfluxDB.ConsistencyLevel.ALL).build();
    for (Skier skier : skiers) {
      batchPoints.point(getPoint(skier));
    }
    InfluxDBUtil.batchInsert(batchPoints);
  }

  private Point getPoint(Skier skier) {
    Map<String, String> tags = new HashMap<>();
    tags.put(RESORT_ID, skier.getResortID().toString());
    tags.put(SKIER_ID, skier.getSkierID().toString());
    Map<String, Object> fields = new HashMap<>();
    fields.put("LIFT_ID", skier.getLiftID() * 10);
    Long timestamp = getTimeStamp(skier.getSeasonID(), skier.getDayID(), skier.getTime());
    return Point.measurement(MEASUREMENT).tag(tags).fields(fields).time(timestamp, TimeUnit.MILLISECONDS).build();
  }


  @Override
  public Object getTotalVertical(Integer resortID, String seasonID, String dayID, Integer skierID) {
    String query = "SELECT SUM(LIFT_ID) FROM " + MEASUREMENT
            + " where RESORT_ID = '" + resortID + "' and SKIER_ID = '" + skierID
            + "' and time >= '" +  sdf.format(new Date(getTimeStamp(seasonID, dayID, 0) + 28800000))
            + "' and time <= '" +  sdf.format(new Date(getTimeStamp(seasonID, dayID, 420) + 28800000)) + "'";

    QueryResult result = InfluxDBUtil.query(DATABASE, query);
    System.out.println(query);
    if(result.getResults() == null){
      return 0;
    }
    Object ret = 0;
    for (QueryResult.Result res : result.getResults()) {
      List<QueryResult.Series> series= res.getSeries();
      if (series == null || series.size() == 0) return 0;
      for (QueryResult.Series serie : series) {
        List<List<Object>>  values = serie.getValues();
        List<String> columns = serie.getColumns();
        System.out.println(values);
        System.out.println(columns);
        for (int i = 0 ; i < columns.size(); i++) {
          if (columns.get(i).equals("sum")) {
            ret = values.get(0).get(i);
            break;
          }
        }
      }
    }
    return ret;
  }

  private Long getTimeStamp(String seasonID, String dayID, int time) {
    int year = cal.get(Calendar.YEAR);
    int season = Integer.parseInt(seasonID) - 1;
    int day = Integer.parseInt(dayID);
    int month;
    for (month = season * 3; month < DAYS.length && day > DAYS[month]; day -=DAYS[month], month++);
    day = day <= DAYS[month] ?  day : DAYS[month];
    int hour = START_HOUR + (time / 60);
    int min = time % 60;
    cal.set(year, month, day, hour, min, 0);
    System.out.println("time: " + time + "hour: " + hour);
    System.out.println(cal.getTime());
    return cal.getTimeInMillis();
  }





}
