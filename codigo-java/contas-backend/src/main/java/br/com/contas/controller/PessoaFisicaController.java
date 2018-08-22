package br.com.contas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
	public ResponseEntity<?> pesquisar(@RequestParam("cpf") String cpf, @RequestParam("nomeCompleto") String nomeCompleto) throws Exception {
		
		try {
			
			if (!StringUtils.isEmpty(cpf) || !StringUtils.isEmpty(nomeCompleto)) {
				return recuperarPorFiltro(cpf, nomeCompleto);
			}
			
			return recuperarTotos();
		} catch (ServiceException e) {
			return Response.service(e);
		} catch (Exception e) {
			return  Response.exception(e);
		}
	}

	private ResponseEntity<?> recuperarTotos() throws ServiceException {
		List<PessoaFisica> lista = pessoaFisicaService.recuperarTodos();
		return configurarRetorno(lista);
	}

	private ResponseEntity<?> recuperarPorFiltro(String cpf, String nome) throws ServiceException {
		List<PessoaFisica> lista = pessoaFisicaService.recuperar(cpf, nome);	
		return configurarRetorno(lista);
	}

	private ResponseEntity<?> configurarRetorno(List<PessoaFisica> lista) {
		if (lista.isEmpty()) {
			return Response.ok("Pesquisa não retornou nenhum resultado",lista);
		}
		return Response.ok("Pesquisa realizada com sucesso",lista);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> criar(@ModelAttribute PessoaFisica pessoaFisica, String dataFormatada) throws Exception {
		
		try {
			PessoaFisica pf = pessoaFisicaService.criar(pessoaFisica.getCpf(), pessoaFisica.getNomeCompleto(), dataFormatada);
			return Response.ok("Cadastro realizado !!!", pf);
		} catch (ServiceException e) {
			
			return Response.service(e.getMessage(), e);
		} catch (Exception e) {
			return Response.exception("Sistema indisponível", e);
		}
	}

	@RequestMapping(method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<?> atualiar(@PathVariable String cpf,  String nomeCompleto, String dataFormatada) throws Exception {
		
		try {
			
			PessoaFisica pf = pessoaFisicaService.atualizar(cpf, nomeCompleto, dataFormatada);
			return Response.ok("Cadastro realizado !!!", pf);
		} catch (ServiceException e) {
			
			return Response.service(e.getMessage(), e);
		} catch (Exception e) {
			return Response.exception("Sistema indisponível", e);
		}
	}

	

}
