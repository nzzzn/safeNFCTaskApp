package project.safenfctaskapp.mcoupon;

public class MCouponSchemeSrc {
	public static String Library = "(define (mkByteArray size list)" +
	"	(begin (define b (java.lang.reflect.Array.newInstance (java.lang.Byte.TYPE$) size))" +
	"		(initByteArray b 0 size list)))" +
	
	"(define (initByteArray b i size l)" +
	"	(if (< i size)" +
	"		(if (equal? l '())" +
	"				b"+				
	"				(begin (java.lang.reflect.Array.setByte b i (car l))" +
	"					   (initByteArray b (+ i 1) size (cdr l))))" +
	"		b))" +
	
	"(define (ByteToObject bytes)" +
	"	(begin" +
	"		(define bis (java.io.ByteArrayInputStream. bytes))" +
	"		(define in (java.io.ObjectInputStream. bis))" +
	"		(define obj (.readObject in))" +
	"		obj))" +
	
	"(define (encrypt byteArray privateKey)" +
	"	(begin" +
	"		(define cipher (javax.crypto.Cipher.getInstance \"RSA/ECB/NoPadding\"))" +
	"		(.init cipher (javax.crypto.Cipher.ENCRYPT_MODE$) privateKey)" +
	"		(define input (java.io.ByteArrayInputStream. byteArray))" +
	"  			(define output (java.io.ByteArrayOutputStream.))" +
	"  			(repCipher input output cipher)" +
	"		(.toByteArray output)))" +
	
	"(define (repCipher input output cipher)" +
	"	(let ((a (.available input)))" +
	"		(if (= a 0)" +
	"			()" +
	"			(begin" +
	"				(define t0 (java.lang.reflect.Array.newInstance (java.lang.Byte.TYPE$) 100))" +
	"				(.read input t0)" +
	"				(.write output (.doFinal cipher t0))" +
	"				(repCipher input output cipher)" +
	"			))))" +
	
	"(define (ObjectToByte obj)" +
	"	(begin " +
	"		(define bos (java.io.ByteArrayOutputStream.)) " +
	"		(define out (java.io.ObjectOutputStream. bos)) " +
	"		(.writeObject out obj) " +
	"		(define bytes (.toByteArray bos)) " +
	"		bytes)) " +

	
	//수정 2013-07-17 unsigned 타입이 없으므로 byte가 1000 0000 인경우 음수로 인식 그래서 4bit씩 짤라서 넣음.
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
	
	//추가  2013-07-16
	"(define (byteToInt b) " + 
	" 		(begin " + 
	" 			(define i 0)" +
	" 			(set! i (+ i (* 268435456 (java.lang.reflect.Array.getByte b 0)))) " +
	" 			(set! i (+ i (* 16777216 (java.lang.reflect.Array.getByte b 1)))) " + 
	" 			(set! i (+ i (* 1048576 (java.lang.reflect.Array.getByte b 2)))) " + 
	" 			(set! i (+ i (* 65536 (java.lang.reflect.Array.getByte b 3)))) " + 
	" 			(set! i (+ i (* 4096 (java.lang.reflect.Array.getByte b 4)))) " + 
	" 			(set! i (+ i (* 256 (java.lang.reflect.Array.getByte b 5)))) " + 
	" 			(set! i (+ i (* 16 (java.lang.reflect.Array.getByte b 6)))) " + 
	" 			(set! i (+ i (java.lang.reflect.Array.getByte b 7))) " + 
	"			i)) " + 
	
