package br.com.contas.service.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import config.app.AppConfig;


@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = {AppConfig.class})
@ComponentScan(basePackages = "br.com.contas.controller")
public class SpringTest {
	
	
	@Test
	public void contextTest() {
		assertTrue(true);
	}
}
