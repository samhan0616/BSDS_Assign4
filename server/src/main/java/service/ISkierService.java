package service;

import entity.Skier;
import util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author create by Xiao Han 10/22/19
 * @version 1.0
 * @since jdk 1.8
 */
public interface ISkierService {
  public static final int PARAM_INVALID_CODE = 400;
  public static final int SUCCESS_CODE = 201;

  void createSkierHistory(HttpServletRequest req, HttpServletResponse resp) throws IOException;

  void getTotalVertical(HttpServletRequest req, HttpServletResponse resp) throws IOException;

}
