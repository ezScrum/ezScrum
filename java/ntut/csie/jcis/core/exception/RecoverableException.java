package ntut.csie.jcis.core.exception;

public class RecoverableException extends Exception {
	private static final long serialVersionUID = 68402643538533869L;
	
	public RecoverableException(){
		
	}
	
    public RecoverableException(String aMessage) {
        super(aMessage);
    }
	
    public RecoverableException(String aMessage, Throwable aCause) {
        super(aMessage, aCause);
    }
	
	public RecoverableException(Throwable e){
		super(e);
	}
}
