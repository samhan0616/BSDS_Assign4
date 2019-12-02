package util;

import io.swagger.client.ApiClient;
import io.swagger.client.api.ResortsApi;
import io.swagger.client.api.SkiersApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HttpUtil {

  private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);
  private static String baseUrl;

  static {
    InputStream is = HttpUtil.class.getClassLoader().getResourceAsStream("remote.properties");
    Properties props = new Properties();
    try {
      props.load(is);
    } catch (IOException e) {
    }
    baseUrl = props.getProperty("remoteurl");
  }


  /**
   *
   * @return
   */
  public static ApiClient apiClient() {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(baseUrl);
    return apiClient;
  }

  /**
   *
   * @param baseUrl
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