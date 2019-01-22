package br.ce.wcaquino.matchers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import br.ce.wcaquino.utils.DataUtils;

public class DataDiferencaDiasMatcher extends TypeSafeMatcher<Date> {

	private Integer qtdDias;

	public DataDiferencaDiasMatcher(Integer qtdDias) {
		this.qtdDias = qtdDias;
	}

	@Override
	public void describeTo(Description description) {
		Date dataEsperada = DataUtils.obterDataComDiferencaDias(qtdDias);
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
		description.appendText(dateFormat.format(dataEsperada));
	}

	@Override
	protected boolean matchesSafely(Date date) {
		return DataUtils.isMesmaData(date, DataUtils.obterDataComDiferencaDias(qtdDias));
	}
}
