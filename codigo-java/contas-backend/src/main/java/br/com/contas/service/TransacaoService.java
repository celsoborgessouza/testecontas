package br.com.contas.service;

import java.math.BigDecimal;
import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.contas.dao.TransacaoDAO;
import br.com.contas.domain.rdbs.Conta;
import br.com.contas.domain.rdbs.Transacao;
import br.com.contas.service.exception.ServiceException;

@Service
public class TransacaoService {
	
	@Autowired
	TransacaoDAO dao;
	
	@Autowired
	ContaService contaSerivice;
	
	@Autowired
	SituacaoContaService situacaoContaService;
	
	@Autowired
	TipoContaService tipoContaService;
	
	@Autowired
	TipoStatusTransacaoService tipoStatusTranacao;

	@Autowired
	TipoAcaoTransacaoService tipoAcaoTransacao;
	
	@Autowired
	AporteService aporteService;
	
	
	// TODO
	@Transactional(rollbackOn = Exception.class)
	public void receberAporte(Long idConta, BigDecimal aporte, String codigo, String descricao) throws ServiceException {
		
		try {
			Long idContaOrigem = null; // Aporte é de uma conta qualquer
			Long idAcaoAporte = tipoAcaoTransacao.recuperarAporte();
			Conta conta = contaSerivice.recuperarConta(idConta);
			// salvar aporte e recuperar identificador
			Long idAporte = aporteService.criar(codigo, descricao);


			// validar se conta é matriz
			boolean isContaMatriz = isContaMatriz(conta);
			if(!isContaMatriz){
				salvarTranacaoNaoEContaMatriz(idConta, aporte, idContaOrigem, idAcaoAporte, idAporte);
				return;
			};
			
			// validar situaçao da conta é ativa
			boolean isContaAtiva = isContaAtivo(conta);;
			if (!isContaAtiva) {
				salvarTransacaoNaoEContaAtiva(idConta, aporte, idContaOrigem, idAcaoAporte, idAporte);
				return;
			}
			
			// atualizar saldo da conta
			// recupera valor atual e calcular valor atual + aporte
			BigDecimal saldoAtual = conta.getSaldo();
			atualizarSaldoConta(idConta, aporte, saldoAtual);
			
			// salvar trasnacao
			salvarTransacaoSucesso(idConta, aporte, idContaOrigem, idAcaoAporte, idAporte);
			

		} catch (ServiceException e) {
			throw new ServiceException(e);
		}

	}

	private void salvarTransacaoNaoEContaAtiva(Long idConta, BigDecimal aporte, Long idContaOrigem, Long idAcaoAporte,
			Long idAporte) throws ServiceException {
		String descricaoStatusTransacao = "Somente conta ativa pode receber aporte";
		Long idTipoStatus = tipoStatusTranacao.recuperarStatusFalha();
		registrarTransacao(idConta, aporte, idContaOrigem, idAcaoAporte, idAporte, descricaoStatusTransacao,
				idTipoStatus);
	}

	private void salvarTranacaoNaoEContaMatriz(Long idConta, BigDecimal aporte, Long idContaOrigem, Long idAcaoAporte,
			Long idAporte) throws ServiceException {
		String descricaoStatusTransacao = "Somente conta matriz pode receber aporte";
		Long idTipoStatus = tipoStatusTranacao.recuperarStatusFalha();
		registrarTransacao(idConta, aporte, idContaOrigem, idAcaoAporte, idAporte, descricaoStatusTransacao,
				idTipoStatus);
	}

	private void salvarTransacaoSucesso(Long idConta, BigDecimal aporte, Long idContaOrigem, Long idAcaoAporte,
			Long idAporte) throws ServiceException {
		String descricaoStatusTransacao = "Aporte realizado com sucesso";
		Long idTipoStatus = tipoStatusTranacao.recuperarStatusSucesso();

		registrarTransacao(idConta, aporte, idContaOrigem, idAcaoAporte, idAporte, descricaoStatusTransacao,
				idTipoStatus);
	}

	private void registrarTransacao(Long idConta, BigDecimal aporte, Long idContaOrigem, Long idAcaoAporte,
			Long idAporte, String descricaoStatusTransacao, Long idTipoStatus) {
		Transacao transacao = new Transacao();
		transacao.setDataTransacao(new Date());
		transacao.setValor(aporte);
		transacao.setDescricaoStatusTransacao(descricaoStatusTransacao);
		transacao.setIdTipoStatusTransacao(idTipoStatus);
		transacao.setIdTipoAcaoTransacao(idAcaoAporte);
		transacao.setIdAporte(idAporte);
		transacao.setIdContaDestino(idConta);
		transacao.setIdContaOrigem(idContaOrigem);
		
		dao.save(transacao);
	}

	private void atualizarSaldoConta(Long idConta, BigDecimal valor, BigDecimal saldoAtual) {
		BigDecimal novoSaldo = saldoAtual.add(valor);
		contaSerivice.atualizarSaldo(idConta, novoSaldo);
	}

	private boolean isContaAtivo(Conta conta) throws ServiceException {
		Long idSituacaoContaAtivo = situacaoContaService.recuperarIdAtivo();
		return conta.getIdSituacaoConta() != idSituacaoContaAtivo;
	}

	private boolean isContaMatriz(Conta conta) throws ServiceException {
		Long idTipoContaMatriz = tipoContaService.recuperarIdContaMatriz(); 
		return conta.getIdTipoConta() == idTipoContaMatriz;
	}

	// TODO
	public void estronoAporte(Long idTransacao, String codigo) {
		
		
	}

	// TODO
	@Transactional(rollbackOn = Exception.class)
	public void transferencaia(Long idContaOrigm, Long idContaDestino, BigDecimal valor) {

	}

	// TODO
	@Transactional(rollbackOn = Exception.class)
	public void estornoTransferecnia(Long idTransacao) {

	}

	// TODO
	public void recuperarHistoricoTransacoes(Long idConta) {

	}


}
