
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
    
        public Token removeToken(Token token) {
        return persist(token);
    }
}
