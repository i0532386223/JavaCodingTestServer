/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socialsky.codingtest.dao;

import jersey.repackaged.com.google.common.base.Optional;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;

public class UserDAO extends AbstractDAO<User> {

    public UserDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<User> findById(Long id) {
        return Optional.fromNullable(get(id));
    }

    public List<User> findUserByName(String name) {
            return list(namedQuery("socialsky.codingtest.dao.User.findByName").setParameter("pname", name));
    }

    public User findOneName(String name) {
        List<User> ls = findUserByName(name);
        if (ls.isEmpty() == false && ls.size() > 0) {
            return ls.get(0);
        } else {
            return null;
        }
    }

    public User create(User user) {
        return persist(user);
    }

//    public List<User> findAll() {
//        return list(namedQuery("socialsky.codingtest.dao.User.findAll"));
//    }
}
