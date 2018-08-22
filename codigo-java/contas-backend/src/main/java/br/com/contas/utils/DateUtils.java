package br.com.contas.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	private static final String FORMATO_DATA_PADRAO = "dd/MM/yyyy";
	
	public static Date converterParaDate(String data) throws ParseException {
		return new SimpleDateFormat(FORMATO_DATA_PADRAO).parse(data);
	}

	public static String converterParaFormatoPadrao(Date data) {
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		return formato.format(data);
	}
}
