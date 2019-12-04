package ccis.neu.cs6650.dbserver.dao;


import ccis.neu.cs6650.dbserver.RangeEnum;
import ccis.neu.cs6650.dbserver.entity.Skier;
import ccis.neu.cs6650.dbserver.entity.TopLiftDTO;
import ccis.neu.cs6650.dbserver.entity.TopSkierDTO;

import java.util.List;

/**
 * @author create by Xiao Han 10/22/19
 * @version 1.0
 * @since jdk 1.8
 */
public interface ISkierDao {


  void createSkierHistory(Skier skier);

  void createSkierHistoryBatch(List<Skier> skiers);

  Integer getTotalVertical(String resortID, String seasonID, String dayID, String skierID);

  TopLiftDTO getTopLift(Long from, Long to);

  TopSkierDTO getTopSkier(Long from, Long to);

  String getMeanVisit(Long from, Long to, RangeEnum rangeEnum);
}