	"(define (writeObjs i n objs outputStream) " +
	"	(if (< i n) " +
	"		(begin "+
	"			   (define canonicalName (.getCanonicalName (.getClass (car objs)))) " +
	"			   (if (.equals canonicalName \"java.lang.Character\") " +
	"					(begin	(define b (java.lang.Byte. 67)) " +
	"							(.write outputStream b)) " +
	"				((if (.equals canonicalName \"java.lang.Boolean\") " +
	"					(begin	(define b (java.lang.Byte. 66)) " +
	"							(.write outputStream b)) " +
	"					((if (.equals canonicalName \"java.lang.Integer\") " +
	"							(begin	(define b (java.lang.Byte. 73)) " +
	"									(.write outputStream b)) " +
	"						((if (.equals canonicalName \"java.lang.Double\") " +
	"							(begin	(define b (java.lang.Byte. 68)) " +
	"									(.write outputStream b)) " +
	"							((if (.equals canonicalName \"java.lang.String\") " +
	"								(begin	(define b (java.lang.Byte. 83)) " +
	"										(.write outputStream b)) " +
	"								(begin	(define b (java.lang.Byte. 85)) " +
	"										(.write outputStream b)) " +
	"							))))))))) " +
	"			   (.write outputStream (intToByte (java.lang.reflect.Array.getLength (ObjectToByte (car objs))))) " +
	"			   (writeObjs (+ i 1) n (cdr objs) outputStream)) " +
	"		())) " +

	
	"(define (writeObjsBytes i n objs outputStream) " +
	"	(if (< i n) " +
	"		(begin (.write outputStream (ObjectToByte (car objs))) " +
	"			   (writeObjsBytes (+ i 1) n (cdr objs) outputStream)) " +
	"		())) " +

	
	"(define (MsgObjectToByteArray index obj)  " +
	"	(begin " +
	"		(define outputStream (java.io.ByteArrayOutputStream.)) " +
	"		(.write outputStream 1) " +
	"		(.write outputStream (intToByte index)) " +
	"		(writeObjs 0 index obj outputStream) " +
	"		(writeObjsBytes 0 index obj outputStream) " +
	"		(.toByteArray outputStream))) ";

	
	
	
	
	
	public static String issuer1 = Library + "(define PORT 1234)" +
			"(define host (java.net.InetAddress.getByName \"nzzzn.iptime.org\"))" +

			"(define link (java.net.Socket. host PORT))" +
			"(define listidi '(48 57 102 51 98 101 48 50 45 48 51 57 53 45 52 55 101 56 45 57 101 48 57 45 97 52 99 101 97 54 101 51 100 51 50 52))" +
			"(define idi (mkByteArray (length listidi) listidi))" +
			
			//추가 2013-07-16 사이즈 먼저 보내기 위해서
			"(define sendSize (intToByte (java.lang.reflect.Array.getLength idi)))" +
			"(.write (.getOutputStream link) sendSize)" +
			
			"(.write (.getOutputStream link) idi)" +
			
