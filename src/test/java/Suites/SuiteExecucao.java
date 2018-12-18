package Suites;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import br.ce.wcaquino.servicos.CalculadoraTest;
import br.ce.wcaquino.servicos.CalculoValorLocacaoTest;
import br.ce.wcaquino.servicos.LocacaoServiceTest;

//@RunWith(Suite.class)
@Suite.SuiteClasses({ CalculadoraTest.class, CalculoValorLocacaoTest.class, LocacaoServiceTest.class })
public class SuiteExecucao {
    // Remova se puder!

    @BeforeClass
    public static void before() {
        System.out.println("before");
    }

    @AfterClass
    public static void after() {
        System.out.println("after");
    }
}
