package socialsky.codingtest.app;

import com.github.toastshaman.dropwizard.auth.jwt.JWTAuthFilter;
import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenParser;
import com.github.toastshaman.dropwizard.auth.jwt.hmac.HmacSHA512Verifier;
import com.github.toastshaman.dropwizard.auth.jwt.parser.DefaultJsonWebTokenParser;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.io.UnsupportedEncodingException;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import socialsky.codingtest.dao.Token;
import socialsky.codingtest.dao.TokenDAO;
import socialsky.codingtest.dao.User;
import socialsky.codingtest.dao.UserDAO;
import socialsky.codingtest.resources.BaseResource;

public class JavaCodingApplication extends Application<JavaCodingConfiguration> {

    public static void main(String[] args) throws Exception {
        new JavaCodingApplication().run(args);
    }

    @Override
    public String getName() {
        return "JavaCodingApplication";
    }

    @Override
    public void initialize(Bootstrap<JavaCodingConfiguration> bootstrap) {

        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );

        bootstrap.addBundle(hibernate);
    }

    private final HibernateBundle<JavaCodingConfiguration> hibernate
            = new HibernateBundle<JavaCodingConfiguration>(User.class, Token.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(JavaCodingConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    @Override
    public void run(JavaCodingConfiguration configuration,
            Environment environment) throws UnsupportedEncodingException, AuthenticationException {

        // For cross-domain requests
        final FilterRegistration.Dynamic filterregistration = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        filterregistration.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filterregistration.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
        filterregistration.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        filterregistration.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        filterregistration.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
        filterregistration.setInitParameter("allowCredentials", "true");

        final JsonWebTokenParser tokenParser = new DefaultJsonWebTokenParser();
        final HmacSHA512Verifier tokenVerifier = new HmacSHA512Verifier(configuration.getjwtsecrettoken());

        final Authenticator aut = new AuthClient();
        final AuthDynamicFeature adf = new AuthDynamicFeature(
                new JWTAuthFilter.Builder<>()
                .setTokenParser(tokenParser)
                .setTokenVerifier(tokenVerifier).setRealm("Realm")
                .setPrefix("SecretPrefix")
                .setAuthenticator(aut).buildAuthFilter());
        environment.jersey().register(adf);

        final UserDAO userdao = new UserDAO(hibernate.getSessionFactory());
        final TokenDAO tokendao = new TokenDAO(hibernate.getSessionFactory());
        final BaseResource resource = new BaseResource(userdao, tokendao, configuration.getjwtsecrettoken());
        environment.jersey().register(resource);

    }

}