			"(define keyByte (mkByteArray 1220 '() ))" +
			"(define listkeyByte '(-84 -19 0 5 115 114 0 21 106 97 118 97 46 115 101 99 117 114 105 116 121 46 75 101 121 80 97 105 114 -105 3 12 58 -46 -51 18 -109 2 0 2 76 0 10 112 114 105 118 97 116 101 75 101 121 116 0 26 76 106 97 118 97 47 115 101 99 117 114 105 116 121 47 80 114 105 118 97 116 101 75 101 121 59 76 0 9 112 117 98 108 105 99 75 101 121 116 0 25 76 106 97 118 97 47 115 101 99 117 114 105 116 121 47 80 117 98 108 105 99 75 101 121 59 120 112 115 114 0 20 106 97 118 97 46 115 101 99 117 114 105 116 121 46 75 101 121 82 101 112 -67 -7 79 -77 -120 -102 -91 67 2 0 4 76 0 9 97 108 103 111 114 105 116 104 109 116 0 18 76))" +
			"(initByteArray keyByte 0 175 listkeyByte)" +
			"(define listkeyByte '(106 97 118 97 47 108 97 110 103 47 83 116 114 105 110 103 59 91 0 7 101 110 99 111 100 101 100 116 0 2 91 66 76 0 6 102 111 114 109 97 116 113 0 126 0 5 76 0 4 116 121 112 101 116 0 27 76 106 97 118 97 47 115 101 99 117 114 105 116 121 47 75 101 121 82 101 112 36 84 121 112 101 59 120 112 116 0 3 82 83 65 117 114 0 2 91 66 -84 -13 23 -8 6 8 84 -32 2 0 0 120 112 0 0 2 123 48 -126 2 119 2 1 0 48 13 6 9 42 -122 72 -122 -9 13 1 1 1 5 0 4 -126 2 97 48 -126 2 93 2 1 0 2 -127 -127 0 -94 -45 53 115 60 100 -5 -85 -75 -68 17 109 -64 99 -50 -104 35 -84 -96 2 111 -9 -25 -31))" +
			"(initByteArray keyByte 175 350 listkeyByte)" +
			"(define listkeyByte '(-119 3 21 31 76 -90 -77 5 73 -30 52 -17 96 46 11 -88 97 1 -30 -21 52 98 23 104 13 87 80 -24 -15 117 -27 82 111 -94 -125 -70 -29 -4 110 120 126 -64 18 -121 64 -85 96 103 -67 45 65 -29 -11 120 -2 64 56 -16 -67 91 -91 -9 -103 53 123 -81 75 -95 -32 26 -91 -11 68 82 67 106 -31 -27 -81 -66 -49 48 11 -64 103 -99 -85 -36 -46 86 -94 12 -119 -15 -116 -51 -122 -48 -11 -96 -74 82 -126 -109 2 3 1 0 1 2 -127 -128 75 98 -20 -95 96 -6 4 71 -39 9 -83 44 26 98 91 121 -73 50 -86 -106 -42 78 122 78 -62 -57 -23 -108 -89 65 -101 43 60 -84 -2 3 66 -115 -81 26 19 103 -13 47 32 -2 -75 -123 -104 127 -81 -41 90 76 82 -109 -101 90 44 17 112 -109 36))" +
			"(initByteArray keyByte 350 525 listkeyByte)" +
			"(define listkeyByte '(117 -24 -63 81 92 -123 -82 -2 -124 -65 -49 107 102 -25 82 9 -36 13 -114 114 -100 124 -80 -96 -7 -79 -109 99 92 -116 -100 112 99 33 -38 -75 -106 106 -52 6 -17 -1 -122 -101 38 4 -2 -68 75 98 83 -107 -6 59 125 41 -99 96 -25 106 -57 -47 -97 -73 57 2 65 0 -23 101 -102 0 -32 87 -102 -80 -36 18 52 -37 -105 -91 -115 -9 -113 37 -104 -65 39 -39 116 62 -98 85 12 81 -107 -119 -27 -49 -51 15 96 59 -15 -73 -85 98 -38 -83 81 36 -124 90 17 10 -63 -64 -51 -16 99 94 7 21 -114 -89 -83 84 -27 -12 -86 -17 2 65 0 -78 -105 -5 -87 26 32 -53 -121 -112 4 120 -40 64 -36 -43 -12 -80 4 5 -30 127 -48 5 -109 -111 3 -40 -119 68 4 -67 -98 112 -84 101 45 119 -119 -124 99))" +
			"(initByteArray keyByte 525 700 listkeyByte)" +
			"(define listkeyByte '(117 -60 81 -26 -18 -71 50 8 -102 -102 -77 -41 40 -112 100 49 -62 122 -86 -55 68 77 50 -99 2 65 0 -45 108 117 56 76 1 47 -96 -4 -118 5 -107 -46 102 103 23 21 118 2 75 79 38 -35 -11 -121 106 21 113 -90 27 -73 -79 40 -39 -65 -36 45 87 -85 10 46 123 -120 32 -93 64 15 102 6 35 -56 -87 -12 -23 -115 83 74 -39 -69 -37 71 63 -2 -61 2 65 0 -126 76 12 25 117 -84 -109 -111 85 -21 77 87 73 11 85 53 -19 -19 33 117 -4 -106 2 91 -123 35 111 41 -58 108 71 3 127 100 -25 116 98 -113 -5 -4 -84 -127 12 -40 21 55 28 95 -42 -100 35 -75 72 45 0 76 -91 124 -27 121 109 -47 124 -87 2 64 72 -48 -52 45 -58 75 -89 54 -94 88 -21 21 94 67 -57))" +
			"(initByteArray keyByte 700 875 listkeyByte)" +
			"(define listkeyByte '(92 -19 -121 -120 -82 65 41 -46 5 67 58 -49 -89 -97 48 103 95 67 -40 -41 -63 30 -24 -53 -119 -99 27 48 -51 -125 -28 -118 112 -73 71 107 -85 -29 48 31 17 -16 104 8 -79 -102 -86 -47 85 116 0 6 80 75 67 83 35 56 126 114 0 25 106 97 118 97 46 115 101 99 117 114 105 116 121 46 75 101 121 82 101 112 36 84 121 112 101 0 0 0 0 0 0 0 0 18 0 0 120 114 0 14 106 97 118 97 46 108 97 110 103 46 69 110 117 109 0 0 0 0 0 0 0 0 18 0 0 120 112 116 0 7 80 82 73 86 65 84 69 115 113 0 126 0 4 113 0 126 0 9 117 113 0 126 0 10 0 0 0 -94 48 -127 -97 48 13 6 9 42 -122 72 -122 -9 13 1 1))" +
			"(initByteArray keyByte 875 1050 listkeyByte)" +
			"(define listkeyByte '(1 5 0 3 -127 -115 0 48 -127 -119 2 -127 -127 0 -94 -45 53 115 60 100 -5 -85 -75 -68 17 109 -64 99 -50 -104 35 -84 -96 2 111 -9 -25 -31 -119 3 21 31 76 -90 -77 5 73 -30 52 -17 96 46 11 -88 97 1 -30 -21 52 98 23 104 13 87 80 -24 -15 117 -27 82 111 -94 -125 -70 -29 -4 110 120 126 -64 18 -121 64 -85 96 103 -67 45 65 -29 -11 120 -2 64 56 -16 -67 91 -91 -9 -103 53 123 -81 75 -95 -32 26 -91 -11 68 82 67 106 -31 -27 -81 -66 -49 48 11 -64 103 -99 -85 -36 -46 86 -94 12 -119 -15 -116 -51 -122 -48 -11 -96 -74 82 -126 -109 2 3 1 0 1 116 0 5 88 46 53 48 57 126 113 0 126 0 13 116 0 6 80 85 66 76 73 67))" +
			"(initByteArray keyByte 1050 1220 listkeyByte)" +
			"(define userPrivateKey (.getPrivate (ByteToObject keyByte)))" +
			
