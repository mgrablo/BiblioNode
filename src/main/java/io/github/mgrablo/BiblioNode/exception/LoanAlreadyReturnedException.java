package io.github.mgrablo.BiblioNode.exception;

public class LoanAlreadyReturnedException extends RuntimeException {
	public LoanAlreadyReturnedException(String message) {
		super(message);
	}
}
