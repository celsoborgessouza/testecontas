package config.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import config.app.AppConfig;

@Configuration
@EnableWebMvc
@Import(AppConfig.class)
//@ComponentScan(basePackages = "br.com.contas")
public class WebConfig {


}
