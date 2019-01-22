package br.ce.wcaquino.servicos;

import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class CalculadoraMockTest {

	@Mock
	private Calculadora calcMock;

	@Spy
	private Calculadora calcSpy;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void devoMostrarDiferencaEntreMockESpy() {
		when(calcMock.somar(1, 2)).thenReturn(5);
		// when(calcSpy.somar(1, 2)).thenReturn(5);
		Mockito.doReturn(5).when(calcSpy).somar(1, 2);
		Mockito.doNothing().when(calcSpy).imprime();

		System.out.println("Mock: " + calcMock.somar(1, 2));
		System.out.println("Spy: " + calcSpy.somar(1, 2));
		calcMock.imprime();
		calcSpy.imprime();
	}

	@Test
	public void teste() {
		Calculadora calculadora = Mockito.mock(Calculadora.class);

		ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
		when(calculadora.somar(argumentCaptor.capture(), Mockito.anyInt())).thenReturn(5);

		Assert.assertEquals(5, calculadora.somar(1, 10000));
	}
}
