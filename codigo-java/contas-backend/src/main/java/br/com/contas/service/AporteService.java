package br.com.contas.service;

import java.math.BigDecimal;

import javax.transaction.Transactional;

import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.contas.dao.AporteDAO;
import br.com.contas.domain.rdbs.Aporte;

@Service
public class AporteService {
	
	@Autowired
	private AporteDAO dao;
	
	
	@Transactional(rollbackOn = Exception.class)
	public Long criar(String codigo, String descricao) {
		
		try {
			Aporte aporte = new Aporte();
			aporte.setCodigo(codigo);
			aporte.setDescricao(descricao);
			return (Long) dao.save(aporte);
		} catch (Exception e) {
			throw new ServiceException("Flha ao criar pessoa", e);
		}
	}
	
}
