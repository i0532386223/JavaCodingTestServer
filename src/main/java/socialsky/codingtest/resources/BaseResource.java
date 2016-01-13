package socialsky.codingtest.resources;

import com.google.common.base.Optional;
import com.codahale.metrics.annotation.Timed;
import com.github.toastshaman.dropwizard.auth.jwt.hmac.HmacSHA512Signer;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebTokenClaim;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebTokenHeader;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.hibernate.UnitOfWork;
import java.security.Key;
import java.util.AbstractMap.SimpleEntry;
import static java.util.Collections.singletonMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import org.joda.time.DateTime;

import socialsky.codingtest.dao.Token;
import socialsky.codingtest.dao.TokenDAO;
import socialsky.codingtest.dao.User;
import socialsky.codingtest.dao.UserDAO;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class BaseResource {

    private final UserDAO userdao;
    private final TokenDAO tokendao;
    private final byte[] tokenbyte;

    public BaseResource(UserDAO userdao, TokenDAO tokendao, byte[] tokenbyte) {
        this.userdao = userdao;
        this.tokendao = tokendao;
        this.tokenbyte = tokenbyte;
    }

    // @Auth 
    
    @Produces(MediaType.TEXT_PLAIN)
    @POST
    @UnitOfWork
    @Path("/message")
    public String message(@Auth String query) {
        return "Hello " + query + "!";
    }

    @Path("/create")
    @UnitOfWork
    @POST
    @Timed
    public User create(User user) {
        if (user != null) {
            System.out.println("create: " + user.getName());
            User existinguser = userdao.findOneName(user.getName());
            if (existinguser == null || existinguser.getId() == 0) {
                User user2 = userdao.create(new User(user.getName(), user.getPassword()));
                return user2;
            }
        }
        throw new SecurityException("The user already exists");
        // return new User("error", "The user already exists");
    }

//    @Path("/create")
//    @UnitOfWork
//    @GET
//    @Timed
//    public User create(@QueryParam("name") Optional<String> name,
//            @QueryParam("password") Optional<String> password) {
//        System.out.println("create: "+name.or(""));
//        User existinguser = userdao.findOneName(name.or(""));
//        if (existinguser == null || existinguser.getId() == 0) {
//            User user = userdao.create(new User(name.or(""), password.or("")));
//            return user;
//        } else {
//            //throw new AuthenticationException("The user already exists");
//            return new User("error","The user already exists");
//        }
//    }
    @Path("/login")
    @POST
    @Timed
    @UnitOfWork
    public SimpleEntry<String, String> login(User user) throws AuthenticationException {
        if (user!=null && user.getName().length() > 0) {
            System.out.println("login: " + user.getName());
            User existinguser = userdao.findOneName(user.getName());
            if (existinguser != null && user.getPassword().equals(user.getPassword())) {
                HmacSHA512Signer signet = new HmacSHA512Signer(tokenbyte);
                JsonWebToken token = JsonWebToken.builder().header(JsonWebTokenHeader.HS512())
                        .claim(JsonWebTokenClaim.builder().subject("verified").issuedAt(DateTime.now())
                                .expiration(new DateTime().plusMinutes(15)).build()).build();
                String sToken = signet.sign(token);
                Token tokenDB = new Token(sToken);
                tokenDB.setUser(user);
                tokendao.addToken(tokenDB);
                user.setToken(tokenDB);
                return new SimpleEntry<>("token", sToken);
            }
        }
         // return new SimpleEntry<String, String>("status", "Incorrect username or password");
        throw new AuthenticationException("Incorrect username or password");
    }

    @Path("/logout")
    @UnitOfWork
    @GET
    @Timed
    public SimpleEntry<String, String> logout() {
        return new SimpleEntry<String, String>("token", "");
    }

}
