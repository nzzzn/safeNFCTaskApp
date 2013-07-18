package project.safenfctaskapp.mcoupon;

import java.security.KeyPair;

public class User {
	
	private Certificate cert = new Certificate();
	private CertificateIDPair cpi = new CertificateIDPair();
	
	public User()
	{
		cpi = cert.getKey("User");
	}
	
	public String getID()
	{
		return cpi.ID;
	}
	
	public KeyPair getKey()
	{
		return cpi.keyPair;
	}
}
