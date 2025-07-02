package com.avocado.glampe_mobile.model.dto.user.resp;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Long id;
    String email;
    String firstName;
    String lastName;
    String address;
    String phoneNumber;
    String dob;
    Boolean status;
    String role;
    String connectionId;
    Boolean isRestricted;
}
