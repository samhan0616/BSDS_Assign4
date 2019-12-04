package ccis.neu.cs6650.dbserver.controller;

import ccis.neu.cs6650.dbserver.RangeEnum;
import ccis.neu.cs6650.dbserver.entity.TopLiftDTO;
import ccis.neu.cs6650.dbserver.entity.TopSkierDTO;
import ccis.neu.cs6650.dbserver.service.SkierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/skiers")
public class SkierController {

    @Autowired
    private SkierService skierService;


    @GetMapping("/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}")
    public String getTotalVertical(@PathVariable(name = "resortID")String resortID,
                         @PathVariable(name = "seasonID")String seasonID,
                         @PathVariable(name = "dayID")String dayID,
                         @PathVariable(name = "skierID")String skierID) {
        return String.valueOf(skierService.getTotalVertical(resortID, seasonID, dayID, skierID));
    }

    @GetMapping("/top_lift")
    public TopLiftDTO getTopLift(@RequestParam("from") Long from, @RequestParam("to")Long to) {
        return skierService.getTopLift(from, to);
    }
    @GetMapping("/top_skier")
    public TopSkierDTO getTopSkier(@RequestParam("from") Long from, @RequestParam("to")Long to) {
        return skierService.getTopSkier(from, to);
    }

    @GetMapping("/mean_visit")
    public String getMeanVisit(@RequestParam("from") Long from, @RequestParam("to")Long to, @RequestParam("scale")RangeEnum rangeEnum) {
        return skierService.getMeanVisit(from, to, rangeEnum);
    }

}
