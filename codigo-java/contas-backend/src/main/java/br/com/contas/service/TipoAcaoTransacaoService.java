package br.com.contas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.contas.dao.TipoAcaoTransacaoDAO;
import br.com.contas.service.exception.ServiceException;

@Service
public class TipoAcaoTransacaoService {

	@Autowired
	private TipoAcaoTransacaoDAO dao;

	public Long recuperarAporte() throws ServiceException {

		try {

			return dao.recuperarIdPorNome("APORTE");
		} catch (Exception e) {

			throw new ServiceException("Falha ao recuperar identificador da ação de aporte", e);
		}
	}

	public Long recuperarTransferencia() throws ServiceException {

		try {

			return dao.recuperarIdPorNome("TRANSFERENCIA");
		} catch (Exception e) {

			throw new ServiceException("Falha ao recuperar identificador da ação de transferencia", e);
		}
	}

	public Long recuperarEstorno() throws ServiceException {

		try {

			return dao.recuperarIdPorNome("ESTORNO");
		} catch (Exception e) {

			throw new ServiceException("Falha ao recuperar identificador da ação de estorno", e);
		}
	}

}
