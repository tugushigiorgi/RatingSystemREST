package com.leverx.RatingSystemRest.Business.Service;

import com.leverx.RatingSystemRest.Infrastructure.Repositories.TokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TokenService {
    private TokenRepository tokenRepository;
}
