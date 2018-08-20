package br.com.contas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import br.com.contas.domain.rdbs.PessoaFisica;
import br.com.contas.service.PessoaFisicaService;
import br.com.contas.service.exception.ServiceException;
import br.com.contas.utils.rest.Response;

@Controller
@RequestMapping("/pessoas-fisicas")
public class PessoaFisicaController {
	
	@Autowired
	PessoaFisicaService pessoaFisicaService;
	

	
	@RequestMapping(method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> recuperarTodos() throws Exception {
		
		try {
			List<PessoaFisica> lista = pessoaFisicaService.recuperarTodos();	
			return Response.ok("Pesquisa realizada com sucesso",lista);
		} catch (ServiceException e) {
			return Response.service(e);
		} catch (Exception e) {
			return  Response.exception(e);
		}
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> criar(@ModelAttribute PessoaFisica pessoaFisica, String dataFormatada) throws Exception {
		
		try {
			Long idPessoaFisica = pessoaFisicaService.criar(pessoaFisica.getCpf(), pessoaFisica.getNomeCompleto(), dataFormatada);
			return Response.ok("Cadastro realizado !!!", idPessoaFisica);
		} catch (ServiceException e) {
			
			return Response.service(e.getMessage(), e);
		} catch (Exception e) {
			return Response.exception("Sistema indisponível", e);
		}
	}

}
