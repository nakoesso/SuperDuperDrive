package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.entities.Users;
import com.udacity.jwdnd.course1.cloudstorage.mappers.UsersMapper;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UsersMapper usersMapper;
    private final HashService hashService;

    public UserService(UsersMapper usersMapper, HashService hashService) {
        this.usersMapper = usersMapper;
        this.hashService = hashService;
    }

    public Users getUser(String username) {
        return usersMapper.getUser(username);
    }

    public boolean isValidCredentials(String username, String password) {
        Users user = usersMapper.getUser(username);
        if (user == null) return false;
        String hashedAttempt = hashService.getHashedValue(password, user.getSalt());
        return hashedAttempt.equals(user.getPassword());
    }
}
