package org.lamuela.User;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer>{

    public Collection<User> findAll();

}
