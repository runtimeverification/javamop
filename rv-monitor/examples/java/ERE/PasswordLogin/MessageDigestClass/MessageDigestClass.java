package MessageDigestClass;

import java.security.MessageDigest;


public class MessageDigestClass {

	public static void main(String[] args) throws Exception {
		// Create a Message Digest from a Factory method
	      MessageDigest md = MessageDigest.getInstance("SHA-1");
		rvm.PasswordLoginRuntimeMonitor.getInstanceEvent(md);

	      //This part is commented so that the property of message digest is violated.
	      /*
	      String Password = "Get In";
	      byte[] msg = Password.getBytes();
	      md.update(msg);
	      rvm.PasswordLoginRuntimeMonitor.updateEvent(md);
	      */

		rvm.PasswordLoginRuntimeMonitor.digestEvent(md);
	      byte[] aMessageDigest = md.digest();

	      // Printout
	//      System.out.println("Original: " + new String(msg));
	//      System.out.println("Message Digest: " + new String(aMessageDigest));
	}

}
