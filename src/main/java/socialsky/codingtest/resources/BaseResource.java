package socialsky.codingtest.resources;

import com.google.common.base.Optional;
import com.codahale.metrics.annotation.Timed;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;
import socialsky.codingtest.api.Saying;
import socialsky.codingtest.dao.User;
import socialsky.codingtest.dao.UserDAO;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class BaseResource {

    private final UserDAO userdao;

    public BaseResource(UserDAO dao) {
        this.userdao = dao;
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
            @QueryParam("password") Optional<String> password) {
        System.out.println("name: "+name.or(""));
        // List<User> users= userdao.findName(name.or(""));
        User user= userdao.findOneName("a1");
        // User user = userdao.findOneName(name.or(""));
        String txt="User: ";
        if (user!=null)
        {
            txt+=user.toString();
        }
        return new SimpleEntry<String, String>("status", "ok - "+txt);
    }

}
