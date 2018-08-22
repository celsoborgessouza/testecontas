package br.com.contas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.contas.dao.SituacaoContaDAO;
import br.com.contas.service.exception.ServiceException;

@Service
public class SituacaoContaService {

	@Autowired
	private SituacaoContaDAO dao;

	public Long recuperarIdAtivo() throws ServiceException {

		return recuperarIdPorNome("ATIVA");
	}

	public Long recuperarIdBloqueado() throws ServiceException {
		
		return recuperarIdPorNome("BLOQUEADO");
	}

	public Long recuperarIdCancelado() throws ServiceException  {

		return recuperarIdPorNome("CANCELADOR");

	}

	public Long recuperarIdPorNome(String nomeSituacao) throws ServiceException {
		
		try {

			return dao.recuperarIdPorNome(nomeSituacao);
		} catch (Exception e) {

			throw new ServiceException("Falha ao recuperar situação da conta", e);
		}
	}

}
