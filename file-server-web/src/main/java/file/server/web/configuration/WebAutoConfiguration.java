package file.server.web.configuration;

import file.server.service.configuration.ServiceModelAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;


@Configuration
@AutoConfigureAfter(ServiceModelAutoConfiguration.class)
public class WebAutoConfiguration {
	public WebAutoConfiguration() {

	}

}
