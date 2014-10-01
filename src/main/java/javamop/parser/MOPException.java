// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser;

import javamop.ParserService;

/**
 * A general exception class signifying something went wrong in the JavaMOP logic.
 * @author fengchen
 */
public class MOPException extends ParserService.MOPExceptionImpl {
    private static final long serialVersionUID = 2145299315023315212L;
    
    /**
     * Wrap another exception as a MOP logic exception.
     * @param e The exception to wrap.
     */
    protected MOPException(final Exception e){
        super("MOP Expection:" + e.getMessage(), e);
    }
    
    /**
     * Construct a MOPException with an informative message.
     * @param str An informative message describing the error.
     */
    protected MOPException(final String str){
        super(str);
    }
    
    /**
     * Wrap another exception as a MOP logic exception, with an additional message.
     * @param message A descriptive message for the error.
     * @param e The exception to be wrapped.
     */
    protected MOPException(final String message, final Exception e) {
        super("MOP Exception: " + message + ": " + e.getMessage(), e);
    }
}
