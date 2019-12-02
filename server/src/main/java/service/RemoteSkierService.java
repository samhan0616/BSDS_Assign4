package service;

import com.google.gson.JsonSyntaxException;
import dao.ISkierDao;
import entity.LiftRide;
import entity.Skier;
import io.swagger.client.ApiException;
import io.swagger.client.api.SkiersApi;
import service.middle.IQueueService;
import service.middle.SQSQueueService;
import util.GsonUtil;
import util.HttpUtil;
import util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class RemoteSkierService implements ISkierService {

    private IQueueService<Skier> queueService;

    public RemoteSkierService() {
        this.queueService = new SQSQueueService();

    }

    @Override
    public void createSkierHistory(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uri = req.getRequestURI();
        BufferedReader br = req.getReader();
        String str = "";
        StringBuilder json = new StringBuilder();
        while((str = br.readLine()) != null){
            json.append(str);
        }
        br.close();
        LiftRide liftRide = null;
        try{
            liftRide = GsonUtil.fromJson(json.toString(), LiftRide.class);
        } catch (JsonSyntaxException e) {
            writeErrorMsg(resp);
            return;
        }
        uri = uri.replace("/server_war/", "");
        String[] paras = uri.split("/");
        if (!isValid(paras)) {
            writeErrorMsg(resp);
            return;
        }

        Skier skier = new Skier(Integer.parseInt(paras[2]), paras[4], paras[6],
                Integer.parseInt(paras[8]), liftRide.getTime(), liftRide.getLiftID() * 10);
        queueService.enqueue(skier);
        resp.setStatus(SUCCESS_CODE);
    }

    @Override
    public void getTotalVertical(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SkiersApi skiersApi = HttpUtil.skiersApi();
        String uri = req.getRequestURI();
        uri = uri.replace("/server_war", "");
        String[] paras = uri.split("/");
        PrintWriter printWriter = resp.getWriter();
        Integer res = 0;
        try {
            res = skiersApi.getSkierDayVertical(Integer.parseInt(paras[2]), paras[4], paras[6], Integer.parseInt(paras[8]));
        } catch (ApiException e) {
            e.printStackTrace();
        }
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
