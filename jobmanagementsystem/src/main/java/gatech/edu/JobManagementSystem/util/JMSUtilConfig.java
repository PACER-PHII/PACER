package gatech.edu.JobManagementSystem.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Configuration
@ConfigurationProperties(prefix="resultsmanager")
@Primary
public class JMSUtilConfig {
    public String endpoint;

    public JMSUtilConfig() {
    }

    public JMSUtilConfig(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public JMSUtilConfig endpoint(String endpoint) {
        setEndpoint(endpoint);
        return this;
    }
    
}
