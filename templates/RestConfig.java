
import org.springframework.stereotype.Component;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;

@Component
public class RestConfig implements RepositoryRestConfigurer {

    @Override
      public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        
      }

}