			"(define listnibyte '(91 -35 -119 -40 -70 -20 111 -18 -83 76 -61 125 15 -5 -125 109 -104 -91 -74 43)) " +
	        "(define nibyte (mkByteArray (length listnibyte) listnibyte)) " +
			
			//추가 2013-07-16 사이즈 먼저 보내기 위해서
			"(define sendSize (intToByte (java.lang.reflect.Array.getLength (encrypt nibyte userPrivateKey))))" +
			"(.write (.getOutputStream link) sendSize)" +
			
	        "(.write (.getOutputStream link) (encrypt nibyte userPrivateKey))" +
	        
			"(define listnu '(-47 65 -23 65 -10 22 -109 -53 7 109 59 108 -13 25 -43 -15 15 -120 112 -67))" +
			"(define nu (mkByteArray (length listnu) listnu)) " +
			
			//추가 2013-07-16 사이즈 먼저 보내기 위해서
			"(define sendSize (intToByte (java.lang.reflect.Array.getLength nu)))" +
			"(.write (.getOutputStream link) sendSize)" +
			
			"(.write (.getOutputStream link) nu)" +
			
			
			"(define readSize (mkByteArray 8 '()))" +
			"(.read (.getInputStream link) readSize) " +
			
			"(define mcoupon (mkByteArray (byteToInt readSize) '()))" +
			"(.read (.getInputStream link) mcoupon) " +


			"(.write (.getOutputStream link) (intToByte (byteToInt readSize)))" +
			
