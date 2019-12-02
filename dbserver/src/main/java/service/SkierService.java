package service;

import com.amazonaws.services.sqs.model.Message;
import dao.ISkierDao;
import dao.SkierDaoImpl;
import entity.Skier;
import entity.StatisPojo;
import service.middle.IQueueService;
import service.middle.SQSQueueService;
import util.GsonUtil;
import util.InfluxDBUtil;
import util.StringUtil;
import util.sqs.SQSUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

public class SkierService {

    public static final int PARAM_INVALID_CODE = 400;
    public static final int SUCCESS_CODE = 201;

    private IQueueService<Skier> queueService;
    private ISkierDao skierDao;

    public SkierService() {
        this.queueService = new SQSQueueService();
        this.skierDao = new SkierDaoImpl();
        cronJob();
    }

    public void cronJob() {
        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        List<Skier> skiers = queueService.dequeue();
                        skierDao.createSkierHistoryBatch(skiers);
                    }
                }
            }).start();
        }
    }

    public void getTotalVertical(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uri = req.getRequestURI();
        uri = uri.replace("/dbserver_war", "");
        String[] paras = uri.split("/");
        if (!isValid(paras)) {
            writeErrorMsg(resp);
            return;
        }
        PrintWriter printWriter = resp.getWriter();
        Object res = skierDao.getTotalVertical(Integer.parseInt(paras[2]),
                paras[4], paras[6], Integer.parseInt(paras[8]));
        resp.setStatus(SUCCESS_CODE);
        printWriter.write(String.valueOf(res));
        printWriter.close();
    }

    private boolean isValid(String[] paras) {
        return StringUtil.isNum(paras[2]) && StringUtil.isNum(paras[8]);
    }

    private void writeErrorMsg(HttpServletResponse resp) throws IOException {
        resp.setStatus(PARAM_INVALID_CODE);
//    resp.setContentType("application/json;charset=utf-8");
//    PrintWriter printWriter = resp.getWriter();
//    printWriter.write(GsonUtil.toJson(Result.error(PARAM_INVALID_CODE, null)));
    }

}