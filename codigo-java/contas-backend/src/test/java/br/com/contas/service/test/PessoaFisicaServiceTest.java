package br.com.contas.service.test;

import org.junit.Test;

import br.com.contas.domain.rdbs.PessoaFisica;
import br.com.contas.service.PessoaFisicaService;
import br.com.contas.utils.test.SpringUtil;
import junit.framework.TestCase;

public class PessoaFisicaServiceTest extends TestCase {

	private PessoaFisicaService service;
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
		service = (PessoaFisicaService) instance.getBean(PessoaFisicaService.class);
	}
	
	@Override
	protected void tearDown() throws Exception {
		instance.closeSession();
	}

	@Test
	public void testCriarPessoaFisica() throws Exception {
		
		Long id = service.criar("26716580018", "NOME TESTE DOIS", "13/08/2000");
		assertNotNull(id);
	}
	
	@Test
	public void testRecuperarPessoaFisica() throws Exception {
		
		
		PessoaFisica pessoaFisica = service.recuperarPorCpf("26716580018");
		assertNotNull(pessoaFisica);
	}


}
