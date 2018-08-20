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
import br.com.contas.service.exception.ValidaTransacaoService;

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
			throws ServiceException, ValidaTransacaoService {

		try {
			
			validarParametrosTransacaoAporte(idContaDestino, valorAporte);
			
			// Ação de aporte
			Long idAcaoAporte = tipoAcaoTransacao.recuperarAporte();
			// Aporte é de uma conta qualquer, então conta de origem é null
			Long idContaOrigem = null; 

			Conta conta = contaSerivice.recuperarConta(idContaDestino);

			// validar se conta é matriz, não precisa ter uma conta pai
			boolean isContaMatriz = isContaMatriz(conta);
			if (!isContaMatriz) {
				return salvarTranacaoNaoContaMatriz(idAcaoAporte, idContaOrigem, idContaDestino, valorAporte);
			};

			// validar se conta é ativa
			boolean isContaAtiva = isContaAtivo(conta);
			if (!isContaAtiva) {
				salvarTransacaoNaoContaAtiva(idAcaoAporte, idContaOrigem, idContaDestino, valorAporte);
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


	private Transacao salvarTransacaoNaoContaAtiva(Long idAcaoAporte, Long idContaOrigem, Long idContaDestino, BigDecimal aporte) throws ValidaTransacaoService, ServiceException  {
		
		String descricaoStatusTransacao = "Somente conta ativa pode receber aporte";
		Long idTipoStatus = tipoStatusTranacao.recuperarStatusFalha();
		return registrarTransacao(idAcaoAporte, idContaOrigem, idContaDestino, idTipoStatus, descricaoStatusTransacao, aporte);
	}

	private Transacao salvarTranacaoNaoContaMatriz(Long idAcaoAporte, Long idContaOrigem, Long idContaDestino, BigDecimal aporte)
			throws ServiceException, ValidaTransacaoService {
		
		String descricaoStatusTransacao = "Somente conta matriz pode receber aporte";
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
			String descricaoStatusTransacao, BigDecimal valor) {

		return registrarTransacao(idAcao, idContaOrigem, idContaDestino, idTipoStatus, descricaoStatusTransacao, valor,
				null);

	}

	private Transacao registrarTransacao(Long idAcao, Long idContaOrigem, Long idContaDestino, Long idTipoStatus,
			String descricaoStatusTransacao, BigDecimal valor, Long idAporte) {

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
	public Transacao estronarAporte(String codigoAprote) throws  ValidaTransacaoService, ServiceException {
		
		
		try {
			// Necessário informar código alfanumérico para a transação ser estornada
			validarEstornoAporte(codigoAprote);
			
			Long idAcaoEstornoAporte = tipoAcaoTransacao.recuperarEstornoAporte();
			Transacao transacao = recuperarTransacaoAporte(codigoAprote);
			
			// conta qualquer que solicitou o estorno e vai ser creditada
			Long idContaDestino = null;
			// conta de origem é a conta que recebeu aporte e vai ser debitada
			Long idContaOrigem = transacao.getIdContaDestino();
			Long idAporte = transacao.getIdAporte();
			BigDecimal valorAporte = transacao.getValor();
			
			
			// validar estorno já foi realizado para o identificador do aporte
			boolean isEstornoRealizado = isEstornoJaRealizado(codigoAprote);
			if (isEstornoRealizado) {
				salvarTransacaoEstornoJaRealizado(codigoAprote, idAcaoEstornoAporte, idContaDestino, idContaOrigem,
						valorAporte);
			}					
			
			Conta conta = contaSerivice.recuperarConta(idContaOrigem);
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
			throw new ServiceException("Não foi possível realizar estorno de aporte.", e);
		}
		
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
				idAporte);
	}


	private boolean isEstornoJaRealizado(String codigoAprote) throws ServiceException {
		Long idTipoStatusSucesso = tipoStatusTranacao.recuperarStatusSucesso();
		boolean isEstornoRealizado = dao.isEstornoJaRealizado(codigoAprote, idTipoStatusSucesso, idTipoStatusSucesso);
		return isEstornoRealizado;
	}


	private void salvarTransacaoEstornoJaRealizado(String codigoAprote, Long idAcaoEstornoAporte, Long idContaDestino,
			Long idContaOrigem, BigDecimal aporteRecebido) throws ServiceException, ValidaTransacaoService {
		String descricaoStatusTransacao = String.format("Já foi realiadao um estorno para identificador de aporte %s", codigoAprote);
		Long idTipoStatus = tipoStatusTranacao.recuperarStatusFalha();
		registrarTransacao(idAcaoEstornoAporte, idContaOrigem, idContaDestino, idTipoStatus, descricaoStatusTransacao, aporteRecebido);
		throw new ValidaTransacaoService(descricaoStatusTransacao);
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
	@Transactional(rollbackFor = Exception.class, noRollbackFor = ValidaTransacaoService.class)
	public Transacao realizarTransferencia(Long idContaOrigem, Long idContaDestino, BigDecimal valor) throws ServiceException, ValidaTransacaoService  {
		
		try {
			validarParametroTransferencia(idContaOrigem, idContaDestino, valor);
			
			/*
			 * Apenas contas ativas poderm receber transacoes
			 * Conta matriz (destino) não pode receber transferência de outras contas 
			 */
			Conta contaDestino = validarTransacaoContaDestino(idContaOrigem, idContaDestino, valor);
			
			// ------------------------------------------------------------
			// SE CONTA ORIGEM É MATRIZ
			// ------------------------------------------------------------						
			// Se conta origem é matriz então pode fazer transferência para uma conta destino filial qualquer
			
			Conta contaOrigem = contaSerivice.recuperarConta(idContaOrigem);
			boolean isContaOrigemMatriz = isContaMatriz(contaOrigem);
			if (isContaOrigemMatriz) {

				return exectuarTransacaoTransferencia(contaDestino, contaOrigem, valor);
			}
			
			// ------------------------------------------------------------
			// SE CONTA ORIGEM É FILIAL
			// ------------------------------------------------------------											
			// Se conta origem é filial então pode fazer transferência para conta destino filial que esteja abaixo da mesma árvore
			// . se idContaPrincipal for igual pode transferir
			Long idContaPaiDestino = contaDestino.getIdContaPai();
			Long idContaPrinciapal = contaSerivice.recuperarContaPrincipal(idContaPaiDestino);
			Long idContaPrincipalOrigem = contaOrigem.getIdContaPrincipal(); 
			if (idContaPrincipalOrigem != idContaPrinciapal) {
				
				Long idAcaoTransferencia = tipoAcaoTransacao.recuperarTransferencia();
				salvarTransacaoNaoEstaNaMesmaArvore(idAcaoTransferencia, idContaOrigem, idContaDestino, valor);
			}
			
			return exectuarTransacaoTransferencia(contaDestino, contaOrigem, valor);
		
		}  catch (ServiceException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException("Não foi possível realizar transação.", e);
		}
		
		

	}


	private void salvarTransacaoNaoEstaNaMesmaArvore(Long idAcao, Long idContaOrigem, Long idContaDestino,
			BigDecimal valor) throws ServiceException, ValidaTransacaoService {
		
		String descricaoStatusTransacao = "Conta origem e conta destino não fazem parte da mesa árvore";
		Long idTipoStatus = tipoStatusTranacao.recuperarStatusFalha();
		registrarTransacao(idAcao, idContaOrigem, idContaDestino, idTipoStatus, descricaoStatusTransacao, valor);
		throw new ValidaTransacaoService(descricaoStatusTransacao);
		
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

	/**
	 * Apenas contas ativas poderm receber transacoes
	 * 
	 * Conta matriz não pode receber transferência de outras contas
	 * 
	 * @param idContaOrigem
	 * @param idContaDestino
	 * @param valor
	 * @return
	 * @throws ServiceException
	 * @throws ValidaTransacaoService
	 */
	private Conta validarTransacaoContaDestino(Long idContaOrigem, Long idContaDestino, BigDecimal valor)
			throws ServiceException, ValidaTransacaoService {
		
		Conta contaDestino = contaSerivice.recuperarConta(idContaDestino);
		
		boolean isContaAtiva = isContaAtivo(contaDestino);
		if (!isContaAtiva) {
			Long idAcaoTransferencia = tipoAcaoTransacao.recuperarTransferencia();
			salvarTransacaoNaoContaAtiva(idAcaoTransferencia, idContaOrigem, idContaDestino, valor);
		}
		
		boolean isContaDestinoMatriz = isContaMatriz(contaDestino);
		if (isContaDestinoMatriz) {
			Long idAcaoTransferencia = tipoAcaoTransacao.recuperarTransferencia();
			salvarTransacaoNaoPodeTransferirContaMatriz(idAcaoTransferencia, idContaOrigem, idContaDestino, valor);
		}
		
		return contaDestino;
	}

	private void salvarTransacaoNaoPodeTransferirContaMatriz(Long idAcaoTransferencia, Long idContaOrigem,
			Long idContaDestino, BigDecimal valor) throws ValidaTransacaoService, ServiceException {
		
		String descricaoStatusTransacao = "Conta matriz não pode receber transferência de outras contas";
		Long idTipoStatus = tipoStatusTranacao.recuperarStatusFalha();
		registrarTransacao(idAcaoTransferencia, idContaOrigem, idContaDestino, idTipoStatus, descricaoStatusTransacao, valor);
		throw new ValidaTransacaoService(descricaoStatusTransacao);
	}


	private Transacao salvarTransacaoTransferencia(Long idAcao, Long idContaOrigem, Long idContaDestino,
			BigDecimal valor) throws ServiceException {
		
		String descricaoStatusTransacao = "Transacao entre contas realizada com sucesso";
		Long idTipoStatus = tipoStatusTranacao.recuperarStatusSucesso();
		return registrarTransacao(idAcao, idContaOrigem, idContaDestino, idTipoStatus, descricaoStatusTransacao, valor);
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
		//TODO
		// verificar conta destino está ativa
		
		return null;
	}


	/**
	 *
	 * Todo histórico de transação deve ser armazenado e consultado
	 * 
	 * @param idConta
	 * @return 
	 * @throws ServiceException 
	 */
	public List<Transacao> recuperarHistoricoTransacoesPelaConta(Long idConta) {
		
		return dao.recuperarHistoricoTransacoesPelaConta(idConta);
	}
	

}
