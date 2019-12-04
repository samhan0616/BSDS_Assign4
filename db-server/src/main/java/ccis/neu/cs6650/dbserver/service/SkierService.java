package ccis.neu.cs6650.dbserver.service;


import ccis.neu.cs6650.dbserver.RangeEnum;
import ccis.neu.cs6650.dbserver.dao.ISkierDao;
import ccis.neu.cs6650.dbserver.entity.TopLiftDTO;
import ccis.neu.cs6650.dbserver.entity.TopSkierDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SkierService {

    public static final int PARAM_INVALID_CODE = 400;
    public static final int SUCCESS_CODE = 201;

    @Autowired
    private ISkierDao skierDao;


    public Integer getTotalVertical(String resortID, String seasonID, String dayID, String skierID)  {
        Integer res = skierDao.getTotalVertical(resortID, seasonID, dayID, skierID);
        return res;
    }

    public TopLiftDTO getTopLift(Long from, Long to) {
        return skierDao.getTopLift(from, to);
    }

    public TopSkierDTO getTopSkier(Long from, Long to) {
        return skierDao.getTopSkier(from, to);
    }

    public String getMeanVisit(Long from, Long to, RangeEnum rangeEnum) {
        return skierDao.getMeanVisit(from, to, rangeEnum);
    }
}