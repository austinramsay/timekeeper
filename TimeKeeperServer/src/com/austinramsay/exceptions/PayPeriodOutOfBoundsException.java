package com.austinramsay.exceptions;

/**
 * Exception should be thrown if reaching the last pay period of the year and we need to build another stack of pay periods for the next year.
 * This exception should be thrown upon failing to find a fitting pay period for the current date.
 */
public class PayPeriodOutOfBoundsException extends Exception {

    public PayPeriodOutOfBoundsException(String message) {
        super(message);
    }

}
