package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.dto.account.AuthResponseDTO;
import org.example.dto.account.GoogleAuthDTO;
import org.example.dto.account.LoginDTO;
import org.example.dto.account.RegisterDTO;
import org.example.entities.UserEntity;
import org.example.repositories.UserRepository;
import org.example.services.AccountService;
import org.example.services.GoogleApiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("api/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService service;
    private final GoogleApiService googleApiService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String RECAPTCHA_SECRET_KEY = "6LcxSFIoAAAAAHzMtEUTMHmdCQsrAclnMdC1XxsY";
    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDTO dto) {
        try {
            if (verifyRecaptcha(dto.getRecaptchaToken())) {
                var auth = service.login(dto);
                return ResponseEntity.ok(auth);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }
        catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterDTO registrationRequest) {
        try {
            service.register(registrationRequest);
            var auth = service.login(LoginDTO.builder()
                    .email(registrationRequest.getEmail())
                    .password(registrationRequest.getPassword())
                    .build());
            return ResponseEntity.ok(auth);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private boolean verifyRecaptcha(String recaptchaToken) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("secret", RECAPTCHA_SECRET_KEY);
        requestBody.add("response", recaptchaToken);

        ResponseEntity<Map> response = restTemplate.postForEntity(RECAPTCHA_VERIFY_URL, requestBody, Map.class);

        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.containsKey("success")) {
            boolean success = (boolean) responseBody.get("success");
            return success;
        }

        return false;
    }

    @PostMapping("google")
    public ResponseEntity<AuthResponseDTO> googleLogin(@RequestBody GoogleAuthDTO dto) {
        try {
            var userInfo = googleApiService.getUserInfo(dto.getAccess_token());
            var userAuth = userRepository.findByEmail(userInfo.getEmail());
            UserEntity user = null;
            if(!userAuth.isPresent()) {
                user = UserEntity
                        .builder()
                        .firstName(userInfo.getGiven_name())
                        .lastName(userInfo.getGiven_name())
                        .email(userInfo.getEmail())
                        .phone("00 00 000 00 00")
                        .password(passwordEncoder.encode("123456"))
                        .isGoogleAuth(true)
                        .build();
                userRepository.save(user);
            }
            else
                user=userAuth.get();
            var auth = service.getUserToken(user);
            return ResponseEntity.ok(auth);
        }catch(Exception ex) {
            System.out.println("---Error----"+ ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
