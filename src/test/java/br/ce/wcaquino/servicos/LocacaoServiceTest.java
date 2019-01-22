package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFile;
import static br.ce.wcaquino.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.ce.wcaquino.builders.LocacaoBuilder.umaLocacao;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {

	@Spy
	@InjectMocks
	private LocacaoService service;

	@Mock
	private SPCService spcService;

	@Mock
	private LocacaoDAO locacaoDAO;

	@Mock
	private EmailService emailService;

	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		System.out.println("Iniciando 2...");
		CalculadoraTest.ordem.append("2");
	}

	@After
	public void tearDown() {
		System.out.println("Finalizando 2...");
	}

	@AfterClass
	public static void tearDownClass() {
		System.out.println(CalculadoraTest.ordem.toString());
	}

	@Test
	public void deveAlugarFilme() throws Exception {
		// cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFile().comValor(5.0).agora());

		Mockito.doReturn(DataUtils.obterData(28, 4, 2017)).when(service).obterData();

		// acao
		Locacao locacao = service.alugarFilme(usuario, filmes);

		// verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(28, 4, 2017)), is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(29, 4, 2017)), is(true));
	}

	@Test(expected = FilmeSemEstoqueException.class)
	public void deveLancarExcecaoAoAlugarFilmeSemEstoque() throws Exception {
		// cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilmeSemEstoque().agora());

		// acao
		service.alugarFilme(usuario, filmes);
	}

	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
		LocacaoService service = new LocacaoService();
		List<Filme> filmes = Arrays.asList(umFile().agora());

		// acao
		try {
			service.alugarFilme(null, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Usuario vazio"));
		}
	}

	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
		// cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = umUsuario().agora();

		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");
		service.alugarFilme(usuario, null);
	}

	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

		// cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFile().agora());

		Mockito.doReturn(DataUtils.obterData(29, 4, 2017)).when(service).obterData();

		// acao
		Locacao locacao = service.alugarFilme(usuario, filmes);

		// verificacao
		Assert.assertThat(locacao.getDataRetorno(), caiNumaSegunda());
	}

	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
		// cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFile().agora());

		when(spcService.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);

		// acao
		try {
			service.alugarFilme(usuario, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Usuário Negativado"));
		}

		// verificacao
		verify(spcService).possuiNegativacao(usuario);
	}

	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() {
		// cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		Usuario usuario2 = UsuarioBuilder.umUsuario().comNome("Paulão").agora();
		Usuario usuario3 = UsuarioBuilder.umUsuario().comNome("Paulinha").agora();
		List<Locacao> locacoes = Arrays.asList( //
				umaLocacao().atrasada().comUsuario(usuario).agora(), //
				umaLocacao().comUsuario(usuario2).agora(), //
				umaLocacao().atrasada().comUsuario(usuario3).agora(),
				umaLocacao().atrasada().comUsuario(usuario3).agora());
		when(locacaoDAO.obterLocacoesPendentes()).thenReturn(locacoes);

		// acao
		service.notificarAtrasos();

		// validacao
		verify(emailService, times(3)).notificarAtraso(Mockito.any(Usuario.class));
		verify(emailService).notificarAtraso(usuario);
		verify(emailService, Mockito.atLeastOnce()).notificarAtraso(usuario3);
		verify(emailService, never()).notificarAtraso(usuario2);
		verifyNoMoreInteractions(emailService);
	}

	@Test
	public void deveTratarErrosSPC() throws Exception {
		// cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFile().agora());

		when(spcService.possuiNegativacao(usuario)).thenThrow(new Exception("Problemas com SPC, tente novamente"));

		// validacao
		exception.expect(LocadoraException.class);
		exception.expectMessage("Problemas com SPC, tente novamente");

		// acao
		service.alugarFilme(usuario, filmes);
	}

	@Test
	public void deveProrrogarUmaLocacao() {
		// cenario
		Locacao locacao = umaLocacao().agora();

		// acao
		service.prorrogarLocacao(locacao, 3);

		// validacao
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
		Mockito.verify(locacaoDAO).salvar(argCapt.capture());
		Locacao locacaoRetornada = argCapt.getValue();

		error.checkThat(locacaoRetornada.getValor(), is(12.0));
		error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
		error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeComDiferencaDias(3));
	}

	@Test
	public void deveCalcularValorLocacao() throws Exception {
		// cenario
		List<Filme> filmes = Arrays.asList(umFile().agora());

		// acao
		Class<LocacaoService> clazz = LocacaoService.class;
		Method metodo = clazz.getDeclaredMethod("calcularValorLocacao", List.class);
		metodo.setAccessible(true);
		Double valor = (Double) metodo.invoke(service, filmes);

		// verificacao
		assertThat(valor, is(4.0));
	}

}
