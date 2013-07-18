package project.safenfctaskapp.mcoupon;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyPair;


import android.content.Context;
import android.util.Log;

public class testCashier extends Thread {
	private Context context;
	public String bonus;
	
	public testCashier(Context context)
	{
		this.context = context;
	}
	
	@Override
	public void run()
	{
		/*
			(define (mkByteArray size list)
				(let (( (define b (java.lang.reflect.Array.newInstance. (java.lang.Byte.TYPE$) size)) ))
					(initByteArray b 0 size list)))
					
			(define (initByteArray b i size l)
				(if (< i size)
					(if (equal? l #null)
							(begin (.setbyte b i (car l))
								   (initByteArray b (+ i 1) size (cdr l)))
							(begin (.setbyte b i 0)
								   (initByteArray b (+ i 1) size l)))
					(b)))
							   
			
			
			(define (ByteToObject bytes) 
				(begin
					(define bis (java.io.ByteArrayInputStream.ByteArrayInputStream. bytes))
					(define in (java.io.ObjectInputStream.ObjectInputStream. bis))
					(define obj (.readObject in))
					obj))
			
			
        	(define (encrypt byteArray privateKey) 
				(begin
					(define cipher (javax.crypto.Cipher.getInstance "RSA/ECB/PKCS1Padding"))
					(.init cipher (Cipher.ENCRYPT_MODE$) privateKey)
					(define input (java.io.ByteArrayInputStream.ByteArrayInputStream. byteArray))
        			(define output (java.io.ByteArrayOutputStream.ByteArrayOutputStream.))
        			(repCipher input output cipher)
					(.toByteArray output)))
					
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
						
			(define (ObjectToByte obj) 
				(begin
					(define bos (java.io.ByteArrayOutputStream.ByteArrayOutputStream.))
					(define out (java.io.ObjectOutputStream.ObjectOutputStream. bos))
					(.writeObject out obj)
					(define bytes (.toByteArray bos))
					bytes))
					
	    	(define (intToByte i) 
				(begin
					(define t0 (java.lang.reflect.Array.newInstance. (java.lang.Byte.TYPE$) 4))
					(.setbyte t0 0 (floor (/ (modulo i 4294967295) 16777216)))
					(.setbyte t0 1 (floor (/ (modulo i 16777215) 65536)))
					(.setbyte t0 2 (floor (/ (modulo i 65535) 256)))
					(.setbyte t0 3 (floor (modulo i 255)))
					(.toByteArray outputStream)))
					
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
					
			(define (writeObjsBytes i n objs outputStream)
				(if (< i n)
					(begin (.write outputStream (.ObjectToByte (.length$ (java.lang.reflect.Array.get. objs i))))
						   (writeObjsBytes (+ i 1) n objs outputStream))
					()))
			
        	(define (MsgObjectToByteArray index obj) 
				(begin
					(define outputStream (java.io.ByteArrayOutputStream.ByteArrayOutputStream.))
					(.write outputStream 1)
					(.write outputStream intToByte index)
					(writeObjs 0 index obj outputStream)
					(writeObjsBytes 0 index obj outputStream)
					(.toByteArray outputStream)))

		 */
		
		InetAddress host = null;
		
		int PORT = 1235;
		/*
		   (define PORT 1235)
		 */
		try {
	        
			host = InetAddress.getByName("115.86.90.4");
	        /*
				(define host (java.net.InetAddress.getByName "115.86.90.4"))
	         */
	        
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		byte[] mcoupon = new byte[2048];
		/*
			(define mcoupon (mkByteArray 2048 '()))
		 */
		
		Socket link = null;
		
		//mcoupon 읽기.. data/data/패키지명/파일명
		try {
			FileInputStream fis = context.openFileInput("mcoupon.text");
			/*
				(define fis (.openFileInput context \"mcoupon.text\"))
			 */
			fis.read(mcoupon);
			/*
				(.read fis mcoupon)
			 */
			fis.close();
			/*
				(.close fis)
			 */
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try
		{
			link = new Socket(host,PORT);
			/*
		 		(define link (java.net.Socket. host PORT))  
			 */

			byte[] idc = {55, 53, 100, 52, 53, 49, 97, 48, 45, 102, 49, 49, 53, 45, 52, 97, 51, 102, 45, 98, 49, 51, 55, 45, 53, 51, 97, 55, 56, 52, 51, 57, 102, 52, 52, 53};
			/*
			   (define listidc '(55 53 100 52 53 49 97 48 45 102 49 49 53 45 52 97 51 102 45 98 49 51 55 45 53 51 97 55 56 52 51 57 102 52 52 53))
			   (define idc (mkByteArray (length listidc) listidc))
			 */
			
			byte[] nc = {118, 39, -19, 120, 42, -5, -72, 111, -73, -69, 107, 60, -74, 88, -33, -124, -10, -64, -13, -84};
			/*
			   (define listnc '(118 39 -19 120 42 -5 -72 111 -73 -69 107 60 -74 88 -33 -124 -10 -64 -13 -84))
			   (define nc (mkByteArray (length listnc) listnc))
			 */
			
			byte[] idu = {48, 57, 102, 51, 98, 101, 48, 50, 45, 48, 51, 57, 53, 45, 52, 55, 101, 56, 45, 57, 101, 48, 57, 45, 97, 52, 99, 101, 97, 54, 101, 51, 100, 51, 50, 52};
			link.getOutputStream().write(idu);
			/*
			   (define listidu '(48 57 102 51 98 101 48 50 45 48 51 57 53 45 52 55 101 56 45 57 101 48 57 45 97 52 99 101 97 54 101 51 100 51 50 52))
			   (define idu (mkByteArray (length listidu) listidu))
			   (.write (.getOutputStream link) idu)
			 */
			
			byte[] Nu2 = {-4, 6, 46, -30, -10, 21, 51, 57, 72, 32, -20, -1, 84, 100, 81, 80, 76, 22, 72, -11};
			link.getOutputStream().write(Nu2);
			/*
			   (define listNu2 '(-4 6 46 -30 -10 21 51 57 72 32 -20 -1 84 100 81 80 76 22 72 -11))
			   (define Nu2 (mkByteArray (length listNu2) listNu2))
			   (.write (.getOutputStream link) Nu2)
			 */
			
			link.getOutputStream().write(mcoupon);
			/*
			   (.write (.getOutputStream link) mcoupon)
			 */
			
			byte[] keyByte = {-84, -19, 0, 5, 115, 114, 0, 21, 106, 97, 118, 97, 46, 115, 101, 99, 117, 114, 105, 116, 121, 46, 75, 101, 121, 80, 97, 105, 114, -105, 3, 12, 58, -46, -51, 18, -109, 2, 0, 2, 76, 0, 10, 112, 114, 105, 118, 97, 116, 101, 75, 101, 121, 116, 0, 26, 76, 106, 97, 118, 97, 47, 115, 101, 99, 117, 114, 105, 116, 121, 47, 80, 114, 105, 118, 97, 116, 101, 75, 101, 121, 59, 76, 0, 9, 112, 117, 98, 108, 105, 99, 75, 101, 121, 116, 0, 25, 76, 106, 97, 118, 97, 47, 115, 101, 99, 117, 114, 105, 116, 121, 47, 80, 117, 98, 108, 105, 99, 75, 101, 121, 59, 120, 112, 115, 114, 0, 20, 106, 97, 118, 97, 46, 115, 101, 99, 117, 114, 105, 116, 121, 46, 75, 101, 121, 82, 101, 112, -67, -7, 79, -77, -120, -102, -91, 67, 2, 0, 4, 76, 0, 9, 97, 108, 103, 111, 114, 105, 116, 104, 109, 116, 0, 18, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 91, 0, 7, 101, 110, 99, 111, 100, 101, 100, 116, 0, 2, 91, 66, 76, 0, 6, 102, 111, 114, 109, 97, 116, 113, 0, 126, 0, 5, 76, 0, 4, 116, 121, 112, 101, 116, 0, 27, 76, 106, 97, 118, 97, 47, 115, 101, 99, 117, 114, 105, 116, 121, 47, 75, 101, 121, 82, 101, 112, 36, 84, 121, 112, 101, 59, 120, 112, 116, 0, 3, 82, 83, 65, 117, 114, 0, 2, 91, 66, -84, -13, 23, -8, 6, 8, 84, -32, 2, 0, 0, 120, 112, 0, 0, 2, 123, 48, -126, 2, 119, 2, 1, 0, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 4, -126, 2, 97, 48, -126, 2, 93, 2, 1, 0, 2, -127, -127, 0, -94, -45, 53, 115, 60, 100, -5, -85, -75, -68, 17, 109, -64, 99, -50, -104, 35, -84, -96, 2, 111, -9, -25, -31, -119, 3, 21, 31, 76, -90, -77, 5, 73, -30, 52, -17, 96, 46, 11, -88, 97, 1, -30, -21, 52, 98, 23, 104, 13, 87, 80, -24, -15, 117, -27, 82, 111, -94, -125, -70, -29, -4, 110, 120, 126, -64, 18, -121, 64, -85, 96, 103, -67, 45, 65, -29, -11, 120, -2, 64, 56, -16, -67, 91, -91, -9, -103, 53, 123, -81, 75, -95, -32, 26, -91, -11, 68, 82, 67, 106, -31, -27, -81, -66, -49, 48, 11, -64, 103, -99, -85, -36, -46, 86, -94, 12, -119, -15, -116, -51, -122, -48, -11, -96, -74, 82, -126, -109, 2, 3, 1, 0, 1, 2, -127, -128, 75, 98, -20, -95, 96, -6, 4, 71, -39, 9, -83, 44, 26, 98, 91, 121, -73, 50, -86, -106, -42, 78, 122, 78, -62, -57, -23, -108, -89, 65, -101, 43, 60, -84, -2, 3, 66, -115, -81, 26, 19, 103, -13, 47, 32, -2, -75, -123, -104, 127, -81, -41, 90, 76, 82, -109, -101, 90, 44, 17, 112, -109, 36, 117, -24, -63, 81, 92, -123, -82, -2, -124, -65, -49, 107, 102, -25, 82, 9, -36, 13, -114, 114, -100, 124, -80, -96, -7, -79, -109, 99, 92, -116, -100, 112, 99, 33, -38, -75, -106, 106, -52, 6, -17, -1, -122, -101, 38, 4, -2, -68, 75, 98, 83, -107, -6, 59, 125, 41, -99, 96, -25, 106, -57, -47, -97, -73, 57, 2, 65, 0, -23, 101, -102, 0, -32, 87, -102, -80, -36, 18, 52, -37, -105, -91, -115, -9, -113, 37, -104, -65, 39, -39, 116, 62, -98, 85, 12, 81, -107, -119, -27, -49, -51, 15, 96, 59, -15, -73, -85, 98, -38, -83, 81, 36, -124, 90, 17, 10, -63, -64, -51, -16, 99, 94, 7, 21, -114, -89, -83, 84, -27, -12, -86, -17, 2, 65, 0, -78, -105, -5, -87, 26, 32, -53, -121, -112, 4, 120, -40, 64, -36, -43, -12, -80, 4, 5, -30, 127, -48, 5, -109, -111, 3, -40, -119, 68, 4, -67, -98, 112, -84, 101, 45, 119, -119, -124, 99, 117, -60, 81, -26, -18, -71, 50, 8, -102, -102, -77, -41, 40, -112, 100, 49, -62, 122, -86, -55, 68, 77, 50, -99, 2, 65, 0, -45, 108, 117, 56, 76, 1, 47, -96, -4, -118, 5, -107, -46, 102, 103, 23, 21, 118, 2, 75, 79, 38, -35, -11, -121, 106, 21, 113, -90, 27, -73, -79, 40, -39, -65, -36, 45, 87, -85, 10, 46, 123, -120, 32, -93, 64, 15, 102, 6, 35, -56, -87, -12, -23, -115, 83, 74, -39, -69, -37, 71, 63, -2, -61, 2, 65, 0, -126, 76, 12, 25, 117, -84, -109, -111, 85, -21, 77, 87, 73, 11, 85, 53, -19, -19, 33, 117, -4, -106, 2, 91, -123, 35, 111, 41, -58, 108, 71, 3, 127, 100, -25, 116, 98, -113, -5, -4, -84, -127, 12, -40, 21, 55, 28, 95, -42, -100, 35, -75, 72, 45, 0, 76, -91, 124, -27, 121, 109, -47, 124, -87, 2, 64, 72, -48, -52, 45, -58, 75, -89, 54, -94, 88, -21, 21, 94, 67, -57, 92, -19, -121, -120, -82, 65, 41, -46, 5, 67, 58, -49, -89, -97, 48, 103, 95, 67, -40, -41, -63, 30, -24, -53, -119, -99, 27, 48, -51, -125, -28, -118, 112, -73, 71, 107, -85, -29, 48, 31, 17, -16, 104, 8, -79, -102, -86, -47, 85, 116, 0, 6, 80, 75, 67, 83, 35, 56, 126, 114, 0, 25, 106, 97, 118, 97, 46, 115, 101, 99, 117, 114, 105, 116, 121, 46, 75, 101, 121, 82, 101, 112, 36, 84, 121, 112, 101, 0, 0, 0, 0, 0, 0, 0, 0, 18, 0, 0, 120, 114, 0, 14, 106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 69, 110, 117, 109, 0, 0, 0, 0, 0, 0, 0, 0, 18, 0, 0, 120, 112, 116, 0, 7, 80, 82, 73, 86, 65, 84, 69, 115, 113, 0, 126, 0, 4, 113, 0, 126, 0, 9, 117, 113, 0, 126, 0, 10, 0, 0, 0, -94, 48, -127, -97, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3, -127, -115, 0, 48, -127, -119, 2, -127, -127, 0, -94, -45, 53, 115, 60, 100, -5, -85, -75, -68, 17, 109, -64, 99, -50, -104, 35, -84, -96, 2, 111, -9, -25, -31, -119, 3, 21, 31, 76, -90, -77, 5, 73, -30, 52, -17, 96, 46, 11, -88, 97, 1, -30, -21, 52, 98, 23, 104, 13, 87, 80, -24, -15, 117, -27, 82, 111, -94, -125, -70, -29, -4, 110, 120, 126, -64, 18, -121, 64, -85, 96, 103, -67, 45, 65, -29, -11, 120, -2, 64, 56, -16, -67, 91, -91, -9, -103, 53, 123, -81, 75, -95, -32, 26, -91, -11, 68, 82, 67, 106, -31, -27, -81, -66, -49, 48, 11, -64, 103, -99, -85, -36, -46, 86, -94, 12, -119, -15, -116, -51, -122, -48, -11, -96, -74, 82, -126, -109, 2, 3, 1, 0, 1, 116, 0, 5, 88, 46, 53, 48, 57, 126, 113, 0, 126, 0, 13, 116, 0, 6, 80, 85, 66, 76, 73, 67};
			KeyPair keyPair = (KeyPair)MCouponUtil.ByteToObject(keyByte);
			/*
			 * ByteToObject 필요함.. ok - 최상단에 넣어놓음
			 * 
			   (define listkeyByte '(-84 -19 0 5 115 114 0 21 106 97 118 97 46 115 101 99 117 114 105 116 121 46 75 101 121 80 97 105 114 -105 3 12 58 -46 -51 18 -109 2 0 2 76 0 10 112 114 105 118 97 116 101 75 101 121 116 0 26 76 106 97 118 97 47 115 101 99 117 114 105 116 121 47 80 114 105 118 97 116 101 75 101 121 59 76 0 9 112 117 98 108 105 99 75 101 121 116 0 25 76 106 97 118 97 47 115 101 99 117 114 105 116 121 47 80 117 98 108 105 99 75 101 121 59 120 112 115 114 0 20 106 97 118 97 46 115 101 99 117 114 105 116 121 46 75 101 121 82 101 112 -67 -7 79 -77 -120 -102 -91 67 2 0 4 76 0 9 97 108 103 111 114 105 116 104 109 116 0 18 76 106 97 118 97 47 108 97 110 103 47 83 116 114 105 110 103 59 91 0 7 101 110 99 111 100 101 100 116 0 2 91 66 76 0 6 102 111 114 109 97 116 113 0 126 0 5 76 0 4 116 121 112 101 116 0 27 76 106 97 118 97 47 115 101 99 117 114 105 116 121 47 75 101 121 82 101 112 36 84 121 112 101 59 120 112 116 0 3 82 83 65 117 114 0 2 91 66 -84 -13 23 -8 6 8 84 -32 2 0 0 120 112 0 0 2 123 48 -126 2 119 2 1 0 48 13 6 9 42 -122 72 -122 -9 13 1 1 1 5 0 4 -126 2 97 48 -126 2 93 2 1 0 2 -127 -127 0 -94 -45 53 115 60 100 -5 -85 -75 -68 17 109 -64 99 -50 -104 35 -84 -96 2 111 -9 -25 -31 -119 3 21 31 76 -90 -77 5 73 -30 52 -17 96 46 11 -88 97 1 -30 -21 52 98 23 104 13 87 80 -24 -15 117 -27 82 111 -94 -125 -70 -29 -4 110 120 126 -64 18 -121 64 -85 96 103 -67 45 65 -29 -11 120 -2 64 56 -16 -67 91 -91 -9 -103 53 123 -81 75 -95 -32 26 -91 -11 68 82 67 106 -31 -27 -81 -66 -49 48 11 -64 103 -99 -85 -36 -46 86 -94 12 -119 -15 -116 -51 -122 -48 -11 -96 -74 82 -126 -109 2 3 1 0 1 2 -127 -128 75 98 -20 -95 96 -6 4 71 -39 9 -83 44 26 98 91 121 -73 50 -86 -106 -42 78 122 78 -62 -57 -23 -108 -89 65 -101 43 60 -84 -2 3 66 -115 -81 26 19 103 -13 47 32 -2 -75 -123 -104 127 -81 -41 90 76 82 -109 -101 90 44 17 112 -109 36 117 -24 -63 81 92 -123 -82 -2 -124 -65 -49 107 102 -25 82 9 -36 13 -114 114 -100 124 -80 -96 -7 -79 -109 99 92 -116 -100 112 99 33 -38 -75 -106 106 -52 6 -17 -1 -122 -101 38 4 -2 -68 75 98 83 -107 -6 59 125 41 -99 96 -25 106 -57 -47 -97 -73 57 2 65 0 -23 101 -102 0 -32 87 -102 -80 -36 18 52 -37 -105 -91 -115 -9 -113 37 -104 -65 39 -39 116 62 -98 85 12 81 -107 -119 -27 -49 -51 15 96 59 -15 -73 -85 98 -38 -83 81 36 -124 90 17 10 -63 -64 -51 -16 99 94 7 21 -114 -89 -83 84 -27 -12 -86 -17 2 65 0 -78 -105 -5 -87 26 32 -53 -121 -112 4 120 -40 64 -36 -43 -12 -80 4 5 -30 127 -48 5 -109 -111 3 -40 -119 68 4 -67 -98 112 -84 101 45 119 -119 -124 99 117 -60 81 -26 -18 -71 50 8 -102 -102 -77 -41 40 -112 100 49 -62 122 -86 -55 68 77 50 -99 2 65 0 -45 108 117 56 76 1 47 -96 -4 -118 5 -107 -46 102 103 23 21 118 2 75 79 38 -35 -11 -121 106 21 113 -90 27 -73 -79 40 -39 -65 -36 45 87 -85 10 46 123 -120 32 -93 64 15 102 6 35 -56 -87 -12 -23 -115 83 74 -39 -69 -37 71 63 -2 -61 2 65 0 -126 76 12 25 117 -84 -109 -111 85 -21 77 87 73 11 85 53 -19 -19 33 117 -4 -106 2 91 -123 35 111 41 -58 108 71 3 127 100 -25 116 98 -113 -5 -4 -84 -127 12 -40 21 55 28 95 -42 -100 35 -75 72 45 0 76 -91 124 -27 121 109 -47 124 -87 2 64 72 -48 -52 45 -58 75 -89 54 -94 88 -21 21 94 67 -57 92 -19 -121 -120 -82 65 41 -46 5 67 58 -49 -89 -97 48 103 95 67 -40 -41 -63 30 -24 -53 -119 -99 27 48 -51 -125 -28 -118 112 -73 71 107 -85 -29 48 31 17 -16 104 8 -79 -102 -86 -47 85 116 0 6 80 75 67 83 35 56 126 114 0 25 106 97 118 97 46 115 101 99 117 114 105 116 121 46 75 101 121 82 101 112 36 84 121 112 101 0 0 0 0 0 0 0 0 18 0 0 120 114 0 14 106 97 118 97 46 108 97 110 103 46 69 110 117 109 0 0 0 0 0 0 0 0 18 0 0 120 112 116 0 7 80 82 73 86 65 84 69 115 113 0 126 0 4 113 0 126 0 9 117 113 0 126 0 10 0 0 0 -94 48 -127 -97 48 13 6 9 42 -122 72 -122 -9 13 1 1 1 5 0 3 -127 -115 0 48 -127 -119 2 -127 -127 0 -94 -45 53 115 60 100 -5 -85 -75 -68 17 109 -64 99 -50 -104 35 -84 -96 2 111 -9 -25 -31 -119 3 21 31 76 -90 -77 5 73 -30 52 -17 96 46 11 -88 97 1 -30 -21 52 98 23 104 13 87 80 -24 -15 117 -27 82 111 -94 -125 -70 -29 -4 110 120 126 -64 18 -121 64 -85 96 103 -67 45 65 -29 -11 120 -2 64 56 -16 -67 91 -91 -9 -103 53 123 -81 75 -95 -32 26 -91 -11 68 82 67 106 -31 -27 -81 -66 -49 48 11 -64 103 -99 -85 -36 -46 86 -94 12 -119 -15 -116 -51 -122 -48 -11 -96 -74 82 -126 -109 2 3 1 0 1 116 0 5 88 46 53 48 57 126 113 0 126 0 13 116 0 6 80 85 66 76 73 67))
			   (define keyByte (mkByteArray (length listkeyByte) listkeyByte))
			   (define keyPair (.ByteToObject keyByte))
			 */
			
			byte[] sigu = MCouponUtil.MsgObjectToByteArray(4, Nu2, nc, idc, mcoupon);
			/*
			 * MsgObjectToByteArray 필요함..
			 * 
				(define sigu (.MsgObjectToByteArray 4, Nu2, nc, idc, mcoupon))
			 */
			
			link.getOutputStream().write(MCouponUtil.encrypt(sigu, keyPair.getPrivate()));
			/*
			 * encrypt 필요함.. ok - 상단에 넣어놓음
			 * 
				(.write (.getOutputStream link) (.encrypt sigu (.getPrivate keyPair)))
			 */
			
			byte[] bonus = new byte[100];
			/*
				(define bonus (mkByteArray 100 '()))
			 */
			
			int index = 0;
			int i;
			InputStream is = link.getInputStream();
			
			while((i = is.read()) != -1)
			{
				bonus[index] = (byte) i;
				index++;
			}
			
			Log.i("bonus size : ", index + " ");
			/*
				(.read (.getInputStream link) bonus)
			 */
			
			this.bonus = new String(bonus, 0, index);
			/*
				???????? UI 처리를 어떻게 하지?
			 */
			
			byte[] sigc = new byte[500];
			/*
				(define sigc (mkByteArray 500 '()))
			 */
			link.getInputStream().read(sigc);
			/*
				(.read (.getInputStream link) sigc)
			 */

			link.close();
			/*
				(.close link)
			 */
		}
		catch(IOException ioEx)
		{
			ioEx.printStackTrace();
		}
		
	}
}
