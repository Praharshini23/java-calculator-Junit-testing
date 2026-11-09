
package com.calculator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CalculatorTest {

    Calculator calculator = new Calculator();

    @Test
    void testAddition() {
        assertEquals(15, calculator.add(10, 5));
    }

    @Test
    void testSubtraction() {
        assertEquals(5, calculator.subtract(10, 5));
    }

    @Test
    void testMultiplication() {
        assertEquals(50, calculator.multiply(10, 5));
    }

    @Test
    void testDivision() {
        assertEquals(2.0, calculator.divide(10, 5), 0.0001);
    }

    @Test
    void testNegativeAddition() {
        assertEquals(-5, calculator.add(-10, 5));
    }

    @Test
    void testAdditionOfTwoNegativeNumbers() {
        assertEquals(-10, calculator.add(-4, -6));
    }

    @Test
    void testNegativeMultiplication() {
        assertEquals(-20, calculator.multiply(-5, 4));
    }

    @Test
    void testMultiplicationOfTwoNegativeNumbers() {
        assertEquals(20, calculator.multiply(-5, -4));
    }

    @Test
    void testSubtractNegativeNumber() {
        assertEquals(15, calculator.subtract(10, -5));
    }

    @Test
    void testSubtractionProducesNegativeResult() {
        assertEquals(-5, calculator.subtract(5, 10));
    }

    @Test
    void testNegativeDivision() {
        assertEquals(-2.0, calculator.divide(-10, 5), 0.0001);
    }

    @Test
    void testDivisionOfTwoNegativeNumbers() {
        assertEquals(2.0, calculator.divide(-10, -5), 0.0001);
    }

    @Test
    void testDivisionWithFractionalResult() {
        assertEquals(3.5, calculator.divide(7, 2), 0.0001);
    }

    @Test
    void testAdditionWithZero() {
        assertEquals(10, calculator.add(10, 0));
    }

    @Test
    void testMultiplicationWithZero() {
        assertEquals(0, calculator.multiply(10, 0));
    }

    @Test
    void testZeroDividedByNonzeroNumber() {
        assertEquals(0.0, calculator.divide(0, 5), 0.0001);
    }

    @Test
    void testDivisionByZero() {
        assertThrows(
            ArithmeticException.class,
            () -> calculator.divide(10, 0)
        );
    }

    @Test
    void testDivisionByZeroIncludesHelpfulMessage() {
        ArithmeticException exception = assertThrows(
            ArithmeticException.class,
            () -> calculator.divide(10, 0)
        );
        assertEquals("Cannot divide by zero", exception.getMessage());
    }

    @Test
    void testNegativeDivisionByZero() {
        assertThrows(
            ArithmeticException.class,
            () -> calculator.divide(-10, 0)
        );
    }

    @Test
    void testNotEquals() {
        assertNotEquals(20, calculator.add(10, 5));
    }

    @Test
    void testAdditionWithIntentionalFailure() {
        assertEquals(16, calculator.add(10, 5)); // Actual result is 15.
    }

    @Test
    void testMultiplicationWithIntentionalFailure() {
        assertEquals(51, calculator.multiply(10, 5)); // Actual result is 50.
    }
}
