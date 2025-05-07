package in.bushansirgur.authify.controller;

import in.bushansirgur.authify.io.ProfileRequest;
import in.bushansirgur.authify.io.ProfileResponse;
import in.bushansirgur.authify.service.EmailService;
import in.bushansirgur.authify.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping // Optional: you can add a base path like "/api"
public class ProfileController {

    private final ProfileService profileService;
    private final EmailService emailService;

    // ✅ Constructor with both dependencies
    public ProfileController(ProfileService profileService, EmailService emailService) {
        this.profileService = profileService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse register(@Valid @RequestBody ProfileRequest request) {
        ProfileResponse response = profileService.createProfile(request);
        emailService.sendWelcomeEmail(response.getEmail(), response.getName());
        return response;
    }

    @GetMapping("/profile")
    public ProfileResponse getProfile(@CurrentSecurityContext(expression = "authentication?.name") String email) {
        return profileService.getProfile(email);
    }
}
