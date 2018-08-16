package br.com.contas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import br.com.contas.domain.rdbs.PessoaFisica;
import br.com.contas.domain.rdbs.PessoaJuridica;
import br.com.contas.service.PessoaFisicaService;
import br.com.contas.service.PessoaJuridicaService;
import br.com.contas.service.exception.ServiceException;

@Controller
@RequestMapping("/pessoas")
public class PessoaController {
	
	@Autowired
	PessoaFisicaService pessoaFisicaService;
	
	@Autowired
	PessoaJuridicaService pessoaJuridicaSerice;

	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/fisica", method=RequestMethod.GET)
	@ResponseBody
	public Response recuperarTodosPF() throws Exception {
		
		try {
			List<PessoaFisica> lista = pessoaFisicaService.recuperarTodos();
			
			return Response.OK(lista);
		} catch (ServiceException e) {
			return Response.Error(e.getMessage());
		} catch (Exception e) {
			return Response.Error("Tente mais tarde");
		}
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/fisica", method = RequestMethod.POST)
	@ResponseBody
	public Response criarPF(@ModelAttribute PessoaFisica pessoaFisica, String dataFormatada) throws Exception {
		
		try {
			Long idPessoaFisica = pessoaFisicaService.criar(pessoaFisica.getCpf(), pessoaFisica.getNomeCompleto(), dataFormatada);
			return Response.OK(idPessoaFisica);
		} catch (ServiceException e) {
			return Response.Error(e.getMessage(), e);
		} catch (Exception e) {
			return Response.Error("Tente mais tarde", e);
		}
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/juridica", method=RequestMethod.GET)
	@ResponseBody
	public Response recuperarTodosPJ() throws Exception {
		
		try {
			List<PessoaJuridica> lista = pessoaJuridicaSerice.recuperarTodos();
			
			return Response.OK(lista);
		} catch (ServiceException e) {
			return Response.Error(e.getMessage());
		} catch (Exception e) {
			return Response.Error("Tente mais tarde");
		}
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/juridica", method = RequestMethod.POST)
	@ResponseBody
	public Response criarPJ(@ModelAttribute PessoaJuridica pessoaJuridica) throws Exception {
		
		try {
		
			Long idPessoaJuridica = pessoaJuridicaSerice.criar(pessoaJuridica.getCnpj(), pessoaJuridica.getRazaoSocial(), pessoaJuridica.getNomeFantasia());
			return Response.OK(idPessoaJuridica);
		} catch (ServiceException e) {
			return Response.Error(e.getMessage(), e);
		} catch (Exception e) {
			return Response.Error("Tente mais tarde", e);
		}
	}
}
