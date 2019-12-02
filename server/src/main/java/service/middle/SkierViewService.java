package service.middle;

import dao.ISkierDao;
import dao.SkierDaoImpl;

/**
 * @author create by Xiao Han 10/25/19
 * @version 1.0
 * @since jdk 1.8
 */
public class SkierViewService {

  public static final int MV_REFRESH_GAP = 60000;

  private ISkierDao skierDao;

  public SkierViewService() {
    this.skierDao = new SkierDaoImpl();
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    startRefresh();
  }

  public void startRefresh() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        skierDao.createSkierMV(System.currentTimeMillis(), MV_REFRESH_GAP);
        while (true) {
          try {
            Thread.sleep(MV_REFRESH_GAP);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }).start();
  }
}
