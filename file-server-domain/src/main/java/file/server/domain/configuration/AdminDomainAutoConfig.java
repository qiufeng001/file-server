package file.server.domain.configuration;

import file.server.domain.repository.FileServerRepository;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackageClasses = { FileServerRepository.class })
public class AdminDomainAutoConfig {
	public AdminDomainAutoConfig() {
		System.out.println("init domain...");
	}
}