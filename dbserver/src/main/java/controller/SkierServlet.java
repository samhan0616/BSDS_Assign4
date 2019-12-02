package controller;

import service.SkierService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SkierServlet extends HttpServlet {

    private SkierService skierService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(1);
        skierService.getTotalVertical(req, resp);
    }

    @Override
    public void init() throws ServletException {
        this.skierService = new SkierService();
    }
}
