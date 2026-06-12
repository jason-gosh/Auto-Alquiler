package com.json.AutoAlquiler.services;

import com.json.AutoAlquiler.config.MissingRequiredElementException;
import com.json.AutoAlquiler.models.Country;
import com.json.AutoAlquiler.models.Identification;
import com.json.AutoAlquiler.models.Location;
import com.json.AutoAlquiler.models.User;
import com.json.AutoAlquiler.models.UserDetail;
import com.json.AutoAlquiler.models.UserRole;
import com.json.AutoAlquiler.repositories.UserDetailRepository;
import com.json.AutoAlquiler.repositories.UserRepository;
import com.json.AutoAlquiler.repositories.UserRoleRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;
    private final UserRoleRepository userRoleRepository;

    @Transactional
    public void saveNewUser(User user) {
        userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findByUsernameOrNewEmpty(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            user = new User();
            user.setDetail(new UserDetail());
            user.getDetail().setCountry(new Country());
            user.getDetail().setLocation(new Location());
            user.getDetail().setTypeIdentification(new Identification());
        }
        if (user.getDetail() == null) {
            user.setDetail(new UserDetail());
            user.getDetail().setCountry(new Country());
            user.getDetail().setLocation(new Location());
            user.getDetail().setTypeIdentification(new Identification());
        }
        return user;
    }

    public User findById(Long id) {
        return userRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("El usuario con ID " + id + " no existe en el sistema."));
    }

    public Optional<UserDetail> findDetailByDocAndType(Long idTypeId, String identification) {
        return userDetailRepository.findByDocAndType(idTypeId, identification);
    }

    public Optional<UserRole> findRoleByRoleName(String roleName) {
        return userRoleRepository.findByRoleName(roleName);
    }

    public User findCurrentAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AccessDeniedException("El usuario no está autenticado.");
        }
    
        return Optional.ofNullable(findByUsername(authentication.getName()))
            .orElseThrow(() -> new AccessDeniedException("Usuario no encontrado en el sistema."));
    }
    
    public User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalStateException("El usuario que solicito el proceso no esta autenticado.");
        }
        User user = findByUsername(authentication.getName());
        if (user == null) {
            throw new MissingRequiredElementException("El registro del usuario en sesión no pudo ser encontrado.");
        }
        return user;
    }
}
