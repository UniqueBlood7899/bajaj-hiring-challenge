package io.pesu.hiring.runner;

import io.pesu.hiring.model.GenerateWebhookResponse;
import io.pesu.hiring.service.SqlSolutionService;
import io.pesu.hiring.service.WebhookClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements ApplicationRunner {

  private final WebhookClient client;
  private final SqlSolutionService sqlService;

  public StartupRunner(WebhookClient client) {
    this.client = client;
    this.sqlService = new SqlSolutionService();
  }

  @Override
  public void run(ApplicationArguments args) {
    try {
      System.out.println("[*] Generating webhook...");
      GenerateWebhookResponse gw = client.generateWebhook();
      System.out.println("[*] Got webhook: " + gw);

      String finalQuery = sqlService.buildFinalQuery();
      System.out.println("[*] Final SQL prepared:\n" + finalQuery);

      System.out.println("[*] Posting final query to webhook...");
      client.postFinalQuery(gw.getWebhook(), gw.getAccessToken(), finalQuery);

      System.out.println("[✓] Done. Final query submitted.");
    } catch (Exception ex) {
      System.err.println("[x] Error: " + ex.getMessage());
      ex.printStackTrace();
    }
  }
}
