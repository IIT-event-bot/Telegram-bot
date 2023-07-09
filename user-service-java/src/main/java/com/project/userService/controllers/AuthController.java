package com.project.userService.controllers;

import com.project.userService.services.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
//@CrossOrigin
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final SessionService service;
    private final AuthenticationManager manager;

    @GetMapping
    public ResponseEntity<?> test(@RequestParam("id") int id,
                                  @RequestParam("first_name") String firstName,
                                  @RequestParam("auth_date") int authDate,
                                  @RequestParam("hash") String hash,
                                  @RequestParam(value = "username", required = false, defaultValue = "") String username,
                                  @RequestParam(value = "photo_url", required = false) String photoUrl,
                                  @RequestParam(value = "last_name", required = false) String lastName,
                                  HttpServletResponse response) throws IOException {
        var token = service.save(id, hash, username);
        var user = service.getUserByToken(token);
        Authentication authUser = new UsernamePasswordAuthenticationToken(
                user,
                token,
                List.of(new SimpleGrantedAuthority(user.getRole().getName().name()))
        );
//        Authentication auth = manager.authenticate(authUser);
        SecurityContextHolder.getContext().setAuthentication(authUser);

        response.addCookie(new Cookie("session-token", token));
//        response.sendRedirect("http://127.0.0.1/");
        return ResponseEntity.ok().build();
    }
}
