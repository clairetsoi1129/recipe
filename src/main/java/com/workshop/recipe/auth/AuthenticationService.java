package com.workshop.recipe.auth;

import com.workshop.recipe.config.JwtTokenUtil;
import com.workshop.recipe.user.Role;
import com.workshop.recipe.user.UserAccount;
import com.workshop.recipe.user.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtTokenUtil jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest req) {
        var user = UserAccount.builder()
                .firstname(req.getFirstname())
                .lastname(req.getLastname())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(Role.REGISTERED_USER)
                .build();
        var savedUser = userAccountRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
//        var token = Token.builder()
//                .userAccount(savedUser)
//                .token(jwtToken)
//                .tokenType(TokenType.BEARER)
//                .isExpired(false)
//                .isRevoked(false)
//                .build();

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getEmail(),
                        req.getPassword()
                )
        );
        var user = userAccountRepository.findByEmail(req.getEmail())
                .orElseThrow(()->new UsernameNotFoundException("User email not found"));

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }
}
