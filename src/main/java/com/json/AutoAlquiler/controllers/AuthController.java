package com.json.AutoAlquiler.controllers;

import com.json.AutoAlquiler.models.User;
import com.json.AutoAlquiler.models.UserRole;
import com.json.AutoAlquiler.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(
        @Valid @ModelAttribute("user") User user,
        @RequestParam("selectedRole") String selectedRole,
        BindingResult result,
        Model model
    ) {
        try {
            if (result.hasErrors()) {
                return "auth/register";
            }

            if (userService.findByUsername(user.getUsername()) != null) {
                model.addAttribute("error", "Ese nombre de usuario ya está en uso");
                return "auth/register";
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEnabled(true);

            UserRole userRole = new UserRole();

            if ("ROLE_ADMIN".equalsIgnoreCase(selectedRole)) {
                userRole.setRoleName("ROLE_ADMIN");
            } else {
                userRole.setRoleName("ROLE_CLIENT");
            }

            userRole.setUser(user);
            user.setRole(userRole);
            userService.saveNewUser(user);
            return "redirect:/auth/login?success";
        } catch (Exception e) {
            System.out.println("[ALERTA] Error al crear al usuario causado por: " + e.getMessage());
            model.addAttribute("error", "Error técnico en el servidor - REINTENTE MAS TARDE ");
            return "auth/register";
        }
    }
}
