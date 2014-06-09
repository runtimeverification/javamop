/*
 * Created on Aug 17, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package javamop;

/**
 * @author fengchen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MOPException extends Exception {
    private static final long serialVersionUID = 2145299315023315212L;
    public MOPException(Exception e){
        super("MOP Expection:" + e.getMessage());
    }
    public MOPException(String str){
        super(str);
    }
}
