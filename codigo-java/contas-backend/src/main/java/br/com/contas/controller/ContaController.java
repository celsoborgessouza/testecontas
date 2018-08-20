package br.com.contas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import br.com.contas.domain.rdbs.Conta;
import br.com.contas.service.ContaFilialService;
import br.com.contas.service.ContaMatrizService;
import br.com.contas.service.exception.ServiceException;
import br.com.contas.utils.rest.Response;

@Controller
@RequestMapping("/")
public class ContaController {
	
	@Autowired
	private ContaMatrizService contaMatrizService;
	
	@Autowired
	private ContaFilialService contaFiliaService;
	

	@RequestMapping(value="/pessoas-fisicas/{cpf}/matrizes", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> criarContaMatrizPf(String nomeConta, @PathVariable("cpf") String cpf) throws Exception {
		
		try {
			Conta conta = contaMatrizService.criarContaPessoaFisica(nomeConta, cpf);
			return Response.ok("Cadastro de conta matriz realizado !!!", conta);
		} catch (ServiceException e) {	
			return Response.service(e.getMessage(), e);
		} catch (Exception e) {
			return Response.exception("Sistema indisponível", e);
		}
	}
	


	@RequestMapping(value="/pessoas-juridicas/{cnpj}/matrizes", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> criarContaMatrizPj(String nomeConta,  @PathVariable("cnpj") String cnpj) throws Exception {
		
		try {
			Conta conta = contaMatrizService.criarContaPessoaJuridica(nomeConta, cnpj);
			return Response.ok("Cadastro de conta matriz realizado !!!", conta);
		} catch (ServiceException e) {	
			return Response.service(e.getMessage(), e);
		} catch (Exception e) {
			return Response.exception("Sistema indisponível", e);
		}
	}
	


	@RequestMapping(value="/pessoas-fisicas/{cpf}/filiais", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> criarContaFilialPf(String nomeConta,  @PathVariable("cpf") String cpf, Long idContaPai) throws Exception {
		try {
			Conta conta = contaFiliaService.criarContaPessoaFisica(nomeConta, cpf, idContaPai);
			return Response.ok("Cadastro de conta filial realizado !!!", conta);
		} catch (ServiceException e) {	
			return Response.service(e.getMessage(), e);
		} catch (Exception e) {
			return Response.exception("Sistema indisponível", e);
		}
	}



	@RequestMapping(value="/pessoas-juridicas/{cnpj}/filiais", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> criarContaFilialPj(String nomeConta,  @PathVariable("cnpj") String cnpj, Long idContaPai) throws Exception {
		try {
			Long idContaFilial = contaFiliaService.criarContaPessoaJuridica(nomeConta, cnpj, idContaPai);
			return Response.ok("Cadastro de conta filial realizado !!!", idContaFilial);
		} catch (ServiceException e) {	
			return Response.service(e.getMessage(), e);
		} catch (Exception e) {
			return Response.exception("Sistema indisponível", e);
		}
	}
	

	
}
