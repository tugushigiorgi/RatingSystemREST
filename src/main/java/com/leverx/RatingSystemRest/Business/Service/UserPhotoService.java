package com.leverx.RatingSystemRest.Business.Service;

import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserPhotoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserPhotoService {
    private final UserPhotoRepository userPhotoRepository;
}
