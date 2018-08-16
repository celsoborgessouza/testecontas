package br.com.contas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.contas.dao.TipoStatusTransacaoDAO;
import br.com.contas.service.exception.ServiceException;

@Service
public class TipoStatusTransacaoService {

	@Autowired
	private TipoStatusTransacaoDAO dao;

	public Long recuperarStatusSucesso() throws ServiceException {

		try {

			return dao.recuperarIdPorNome("SUCESSO");
		} catch (Exception e) {

			throw new ServiceException("Falha ao recuperar identificador do tipo status sucesso", e);
		}
	}

	public Long recuperarStatusFalha() throws ServiceException {
		
		try {
			
			return dao.recuperarIdPorNome("FALHA");
		} catch (Exception e) {
			
			throw new ServiceException("Falha ao recuperar identificador do tipo status falha", e);
		}
	}

}
