package br.ce.wcaquino.suites;

import org.junit.runners.Suite;

import br.ce.wcaquino.servicos.CalculoValorLocacaoTest;
import br.ce.wcaquino.servicos.LocacaoServiceTest;

//@RunWith(Suite.class)
@Suite.SuiteClasses({
//	CalculadoraTest.class,
		CalculoValorLocacaoTest.class, //
		LocacaoServiceTest.class //
})
public class SuiteExecucao {
	// Remova se puder!
}
