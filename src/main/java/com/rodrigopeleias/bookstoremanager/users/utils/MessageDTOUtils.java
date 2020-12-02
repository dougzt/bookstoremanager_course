package com.rodrigopeleias.bookstoremanager.users.utils;

import com.rodrigopeleias.bookstoremanager.users.dto.MessageDTO;
import com.rodrigopeleias.bookstoremanager.users.entity.User;

public class MessageDTOUtils {
    public static MessageDTO creationMessage(User createdUser) {
        return returnMessage(createdUser, "created");
    }

    public static MessageDTO updateMessage(User updateUser) {
        return returnMessage(updateUser, "updated");
    }

    private static MessageDTO returnMessage(User user, String action) {
        String username = user.getUsername();
        Long userID = user.getId();
        String createdUserMessage = String.format("User %s with ID %s successfully %s", username, userID, action);
        return MessageDTO.builder()
                .message(createdUserMessage)
                .build();
    }
}
