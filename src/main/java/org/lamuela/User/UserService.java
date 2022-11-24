package org.lamuela.User;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository uRepo;

    public Collection<User> getAll(){
        return uRepo.findAll();
    }
    
}
