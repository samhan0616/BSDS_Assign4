package util;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.ResortsApi;
import io.swagger.client.api.SkiersApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HttpUtil {

  private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

  public static final String BASEURL = "http://localhost:8001/";


  /**
   *
   * @return
   */
  public static ApiClient apiClient() {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(BASEURL);
    return apiClient;
  }

  /**
   *
   * @return
   */
  public static ResortsApi resortsApi() {
    ApiClient apiClient = apiClient();
    return new ResortsApi(apiClient);
  }

  /**
   *
   * @param baseUrl
   * @return
   */
  public static SkiersApi skiersApi() {
    ApiClient apiClient = apiClient();
    return new SkiersApi(apiClient);
  }

}