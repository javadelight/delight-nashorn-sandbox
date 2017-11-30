package delight.nashornsandbox.exceptions;

import javax.script.ScriptException;

/**
 * Exception thrown when braces "{}" are missed in JS script, when it is not
 * allowed.
 *
 * <p>Created on 2017.11.24</p>
 *
 * @author <a href="mailto:marcin.golebski@verbis.pl">Marcin Golebski</a>
 * @version $Id$
 */
public class BracesException extends ScriptException {
    private static final long serialVersionUID = 1L;
    
    public BracesException(final String s) {
        super(s);
    }
    
}
