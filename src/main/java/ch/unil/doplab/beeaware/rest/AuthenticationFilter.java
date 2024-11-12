package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.Domain.Role;
import ch.unil.doplab.beeaware.domain.ApplicationState;
import ch.unil.doplab.beeaware.domain.Token;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final String REALM = "example";
    private static final String AUTHENTICATION_SCHEME = "Bearer";
    @Inject
    private ApplicationState state;
    @Context
    private ResourceInfo resourceInfo;
    private final Logger logger = Logger.getLogger(AuthenticationFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Get the Authorization header from the request
        String authorizationHeader =
                requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Validate the Authorization header
        if (!isTokenBasedAuthentication(authorizationHeader)) {
            abortWithUnauthorized(requestContext);
            return;
        }

        // Extract the token from the Authorization header
        String token = authorizationHeader
                .substring(AUTHENTICATION_SCHEME.length()).trim();

        try {
            // Validate the token and get the user roles
            Role userRole = validateTokenAndGetRole(token);

            // Check if the user has the required roles for this method
            Method method = resourceInfo.getResourceMethod();
            if (method.isAnnotationPresent(RoleRequired.class)) {
                RoleRequired roleRequired = method.getAnnotation(RoleRequired.class);

                boolean authorized = false;
                for (Role role : roleRequired.value()) {
                    if (userRole == role) {
                        authorized = true;
                        break;
                    }
                }

                if (!authorized) {
                    requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                            .entity("Access denied: Missing role")
                            .build());
                    return;
                }
            }

            if (method.isAnnotationPresent(SameID.class)) {
                Token currentUserToken = getTokenIfExist(token);
                logger.log(Level.INFO, "Current Beezzer: {0}", currentUserToken);
                if (currentUserToken.getRole() != Role.ADMIN) {
                    Long requestedBeezzerId = extractBeezzerIdFromRequest(requestContext);
                    logger.log(Level.INFO, "BeezzerID user: {0}", requestedBeezzerId);

                    if (requestedBeezzerId == null || !requestedBeezzerId.equals(currentUserToken.getBeezzerId())) {
                        requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                                .entity("Access denied: Beezzer ID inccorect")
                                .build());
                    }
                }
            }

        } catch (Exception e) {
            abortWithUnauthorized(requestContext);
        }
    }

    private Long extractBeezzerIdFromRequest(ContainerRequestContext requestContext) {
        String userIdParam = requestContext.getUriInfo().getPathParameters().getFirst("id");
        try {
            return userIdParam != null ? Long.parseLong(userIdParam) : null;
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid user ID format: {0}", userIdParam);
            return null;
        }
    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {

        // Check if the Authorization header is valid
        // It must not be null and must be prefixed with "Bearer" plus a whitespace
        // The authentication scheme comparison must be case-insensitive
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {

        // Abort the filter chain with a 401 status code response
        // The WWW-Authenticate header is sent along with the response
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .header(HttpHeaders.WWW_AUTHENTICATE,
                                AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"")
                        .build());
    }

    private Role validateTokenAndGetRole(String token) throws Exception {
        validateToken(token);
        if (!state.getTokenService().isAuthorizedToAccess(token)) {
            throw new Exception("Token invalide");
        }

        Token tokenValidate = getTokenIfExist(token);

        // Méthode fictive pour récupérer les rôles de l'utilisateur à partir du token
        return tokenValidate.getRole();
    }

    private Token getTokenIfExist(String token) {
        for (Map.Entry<Long, Token> tok : state.getTokenService().getTokens().entrySet()) {
            if (tok.getValue().getKey().equals(token)) {
                logger.log(Level.INFO, "Valid token : {0}", tok.getValue().getKey());
                return tok.getValue();
            }
        }
        logger.log(Level.WARNING, "No valid token for given token");
        return null;
    }

    private void validateToken(String token) throws Exception {
        if (!state.getTokenService().isAuthorizedToAccess(token)) {
            throw new Exception("No valid token founded");
        }
    }
}