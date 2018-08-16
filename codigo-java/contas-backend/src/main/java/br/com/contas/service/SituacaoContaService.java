package br.com.contas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.contas.dao.SituacaoContaDAO;
import br.com.contas.service.exception.ServiceException;

@Service
public class SituacaoContaService {

	@Autowired
	private SituacaoContaDAO dao;


	public Long recuperarIdAtivo() throws ServiceException  {
		
		try {

			return dao.recuperarIdPorNome("ATIVO");
		} catch (Exception e) {
			
			throw new ServiceException("Falha ao recuperar situação da conta", e);
		}
	}

	public Long recuperarIdBloqueado() throws Exception {
		
		try {
			
			return dao.recuperarIdPorNome("BLOQUEADO");
		} catch (Exception e) {
			
			throw new Exception("Falha ao recuperar situação da conta", e);
		}
	}

	public Long recuperarIdCancelador() throws Exception {
		
		try {
			
			return dao.recuperarIdPorNome("CANCELADOR");
		} catch (Exception e) {
			
			throw new Exception("Falha ao recuperar situação da conta", e);
		}
	}

}
