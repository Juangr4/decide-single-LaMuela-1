package org.lamuela.User;

import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    static UserRepository uRepo;

    public Collection<User> getAll(){
        return uRepo.findAll();
    }

    @Transactional
    public static void save(User user){
        uRepo.save(user);
    }
    
}
