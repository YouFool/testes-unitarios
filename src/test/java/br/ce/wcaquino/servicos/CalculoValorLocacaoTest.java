package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFile;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {

    private LocacaoService service;
    
    private LocacaoDAO locacaoDAO;
    
    private SPCService spcService;

    @Parameterized.Parameter
    public  List<Filme> filmes;

    @Parameterized.Parameter(value=1)
    public Double valorLocacao;

    @Parameterized.Parameter(value = 2)
    public String cenario;

    @Before
    public void setup() {
        service = new LocacaoService();
    	locacaoDAO = Mockito.mock(LocacaoDAO.class);
    	service.setLocacaoDAO(locacaoDAO);
		spcService = Mockito.mock(SPCService.class);
		service.setSPCService(spcService);
    }

    private static Filme filme1 = umFile().agora();
    private static Filme filme2 = umFile().agora();
    private static Filme filme3 = umFile().agora();
    private static Filme filme4 = umFile().agora();
    private static Filme filme5 = umFile().agora();
    private static Filme filme6 = umFile().agora();
    private static Filme filme7 = umFile().agora();

    @Parameterized.Parameters(name = "{2}")
    public static Collection<Object[]> getParametros(){
        return Arrays.asList(new Object[][] {
                {Arrays.asList(filme1, filme2), 8.0, "2 Filmes, Sem desconto"},
                {Arrays.asList(filme1, filme2, filme3), 11.0, "3 Filmes, 25%"},
                {Arrays.asList(filme1, filme2, filme3, filme4), 13.0, "4 Filmes, 50%"},
                {Arrays.asList(filme1, filme2, filme3, filme4, filme5), 14.0, "5 Filmes, 75%"},
                {Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14.0, "6 Filmes, 100%"},
                {Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6, filme7), 18.0, "7 Filmes, Sem Desconto"}
        });
    }

    @Test
    public void deveCalcularValorLocacaoConsiderandoDescontos() throws FilmeSemEstoqueException, LocadoraException {
        //cenario
        Usuario usuario = new Usuario("Usuario 1");

        //acao
        Locacao locacao = service.alugarFilme(usuario, filmes);

        //verificacao
        assertThat(locacao.getValor(), is(valorLocacao));
    }


}
