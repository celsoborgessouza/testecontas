package br.com.contas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import br.com.contas.domain.rdbs.PessoaJuridica;
import br.com.contas.service.PessoaJuridicaService;
import br.com.contas.service.exception.ServiceException;
import br.com.contas.utils.rest.Response;

@Controller
@RequestMapping("/pessoas-juridicas")
public class PessoaJuridicaController {
	
	
	@Autowired
	PessoaJuridicaService pessoaJuridicaSerice;


	@RequestMapping(method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> recuperarTodos() throws Exception {
		
		try {
			List<PessoaJuridica> lista = pessoaJuridicaSerice.recuperarTodos();
			return Response.ok("Pesquisa realizada com sucesso",lista);
		} catch (ServiceException e) {
			return Response.service(e);
		} catch (Exception e) {
			return Response.exception(e);
		}
	}

	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> criar(@ModelAttribute PessoaJuridica pessoaJuridica)  {
		
		try {
		
			Long idPessoaJuridica = pessoaJuridicaSerice.criar(pessoaJuridica.getCnpj(), pessoaJuridica.getRazaoSocial(), pessoaJuridica.getNomeFantasia());

			return Response.ok("Cadastro realizado !!!", idPessoaJuridica);
		} catch (ServiceException e) {
			return Response.service(e);
		} catch (Exception e) {
			return Response.exception(e);
		}
	}
}
