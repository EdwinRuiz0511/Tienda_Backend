/*
    Clase de apoyo: centraliza el acceso a los datos del usuario autenticado (quién es, si es administrador),
    para no repetir esta lógica en cada service
*/

package com.tienda.backend.security.serviceSecurity;

import com.tienda.backend.utils.RolesConstantes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public String obtenerUsernameAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public boolean esAdministrador() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + RolesConstantes.ADMIN));
    }
}