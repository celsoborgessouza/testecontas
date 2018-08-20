package br.com.contas.service;

import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import br.com.contas.dao.AporteDAO;
import br.com.contas.domain.rdbs.Aporte;
import br.com.contas.service.exception.ServiceException;

@Service
public class AporteService {

	@Autowired
	private AporteDAO dao;

	/**
	 * Criar código alfanumérico Criar aporte
	 * 
	 * @param descricao
	 * @return
	 * @throws ServiceException
	 */
	@Transactional(rollbackOn = Exception.class)
	public Aporte criar(String descricao) throws ServiceException {

		try {
			validarDescricao(descricao);

			String codigo = gerarCodigoAlfanumerico();

			Aporte aporte = new Aporte();
			aporte.setCodigo(codigo);
			aporte.setDescricao(descricao);
			
			Long idAporte = (Long) dao.save(aporte);
			return dao.load(idAporte);
		} catch (ServiceException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException("Não foi possível criar o aprote", e);
		}

	}

	private void validarDescricao(String descricao) throws ServiceException {
		int maxLength = 50;
		if (StringUtils.isEmpty(descricao) || descricao.length() > maxLength) {
			throw new ServiceException(String.format("Defina uma descrição do aparte em até %d carateres ", maxLength));
		}
	}

	private String gerarCodigoAlfanumerico() {

		return UUID.randomUUID().toString().replaceAll("-", "");
	}

}
