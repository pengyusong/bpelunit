/**
* This file belongs to the BPELUnit utility and Eclipse plugin set. See enclosed
* license file for more information.
*
*/
package org.bpelunit.framework.exception;

/**
* Root class for all BPELUnit Exceptions
*
* @version $Id$
* @author Philip Mayer
*
*/
public class BPELUnitException extends Exception {

private static final long serialVersionUID= 1640706044781170456L;

private Throwable fOriginalException;

public BPELUnitException(String message, Throwable e) {
super(message);
fOriginalException= e;
}

public BPELUnitException(String message) {
super(message);
fOriginalException= null;
}

@Override
public String getMessage() {
StringBuffer buffer= new StringBuffer();
buffer.append(super.getMessage() + "\n");
if (fOriginalException != null) {
buffer.append("Original Exception Message: " + fOriginalException.getMessage());
}
return buffer.toString();
}

}