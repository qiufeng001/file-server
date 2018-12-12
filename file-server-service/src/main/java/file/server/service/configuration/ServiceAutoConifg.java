package file.server.service.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"file.server.service.service"})
public class ServiceAutoConifg {
	public ServiceAutoConifg() {
		System.out.println("init service ...");
	}
}