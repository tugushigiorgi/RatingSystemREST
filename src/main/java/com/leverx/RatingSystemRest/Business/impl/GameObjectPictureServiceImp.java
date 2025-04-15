package com.leverx.RatingSystemRest.Business.impl;

import com.leverx.RatingSystemRest.Business.Interfaces.GameObjectPictureService;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.GameObjectPictureRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GameObjectPictureServiceImp implements GameObjectPictureService {

    private GameObjectPictureRepository gameObjectPictureRepository;
}
