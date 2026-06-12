package com.json.AutoAlquiler.controllers;

import com.json.AutoAlquiler.models.User;
import com.json.AutoAlquiler.models.UserDetail;
import com.json.AutoAlquiler.services.CountryService;
import com.json.AutoAlquiler.services.IdentificationService;
import com.json.AutoAlquiler.services.LocationService;
import com.json.AutoAlquiler.services.ReservationHistService;
import com.json.AutoAlquiler.services.UserDetailService;
import com.json.AutoAlquiler.services.UserService;
import com.json.AutoAlquiler.utils.SqliteErrorTranslator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserDetailService userDetailService;
    private final IdentificationService identificationService;
    private final CountryService countryService;
    private final LocationService locationService;
    private final ReservationHistService reservationHistService;

    @GetMapping("/lookup")
    @ResponseBody
    public Map<String, Object> lookupUser(
        @RequestParam("idTypeId") Long idTypeId, 
        @RequestParam("identification") String identification
    ) {
        Map<String, Object> response = new HashMap<>();

        userService.findDetailByDocAndType(idTypeId, identification).ifPresentOrElse(
            userDetail -> {
                boolean hasLocation = userDetail.getLocation().getId() != null;
                List<String> preloadedDeps = hasLocation
                    ? locationService.getDepartmentsByCountry(userDetail.getLocation().getCountry().getId())
                    : List.of();

                List<String> preloadedMunis = hasLocation
                    ? locationService.getMunicipalitiesByCountryAndDepartment(
                          userDetail.getLocation().getCountry().getId(),
                          userDetail.getLocation().getDepartment()
                      )
                    : List.of();

                response.put("found", true);
                response.put("name", userDetail.getName());
                response.put("phone", userDetail.getPhone());
                response.put("address", userDetail.getAddress());
                response.put("countryId", userDetail.getCountry().getId());
                response.put("locationId", userDetail.getLocation().getId());
                response.put("department", userDetail.getLocation().getDepartment());
                response.put("municipality", userDetail.getLocation().getMunicipality());
                response.put("preloadedDepartments", preloadedDeps);
                response.put("preloadedMunicipalities", preloadedMunis);
            },
            () -> {
                Long userId = userService.findCurrentAuthenticatedUser().getId();
                response.put("found", false);
                response.put("userId", userId);
            }
        );
        System.out.println("KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK");
        System.out.println(response);
        return response;
    }

    @GetMapping("/profile")
    public String showProfileForm(Authentication authentication, Model model) {
        String username = authentication.getName();
        User currentUser = userService.findByUsernameOrNewEmpty(username);
        UserDetail currentUserDetail = currentUser.getDetail();
        boolean hasLocation = currentUserDetail.getLocation().getId() != null;

        List<String> preloadedDeps = hasLocation
            ? locationService.getDepartmentsByCountry(currentUserDetail.getLocation().getCountry().getId())
            : List.of();

        List<String> preloadedMunis = hasLocation
            ? locationService.getMunicipalitiesByCountryAndDepartment(
                  currentUserDetail.getLocation().getCountry().getId(),
                  currentUserDetail.getLocation().getDepartment()
              )
            : List.of();
        model.addAttribute("user", currentUser);
        model.addAttribute("userDetail", currentUserDetail);
        model.addAttribute("identifications", identificationService.findAll());
        model.addAttribute("countries", countryService.findAll());
        model.addAttribute("preloadedDepartments", preloadedDeps);
        model.addAttribute("preloadedMunicipalities", preloadedMunis);
        model.addAttribute("reservationsHist", reservationHistService.getReservationHistByOwner(currentUser));

        return "clients/profile";
    }

    @PostMapping("/profile/save")
    public String saveOrUpdateProfile(
        @ModelAttribute("userDetail") UserDetail userDetail,
        @RequestParam("country") Long countryId,
        @RequestParam("department") String department,
        @RequestParam("municipality") String municipality,
        Authentication authentication,
        RedirectAttributes flash
    ) {
        try {
            userDetail.setLocation(locationService.validateAndGetLocationByUniqueKeyOrElseThrow(countryId, department, municipality));
            userDetailService.upsertUserDetail(authentication.getName(), userDetail);
            return "redirect:/users/profile?success=true";
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("errorMessage", e.getMessage());
        } catch (JpaSystemException e) {
            String userFriendlyError = SqliteErrorTranslator.translate(e);
            flash.addFlashAttribute("errorMessage", userFriendlyError);
        } catch (Exception e) {
            System.out.println("🚨 Error crítico al guardar los detalles de usuario causado por: " + e);
            flash.addFlashAttribute("errorMessage", "No se pudo guardar los datos del perfil");
        }
        return "redirect:/users/profile?error";
    }
}
