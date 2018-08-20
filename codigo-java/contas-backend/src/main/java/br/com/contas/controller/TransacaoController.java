package br.com.contas.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import br.com.contas.domain.rdbs.Transacao;
import br.com.contas.service.TransacaoService;
import br.com.contas.service.exception.ServiceException;
import br.com.contas.utils.rest.Response;

@Controller
@RequestMapping("/transacoes")
public class TransacaoController {

	@Autowired
	private TransacaoService transacaoService;

	@RequestMapping(value="/aporte", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> realizarAporte(Long idContaDestino, BigDecimal valorAporte, String descricao)  {
		
		try {
		
			Transacao transacao = transacaoService.realizarAporte(idContaDestino, valorAporte, descricao);
			return Response.ok("Cadastro realizado !!!", transacao);
		} catch (ServiceException e) {
			return Response.service(e);
		} catch (Exception e) {
			return Response.exception(e);
		}
	}

	@RequestMapping(value="/estorno-aporte", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> estornarAporte(String codigoAporte)  {
		
		try {
			
			Transacao transacao = transacaoService.estronarAporte(codigoAporte);
			
			return Response.ok("Cadastro realizado !!!", transacao);
		} catch (ServiceException e) {
			return Response.service(e);
		} catch (Exception e) {
			return Response.exception(e);
		}
	}

	@RequestMapping(value="/transferencia", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> realizarTransferencia(Long idContaOrigem, Long idContaDestino, BigDecimal valor)  {
		
		try {
			
			Transacao transacao = transacaoService.realizarTransferencia(idContaOrigem, idContaDestino, valor);
			
			return Response.ok("Transação realizado !!!", transacao);
		} catch (ServiceException e) {
			return Response.service(e);
		} catch (Exception e) {
			return Response.exception(e);
		}
	}

	@RequestMapping(value="/estorno-transferencia", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> estornarTransferencia(Long idTransacao)  {
		
		try {
			
			Transacao transacao = transacaoService.estornoTransferecnia(idTransacao);
			
			return Response.ok("Transação realizada !!!", transacao);
		} catch (ServiceException e) {
			return Response.service(e);
		} catch (Exception e) {
			return Response.exception(e);
		}
	}

	@RequestMapping(value="/historico", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> recuperaHistoricoTransferencia(Long idConta)  {
		
		try {
			
			List<Transacao> transacoes = transacaoService.recuperarHistoricoTransacoesPelaConta(idConta);
			
			return Response.ok("Consulta realizar !!!", transacoes);
		} catch (Exception e) {
			return Response.exception(e);
		}
	}
	
	
	
}
