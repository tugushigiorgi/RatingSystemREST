package com.leverx.RatingSystemRest.Business;


import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserRepository;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.DetailedUserDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.UserInfoDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetDetailedRegisteredUsersByUsername_NoContent() {

        String username = "giorgii";
        when(userRepository.GetRegisteredSellerByUsername(username)).thenReturn(Collections.emptyList());
        ResponseEntity<List<DetailedUserDto>> response = userService.GetDetailedRegisteredUsersByUsername(username);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetTopRatedSellers_SellersNotFound() {
         when(userRepository.findTop5RatedSellers()).thenReturn(null);
         ResponseEntity<List<UserInfoDto>> response = userService.getTopRatedSellers();
         assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

//    @Test
//    void testGetSellerProfileById_SellerNotFound() {
//        int userId = 1;
//        when(userRepository.findById(userId)).thenReturn(Optional.empty());
//
//        ResponseEntity<SellerProfileDto> response = userService.GetSellerProfileById(userId);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//    }



}
