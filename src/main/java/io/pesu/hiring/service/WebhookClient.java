package io.pesu.hiring.service;

import io.pesu.hiring.model.FinalQueryRequest;
import io.pesu.hiring.model.GenerateWebhookRequest;
import io.pesu.hiring.model.GenerateWebhookResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WebhookClient {

  private final RestTemplate restTemplate;

  @Value("${app.generate-url}")
  private String generateUrl;

  @Value("${app.default-webhook-url}")
  private String defaultWebhookUrl;

  @Value("${app.name}")
  private String name;

  @Value("${app.regNo}")
  private String regNo;

  @Value("${app.email}")
  private String email;

  public WebhookClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public GenerateWebhookResponse generateWebhook() {
    GenerateWebhookRequest payload = new GenerateWebhookRequest(name, regNo, email);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<GenerateWebhookRequest> entity = new HttpEntity<>(payload, headers);

    ResponseEntity<GenerateWebhookResponse> resp =
        restTemplate.exchange(generateUrl, HttpMethod.POST, entity, GenerateWebhookResponse.class);

    if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
      throw new IllegalStateException("Failed to generate webhook: HTTP " + resp.getStatusCode());
    }
    return resp.getBody();
  }

  public void postFinalQuery(String webhookUrl, String accessToken, String finalQuery) {
    String target = (webhookUrl != null && !webhookUrl.isBlank())
        ? webhookUrl
        : defaultWebhookUrl;

    FinalQueryRequest body = new FinalQueryRequest(finalQuery);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", accessToken);

    HttpEntity<FinalQueryRequest> entity = new HttpEntity<>(body, headers);

    ResponseEntity<String> resp =
        restTemplate.exchange(target, HttpMethod.POST, entity, String.class);

    if (!resp.getStatusCode().is2xxSuccessful()) {
      throw new IllegalStateException("Final query post failed: HTTP " + resp.getStatusCode());
    }
  }
}