			"(.close link)" +
			"(define fos (.openFileOutput context \"mcoupon.text\" (android.content.Context.MODE_PRIVATE$)))" +
			"(.write fos readSize)" +
			"(.write fos mcoupon)" +
			"(.close fos)";
	
	
	
	public static String cashier1 = Library +"(define PORT 1235) " +
			"(define host (java.net.InetAddress.getByName \"nzzzn.iptime.org\")) " +
			"(define fis (.openFileInput context \"mcoupon.text\")) " +
			"(define sendSize (mkByteArray 8 '())) " +
			"(.read fis sendSize) " +
			
			"(define mcoupon (mkByteArray (byteToInt sendSize) '())) " +
			"(.read fis mcoupon) " +
			"(.close fis) " +

			"(define link (java.net.Socket. host PORT))  " +
			"(define listidc '(55 53 100 52 53 49 97 48 45 102 49 49 53 45 52 97 51 102 45 98 49 51 55 45 53 51 97 55 56 52 51 57 102 52 52 53)) " +
			"(define idc (mkByteArray (length listidc) listidc)) " +
			"(define listnc '(118 39 -19 120 42 -5 -72 111 -73 -69 107 60 -74 88 -33 -124 -10 -64 -13 -84)) " +
			"(define nc (mkByteArray (length listnc) listnc)) " +
			"(define listidu '(48 57 102 51 98 101 48 50 45 48 51 57 53 45 52 55 101 56 45 57 101 48 57 45 97 52 99 101 97 54 101 51 100 51 50 52)) " +
			"(define idu (mkByteArray (length listidu) listidu)) " +
			
			"(define sendSize (intToByte (java.lang.reflect.Array.getLength idu)))" +
			"(.write (.getOutputStream link) sendSize)" +
			
			"(.write (.getOutputStream link) idu) " +

			
			"(define listNu2 '(-4 6 46 -30 -10 21 51 57 72 32 -20 -1 84 100 81 80 76 22 72 -11)) " +
			"(define Nu2 (mkByteArray (length listNu2) listNu2)) " +
			
			"(define sendSize (intToByte (java.lang.reflect.Array.getLength Nu2)))" +
			"(.write (.getOutputStream link) sendSize)" +
			
			"(.write (.getOutputStream link) Nu2) " +
			
			"(define sendSize (intToByte (java.lang.reflect.Array.getLength mcoupon)))" +
			"(.write (.getOutputStream link) sendSize)" +
			
			"(.write (.getOutputStream link) mcoupon) " +

