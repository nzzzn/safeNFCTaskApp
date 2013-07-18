package project.safenfctaskapp.mcoupon;

import java.security.KeyPair;

public class Cashier {

	private Certificate cert = new Certificate();
	private CertificateIDPair cpi = new CertificateIDPair();
	
	public Cashier(int storeNumber)
	{
		if(storeNumber == 0)
		{
			cpi = cert.getKey("Issuer1");
		}
		else if(storeNumber == 1)
		{
			cpi = cert.getKey("Issuer2");
		}
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
