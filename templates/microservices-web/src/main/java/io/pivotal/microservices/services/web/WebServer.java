package io.pivotal.microservices.services.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

import io.pivotal.microservices.services.registration.RegistrationServer;

/**
 * Accounts web-server. Works as a microservice client, fetching data from the
 * Account-Service. Uses the Discovery Server (Eureka) to find the microservice.
 * 
 * @author Paul Chapman
 */
@SpringBootApplication(exclude = { HibernateJpaAutoConfiguration.class, //
        DataSourceAutoConfiguration.class })
@EnableDiscoveryClient
@ComponentScan(useDefaultFilters = false) // Disable component scanner
public class WebServer {

    /**
     * URL uses the logical name of account-service - upper or lower case, doesn't
     * matter.
     */
    public static final String OWNER_SERVICE_URL = "http://OWNER-SERVICE";

    public static final String PET_SERVICE_URL = "http://PET-SERVICE";

    /**
     * Run the application using Spring Boot and an embedded servlet engine.
     * 
     * @param args Program arguments - ignored.
     */
    public static void main(String[] args) {
        // Default to registration server on localhost
        if (System.getProperty(RegistrationServer.REGISTRATION_SERVER_HOSTNAME) == null)
            System.setProperty(RegistrationServer.REGISTRATION_SERVER_HOSTNAME, "localhost");

        // Tell server to look for web-server.properties or web-server.yml
        System.setProperty("spring.config.name", "web-server");
        SpringApplication.run(WebServer.class, args);
    }

    /**
     * A customized RestTemplate that has the ribbon load balancer build in. Note
     * that prior to the "Brixton"
     * 
     * @return
     */
    @LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public WebOwnersService ownerService() {
        return new WebOwnersService(OWNER_SERVICE_URL);
    }

    /**
     * Create the controller, passing it the {@link WebAccountsService} to use.
     * 
     * @return
     */
    @Bean
    public WebOwnerController ownerController() {
        return new WebOwnerController(ownerService());
    }

    @Bean
    public WebPetService petService() {
        return new WebPetService(PET_SERVICE_URL);
    }

    /**
     * Create the controller, passing it the {@link WebAccountsService} to use.
     * 
     * @return
     */
    @Bean
    public WebPetController petController() {
        return new WebPetController(petService());
    }
}
