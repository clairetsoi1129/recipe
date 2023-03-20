package com.workshop.recipe.auth;

import com.workshop.recipe.config.JwtTokenUtil;
import com.workshop.recipe.token.Token;
import com.workshop.recipe.token.TokenRepository;
import com.workshop.recipe.token.TokenType;
import com.workshop.recipe.user.Role;
import com.workshop.recipe.user.UserAccount;
import com.workshop.recipe.user.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService{
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest req) {
        var user = UserAccount.builder()
                .firstname(req.getFirstname())
                .lastname(req.getLastname())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(Role.REGISTERED_USER)
                .build();
        var savedUser = userAccountRepository.save(user);
        var jwtToken = jwtTokenUtil.generateToken(user);
        saveUserToken(savedUser, jwtToken);

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

        var jwtToken = jwtTokenUtil.generateToken(user);
        revokeAllUserToken(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }

    private void revokeAllUserToken(UserAccount user){
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(t->{
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void saveUserToken(UserAccount user, String jwtToken) {
        var token = Token.builder()
                .userAccount(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
}
