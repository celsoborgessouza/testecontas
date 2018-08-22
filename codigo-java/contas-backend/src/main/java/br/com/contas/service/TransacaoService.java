package br.com.contas.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import br.com.contas.dao.TransacaoDAO;
import br.com.contas.domain.rdbs.Aporte;
import br.com.contas.domain.rdbs.Conta;
import br.com.contas.domain.rdbs.Transacao;
import br.com.contas.service.exception.ServiceException;


@Service
public class TransacaoService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

	

	/**
	 * Entrada de valores diretamente na Conta Matriz, através de uma transação qualquer
     *
	 * A Conta Matriz não pode receber transferências de outras contas, apenas
	 * Aportes que devem possuir um código alfanumérico único
	 * 
	 * Apenas contas ativas podem receber Cargas ou transferências
	 * 
	 * @param idContaDestino
	 * @param valorAporte
	 * @param descricao
	 * @throws ServiceException
	 * @throws ValidaTransacaoService 
	 */
	@Transactional(rollbackFor = Exception.class)
	public Transacao realizarAporte(Long idContaDestino, BigDecimal valorAporte, String descricao)
			throws ServiceException {

		try {
			
			validarParametrosTransacaoAporte(idContaDestino, valorAporte);
			
			// Ação de aporte
			Long idAcaoAporte = tipoAcaoTransacao.recuperarAporte();
			// Aporte é de uma conta qualquer, então conta de origem é null
			Long idContaOrigem = null; 

			Conta conta = contaSerivice.carregarConta(idContaDestino);

			// validar se conta é matriz, não precisa ter uma conta pai
			boolean isContaMatriz = isContaMatriz(conta);
			if (!isContaMatriz) {
				return salvarTranacaoNaoContaMatriz(idAcaoAporte, idContaOrigem, idContaDestino, valorAporte);
			};

			// validar se conta é ativa
			boolean isContaAtiva = isContaAtivo(conta);
			if (!isContaAtiva) {
				return salvarTransacaoContaDestinoNaoAtiva(idAcaoAporte, idContaOrigem, idContaDestino, valorAporte);
			}

			// salvar aporte com um identificador alfanumerico único e recuperar identificador
			Aporte aporte = aporteService.criar(descricao);
			Long idAporte = aporte.getId();

			// atualizar saldo da conta
			// recupera valor atual e creditar valor
			BigDecimal saldoAtual = conta.getSaldo();
			creditarValorConta(idContaDestino, saldoAtual, valorAporte);

			// salvar trasnacao
			Transacao transacao = salvarTransacaoAporte(idAcaoAporte, idContaOrigem, idContaDestino, valorAporte, idAporte);
			
			return transacao;

		} catch (ServiceException e) {
			logger.info("error", e);
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			logger.info("error",e);
			throw new ServiceException("Não foi possível realizar aporte.", e);
		}

	}


	private void validarParametrosTransacaoAporte(Long idContaDestino, BigDecimal valorAporte) throws ServiceException {
		if (idContaDestino == null) {
			throw new ServiceException("A conta destino deve ser informada");
		}
		
		if (valorAporte == null) {
			throw new ServiceException("Valor do aparte deve ser informado");
		}
	}


	private Transacao salvarTransacaoContaDestinoNaoAtiva(Long idAcaoAporte, Long idContaOrigem, Long idContaDestino, BigDecimal aporte) throws  ServiceException  {
		
		String descricaoStatusTransacao = "Somente conta ativa pode receber transferência";
		Long idTipoStatus = tipoStatusTranacao.recuperarStatusFalha();
		return registrarTransacao(idAcaoAporte, idContaOrigem, idContaDestino, idTipoStatus, descricaoStatusTransacao, aporte);
	}

	private Transacao salvarTranacaoNaoContaMatriz(Long idAcaoAporte, Long idContaOrigem, Long idContaDestino, BigDecimal aporte)
			throws ServiceException {
		
		String descricaoStatusTransacao = "Somente conta matriz pode receber transferência";
		Long idTipoStatus = tipoStatusTranacao.recuperarStatusFalha();
		return registrarTransacao(idAcaoAporte, idContaOrigem, idContaDestino, idTipoStatus, descricaoStatusTransacao, aporte);
	}
	



	private Transacao salvarTransacaoAporte(Long idAcaoAporte, Long idContaOrigem, Long idContaDestino, BigDecimal valor,
			Long idAporte) throws ServiceException {
		
		String descricaoStatusTransacao = "Aporte realizado com sucesso";
		Long idTipoStatus = tipoStatusTranacao.recuperarStatusSucesso();

		return registrarTransacao(idAcaoAporte, idContaOrigem, idContaDestino, idTipoStatus, descricaoStatusTransacao, valor,
				idAporte);
	}


	private Transacao registrarTransacao(Long idAcao, Long idContaOrigem, Long idContaDestino, Long idTipoStatus,
			String descricaoStatusTransacao, BigDecimal valor, Long idAporte) {

		return registrarTransacao(idAcao, idContaOrigem, idContaDestino, idTipoStatus, descricaoStatusTransacao, valor,
				idAporte, null);

	}
	
	private Transacao registrarTransacao(Long idAcaoAporte, Long idContaOrigem, Long idContaDestino, Long idTipoStatus,
			String descricaoStatusTransacao, BigDecimal aporte) {
		
		return registrarTransacao(idAcaoAporte, idContaOrigem, idContaDestino, idTipoStatus, descricaoStatusTransacao, aporte, null);

	}

	private Transacao registrarTransacao(Long idAcao, Long idContaOrigem, Long idContaDestino, Long idTipoStatus,
			String descricaoStatusTransacao, BigDecimal valor, Long idAporte, Long idTransferenciaEstornado) {

		Transacao transacao = new Transacao();
		transacao.setDataTransacao(new Date());
		transacao.setValor(valor);
		transacao.setDescricaoStatusTransacao(descricaoStatusTransacao);
		transacao.setIdTipoStatusTransacao(idTipoStatus);
		transacao.setIdTipoAcaoTransacao(idAcao);
		transacao.setIdAporte(idAporte);
		transacao.setIdContaDestino(idContaDestino);
		transacao.setIdContaOrigem(idContaOrigem);
		transacao.setDataTransacao(new Date());
		transacao.setIdTransferenciaEstornado(idTransferenciaEstornado);
		
		Long id = (Long) dao.save(transacao);
		return dao.load(id);
	}

	private void creditarValorConta(Long idConta, BigDecimal saldoAtual, BigDecimal valor) {
		BigDecimal novoSaldo = saldoAtual.add(valor);
		contaSerivice.atualizarSaldo(idConta, novoSaldo);
	}

	private boolean isContaAtivo(Conta conta) throws ServiceException {
		Long idSituacaoContaAtivo = situacaoContaService.recuperarIdAtivo();
		return conta.getIdSituacaoConta() == idSituacaoContaAtivo;
	}

	private boolean isContaMatriz(Conta conta) throws ServiceException {
		Long idTipoContaMatriz = tipoContaService.recuperarIdContaMatriz();
		return conta.getIdTipoConta() == idTipoContaMatriz;
	}
	
	/**
	 * Toda transação deve ser estronada (no caso de um estorno de um Aporte é necessário informar o código  alfanumérico para que
	 * a transação possa ser estornada)
	 * 
	 * 
	 * @param codigoAprote
	 * @return
	 * @throws ServiceException
	 * @throws ValidaTransacaoService 
	 */
	@Transactional(rollbackFor = Exception.class)
	public Transacao estronarAporte(String codigoAprote) throws ServiceException {
		
		
		try {
			// Necessário informar código alfanumérico para a transação ser estornada
			validarEstornoAporte(codigoAprote);
			
			Long idAcaoEstornoAporte = tipoAcaoTransacao.recuperarEstornoAporte();
			
			// validar estorno já foi realizado para o identificador do aporte
			boolean isEstornoRealizado = isEstornoJaRealizado(codigoAprote, idAcaoEstornoAporte);
			if (isEstornoRealizado) {
				return recuperarTransacaoEstornoJaRealizadoESalvar(codigoAprote, idAcaoEstornoAporte);
			}					

			Transacao transacao = recuperarTransacaoAporte(codigoAprote);
			
			// conta qualquer que solicitou o estorno e vai ser creditada
			Long idContaDestino = null;
			// conta de origem é a conta que recebeu aporte e vai ser debitada
			Long idContaOrigem = transacao.getIdContaDestino();
			Long idAporte = transacao.getIdAporte();
			BigDecimal valorAporte = transacao.getValor();
			
			
			
			Conta conta = contaSerivice.carregarConta(idContaOrigem);
			// Validar se é conta matriz
			boolean isContaMatriz = isContaMatriz(conta);
			if (!isContaMatriz) {
				salvarTranacaoNaoContaMatriz(idAcaoEstornoAporte, idContaOrigem, idContaDestino, valorAporte);
			}
			
			
			// calcular novo débito
			BigDecimal saldoAtual = conta.getSaldo();
			debitarValorConta(idContaOrigem, saldoAtual, valorAporte);
			
			// salvar transacao de estorno de aporte
			return salvarTransacaoEstornoAporte(idAcaoEstornoAporte, idContaOrigem, idContaDestino, valorAporte, idAporte);
			
		} catch (ServiceException e) {
			
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			logger.info("log", e);
			throw new ServiceException("Não foi possível realizar estorno de aporte.", e);
		}
		
	}


	private Transacao recuperarTransacaoEstornoJaRealizadoESalvar(String codigoAprote, Long idAcaoEstornoAporte)
			throws ServiceException {
		Long idTipoStatusSucesso = tipoStatusTranacao.recuperarStatusSucesso();
		Transacao transacao = dao.recuperarEstornoJaRealizado(codigoAprote, idAcaoEstornoAporte, idTipoStatusSucesso);
		return salvarTransacaoEstornoJaRealizado(codigoAprote, idAcaoEstornoAporte, transacao.getIdContaDestino(), transacao.getIdContaOrigem(),
				transacao.getValor());
	}


	private void debitarValorConta(Long idContaDebitar, BigDecimal saldoAtual, BigDecimal valorDebitar) {
		BigDecimal saldo = saldoAtual.subtract(valorDebitar);
		contaSerivice.atualizarSaldo(idContaDebitar, saldo);
	}


	private Transacao salvarTransacaoEstornoAporte(Long idAcao, Long idContaOrigem, Long idContaDestino,
			BigDecimal valor, Long idAporte) throws ServiceException {
		
		String descricaoStatusTransacao = "Estorno de aporte realizado com sucesso";
		Long idTipoStatus = tipoStatusTranacao.recuperarStatusSucesso();

		return registrarTransacao(idAcao, idContaOrigem, idContaDestino, idTipoStatus, descricaoStatusTransacao, valor,
				idAporte );
	}


	private boolean isEstornoJaRealizado(String codigoAporte, Long idAcaoEstornoAporte) throws ServiceException {
		Long idTipoStatusSucesso = tipoStatusTranacao.recuperarStatusSucesso();
		boolean isEstornoRealizado = dao.isEstornoAporteJaRealizado(codigoAporte, idAcaoEstornoAporte, idTipoStatusSucesso);
		return isEstornoRealizado;
	}


	private Transacao salvarTransacaoEstornoJaRealizado(String codigoAprote, Long idAcaoEstornoAporte, Long idContaDestino,
			Long idContaOrigem, BigDecimal aporteRecebido) throws ServiceException {
		String descricaoStatusTransacao = String.format("Já foi realiadao um estorno para identificador de aporte %s", codigoAprote);
		Long idTipoStatus = tipoStatusTranacao.recuperarStatusFalha();
		return registrarTransacao(idAcaoEstornoAporte, idContaOrigem, idContaDestino, idTipoStatus, descricaoStatusTransacao, aporteRecebido );
	}

	private void validarEstornoAporte(String codigoAprote) throws ServiceException {
		if (StringUtils.isEmpty(codigoAprote)) {
			throw new ServiceException("É necessário informa código de aporte para realizar o estorno");
		}
	}


	private Transacao recuperarTransacaoAporte(String codigoAprote) throws ServiceException {
		try {
			return dao.recuperarPorCodigoAporte(codigoAprote);
		} catch (Exception e) {
			logger.error("error", e);
			throw new ServiceException("Não foi possível recuperar transação pelo código");
		}
	}

	/**
	 * 
	 * Toda Conta Filial pode efetuar transferências desde que 
	 * a conta que receberá a transferência esteja debaixo da  mesma árvore 
	 * e não seja uma conta Matriz
	 * 
	 * Conta matriz pode fazer transferência para qualquer conta filial
	 * 
	 * Apenas contas ativas podem receber Cargas ou transferências
	 * 
	 * @param idContaOrigm
	 * @param idContaDestino
	 * @param valor
	 * @return 
	 * @throws ValidaTransacaoService 
	 * @throws ServiceException 
	 */
	@Transactional(rollbackFor = Exception.class)
	public Transacao realizarTransferencia(Long idContaOrigem, Long idContaDestino, BigDecimal valor) throws ServiceException  {
		
		try {
			
			Transacao transacao = null;
			
			validarParametroTransferencia(idContaOrigem, idContaDestino, valor);
			
			/*
			 * Apenas contas ativas poderm receber transacoes
			 * Conta matriz (destino) não pode receber transferência de outras contas 
			 */
			Conta contaDestino = contaSerivice.carregarConta(idContaDestino);
			transacao = validarContaDestino(contaDestino, idContaOrigem, valor);
			if (transacao != null) {
				return transacao;
			}

			
			// ------------------------------------------------------------
			// SE CONTA ORIGEM É MATRIZ
			// ------------------------------------------------------------						
			// Se conta origem é matriz então pode fazer transferência para uma conta destino filial qualquer
			
			Conta contaOrigem = contaSerivice.carregarConta(idContaOrigem);
			boolean isContaOrigemMatriz = isContaMatriz(contaOrigem);
			if (isContaOrigemMatriz) {
				return exectuarTransacaoTransferencia(contaDestino, contaOrigem, valor);
			}
			
			// ------------------------------------------------------------
			// SE CONTA ORIGEM É FILIAL
			// ------------------------------------------------------------											
			// Se conta origem é filial então pode fazer transferência para conta destino filial que esteja abaixo da mesma árvore
			// . se idContaPrincipal for igual pode transferir
			Long idContaPrincipalOrigem = contaOrigem.getIdContaPrincipal(); 
			Long idContaPrincipalDestino = contaDestino.getIdContaPrincipal();
			if (idContaPrincipalDestino != idContaPrincipalOrigem) {
				
				Long idAcaoTransferencia = tipoAcaoTransacao.recuperarTransferencia();
				return salvarTransacaoContasNaoEstaoNaMesmaArvore(idAcaoTransferencia, idContaOrigem, idContaDestino, valor);
			}
			
			return exectuarTransacaoTransferencia(contaDestino, contaOrigem, valor);
		
		}  catch (ServiceException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException("Não foi possível realizar transação.", e);
		}
		
		

	}


	private Transacao validarContaDestino(Conta contaDestino, Long idContaOrigem, BigDecimal valor)
			throws ServiceException {
		
		Long idContaDestino = contaDestino.getId();
		boolean isContaAtiva = isContaAtivo(contaDestino);
		if (!isContaAtiva) {
			Long idAcaoTransferencia = tipoAcaoTransacao.recuperarTransferencia();
			return salvarTransacaoContaDestinoNaoAtiva(idAcaoTransferencia, idContaOrigem, idContaDestino, valor);
		}
		
		boolean isContaDestinoMatriz = isContaMatriz(contaDestino);
		if (isContaDestinoMatriz) {
			Long idAcaoTransferencia = tipoAcaoTransacao.recuperarTransferencia();
			return salvarTransacaoContaDestinoNaoMatriz(idAcaoTransferencia, idContaOrigem, idContaDestino, valor);
		}
		return null;
	}


	private Transacao salvarTransacaoContasNaoEstaoNaMesmaArvore(Long idAcao, Long idContaOrigem, Long idContaDestino,
			BigDecimal valor) throws ServiceException  {
		
		String descricaoStatusTransacao = "Conta origem e conta destino não fazem parte da mesa árvore";
		Long idTipoStatus = tipoStatusTranacao.recuperarStatusFalha();
		return registrarTransacao(idAcao, idContaOrigem, idContaDestino, idTipoStatus, descricaoStatusTransacao, valor );

		
	}


	private Transacao exectuarTransacaoTransferencia(Conta contaDestino, Conta contaOrigem, BigDecimal valor)
			throws ServiceException {
		Long idAcaoTransferencia = tipoAcaoTransacao.recuperarTransferencia();

		// Debitar valor na conta origem
		BigDecimal saldoAtualContaOrigem = contaOrigem.getSaldo();
		debitarValorConta(contaOrigem.getId(), saldoAtualContaOrigem, valor );
		
		// Creditar valor na conta destino
		BigDecimal saldoAtualContaDestino = contaDestino.getSaldo();
		creditarValorConta(contaDestino.getId(), saldoAtualContaDestino, valor);
		
		// salvar trasnacao
		return salvarTransacaoTransferencia(idAcaoTransferencia, contaOrigem.getId(), contaDestino.getId(), valor);
	}


	private Transacao salvarTransacaoContaDestinoNaoMatriz(Long idAcaoTransferencia, Long idContaOrigem,
			Long idContaDestino, BigDecimal valor) throws ServiceException {
		
		String descricaoStatusTransacao = "Conta matriz não pode receber transferência de outras contas";
		Long idTipoStatus = tipoStatusTranacao.recuperarStatusFalha();
		return registrarTransacao(idAcaoTransferencia, idContaOrigem, idContaDestino, idTipoStatus, descricaoStatusTransacao, valor );
	}


	private Transacao salvarTransacaoTransferencia(Long idAcao, Long idContaOrigem, Long idContaDestino,
			BigDecimal valor) throws ServiceException {
		
		String descricaoStatusTransacao = "Transacao entre contas realizada com sucesso";
		Long idTipoStatus = tipoStatusTranacao.recuperarStatusSucesso();
		return registrarTransacao(idAcao, idContaOrigem, idContaDestino, idTipoStatus, descricaoStatusTransacao, valor );
	}


	private void validarParametroTransferencia(Long idContaOrigem, Long idContaDestino, BigDecimal valor) throws ServiceException {

		if (idContaOrigem == null) {
			throw new ServiceException("É necessário informar o identificiador da conta origem.");
		}

		if (idContaDestino == null) {
			throw new ServiceException("É necessário informar o identificiador da conta destino.");
		}

		if (valor == null) {
			throw new ServiceException("É necessário informar o valor da transferência.");
		}
		
	}


	// TODO
	/**
	 * Toda transação pode ser extornada
	 * 
	 * Apenas contas ativas podem receber Cargas ou transferências
	 * 
	 * @param idTransacao
	 * @return 
	 */
	@Transactional(rollbackFor = Exception.class)
	public Transacao estornoTransferecnia(Long idTransacao) throws ServiceException {
		
		
		Long idAcaoEstornoTransferencia = tipoAcaoTransacao.recuperarEstorno();
		validarParametroEstornoTransacao(idTransacao, idAcaoEstornoTransferencia);
		
		Transacao transacao = validarTranacao(idTransacao);
		// conta que vai ser creditada é a conta que originou a transação 
		Long idContaDestino = transacao.getIdContaOrigem();
		// conta que vai ser debitada é a conta de destino da transação 
		Long idContaOrigem = transacao.getIdContaDestino();
		BigDecimal valor = transacao.getValor();
		
		
		// validar estorno já foi realizado
		boolean isEstornoRealizado = isEstornoJaRealizado(idTransacao, idAcaoEstornoTransferencia);
		if (isEstornoRealizado) {
			return salvarTransacaoEstornoJaRealizado(idTransacao, idAcaoEstornoTransferencia, idContaOrigem, idContaDestino, valor);
		}
				
		return exectuarTransacaoEstornoTransferencia(idAcaoEstornoTransferencia, idContaOrigem, idContaDestino, valor, idTransacao);
	}


	private Transacao validarTranacao(Long idTransacao) throws ServiceException {
		Transacao transacao = dao.get(idTransacao);
		
		
		if (transacao == null) {
			throw new ServiceException("Não foi possível recuperar transação válida.");
		}
		
		// Validar se tranação é do tipo TRANSFERENCIA e foi executada com sucesso
		// recuperar transação válida
		Long idAcaoTransferencia = tipoAcaoTransacao.recuperarTransferencia();
		if (! (idAcaoTransferencia == transacao.getIdTipoAcaoTransacao() && transacao.getIdTipoStatusTransacao() == 1)   ) {
			throw new ServiceException("Identificador não refere a uma transação de transferência com status de sucesso !!");
		}
		return transacao;
	}


	private Transacao salvarTransacaoEstornoJaRealizado(Long idTransacao, Long idAcaoEstornoTransacao, Long idContaOrigemm, Long idContaDestino, BigDecimal valor) throws ServiceException {
		
		String descricaoStatusTransacao = String.format("Já foi realiadao um estorno para a transacao %d", idTransacao);
		Long idTipoStatus = tipoStatusTranacao.recuperarStatusFalha();
		return registrarTransacao(idAcaoEstornoTransacao, idContaOrigemm, idContaDestino, idTipoStatus, descricaoStatusTransacao, valor );
	}


	private Transacao exectuarTransacaoEstornoTransferencia(Long idAcaoEstornoTransferencia, Long idContaOrigem,
			Long idContaDestino, BigDecimal valor, Long idTransferenciaEstornado) throws ServiceException {
		

		// Debitar valor na conta origem
		Conta contaOrigem = contaSerivice.carregarConta(idContaOrigem);
		BigDecimal saldoAtualContaOrigem = contaOrigem.getSaldo();
		debitarValorConta(contaOrigem.getId(), saldoAtualContaOrigem, valor );
		
		// Creditar valor na conta destino
		Conta contaDestino = contaSerivice.carregarConta(idContaDestino);
		BigDecimal saldoAtualContaDestino = contaDestino.getSaldo();
		creditarValorConta(contaDestino.getId(), saldoAtualContaDestino, valor);
		
		// salvar trasnacao
		return salvarTransacaoEstornoTransferencia(idAcaoEstornoTransferencia, contaOrigem.getId(), contaDestino.getId(), valor, idTransferenciaEstornado);
	}


	private Transacao salvarTransacaoEstornoTransferencia(Long idAcao, Long idContaOrigem, Long idContaDestino,
			BigDecimal valor, Long idTransferenciaEstornado) throws ServiceException {
		
		String descricaoStatusTransacao = String.format("Transacao estorno de transferencia entre as contas %d e %d realizado com sucesso", idContaOrigem, idContaDestino);
		Long idTipoStatus = tipoStatusTranacao.recuperarStatusSucesso();
		return registrarEstornoTransacao(idAcao, idContaOrigem, idContaDestino, idTipoStatus, descricaoStatusTransacao, valor, idTransferenciaEstornado);
	}


	private Transacao registrarEstornoTransacao(Long idAcao, Long idContaOrigem, Long idContaDestino, Long idTipoStatus,
			String descricaoStatusTransacao, BigDecimal valor, Long idTransferenciaEstornado) {
		
		return registrarTransacao(idAcao, idContaOrigem, idContaDestino, idTipoStatus, descricaoStatusTransacao, valor,
				null, idTransferenciaEstornado);

	}


	private boolean isEstornoJaRealizado(Long idTransacao, Long idAcaoEstornoAporte) throws ServiceException {

		Long idTipoStatusSucesso = tipoStatusTranacao.recuperarStatusSucesso();
		boolean isEstornoRealizado = dao.isEstornoTransacaoJaRealizado(idTransacao, idAcaoEstornoAporte, idTipoStatusSucesso);
		return isEstornoRealizado;
	}


	private void validarParametroEstornoTransacao(Long idTransacao, Long idAcaoEstornoTransacao) throws ServiceException {
		
		// validar se idTransacao foi informado
		if (idTransacao == null) {
			throw new ServiceException("Identificador da transação deve ser informado");
		}
		
		if (idAcaoEstornoTransacao == null) {
			throw new ServiceException("Não foi possível recuperar identificador de ação da trasação");
		}
	}


	/**
	 *
	 * Todo histórico de transação deve ser armazenado e consultado
	 * 
	 * @param idConta
	 * @return 
	 * @throws ServiceException 
	 */
	@Transactional
	public List<Transacao> recuperarHistoricoTransacoesPelaConta(Long idConta) {
		
		try {
			return dao.recuperarHistoricoTransacoesPelaConta(idConta);
		} catch (Exception e) {
			logger.error("erro:",e);
		}
		return null;
	}
	

}
