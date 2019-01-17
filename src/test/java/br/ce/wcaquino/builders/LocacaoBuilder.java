package br.ce.wcaquino.builders;

import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;

import java.util.Arrays;
import java.util.Date;

import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoBuilder {

	private Locacao locacao;

	public LocacaoBuilder() {
	}

	public static LocacaoBuilder umaLocacao() {
		LocacaoBuilder builder = new LocacaoBuilder();
		Locacao locacao = new Locacao();
		locacao.setUsuario(UsuarioBuilder.umUsuario().agora());
		locacao.setDataLocacao(new Date());
		locacao.setDataRetorno(DataUtils.adicionarDias(locacao.getDataLocacao(), 7));
		locacao.setValor(4D);
		locacao.setFilmes(Arrays.asList(FilmeBuilder.umFile().agora()));
		builder.locacao = locacao;
		return builder;
	}

	public Locacao agora() {
		return this.locacao;
	}

	public LocacaoBuilder comUsuario(Usuario usuario) {
		locacao.setUsuario(usuario);
		return this;
	}

	public LocacaoBuilder comDataRetorno(Date date) {
		locacao.setDataRetorno(date);
		return this;
	}

	public LocacaoBuilder comDataLocacao(Date date) {
		locacao.setDataLocacao(date);
		return this;
	}

	public LocacaoBuilder atrasada() {
		locacao.setDataLocacao(obterDataComDiferencaDias(-4));
		locacao.setDataRetorno(obterDataComDiferencaDias(-2));
		return this;
	}

}
