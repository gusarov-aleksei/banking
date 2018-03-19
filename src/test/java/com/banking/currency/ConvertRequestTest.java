package com.banking.currency;

import org.junit.Test;

import java.math.BigDecimal;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;

public class ConvertRequestTest {

    ConvertRequest request = new ConvertRequest(Currency.GBP, Currency.EUR, BigDecimal.TEN);
    ConvertRequest other = new ConvertRequest(Currency.GBP, Currency.EUR, BigDecimal.TEN);

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_sourceIsNull_throwIllegalArgumentException(){
        new ConvertRequest(null, Currency.USD, BigDecimal.TEN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_targetIsNull_throwIllegalArgumentException(){
        new ConvertRequest(Currency.EUR, null, BigDecimal.TEN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_amountIsNull_throwIllegalArgumentException(){
        new ConvertRequest(Currency.EUR, Currency.AUD, null);
    }

    @Test
    public void testConstructor_inputSourceIsEur_getSourceIsEur(){
       assertThat(request.getSource(), is(Currency.GBP));
    }

    @Test
    public void testConstructor_inputTargetIsAud_getTargetIsAud(){
        assertThat(request.getTarget(), is(Currency.EUR));
    }

    @Test
    public void testConstructor_inputAmountIsTen_getAmountIsTen(){
        assertThat(request.getAmount(), is(BigDecimal.TEN));
    }

    @Test
    public void testEquals_allParametersTheSame_equalsReturnsTrue() {
        assertThat(request.equals(other) && other.equals(request), is(true));
    }

    @Test
    public void testEquals_allParametersTheSame_hashCodeIsTheSame() {
        assertThat(request.hashCode() == other.hashCode(), is(true));
    }

    @Test
    public void testEquals_amountIsDifferent_equalsReturnsFalse() {
        ConvertRequest other = new ConvertRequest(Currency.GBP, Currency.EUR, BigDecimal.valueOf(11));
        assertThat(request.equals(other) && other.equals(request), is(false));
    }

    @Test
    public void testEquals_amountIsDifferent_hashCodeIsDifferent() {
        ConvertRequest other = new ConvertRequest(Currency.GBP, Currency.EUR, BigDecimal.valueOf(11));
        assertThat(request.hashCode() == other.hashCode(), is(false));
    }

    @Test
    public void testToString_allMandatoryParametersAreNonEmpty_outputStringIsGenerated() {
        assertThat(request.toString(), is("ConvertRequest{source=GBP, target=EUR, amount=10}"));
    }

}