			"(define keyByte (mkByteArray 1220 '() ))" + 
			"(define listkeyByte '(-84 -19 0 5 115 114 0 21 106 97 118 97 46 115 101 99 117 114 105 116 121 46 75 101 121 80 97 105 114 -105 3 12 58 -46 -51 18 -109 2 0 2 76 0 10 112 114 105 118 97 116 101 75 101 121 116 0 26 76 106 97 118 97 47 115 101 99 117 114 105 116 121 47 80 114 105 118 97 116 101 75 101 121 59 76 0 9 112 117 98 108 105 99 75 101 121 116 0 25 76 106 97 118 97 47 115 101 99 117 114 105 116 121 47 80 117 98 108 105 99 75 101 121 59 120 112 115 114 0 20 106 97 118 97 46 115 101 99 117 114 105 116 121 46 75 101 121 82 101 112 -67 -7 79 -77 -120 -102 -91 67 2 0 4 76 0 9 97 108 103 111 114 105 116 104 109 116 0 18 76))" +
			"(initByteArray keyByte 0 175 listkeyByte)" +
			"(define listkeyByte '(106 97 118 97 47 108 97 110 103 47 83 116 114 105 110 103 59 91 0 7 101 110 99 111 100 101 100 116 0 2 91 66 76 0 6 102 111 114 109 97 116 113 0 126 0 5 76 0 4 116 121 112 101 116 0 27 76 106 97 118 97 47 115 101 99 117 114 105 116 121 47 75 101 121 82 101 112 36 84 121 112 101 59 120 112 116 0 3 82 83 65 117 114 0 2 91 66 -84 -13 23 -8 6 8 84 -32 2 0 0 120 112 0 0 2 123 48 -126 2 119 2 1 0 48 13 6 9 42 -122 72 -122 -9 13 1 1 1 5 0 4 -126 2 97 48 -126 2 93 2 1 0 2 -127 -127 0 -94 -45 53 115 60 100 -5 -85 -75 -68 17 109 -64 99 -50 -104 35 -84 -96 2 111 -9 -25 -31))" +
			"(initByteArray keyByte 175 350 listkeyByte)" +
			"(define listkeyByte '(-119 3 21 31 76 -90 -77 5 73 -30 52 -17 96 46 11 -88 97 1 -30 -21 52 98 23 104 13 87 80 -24 -15 117 -27 82 111 -94 -125 -70 -29 -4 110 120 126 -64 18 -121 64 -85 96 103 -67 45 65 -29 -11 120 -2 64 56 -16 -67 91 -91 -9 -103 53 123 -81 75 -95 -32 26 -91 -11 68 82 67 106 -31 -27 -81 -66 -49 48 11 -64 103 -99 -85 -36 -46 86 -94 12 -119 -15 -116 -51 -122 -48 -11 -96 -74 82 -126 -109 2 3 1 0 1 2 -127 -128 75 98 -20 -95 96 -6 4 71 -39 9 -83 44 26 98 91 121 -73 50 -86 -106 -42 78 122 78 -62 -57 -23 -108 -89 65 -101 43 60 -84 -2 3 66 -115 -81 26 19 103 -13 47 32 -2 -75 -123 -104 127 -81 -41 90 76 82 -109 -101 90 44 17 112 -109 36))" +
			"(initByteArray keyByte 350 525 listkeyByte)" +
			"(define listkeyByte '(117 -24 -63 81 92 -123 -82 -2 -124 -65 -49 107 102 -25 82 9 -36 13 -114 114 -100 124 -80 -96 -7 -79 -109 99 92 -116 -100 112 99 33 -38 -75 -106 106 -52 6 -17 -1 -122 -101 38 4 -2 -68 75 98 83 -107 -6 59 125 41 -99 96 -25 106 -57 -47 -97 -73 57 2 65 0 -23 101 -102 0 -32 87 -102 -80 -36 18 52 -37 -105 -91 -115 -9 -113 37 -104 -65 39 -39 116 62 -98 85 12 81 -107 -119 -27 -49 -51 15 96 59 -15 -73 -85 98 -38 -83 81 36 -124 90 17 10 -63 -64 -51 -16 99 94 7 21 -114 -89 -83 84 -27 -12 -86 -17 2 65 0 -78 -105 -5 -87 26 32 -53 -121 -112 4 120 -40 64 -36 -43 -12 -80 4 5 -30 127 -48 5 -109 -111 3 -40 -119 68 4 -67 -98 112 -84 101 45 119 -119 -124 99))" +
			"(initByteArray keyByte 525 700 listkeyByte)" +
			"(define listkeyByte '(117 -60 81 -26 -18 -71 50 8 -102 -102 -77 -41 40 -112 100 49 -62 122 -86 -55 68 77 50 -99 2 65 0 -45 108 117 56 76 1 47 -96 -4 -118 5 -107 -46 102 103 23 21 118 2 75 79 38 -35 -11 -121 106 21 113 -90 27 -73 -79 40 -39 -65 -36 45 87 -85 10 46 123 -120 32 -93 64 15 102 6 35 -56 -87 -12 -23 -115 83 74 -39 -69 -37 71 63 -2 -61 2 65 0 -126 76 12 25 117 -84 -109 -111 85 -21 77 87 73 11 85 53 -19 -19 33 117 -4 -106 2 91 -123 35 111 41 -58 108 71 3 127 100 -25 116 98 -113 -5 -4 -84 -127 12 -40 21 55 28 95 -42 -100 35 -75 72 45 0 76 -91 124 -27 121 109 -47 124 -87 2 64 72 -48 -52 45 -58 75 -89 54 -94 88 -21 21 94 67 -57))" +
			"(initByteArray keyByte 700 875 listkeyByte)" +
			"(define listkeyByte '(92 -19 -121 -120 -82 65 41 -46 5 67 58 -49 -89 -97 48 103 95 67 -40 -41 -63 30 -24 -53 -119 -99 27 48 -51 -125 -28 -118 112 -73 71 107 -85 -29 48 31 17 -16 104 8 -79 -102 -86 -47 85 116 0 6 80 75 67 83 35 56 126 114 0 25 106 97 118 97 46 115 101 99 117 114 105 116 121 46 75 101 121 82 101 112 36 84 121 112 101 0 0 0 0 0 0 0 0 18 0 0 120 114 0 14 106 97 118 97 46 108 97 110 103 46 69 110 117 109 0 0 0 0 0 0 0 0 18 0 0 120 112 116 0 7 80 82 73 86 65 84 69 115 113 0 126 0 4 113 0 126 0 9 117 113 0 126 0 10 0 0 0 -94 48 -127 -97 48 13 6 9 42 -122 72 -122 -9 13 1 1))" +
			"(initByteArray keyByte 875 1050 listkeyByte)" +
			"(define listkeyByte '(1 5 0 3 -127 -115 0 48 -127 -119 2 -127 -127 0 -94 -45 53 115 60 100 -5 -85 -75 -68 17 109 -64 99 -50 -104 35 -84 -96 2 111 -9 -25 -31 -119 3 21 31 76 -90 -77 5 73 -30 52 -17 96 46 11 -88 97 1 -30 -21 52 98 23 104 13 87 80 -24 -15 117 -27 82 111 -94 -125 -70 -29 -4 110 120 126 -64 18 -121 64 -85 96 103 -67 45 65 -29 -11 120 -2 64 56 -16 -67 91 -91 -9 -103 53 123 -81 75 -95 -32 26 -91 -11 68 82 67 106 -31 -27 -81 -66 -49 48 11 -64 103 -99 -85 -36 -46 86 -94 12 -119 -15 -116 -51 -122 -48 -11 -96 -74 82 -126 -109 2 3 1 0 1 116 0 5 88 46 53 48 57 126 113 0 126 0 13 116 0 6 80 85 66 76 73 67))" +
			"(initByteArray keyByte 1050 1220 listkeyByte)" +
			"(define keyPair (ByteToObject keyByte)) " +

