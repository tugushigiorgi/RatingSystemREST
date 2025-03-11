package com.leverx.RatingSystemRest.Business.Service;

import com.leverx.RatingSystemRest.Infrastructure.Repositories.GameObjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GameObjectService {
    private GameObjectRepository gameObjectRepository;
}
