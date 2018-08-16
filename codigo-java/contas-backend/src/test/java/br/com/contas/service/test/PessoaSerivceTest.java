package br.com.contas.service.test;

import java.util.List;

import org.junit.Test;

import br.com.contas.domain.rdbs.Pessoa;
import br.com.contas.service.PessoaService;
import br.com.contas.utils.test.SpringUtil;
import junit.framework.TestCase;

public class PessoaSerivceTest extends TestCase {

	private PessoaService service;
	private SpringUtil instance;
	
		
	
	@Override
	protected void setUp() throws Exception {
		/*
		 * Instanciar por um Singlaton
		 */
		instance = SpringUtil.getInstance();
		
		/*
		 * Substituir "private PessoaService pessoaService = new PessoaService();"
		 * pelo Spring (DI) 
		 */
		service = (PessoaService) instance.getBean(PessoaService.class);
	}
	
	@Override
	protected void tearDown() throws Exception {
		instance.closeSession();
	}

	@Test
	public void testGetPessoas() {
		List<Pessoa> result = service.getPessoas();
		assertNotNull(result);
		assertTrue(result.size() > 0);
	}
	
	

}
