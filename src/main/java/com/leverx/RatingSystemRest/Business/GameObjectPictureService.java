package com.leverx.RatingSystemRest.Business;

import com.leverx.RatingSystemRest.Infrastructure.Repositories.GameObjectPictureRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GameObjectPictureService {

    private GameObjectPictureRepository gameObjectPictureRepository;
}
