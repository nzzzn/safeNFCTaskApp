package project.safenfctaskapp.nfc;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import project.safenfctaskapp.mcoupon.Certificate;
import project.safenfctaskapp.mcoupon.MCouponUtil;

import com.google.common.base.Charsets;
import com.google.common.primitives.Bytes;

import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Parcelable;
import android.util.Log;

public class NfcReadWrite 
{
	private ArrayList<String> mTypes = new ArrayList<String>();
	private ArrayList<String> mPayLoads = new ArrayList<String>();
	private ArrayList<byte[]> mHashPublicKeyPair = new ArrayList<byte[]>();
	private String errorMessage = new String();
	private NdefMessage webMessage = null;
	private NdefMessage beamMessage = null;
	
	Certificate cert = new Certificate();
	
	public NdefMessage getBeamMessage()
	{
		return beamMessage;
	}
	
	public Boolean readNdefNfc(Intent intent) 
	{
		clearMember();
		
        String action = intent.getAction();
        
        //TECH, NDEF, TAG
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action) || NfcAdapter.ACTION_TAG_DISCOVERED.equals(action))
		{
			NdefMessage[] msgs = null;
			Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			
			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];

				for (int i = 0; i < rawMsgs.length; i++)
				{
					msgs[i] = (NdefMessage) rawMsgs[i];
					for(int j = 0; j < msgs[i].getRecords().length; j++)
					{
						try{
							String defaultTextEncoding = "UTF-8";
							String ttype = new String(msgs[i].getRecords()[j].getType(), 0, msgs[i].getRecords()[j].getType().length, defaultTextEncoding);
							
							byte[] payload = null;
				            payload = msgs[i].getRecords()[j].getPayload();

				            
				            
				            if(ttype.compareTo("mime/schemeZip") == 0)
				            {
				            	mTypes.add("mime/scheme");
				            	mPayLoads.add(ByteArrayToDecodingString(byteArrayUnzip(payload)));
				            }
							else if(ttype.compareTo("mime/schemeUrl") == 0)
							{
								mTypes.add("mime/scheme");
					        	DownloadHttpThread down = new DownloadHttpThread(ByteArrayToDecodingString(payload));
					        	down.start();
					        	
					        	try {
									down.join();
								} catch (InterruptedException e) {
									errorMessage = e.toString();
									return false;
								}
					        	
					        	NdefMessage twebMessage = null;
			            		try {
			            			twebMessage = new NdefMessage(down.getDownByteArray());
			    				} catch (FormatException e) {
			    					errorMessage = e.toString();
			    					return false;
			    				}
				        		
				        		mTypes.add("mime/scheme");
				        		if(new String(twebMessage.getRecords()[0].getType(), 0, twebMessage.getRecords()[0].getType().length, defaultTextEncoding).compareTo("mime/schemeZip") == 0)
				        		{
				        			mPayLoads.add(ByteArrayToDecodingString(byteArrayUnzip(twebMessage.getRecords()[0].getPayload())));
				        		}
				        		else
				        		{
				        			mPayLoads.add(ByteArrayToDecodingString(twebMessage.getRecords()[0].getPayload()));
				        		}
				        		

							}
							else if(ttype.compareTo("mime/schemeEnrypted") == 0)
							{
								if(j == 0)
								{
									mTypes.add(ttype);
									mPayLoads.add(ByteArrayToDecodingString(payload));
								}
								else
								{
									mHashPublicKeyPair.add(payload);
								}
								
							}
							else
							{
								mTypes.add(ttype);
								mPayLoads.add(ByteArrayToDecodingString(payload));
							}
								
				        } catch (UnsupportedEncodingException e) {
				        	errorMessage = e.toString();
				        	return false;
				        }
					}
				}
				
				if(mTypes.get(0).compareTo("mime/schemeEnrypted") == 0)		//schemeEnrypted
				{
					byte[] test = MCouponUtil.decrypt(mHashPublicKeyPair.get(1), cert.getKey("CA").keyPair.getPublic());
					PublicKey publicKey = (PublicKey)MCouponUtil.MsgByteArrayToObjectArray(test)[0];
					byte[] hash = MCouponUtil.decrypt(mHashPublicKeyPair.get(0), publicKey);
					
					if(MCouponUtil.byteArrayCompare(getHashSHA256(mPayLoads.get(0)).getBytes(), hash) != 0)
					{
						errorMessage = "integrity check failed";
						return false;
					}
				}
				
				return true;
			}
			else							//null 이면 NDEF가 아님.
			{
				errorMessage = "do not NDEF!";
			}
		}
		
		return false;
    }
	
	public Boolean WriteNdefNfc(Intent intent, String payload, Boolean isCompress, String url, Boolean isHash, KeyPair keyPair, PrivateKey caPrivateKey) 
	{
		clearMember();
		
		Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);		//nfc Tag를 받아옴.
		
		byte[] data = StringToEncodingByteArray(payload);
		
		NdefRecord[] records = null;
		
		NdefRecord cardRecord = null;
		NdefRecord EncryptedRecord = null;
		NdefRecord KeyRecord = null;
		
		NdefRecord webRecord = null;
		
		byte[] id = new byte[1];
		id[0] = 0;
		
		
		if(isCompress == true)											//payload를 압축한다.
		{
			cardRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "mime/schemeZip".getBytes(), id, byteArrayZip(data));
		}
		if(isHash == true)												//Hash를 만들자.
		{
			cardRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "mime/schemeEnrypted".getBytes(), id, data);
			
			//Scheme 코드의 해쉬값을 Sha256으로 구해서 개발자 PrivateKey 암호화
			byte[] hash = MCouponUtil.encrypt(getHashSHA256(payload).getBytes(), keyPair.getPrivate());
			EncryptedRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "mime/schemeEnrypted".getBytes(), id, hash);
			
			//개발자 PublicKey를 CA의 PrivateKey로 암호화
			PublicKey pk = keyPair.getPublic();
			byte[] test = MCouponUtil.MsgObjectToByteArray(1, pk);
			byte[] publicKey = MCouponUtil.encrypt(test, caPrivateKey);
			KeyRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "mime/schemeEnrypted".getBytes(), id, publicKey);
		}
		else															//payload를 그냥 넣는다.
		{
			cardRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "mime/scheme".getBytes(), id, data);
		}
		
		if(url != "")
		{
			webRecord = cardRecord;
			
			NdefRecord[] webRecords = {webRecord};
			webMessage = new NdefMessage(webRecords);
			
			cardRecord = null;
			cardRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "mime/schemeUrl".getBytes(), new byte[0], StringToEncodingByteArray(url));
			
			try {
				UploadFtpThread fup = new UploadFtpThread(webMessage.toByteArray());
				fup.start();
				
				fup.join();  //이런 식으로 해야 되나?
			} catch (InterruptedException e) {
            	errorMessage = "ftp upload : " + e.toString();
                return false;
			}
		}
		
			
		
		try {
			if(isHash == true)
			{
				NdefRecord[] sumRecord = {cardRecord, EncryptedRecord, KeyRecord};
				records = sumRecord;
			}
			else
			{
				NdefRecord[] sumRecord = {cardRecord};
				records = sumRecord;
			}
			
			NdefMessage message = new NdefMessage(records);
			
			Ndef tag = Ndef.get(tagFromIntent);
			if(tag != null)							//NDEF로 포멧된 카드를 덮어쓸 때...
			{
				tag.connect();
				boolean writeable = tag.isWritable();
				if(writeable)
				{   
				    tag.writeNdefMessage(message);
				}
			}
			else									//NDEF로 포멧되지 않은 카드일 때...
			{
                NdefFormatable format = NdefFormatable.get(tagFromIntent);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        return true;
                    } catch (IOException e) {
                    	errorMessage = "Failed to format tag." + e.toString();
                        return false;
                    }
                } else {
                	errorMessage = "Tag doesn't support NDEF.";
                    return false;
                }
            }
			
			return true;
		} catch (Exception e) {
			errorMessage = "Failed to write tag : " + e.toString() ;
		}

		return false;
    }
	
	public Boolean BeamNdefNfc(String payload, Boolean isCompress, String url, Boolean isHash, KeyPair keyPair, PrivateKey caPrivateKey) 
	{
		clearMember();

		byte[] data = StringToEncodingByteArray(payload);
		
		NdefRecord[] records = null;
		
		NdefRecord cardRecord = null;
		NdefRecord EncryptedRecord = null;
		NdefRecord KeyRecord = null;
		
		NdefRecord webRecord = null;
		
		byte[] id = new byte[1];
		id[0] = 0;
		
		
		if(isCompress == true)											//payload를 압축한다.
		{
			cardRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "mime/schemeZip".getBytes(), id, byteArrayZip(data));
		}
		if(isHash == true)												//Hash를 만들자.
		{
			cardRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "mime/schemeEnrypted".getBytes(), id, data);
			
			//Scheme 코드의 해쉬값을 Sha256으로 구해서 개발자 PrivateKey 암호화
			byte[] hash = MCouponUtil.encrypt(getHashSHA256(payload).getBytes(), keyPair.getPrivate());
			EncryptedRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "mime/schemeEnrypted".getBytes(), id, hash);
			
			//개발자 PublicKey를 CA의 PrivateKey로 암호화
			PublicKey pk = keyPair.getPublic();
			byte[] test = MCouponUtil.MsgObjectToByteArray(1, pk);
			byte[] publicKey = MCouponUtil.encrypt(test, caPrivateKey);
			
			KeyRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "mime/schemeEnrypted".getBytes(), id, publicKey);
		}
		else															//payload를 그냥 넣는다.
		{
			cardRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "mime/scheme".getBytes(), id, data);
		}
		
		if(url != "")
		{
			webRecord = cardRecord;
			
			NdefRecord[] webRecords = {webRecord};
			webMessage = new NdefMessage(webRecords);
			
			cardRecord = null;
			cardRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "mime/schemeUrl".getBytes(), new byte[0], StringToEncodingByteArray(url));
			
			try {
				UploadFtpThread fup = new UploadFtpThread(webMessage.toByteArray());
				fup.start();
				
				fup.join();  
			} catch (InterruptedException e) {
            	errorMessage = "ftp upload : " + e.toString();
                return false;
			}
		}
		
			
		
		try {
			if(isHash == true)
			{
				NdefRecord[] sumRecord = {cardRecord, EncryptedRecord, KeyRecord};
				records = sumRecord;
			}
			else
			{
				NdefRecord[] sumRecord = {cardRecord};
				records = sumRecord;
			}
			
			NdefMessage message = new NdefMessage(records);
			beamMessage = message;
			
			return true;
		} catch (Exception e) {
			errorMessage = "Failed to write tag : " + e.toString() ;
		}

		return false;
    }
	
	private byte[] byteArrayZip(byte[] data)
	{
		byte[] zipData = null;
		
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
			ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(bos));
			zos.setLevel(9);
			ZipEntry ze = new ZipEntry("schemeZip");
			
			zos.putNextEntry(ze);
			zos.write(data, 0, data.length);
			zos.flush();
			
			zos.closeEntry();
			zos.close();
			zos = null;
			
			zipData = bos.toByteArray();
			
			Log.i("dataSize : ", Integer.toString(data.length));
			Log.i("zipdataSize : ", Integer.toString(zipData.length));
			
		} catch (IOException e) {
			errorMessage = "Failed to zip : " + e.toString() ;
		}
		
		return zipData;
	}
	
	private byte[] byteArrayUnzip(byte[] zipdata)
	{
		byte[] unzipData = null;
		
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(zipdata);
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(bis));
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int count;
			
			zis.getNextEntry();
			
			while((count = zis.read(buffer)) != -1)
			{
				bos.write(buffer, 0, count);
			}
			
			zis.closeEntry();
			zis.close();
			
			unzipData = bos.toByteArray();
			
			Log.i("zipdataSize : ", Integer.toString(zipdata.length));
			Log.i("unzipdataSize : ", Integer.toString(unzipData.length));
			
		} catch (IOException e) {
			errorMessage = "Failed to unzip : " + e.toString() ;
		}
		
		return unzipData;
	}
	
	private byte[] StringToEncodingByteArray(String data)
	{
		/*
		 *  TYPE : MIME/SCHEME
		 *  Payload incoding : TEXT (모든 Charter set 이 사용 가능 하도록 변경 가능)
		 *  참조 : Spec\NFCForum-TS-RTD_Text_1.0.pdf => 3.2.1 Syntax
		 */
		Locale locale = Locale.US;
		final byte[] langBytes = locale.getLanguage().getBytes(Charsets.US_ASCII);
		
		final byte[] textBytes = data.getBytes(Charsets.UTF_8);
		final int utfBit = 0;
		final char status = (char) (utfBit + langBytes.length);
		final byte[] encodingArray = Bytes.concat(new byte[] {(byte) status}, langBytes, textBytes);
		
		return encodingArray;
	}
	
	private String ByteArrayToDecodingString(byte[] data)
	{
        /*
         * payload[0] contains the "Status Byte Encodings" field, per the
         * NFC Forum "Text Record Type Definition" section 3.2.1.
         *
         * bit7 is the Text Encoding Field.
         *
         * if (Bit_7 == 0): The text is encoded in UTF-8 if (Bit_7 == 1):
         * The text is encoded in UTF16
         *
         * Bit_6 is reserved for future use and must be set to zero.
         *
         * Bits 5 to 0 are the length of the IANA language code.
         */
		String decodingString = null;
		
		try{
		    String textEncoding = ((data[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
		    int languageCodeLength = data[0] & 0077;
	    	decodingString = new String(data, languageCodeLength + 1, data.length - languageCodeLength - 1, textEncoding);
	    } catch (UnsupportedEncodingException e) {
	    	errorMessage = e.toString();
	    	return "";
	    }
	    return decodingString;
	}

	private void clearMember()
	{
		mPayLoads.removeAll(mPayLoads);
		mTypes.removeAll(mTypes);
		errorMessage = "";
		webMessage = null;
	}
	
	public String getPayload(int index)
	{
		return mPayLoads.get(index);
	}
	
	public String getType(int index)
	{
		return mTypes.get(index);
	}
	
	public String getErrorMessage()
	{
		return errorMessage;
	}
	
	public byte[] getWebMessage()
	{
		if(webMessage != null)
		{
			return webMessage.toByteArray();
		}
		
		return null;
	}
	
	public int getLength()
	{
		return mPayLoads.size();
	}
	
	
	public class DownloadHttpThread extends Thread
	{
		private String url;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		public DownloadHttpThread(String Url)
		{
			url = Url;
		}
		
		@Override
		public void run()
		{
	        try {
	        	URL is_from_url = new URL(url);
	        	InputStream is = is_from_url.openStream();
        		
        		byte buffer[] = new byte[1024];
        		int count;
        		while((count = is.read(buffer, 0, 1024)) != -1)
        		{
        			bos.write(buffer, 0, count);
        		}
        		
       		}
       		catch (IOException e) {
    			Log.i ("NFCScript", "[Exception] : " + e.toString() );
       		}
		}
		
		public byte[] getDownByteArray()
		{
			return bos.toByteArray();
		}
		
	}
	
	public String getHashSHA256(String str){
		String SHA = ""; 
		try{
			MessageDigest sh = MessageDigest.getInstance("SHA-256"); 
			sh.update(str.getBytes()); 
			byte byteData[] = sh.digest();
			StringBuffer sb = new StringBuffer(); 
			for(int i = 0 ; i < byteData.length ; i++){
				sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
			}
			SHA = sb.toString();
			
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace(); 
			SHA = null; 
		}
		return SHA;
	}
	
	private class UploadFtpThread extends Thread
	{
        private String server = "nzzzn.dothome.co.kr";
        private OutputStream os = null;
        private String saveName = "test.scm";
        private String user = "nzzzn";
        private String password = "qwer1234";
        private String path = "html/";
        private byte[] message;
        
        public UploadFtpThread(byte[] byteMessage)
        {
        	message = byteMessage;
        }
        
		@Override
		public void run()
		{
	        try {
	            String link = "ftp://" + user + ":" + password + "@" +server + "/" + path + saveName;
	            URL url = new URL(link);
	            URLConnection urlc = url.openConnection();
	            
	            urlc.setDoOutput(true);
	            os = urlc.getOutputStream();
	            os.write(message, 0, message.length);
	            
	            os.flush();
	            os.close();
	            os = null;
	            
	        } catch ( Exception e ) {
	            e.printStackTrace();
	        } finally {
	            if ( os != null ) try { os.close(); } catch (Exception e) {}
	        }
		}
	}
	
}
