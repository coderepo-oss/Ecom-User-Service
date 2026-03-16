package com.easybuy.UserService.Aspect;

import com.easybuy.UserService.Annotation.AuthorizeUser;
import com.easybuy.UserService.Exception.AddressException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Aspect
@Component
@Slf4j
public class AuthorizationAspect {

    @Before("@annotation(authorizeUser)")
    public void checkAuthorization(JoinPoint joinPoint, AuthorizeUser authorizeUser) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes == null) {
                log.error("ServletRequestAttributes is null");
                throw new AddressException(
                        "UNAUTHORIZED",
                        "Request context not found"
                );
            }

            String pathVariable = authorizeUser.pathVariable();

            // Get path variables from request
            @SuppressWarnings("unchecked")
            Map<String, String> pathVars = (Map<String, String>) attributes.getRequest()
                    .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

            if (pathVars == null || !pathVars.containsKey(pathVariable)) {
                log.error("Path variable '{}' not found", pathVariable);
                throw new AddressException(
                        "BAD_REQUEST",
                        "Path variable not found"
                );
            }

            String requestedUserIdStr = pathVars.get(pathVariable);
            Long requestedUserId = Long.parseLong(requestedUserIdStr);

            // Get userId from header (matches API Gateway header name)
            String headerUserIdStr = attributes.getRequest().getHeader("X-UserId");

            if (headerUserIdStr == null || headerUserIdStr.isEmpty()) {
                log.error("X-UserId header is missing - request must go through API Gateway");
                throw new AddressException(
                        "UNAUTHORIZED",
                        "User ID header is missing. Request must go through API Gateway"
                );
            }

            Long headerUserId;
            try {
                headerUserId = Long.parseLong(headerUserIdStr);
            } catch (NumberFormatException e) {
                log.error("Invalid X-UserId header format: {}", headerUserIdStr);
                throw new AddressException(
                        "BAD_REQUEST",
                        "Invalid user ID format in header"
                );
            }

            log.info("Authorization check - Header UserId: {}, Requested UserId: {}", headerUserId, requestedUserId);

            // Compare IDs
            if (!headerUserId.equals(requestedUserId)) {
                log.warn("Forbidden access - user: {} tried to access userId: {}", headerUserId, requestedUserId);
                throw new AddressException(
                        "FORBIDDEN",
                        "You are not allowed to access this resource"
                );
            }

            log.info("Authorization successful for user: {}", headerUserId);

        } catch (AddressException e) {
            throw e;
        } catch (Exception e) {
            log.error("Authorization aspect error: {}", e.getMessage(), e);
            throw new AddressException(
                    "UNAUTHORIZED",
                    "Authorization check failed: " + e.getMessage()
            );
        }
    }
}