/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socialsky.codingtest.dao;

import org.hibernate.SessionFactory;
import io.dropwizard.hibernate.AbstractDAO;

public class TokenDAO extends AbstractDAO<Token> {

    public TokenDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Token addToken(Token token) {
        return persist(token);
    }
}
