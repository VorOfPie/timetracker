package com.vorofpie.timetracker.aspect;

import com.vorofpie.timetracker.domain.User;
import com.vorofpie.timetracker.error.exception.AccessDeniedException;
import com.vorofpie.timetracker.error.exception.ResourceNotFoundException;
import com.vorofpie.timetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import static com.vorofpie.timetracker.domain.RoleName.ADMIN;
import static com.vorofpie.timetracker.error.ErrorMessages.USER_NOT_FOUND_MESSAGE;
import static com.vorofpie.timetracker.error.ErrorMessages.ACCESS_DENIED_ERROR_MESSAGE;

@Aspect
@Component
@RequiredArgsConstructor
public class CheckEmailMatchAspect {

    private final UserRepository userRepository;

    @Pointcut("@annotation(com.vorofpie.timetracker.aspect.annotation.EmailMatchOrAdminAccess)")
    public void checkEmailMatch() {
    }

    @Before("checkEmailMatch() && args(userId,..)")
    public void before(JoinPoint joinPoint, Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        var userAuthorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        User userToOperate = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, userId)));

        boolean isEmailMatch = currentUserEmail.equals(userToOperate.getEmail());
        boolean isAdmin = userAuthorities.contains("ROLE_" + ADMIN.name());

        if (!isEmailMatch && !isAdmin) {
            throw new AccessDeniedException(ACCESS_DENIED_ERROR_MESSAGE);
        }
    }
}
