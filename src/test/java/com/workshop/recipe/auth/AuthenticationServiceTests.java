package com.workshop.recipe.auth;

import com.workshop.recipe.config.JwtTokenUtil;
import com.workshop.recipe.token.Token;
import com.workshop.recipe.token.TokenRepository;
import com.workshop.recipe.token.TokenType;
import com.workshop.recipe.user.UserAccount;
import com.workshop.recipe.user.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.authentication.AuthenticationManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@DataJpaTest
public class AuthenticationServiceTests {

    @Mock
    private UserAccountRepository mockUserAccountRepository;

    @Mock
    private TokenRepository mockTokenRepository;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Test
    public void testLoginSuccessfully() {
        final int userId = 1;
        final int tokenId = 1;
        final String email = "james@gmail.com";
        final String password = "123456";
        final String jwtToken = "eyJhbGciOiJIUzI1NiJ9." +
                "eyJzdWIiOiJqYW1lc0BnbWFpbC5jb20iLCJpYXQiOjE2NzkyMjY4NjksImV4cCI6MTY3OTIyODMwOX0." +
                "W_MVanHeal0oHPlrqZgfpeQUPIOJi__v0aQpYeSsRHI";

        AuthenticationRequest req = new AuthenticationRequest().builder()
                .email(email)
                .password(password)
                .build();

        UserAccount user = new UserAccount().builder()
                .id(userId)
                .email(email)
                .password(password)
                .build();

        AuthenticationResponse res = new AuthenticationResponse().builder()
                .token(jwtToken)
                .build();

        Token saveToken = new Token().builder()
                .expired(false)
                .revoked(false)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .userAccount(user)
                .build();

        Token revokeToken = new Token().builder()
                .expired(true)
                .revoked(true)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .userAccount(user)
                .build();

        List<Token> tokens = new ArrayList<Token>();
        tokens.add(revokeToken);

        when(authenticationManager.authenticate(any())).then(invocation -> invocation.getArguments()[0]);
        when(mockUserAccountRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(mockTokenRepository.findByToken(jwtToken)).thenReturn(Optional.of(saveToken));
        when(mockTokenRepository.save(saveToken)).thenReturn(saveToken);
        when(mockTokenRepository.saveAll(tokens)).thenReturn(tokens);
        when(mockTokenRepository.findAllValidTokensByUser(userId)).thenReturn(tokens);
        when(jwtTokenUtil.generateToken(user)).thenReturn(jwtToken);

        AuthenticationResponse actualResult = authenticationService.authenticate(req);

        assertEquals(actualResult, res);

        verify(mockUserAccountRepository, times(1)).findByEmail(email);
        verify(mockTokenRepository, times(1)).save(saveToken);
        verify(mockTokenRepository, times(1)).saveAll(tokens);
        verify(mockTokenRepository, times(1)).findAllValidTokensByUser(userId);
        verify(jwtTokenUtil, times(1)).generateToken(user);
    }

    @Test
    public void testLoginFailed() {

    }
}
