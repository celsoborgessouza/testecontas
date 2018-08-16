package br.com.contas.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

// Necessário utilizar anotação quando está utilizando JAXB
//@XmlRootElement
public class Response<T> {

	private String status;

	private String msg;

	private T objeto;

	private T lista;

	private String stackTrace;

	public Response() {
	}

	public static <T> Response OK(T object) {
		return OK("", object);
	}

	public static <T> Response OK(String string, T object) {
		Response r = new Response();
		r.setStatus("OK");
		r.setMsg(string);
		r.setObjeto(object);
		return r;
	}

	public static <T> Response OK(List<T> list) {
		return OK("", list);
	}

	public static <T> Response OK(String string, List<T> list) {
		Response r = new Response();
		r.setStatus("OK");
		r.setMsg(string);
		r.setLista(list);
		return r;
	}

	public static <T> Response Error(String string, T object) {
		Response r = new Response();
		r.setStatus("OK");
		r.setMsg(string);
		r.setObjeto(object);
		return r;
	}

	public static <T> Response Error(String string, List<T> list) {
		Response r = new Response();
		r.setStatus("OK");
		r.setMsg(string);
		r.setLista(list);
		return r;
	}

	public static Response OK(String string) {
		Response r = new Response();
		r.setStatus("OK");
		r.setMsg(string);
		return r;
	}

	public static Response Error(String string) {
		Response r = new Response();
		r.setStatus("ERROR");
		r.setMsg(string);
		return r;
	}

	public static Response Error(String string, Throwable e) {
		Response r = new Response();
		r.setStatus("ERROR");
		r.setMsg(string);
		r.setStackTrace(getStackTrace(e));
		return r;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg == null ? "" : msg;
	}

	public T getObjeto() {
		return objeto;
	}

	public void setObjeto(T objeto) {
		this.objeto = objeto;
	}

	public T getLista() {
		return lista;
	}

	public void setLista(T lista) {
		this.lista = lista;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	private static String getStackTrace(Throwable aThrowable) {
		Writer result = new StringWriter();
		PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}

}
