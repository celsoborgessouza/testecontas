package br.com.contas.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

	@RequestMapping(value = "/aportes", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> realizarAporte(Long idContaDestino, BigDecimal valorAporte, String descricao) {

		try {

			Transacao transacao = transacaoService.realizarAporte(idContaDestino, valorAporte, descricao);
			return Response.ok(transacao.getDescricaoStatusTransacao(), transacao);
		} catch (ServiceException e) {
			return Response.service(e);
		} catch (Exception e) {
			return Response.exception(e);
		}
	}

	@RequestMapping(value = "/estorno-aportes", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> estornarAporte(String codigoAporte) {

		try {

			Transacao transacao = transacaoService.estronarAporte(codigoAporte);
			return Response.ok(transacao.getDescricaoStatusTransacao(), transacao);
		} catch (ServiceException e) {
			return Response.service(e);
		} catch (Exception e) {
			return Response.exception(e);
		}
	}

	@RequestMapping(value = "/transferencias", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> realizarTransferencia(Long idContaOrigem, Long idContaDestino, BigDecimal valor) {

		try {

			Transacao transacao = transacaoService.realizarTransferencia(idContaOrigem, idContaDestino, valor);
			return Response.ok(transacao.getDescricaoStatusTransacao(), transacao);
		} catch (ServiceException e) {
			return Response.service(e);
		} catch (Exception e) {
			return Response.exception(e);
		}
	}

	@RequestMapping(value = "/estorno-transferencias", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> estornarTransferencia(Long idTransacao) {

		try {

			Transacao transacao = transacaoService.estornoTransferecnia(idTransacao);
			return Response.ok(transacao.getDescricaoStatusTransacao(), transacao);
		} catch (ServiceException e) {
			return Response.service(e);
		} catch (Exception e) {
			return Response.exception(e);
		}
	}

	@RequestMapping(value = "/historicos", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> recuperaHistoricoTransferencia(@RequestParam("idConta") Long idConta) {

		try {

			List<Transacao> transacoes = transacaoService.recuperarHistoricoTransacoesPelaConta(idConta);
			return Response.ok("Consulta realizada", transacoes);
		} catch (Exception e) {
			return Response.exception(e);
		}
	}

}
