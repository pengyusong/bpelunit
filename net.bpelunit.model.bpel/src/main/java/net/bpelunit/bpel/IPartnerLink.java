package net.bpelunit.bpel;

import javax.xml.namespace.QName;

public interface IPartnerLink {

	String getName();

	void setName(String newName);

	QName getPartnerLinkType();

	void setPartnerLinkType(QName value);

	String getMyRole();

	void setMyRole(String value);

	void setPartnerRole(String value);

	String getPartnerRole();

}
