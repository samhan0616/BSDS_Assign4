package ccis.neu.cs6650.dbserver.dao;

import ccis.neu.cs6650.dbserver.RangeEnum;
import ccis.neu.cs6650.dbserver.entity.Skier;
import ccis.neu.cs6650.dbserver.entity.TopLiftDTO;
import ccis.neu.cs6650.dbserver.entity.TopSkierDTO;
import ccis.neu.cs6650.dbserver.util.InfluxDBUtil;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;
import org.springframework.stereotype.Repository;


import java.sql.Date;
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
@Repository
public class SkierDaoImpl implements ISkierDao {

  public static final int[] DAYS = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
  public static final String DATABASE = "Upic_Resort";
  public static final String MEASUREMENT = "skiers";
  public static final String RESORT_ID = "RESORT_ID";
  public static final String SKIER_ID = "SKIER_ID";
  public static final String LIFT_ID = "LIFT_ID";
  public static final int START_HOUR = 9;
  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


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
    tags.put("RESORT_ID_TAG", skier.getResortID().toString());
    tags.put("SKIER_ID_TAG", skier.getSkierID().toString());
    tags.put("LIFT_ID_TAG", String.valueOf(skier.getLiftID()));
    Map<String, Object> fields = new HashMap<>();
    fields.put("LIFT_ID", skier.getLiftID());
    fields.put("RESORT_ID", skier.getResortID());
    fields.put("SKIER_ID", skier.getSkierID());
    Long timestamp = getTimeStamp(skier.getSeasonID(), skier.getDayID(), skier.getTime());
    return Point.measurement(MEASUREMENT).tag(tags).fields(fields).time(timestamp, TimeUnit.MILLISECONDS).build();
  }


  @Override
  public Integer getTotalVertical(String resortID, String seasonID, String dayID, String skierID) {
    String query = "SELECT SUM(LIFT_ID) FROM " + MEASUREMENT
            + " where RESORT_ID_TAG = '" + resortID + "' and SKIER_ID_TAG = '" + skierID
            + "' and time >= '" +  sdf.format(new Date(getTimeStamp(seasonID, dayID, 0) + 28800000))
            + "' and time <= '" +  sdf.format(new Date(getTimeStamp(seasonID, dayID, 420) + 28800000)) + "'";
    QueryResult result = InfluxDBUtil.query(DATABASE, query);
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
        for (int i = 0 ; i < columns.size(); i++) {
          if (columns.get(i).equals("sum")) {
            ret = values.get(0).get(i);
            break;
          }
        }
      }
    }
    return ((Number)ret).intValue();
  }

  @Override
  public TopLiftDTO getTopLift(Long from, Long to) {
    String query = "SELECT TOP(count, LIFT_ID_TAG, 1) from (SELECT COUNT(LIFT_ID) FROM \"skiers\"" +
            " where time >= '" + sdf.format(new Date(from + 28800000)) +
            "' and time <= '" +   sdf.format(new Date(to + 28800000)) +
            "' GROUP BY LIFT_ID_TAG)";
    QueryResult result = InfluxDBUtil.query(DATABASE, query);
    if(result.getResults() == null){
      return null;
    }
    TopLiftDTO liftDTO = new TopLiftDTO();
    for (QueryResult.Result res : result.getResults()) {
      List<QueryResult.Series> series= res.getSeries();
      if (series == null || series.size() == 0) return null;
      for (QueryResult.Series serie : series) {
        List<List<Object>>  values = serie.getValues();
        List<String> columns = serie.getColumns();
        for (int i = 0 ; i < columns.size(); i++) {
          if (columns.get(i).equals("top")) {
            liftDTO.setTimes(((Number)values.get(0).get(i)).intValue());
          }
          if (columns.get(i).equals("LIFT_ID_TAG")) {
            liftDTO.setLift_ID( values.get(0).get(i).toString());
          }
        }
      }
    }
    return liftDTO;
  }

  @Override
  public TopSkierDTO getTopSkier(Long from, Long to) {
    String query = "SELECT TOP(count, SKIER_ID_TAG, 1) from (SELECT COUNT(SKIER_ID) FROM \"skiers\"" +
            " where time >= '" + sdf.format(new Date(from + 28800000)) +
            "' and time <= '" +   sdf.format(new Date(to + 28800000)) +
            "' GROUP BY SKIER_ID_TAG)";
    QueryResult result = InfluxDBUtil.query(DATABASE, query);
    if(result.getResults() == null){
      return null;
    }
    TopSkierDTO skierDTO = new TopSkierDTO();
    for (QueryResult.Result res : result.getResults()) {
      List<QueryResult.Series> series= res.getSeries();
      if (series == null || series.size() == 0) return null;
      for (QueryResult.Series serie : series) {
        List<List<Object>>  values = serie.getValues();
        List<String> columns = serie.getColumns();
        for (int i = 0 ; i < columns.size(); i++) {
          if (columns.get(i).equals("top")) {
            skierDTO.setTimes(((Number)values.get(0).get(i)).intValue());
          }
          if (columns.get(i).equals("SKIER_ID_TAG")) {
            skierDTO.setSkier_ID( values.get(0).get(i).toString());
          }
        }
      }
    }
    return skierDTO;
  }

  @Override
  public String getMeanVisit(Long from, Long to, RangeEnum rangeEnum) {
    String query = "SELECT mean(count) from (SELECT COUNT(SKIER_ID) FROM \"skiers\" where time >= '" + sdf.format(new Date(from + 28800000)) +
    "' and time <= '" +   sdf.format(new Date(to + 28800000)) + "') GROUP BY time(" + rangeEnum.getName() +" )";
    QueryResult result = InfluxDBUtil.query(DATABASE, query);
    if(result.getResults() == null){
      return null;
    }
    Double ret = 0.0;
    for (QueryResult.Result res : result.getResults()) {
      List<QueryResult.Series> series= res.getSeries();
      if (series == null || series.size() == 0) return null;
      for (QueryResult.Series serie : series) {
        List<List<Object>>  values = serie.getValues();
        List<String> columns = serie.getColumns();
        for (int i = 0 ; i < columns.size(); i++) {
          if (columns.get(i).equals("mean")) {
            ret = (Double)(values.get(0).get(i));
            break;
          }
        }
      }
    }
    return ret.toString();
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
    return cal.getTimeInMillis();
  }



}
