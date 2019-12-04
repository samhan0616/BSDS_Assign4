package ccis.neu.cs6650.dbserver.service.middle;

import ccis.neu.cs6650.dbserver.dao.ISkierDao;
import ccis.neu.cs6650.dbserver.entity.Skier;
import ccis.neu.cs6650.dbserver.util.GsonUtil;
import ccis.neu.cs6650.dbserver.util.sqs.SQSUtil;
import com.amazonaws.services.sqs.model.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SQSQueueService implements IQueueService<Skier> {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ISkierDao skierDao;

    @Override
    public void enqueue(Skier skier) {
        logger.info(GsonUtil.t2Json(skier));
        SQSUtil.sendMessage(GsonUtil.t2Json(skier));
    }

    @Override
    public List<Skier> dequeue(int num) {
        List<Skier> skiers = new ArrayList<>();
        while (skiers.size() < num) {
            List<Message> msgs = SQSUtil.receiveMessages();
            if (msgs == null || msgs.size() == 0) break;
            skiers.addAll(msgs.stream().map(msg -> GsonUtil.fromJson(msg.getBody(), Skier.class)).collect(Collectors.toList()));
            SQSUtil.deleteMessageBatch(msgs);
        }
       return skiers;
    }

    @PostConstruct
    public void schedulerUpdate(){
        for (int i = 0; i < 10 ; i ++) {
            new Thread(() -> {
               while(true){
                   List<Skier> skiers = dequeue(1000);
                   if (skiers == null || skiers.size() == 0) continue;
                   skierDao.createSkierHistoryBatch(skiers);
               }
            }).start();
        }
        System.out.println("bang!!!");
    }
}
