package socialsky.codingtest.resources;

import com.google.common.base.Optional;
import com.codahale.metrics.annotation.Timed;
import io.dropwizard.hibernate.UnitOfWork;
import java.security.Key;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.keys.AesKey;
import org.jose4j.lang.ByteUtil;
import org.jose4j.lang.JoseException;
import socialsky.codingtest.api.Saying;
import socialsky.codingtest.dao.TokenDAO;
import socialsky.codingtest.dao.User;
import socialsky.codingtest.dao.UserDAO;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class BaseResource {

    private final UserDAO userdao;
    private final TokenDAO tokendao;

    public BaseResource(UserDAO userdao, TokenDAO tokendao) {
        this.userdao = userdao;
        this.tokendao = tokendao;
    }

    @GET
    @Timed
    public Saying sayHello(@QueryParam("name") Optional<String> name) {
        return new Saying(1, "hello");
    }

    @Path("/create")
    @UnitOfWork
    @GET
    @Timed
    public User create(@QueryParam("name") Optional<String> name,
            @QueryParam("password") Optional<String> password) {
        System.out.println("createUser() -" + name.or(""));
        User user = userdao.create(new User(name.or(""), password.or("")));
        // return new SimpleEntry<String, String>("status", "ok");
        // return new SimpleEntry<String, String>("User created", user.getName());
        return user;
    }

    @Path("/login")
    @GET
    @Timed
    @UnitOfWork
    public SimpleEntry<String, String> login(@QueryParam("name") Optional<String> name,
            @QueryParam("password") Optional<String> password) throws JoseException {
        if (name.or("").length() > 0) {
            User user = userdao.findOneName(name.or(""));
            if (user != null && user.getPassword().equals(password.or(""))) {
                Key key = new AesKey(ByteUtil.randomBytes(16));
                JsonWebEncryption jwe = new JsonWebEncryption();
                jwe.setPayload("Hello World!");
                jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A128KW);
                jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
                jwe.setKey(key);
                String serializedJwe = jwe.getCompactSerialization();
                return new SimpleEntry<String, String>("text", serializedJwe);
            }
        }
        return new SimpleEntry<String, String>("status", "Incorrect username or password");
    }

}
