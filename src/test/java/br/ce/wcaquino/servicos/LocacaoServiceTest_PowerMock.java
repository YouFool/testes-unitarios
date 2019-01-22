package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFile;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ LocacaoService.class })
public class LocacaoServiceTest_PowerMock {

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
		service = PowerMockito.spy(service);
		System.out.println("Inicializando 4 ...");
		CalculadoraTest.ordem.append("4");
	}

	@After
	public void tearDown() {
		System.out.println("finalizando 4 ...");
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

		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(28, 4, 2017));

		// acao
		Locacao locacao = service.alugarFilme(usuario, filmes);

		// verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(28, 4, 2017)), is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(29, 4, 2017)), is(true));
	}

	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(19, 1, 2019));
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

		// cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFile().agora());

		// acao
		Locacao locacao = service.alugarFilme(usuario, filmes);

		// verificacao
		Assert.assertThat(locacao.getDataRetorno(), caiNumaSegunda());
	}

	@Test
	public void deveAlugarFilme_SemCalcularValor() throws Exception {
		// cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFile().agora());
		PowerMockito.doReturn(1.0).when(service, "calcularValorLocacao", filmes);

		// acao
		Locacao locacao = service.alugarFilme(usuario, filmes);

		// verificacao
		assertThat(locacao.getValor(), is(1.0));
		PowerMockito.verifyPrivate(service).invoke("calcularValorLocacao", filmes);
	}

	@Test
	public void deveCalcularValorLocacao() throws Exception {
		// cenario
		List<Filme> filmes = Arrays.asList(umFile().agora());

		// acao
		Double valor = (Double) Whitebox.invokeMethod(service, "calcularValorLocacao", filmes);

		// verificacao
		assertThat(valor, is(4.0));
	}

}