			"(define sigu (MsgObjectToByteArray 4 '(Nu2 nc idc mcoupon))) " +
			
			"(define sendSigu (encrypt sigu (.getPrivate keyPair)))" + 
			
			"(define sendSize (intToByte (java.lang.reflect.Array.getLength sendSigu)))" +
			"(.write (.getOutputStream link) sendSize)" +
			
			"(.write (.getOutputStream link) sendSigu) " +
			
			"(define readSize (mkByteArray 8 '()))" +
			"(.read (.getInputStream link) readSize) " +
			
			"(define bonus (mkByteArray (byteToInt readSize) '())) " +
			"(.read (.getInputStream link) bonus) " +
			
			"(define sendSize (intToByte (java.lang.reflect.Array.getLength bonus)))" +
			"(.write (.getOutputStream link) sendSize)" +
			
			"(define readSize (mkByteArray 8 '()))" +
			"(.read (.getInputStream link) readSize) " +
			"(define sigc (mkByteArray (byteToInt readSize) '())) " +
			"(.read (.getInputStream link) sigc) " +
			
			"(define sendSize (intToByte (java.lang.reflect.Array.getLength sigc)))" +
			"(.write (.getOutputStream link) sendSize)" +
			
			"(.close link) " +

			"(define fos (.openFileOutput context \"bonus.text\" (android.content.Context.MODE_PRIVATE$)))" +
			"(.write fos bonus)" +
			"(.close fos)";

}
