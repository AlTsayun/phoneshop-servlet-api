package com.es.phoneshop.domain.common.model;

import org.junit.Test;

import static com.es.phoneshop.domain.common.model.PaymentMethod.CASH;
import static com.es.phoneshop.domain.common.model.PaymentMethod.CREDIT_CARD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PaymentMethodTest {

    @Test
    public void testFromString() {
        assertEquals(CASH, PaymentMethod.fromString("cash"));
        assertEquals(CASH, PaymentMethod.fromString("CASH"));
        assertEquals(CASH, PaymentMethod.fromString("caSh"));

        assertEquals(CREDIT_CARD, PaymentMethod.fromString("credit_card"));
        assertEquals(CREDIT_CARD, PaymentMethod.fromString("CREDIT_CARD"));
        assertEquals(CREDIT_CARD, PaymentMethod.fromString("cRedit_cArd"));
    }

    @Test
    public void testFromStringWrongStr() {
        assertNull(PaymentMethod.fromString("123"));
        assertNull(PaymentMethod.fromString("c a sh"));
        assertNull(PaymentMethod.fromString("qwerty"));
        assertNull(PaymentMethod.fromString("credit card"));
    }
}