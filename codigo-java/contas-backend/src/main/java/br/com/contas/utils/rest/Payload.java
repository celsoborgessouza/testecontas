package br.com.contas.utils.rest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;


public class Payload<T> {

	private String status;

	private String msg;

	private T objeto;

	private T lista;

	private String stackTrace;

	

	public static <T> Payload<T> OK(T object) {
		return OK("", object);
	}
	
	public static <T> Payload<T> OK(String string, T object) {
		Payload<T> r = new Payload<T>();
		r.setStatus("OK");
		r.setMsg(string);
		r.setObjeto(object);
		return r;
	}
	
	public static <T> Payload<?> OK(List<T> list) {
		return OK("", list);
	}
	
	public static <T> Payload<List<T>> OK(String string, List<T> list) {
		Payload<List<T>> r = new Payload<List<T>>();
		r.setStatus("OK");
		r.setMsg(string);
		r.setLista(list);
		return r;
	}

	public static <T> Payload<T> Error(String string, T object) {
		Payload<T> r = new Payload<T>();
		r.setStatus("ERROR");
		r.setMsg(string);
		r.setObjeto(object);
		return r;
	}

	public static <T> Payload<List<T>> Error(String string, List<T> list) {
		Payload<List<T>> r = new Payload<List<T>>();
		r.setStatus("OK");
		r.setMsg(string);
		r.setLista(list);
		return r;
	}

	public static Payload<String> OK(String string) {
		Payload<String> r = new Payload<>();
		r.setStatus("OK");
		r.setMsg(string);
		return r;
	}

	public static Payload<String> Error(String string) {
		Payload<String> r = new Payload<>();
		r.setStatus("ERROR");
		r.setMsg(string);
		return r;
	}

	public static Payload<String> Error(String string, Throwable e) {
		Payload<String> r = new Payload<String>();
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
