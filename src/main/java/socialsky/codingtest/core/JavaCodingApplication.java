package socialsky.codingtest.core;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import socialsky.codingtest.dao.User;
import socialsky.codingtest.dao.UserDAO;
import socialsky.codingtest.health.TemplateHealthCheck;
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
        bootstrap.addBundle(hibernate);
    }

    private final HibernateBundle<JavaCodingConfiguration> hibernate = 
            new HibernateBundle<JavaCodingConfiguration>(User.class) {
    @Override
    public DataSourceFactory getDataSourceFactory(JavaCodingConfiguration configuration) {
        return configuration.getDataSourceFactory();
    }
};
    
    @Override
    public void run(JavaCodingConfiguration configuration,
            Environment environment) {
        final UserDAO dao = new UserDAO(hibernate.getSessionFactory());
        final BaseResource resource = new BaseResource(dao);
        environment.jersey().register(resource);

//        final TemplateHealthCheck healthCheck
//                = new TemplateHealthCheck();
//        environment.healthChecks().register("template", healthCheck);
//        environment.jersey().register(resource);
    }

}
