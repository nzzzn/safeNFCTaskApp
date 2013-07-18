package project.safenfctaskapp.mcoupon;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import android.util.Log;

public class MCouponUtil {

	public static byte[] encrypt(byte[] byteArray, PrivateKey privateKey)
	{
        Cipher cipher = null;
        
		try {
			cipher = Cipher.getInstance("RSA/ECB/NoPadding");
			/*
				(define cipher (javax.crypto.Cipher.getInstance "RSA/ECB/NoPadding"))
			 */
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
        try {
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			/*
				(.init cipher (Cipher.ENCRYPT_MODE$) privateKey)
			 */
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        ByteArrayInputStream input = new ByteArrayInputStream(byteArray);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        /*
        	(define input (java.io.ByteArrayInputStream.ByteArrayInputStream. byteArray))
        	(define output (java.io.ByteArrayOutputStream.ByteArrayOutputStream.))
         */
        
		try {
			
			while(input.available() != 0)
			{
				byte[] t0 = new byte[100];
				input.read(t0);
				output.write(cipher.doFinal(t0));
			}
			/*
				(repCipher input output cipher)
				
				
				(define (repCipher input output cipher)
					(let ((a (.available input)))
						(if (= a 0)
							()
							(
								(define t0 (java.lang.reflect.Array.newInstance. (java.lang.Byte.TYPE$) 100))
								(.read input t0)
								(.write output (.doFinal cipher t0))
								(repCipher input output cipher)
							))))
			 */
			
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return output.toByteArray();
        /*
        	(define (encrypt byteArray privateKey) 
				(begin
					(define cipher (javax.crypto.Cipher.getInstance "RSA/ECB/PKCS1Padding"))
					(.init cipher (Cipher.ENCRYPT_MODE$) privateKey)
					(define input (java.io.ByteArrayInputStream.ByteArrayInputStream. byteArray))
        			(define output (java.io.ByteArrayOutputStream.ByteArrayOutputStream.))
        			(repCipher input output cipher)
					(.toByteArray output)))

         */
	}
	
	
	public static byte[] decrypt(byte[] byteArray, PublicKey publicKey)
	{
		Cipher cipher = null;
		
		try {
			
			//cipher = Cipher.getInstance("RSA");	
			//cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher = Cipher.getInstance("RSA/ECB/NoPadding");
			/*
			   A transformation is of the form:
				"algorithm/mode/padding" or
				"algorithm"
			 */
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        try {
        	
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        ByteArrayInputStream input = new ByteArrayInputStream(byteArray);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
		try {
			
			while(input.available() != 0)
			{
				byte[] t0 = new byte[128];
				input.read(t0);
				output.write(cipher.doFinal(t0));
			}
			
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return output.toByteArray();
	}
	
	
	public static int byteArrayCompare(byte[] byte1, byte[] byte2)
	{
		byte[] tByte1 = new byte[byte2.length];
		
		ByteArrayInputStream input = new ByteArrayInputStream(byte1);
		
		try {
			input.read(tByte1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ByteBuffer byteBuf1 = ByteBuffer.wrap(tByte1);
		ByteBuffer byteBuf2 = ByteBuffer.wrap(byte2);
		
		return byteBuf1.compareTo(byteBuf2);
	}
	
	
	//Object to byte[]
	public static byte[] ObjectToByte(Object obj)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		/*
			(define bos (java.io.ByteArrayOutputStream.ByteArrayOutputStream.))
		 */
		ObjectOutput out = null;
		
		try {
			out = new ObjectOutputStream(bos);
			/*
				(define out (java.io.ObjectOutputStream.ObjectOutputStream. bos))
			 */
			out.writeObject(obj);
			/*
				(.writeObject out obj)
			 */
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   

		byte[] Bytes = bos.toByteArray(); 
		/*
			(define bytes (.toByteArray bos))
		 */
		
		return Bytes;
		
		/*
		(define (ObjectToByte obj) 
			(begin
				(define bos (java.io.ByteArrayOutputStream.ByteArrayOutputStream.))
				(define out (java.io.ObjectOutputStream.ObjectOutputStream. bos))
				(.writeObject out obj)
				(define bytes (.toByteArray bos))
				bytes))
		 */
	}

	
	
	//byte[] to Object
	public static Object ByteToObject(byte[] bytes)
	{
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		/*
			(define bis (java.io.ByteArrayInputStream.ByteArrayInputStream. bytes))
		*/
		ObjectInput in = null;
		
		try {
			in = new ObjectInputStream(bis);
			/*
				(define in (java.io.ObjectInputStream.ObjectInputStream. bis))
			 */
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Object obj = null;
		try {
			obj = in.readObject();
			/*
				(define obj (.readObject in))
			 */
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return obj;
		/*
			(define (ByteToObject bytes) 
				(begin
					(define bis (java.io.ByteArrayInputStream.ByteArrayInputStream. bytes))
					(define in (java.io.ObjectInputStream.ObjectInputStream. bis))
					(define obj (.readObject in))
					obj))
		 */
	}
	
	
	/*
	//byte[]를 합친다.
	public static byte[] byteCombine(byte[] byte1, byte[] byte2)
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		try {
			outputStream.write(byte1);
			outputStream.write(byte2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outputStream.toByteArray();
	}
	*/
	
	
	
	public static byte[] MsgObjectToByteArray(int index, Object...obj)
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		/*
			(define outputStream (java.io.ByteArrayOutputStream.ByteArrayOutputStream.))
		 */
		
		try {
			outputStream.write(1);
			outputStream.write(intToByte(index));
			/*
			 * intToByte 함수 만들 필요 있음.
				(.write outputStream 1)
				(.write outputStream intToByte index)
				
			 */
			
			for(int i = 0; i < index; i++)
			{
				String canonicalName = obj[i].getClass().getCanonicalName();
				//java.lang.reflect.Array.get(obj, i);
				/*
					(define canonicalName (.getCanonicalName (.getClass (java.lang.reflect.Array.get. objs i))))
				 */
				Log.i("canonicalName : ", obj[i].getClass().getCanonicalName());
				if(canonicalName.equals("java.lang.Character"))
				{
					outputStream.write('C');
				}
				else if(canonicalName.equals("java.lang.Boolean"))
				{
					outputStream.write("B".getBytes());
				}
				else if(canonicalName.equals("java.lang.Integer"))
				{
					outputStream.write("I".getBytes());
				}
				else if(canonicalName.equals("java.lang.Double"))
				{
					outputStream.write("D".getBytes());
				}
				else if(canonicalName.equals("java.lang.String"))
				{
					outputStream.write("S".getBytes());
				}
				else									//사용자 정의 타입
				{
					outputStream.write("U".getBytes());
				}
				/*
				(if (.equals canonicalName \"java.lang.Character\")
					(.write outputStream (.getBytes "C"))
					((if (.equals canonicalName \"java.lang.Boolean\")
						(.write outputStream (.getBytes "B"))
						((if (.equals canonicalName \"java.lang.Integer\")
							(.write outputStream (.getBytes "I"))
							((if (.equals canonicalName \"java.lang.Double\")
								(.write outputStream (.getBytes "D"))
								((if (.equals canonicalName \"java.lang.String\")
									(.write outputStream (.getBytes "S"))
									(.write outputStream (.getBytes "U"))
								)))))))))
				

				//만약 "C".getBytes() 안되면 new String("C")로 한다.
				*/
				
				
				outputStream.write(intToByte(ObjectToByte(obj[i]).length));
				/*
				 * ObjectToByte 필요함..  OK
				 * intToByte 필요함..	   OK
					(.write outputStream (.intToByte (.ObjectToByte (.length$ (java.lang.reflect.Array.get. objs i)))))
				 */
			}
			
			/*
			 * 
				(writeObjs 0 index obj outputStream)
				
				(define (writeObjs i n objs outputStream)
					(if (< i n)
						(begin (define canonicalName (.getCanonicalName (.getClass (java.lang.reflect.Array.get. objs i))))
							   (if (.equals canonicalName \"java.lang.Character\")
								(.write outputStream (.getBytes "C"))
								((if (.equals canonicalName \"java.lang.Boolean\")
									(.write outputStream (.getBytes "B"))
									((if (.equals canonicalName \"java.lang.Integer\")
										(.write outputStream (.getBytes "I"))
										((if (.equals canonicalName \"java.lang.Double\")
											(.write outputStream (.getBytes "D"))
											((if (.equals canonicalName \"java.lang.String\")
												(.write outputStream (.getBytes "S"))
												(.write outputStream (.getBytes "U"))
											)))))))))
							   (.write outputStream (.intToByte (.ObjectToByte (.length$ (java.lang.reflect.Array.get. objs i)))))
							   (writeObjs (+ i 1) n objs output))
						()))
			*/
			
			
			for(int i = 0; i < index; i++)
			{
				outputStream.write(ObjectToByte(obj[i]));
			}
			/*
				(writeObjsBytes 0 index obj outputStream)
				
				(define (writeObjsBytes i n objs outputStream)
					(if (< i n)
						(begin (.write outputStream (.ObjectToByte (java.lang.reflect.Array.get. objs i)))
							   (writeObjsBytes (+ i 1) n objs outputStream))
						()))
			 */
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outputStream.toByteArray();
		
		/*
        	(define (MsgObjectToByteArray index obj) 
				(begin
					(define outputStream (java.io.ByteArrayOutputStream.ByteArrayOutputStream.))
					(.write outputStream 1)
					(.write outputStream intToByte index)
					(writeObjs 0 index obj outputStream)
					(writeObjsBytes 0 index obj outputStream)
					(.toByteArray outputStream)))

		 */
	}
	
	public static Object[] MsgByteArrayToObjectArray(byte[] byteArray)
	{
		byte[] count = new byte[8];

		ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
		
		inputStream.read(count, 0, 1);			//0001 인경우 000이 지워지는 경우 있어서 1을 추가해서 10001로 들어옴 그러므로 1개 버려야 됨.
		inputStream.read(count, 0, 8);
		
		Object[] obj = new Object[byteToInt(count)];
		
		ArrayList<byte[]> typeArray = new ArrayList<byte[]>();
		ArrayList<byte[]> typeCountArray = new ArrayList<byte[]>();
		
		for(int i = 0; i < byteToInt(count); i++)
		{
			byte[] type = new byte[1];
			byte[] typeCount = new byte[8];
			
			inputStream.read(type, 0, 1);
			typeArray.add(type);
			
			inputStream.read(typeCount, 0, 8);
			typeCountArray.add(typeCount);
		}
		

		for(int i = 0; i < typeArray.size(); i++)
		{
			byte[] data = new byte[byteToInt(typeCountArray.get(i))];
			
			inputStream.read(data, 0, byteToInt(typeCountArray.get(i)));
			
			obj[i] = ByteToObject(data);
		}
		
		return obj;
	}
	
	
	
	public static byte[] intToByte(int i)
	{
		/*
		/*
		 * 2147483647 초과 되는 수는 안됨 int형의 최대의 양수값 임.
		 * 

	"(define (intToByte i)" +
	"	(begin" +
	"		(define t0 (java.lang.reflect.Array.newInstance (java.lang.Byte.TYPE$) 8))" +
	"		(java.lang.reflect.Array.setByte t0 0 (/ (- i (modulo i 268435456)) 268435456))" +
	"		(java.lang.reflect.Array.setByte t0 1 (/ (- (modulo i 268435456) (modulo i 16777216)) 16777216))" +
	"		(java.lang.reflect.Array.setByte t0 2 (/ (- (modulo i 16777216) (modulo i 1048576)) 1048576))" +
	"		(java.lang.reflect.Array.setByte t0 3 (/ (- (modulo i 1048576) (modulo i 65536)) 65536))" +
	"		(java.lang.reflect.Array.setByte t0 4 (/ (- (modulo i 65536) (modulo i 4096)) 4096))" +
	"		(java.lang.reflect.Array.setByte t0 5 (/ (- (modulo i 4096) (modulo i 256)) 256))" +
	"		(java.lang.reflect.Array.setByte t0 6 (/ (- (modulo i 256) (modulo i 16)) 16))" +
	"		(java.lang.reflect.Array.setByte t0 7 (modulo i 16))" +
	"		t0))" +
		 */
		byte[] bi = new byte[8];
		
		bi[0] = (byte) ((i - (i % 268435456)) / 268435456);
		bi[1] = (byte) (((i % 268435456) - (i % 16777216)) / 16777216);
		bi[2] = (byte) (((i % 16777216) - (i % 1048576)) / 1048576);
		bi[3] = (byte) (((i % 1048576) - (i % 65536)) / 65536);
		bi[4] = (byte) (((i % 65536) - (i % 4096)) / 4096);
		bi[5] = (byte) (((i % 4096) - (i % 256)) / 256);
		bi[6] = (byte) (((i % 256) - (i % 16)) / 16);
		bi[7] = (byte) (i % 16);
		
		return bi;
	}
	
	public static int byteToInt(byte[] b)
	{
		/*
		"(define (byteToInt b) " + 
		" 		(begin " + 
		" 			(define i 0)" +
		" 			(set! i (+ i (* 268435456 (java.lang.reflect.Array.getByte b 0))))) " +
		" 			(set! i (+ i (* 16777216 (java.lang.reflect.Array.getByte b 1)))) " + 
		" 			(set! i (+ i (* 1048576 (java.lang.reflect.Array.getByte b 2)))) " + 
		" 			(set! i (+ i (* 65536 (java.lang.reflect.Array.getByte b 3)))) " + 
		" 			(set! i (+ i (* 4096 (java.lang.reflect.Array.getByte b 4)))) " + 
		" 			(set! i (+ i (* 256 (java.lang.reflect.Array.getByte b 5)))) " + 
		" 			(set! i (+ i (* 16 (java.lang.reflect.Array.getByte b 6)))) " + 
		" 			(set! i (+ i (java.lang.reflect.Array.getByte b 7))) " + 
		"			i)) " + 
		 */
		int i = 0;
		
		i += (b[0] * 268435456);
		i += (b[1] * 16777216);
		i += (b[2] * 1048576);
		i += (b[3] * 65536);
		i += (b[4] * 4096);
		i += (b[5] * 256);
		i += (b[6] * 16);
		i += b[7];
		
		return i;
	}
	
	/* Scheme에서 이런 방식 사용 못함.
	public static byte[] intToByte(int i)
	{
		return new byte[] {(byte)(i >>> 24), (byte)(i >>> 16), (byte)(i >>> 8), (byte)i};
	}

	public static int byteToInt(byte[] byteArray)
	{
		return (byteArray[0] << 24) + ((byteArray[1] & 0xFF) << 16) + ((byteArray[2] & 0xFF) << 8) + (byteArray[3] & 0xFF);
	}
	*/
	
}
