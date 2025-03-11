package com.leverx.RatingSystemRest.Business.Service;

import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
}
