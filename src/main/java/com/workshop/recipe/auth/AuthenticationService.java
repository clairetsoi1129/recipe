package com.workshop.recipe.auth;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest req);
    AuthenticationResponse authenticate(AuthenticationRequest req);
}
