package com.elu.authservicesuperfs.service;

import com.elu.authservicesuperfs.dto.UserDtoOpenFeign;
import com.elu.authservicesuperfs.model.Users;

public interface UserService {
    public boolean updateUser(UserDtoOpenFeign user) throws Exception;
}
