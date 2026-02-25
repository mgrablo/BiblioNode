package io.github.mgrablo.BiblioNode.exception;

public class LoanLimitExceededException extends RuntimeException {
	public LoanLimitExceededException(String message) {
		super(message);
	}
}
