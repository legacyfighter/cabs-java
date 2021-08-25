package io.legacyfighter.cabs.money;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void canCreateMoneyFromInteger() {
        //expect
        assertEquals("100.00", new Money(10000).toString());
        assertEquals("0.00", new Money(0).toString());
        assertEquals("10.12", new Money(1012).toString());
    }

    @Test
    void shouldProjectMoneyToInteger() {
        //expect
        assertEquals(10, new Money(10).toInt());
        assertEquals(0, new Money(0).toInt());
        assertEquals(-5, new Money(-5).toInt());
    }

    @Test
    void canAddMoney() {
        //expect
        assertEquals(new Money(1000), new Money(500).add(new Money(500)));
        assertEquals(new Money(1042), new Money(1020).add(new Money(22)));
        assertEquals(new Money(0), new Money(0).add(new Money(0)));
        assertEquals(new Money(-2), new Money(-4).add(new Money(2)));
    }

    @Test
    void canSubtractMoney() {
        //expect
        assertEquals(Money.ZERO, new Money(50).subtract(new Money(50)));
        assertEquals(new Money(998), new Money(1020).subtract(new Money(22)));
        assertEquals(new Money(-1), new Money(2).subtract(new Money(3)));
    }

    @Test
    void canCalculatePercentage() {
        //expect
        assertEquals("30.00", new Money(10000).percentage(30).toString());
        assertEquals("26.40", new Money(8800).percentage(30).toString());
        assertEquals("88.00", new Money(8800).percentage(100).toString());
        assertEquals("0.00", new Money(8800).percentage(0).toString());
        assertEquals("13.20", new Money(4400).percentage(30).toString());
        assertEquals("0.30", new Money(100).percentage(30).toString());
        assertEquals("0.00", new Money(1).percentage(40).toString());
    }

}