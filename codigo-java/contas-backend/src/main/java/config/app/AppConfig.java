package config.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource( { "classpath*:/applicationContext.xml" } )
@ComponentScan( basePackages = "br.com.contas")
public class AppConfig {
	
	

}
