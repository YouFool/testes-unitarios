package br.ce.wcaquino.servicos;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Date;

public class DiaSemanaMatcher extends TypeSafeMatcher <Date> {

    public void describeTo(Description description) {

    }

    protected boolean matchesSafely(Date item) {
        return false;
    }
}
