package org.example.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.example.dto.account.GoogleUserInfo;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class GoogleApiService {

    private final ObjectMapper objectMapper;
    private static final String USERINFO_ENDPOINT = "https://www.googleapis.com/oauth2/v1/userinfo?access_token=";


    public GoogleUserInfo getUserInfo(String accessToken) {
        String url = USERINFO_ENDPOINT + accessToken;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity <String> entity = new HttpEntity <> ("",headers);
        ResponseEntity<GoogleUserInfo> response = restTemplate.exchange(url, HttpMethod.GET, entity, GoogleUserInfo.class);
        var json = response.getBody();

        return json;
    }
}