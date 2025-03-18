package com.leverx.RatingSystemRest.Business.Service;

import com.leverx.RatingSystemRest.Infrastructure.Entities.GameObject;
import com.leverx.RatingSystemRest.Infrastructure.Entities.User;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.GameObjectRepository;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserRepository;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.GameObjectDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameObjectServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameObjectRepository gameObjectRepository;

    @InjectMocks
    private GameObjectService gameObjectService;

    @Test
    void testGetGameObjectsBySellerId_SellerNotFound() {

        int sellerId = 1;
        when(userRepository.findById(sellerId)).thenReturn(Optional.empty());


        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            gameObjectService.getGameObjectsBySellerId(sellerId);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Seller not found", exception.getReason());
    }

    @Test
    void testGetGameObjectsBySellerId_NoContent() throws Exception {

        int sellerId = 1;
        User mockUser = new User();
        when(userRepository.findById(sellerId)).thenReturn(Optional.of(mockUser));
        when(gameObjectRepository.getGameObjectsBySellerId(sellerId)).thenReturn(Collections.emptyList());


        ResponseEntity<List<GameObjectDto>> response = gameObjectService.getGameObjectsBySellerId(sellerId);


        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }


}
