package controller;

import entity.Skier;
import service.ISkierService;
import service.RemoteSkierService;
import service.SkierService;
import util.GsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class SkierServlet extends HttpServlet {

    private ISkierService skierService;

    public SkierServlet() {
        this.skierService = new RemoteSkierService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        skierService.createSkierHistory(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        skierService.getTotalVertical(req, resp);
    }
}
