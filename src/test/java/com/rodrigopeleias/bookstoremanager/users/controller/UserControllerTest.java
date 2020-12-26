package com.rodrigopeleias.bookstoremanager.users.controller;

import com.rodrigopeleias.bookstoremanager.users.builder.JwtRequestBuilder;
import com.rodrigopeleias.bookstoremanager.users.builder.UserDTOBuilder;
import com.rodrigopeleias.bookstoremanager.users.dto.JwtRequest;
import com.rodrigopeleias.bookstoremanager.users.dto.JwtResponse;
import com.rodrigopeleias.bookstoremanager.users.dto.MessageDTO;
import com.rodrigopeleias.bookstoremanager.users.dto.UserDTO;
import com.rodrigopeleias.bookstoremanager.users.exception.UserNotFoundException;
import com.rodrigopeleias.bookstoremanager.users.service.AuthenticationService;
import com.rodrigopeleias.bookstoremanager.users.service.UserService;
import com.rodrigopeleias.bookstoremanager.utils.JsonConversionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private static final String USERS_API_URL_PATH = "/api/v1/users";

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private UserController userController;

    private UserDTOBuilder userDTOBuilder;

    private JwtRequestBuilder jwtRequestBuilder;

    @BeforeEach
    void setUp() {
        userDTOBuilder = UserDTOBuilder.builder().build();
        jwtRequestBuilder = JwtRequestBuilder.builder().build();
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenPOSTIsCalledThenCreatedStatusShouldBeReturned() throws Exception {
        UserDTO expectedUserToCreateDTO = userDTOBuilder.buildUserDTO();
        String expectedCreationMessage = "User rodrigopeleias with ID 1 successfully created";
        MessageDTO expectedCreationMessageDTO = MessageDTO.builder().message(expectedCreationMessage).build();

        when(userService.create(expectedUserToCreateDTO)).thenReturn(expectedCreationMessageDTO);

        mockMvc.perform(post(USERS_API_URL_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .content(JsonConversionUtils.asJsonString(expectedUserToCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", is(expectedCreationMessage)));
    }

    @Test
    void whenPostIsCalledWithoutRequiredFieldThenBadRequestStatusShouldBeReturned() throws Exception {
        UserDTO expectedUserToCreateDTO = userDTOBuilder.buildUserDTO();
        expectedUserToCreateDTO.setUsername(null);

        mockMvc.perform(post(USERS_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConversionUtils.asJsonString(expectedUserToCreateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenDELETEIsCalledThenNoContentShouldBeInformed() throws Exception {
        UserDTO expectedUserToDeleteDTO = userDTOBuilder.buildUserDTO();
        Long expectedUserToDeleteId = expectedUserToDeleteDTO.getId();

        doNothing().when(userService).delete(expectedUserToDeleteId);

        mockMvc.perform(delete(USERS_API_URL_PATH + "/" + expectedUserToDeleteId)
        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenPUTIsCalledThenOkStatusShouldBeReturned() throws Exception {
        UserDTO expectedUserToUpdateDTO = userDTOBuilder.buildUserDTO();
        expectedUserToUpdateDTO.setUsername("rodrigoupdate");
        String expectedUpdateMessage = "User rodrigoupdate with ID 1 successfully updated";
        MessageDTO expectedUpdateMessageDTO = MessageDTO.builder().message(expectedUpdateMessage).build();

        Long expectedUserToUpdateId = expectedUserToUpdateDTO.getId();

        when(userService.update(expectedUserToUpdateId, expectedUserToUpdateDTO))
                .thenReturn(expectedUpdateMessageDTO);

        mockMvc.perform(put(USERS_API_URL_PATH + "/" + expectedUserToUpdateId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConversionUtils.asJsonString(expectedUserToUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(expectedUpdateMessage)));
    }

    @Test
    void whenPostIsCalledToAuthenticateUserThenOkStatusShouldBeReturned() throws Exception {
        JwtRequest jwtRequest = jwtRequestBuilder.buildJwtRequest();
        JwtResponse expectedJwtToken = JwtResponse.builder().jwtToken("fakeToken").build();

        when(authenticationService.createAuthenticationToken(jwtRequest)).thenReturn(expectedJwtToken);

        mockMvc.perform(post(USERS_API_URL_PATH + "/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConversionUtils.asJsonString(jwtRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken", is(expectedJwtToken.getJwtToken())));
    }

    @Test
    void whenPostIsCalledToAuthenticateUserWithoutPasswordFieldThenBadRequestShouldBeReturned() throws Exception {
        JwtRequest jwtRequest = jwtRequestBuilder.buildJwtRequest();
        jwtRequest.setPassword(null);

        mockMvc.perform(post(USERS_API_URL_PATH + "/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConversionUtils.asJsonString(jwtRequest)))
                .andExpect(status().isBadRequest());
    }
}
