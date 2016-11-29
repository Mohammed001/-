// افتح هذا التعليق
// لمشاهدة الشروحات انتقل إلى التابع run

package server;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.HashMap;

import myUtil.AESMessage;
import myUtil.RSATransaction;
import myUtil.Signature;

public class MiniServer extends Thread {

	public void p(String s) {
		try {
			FileWriter writer = new FileWriter("output.txt", true);
			writer.write(s + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Socket socket = null;
	private HashMap<String, Client> clientsRecords = null;

	public MiniServer(Socket socket, HashMap<String, Client> hm) {
		super("MiniServer");
		this.socket = socket;
		clientsRecords = hm;
	}

	void printDB() {
		p((clientsRecords).toString());
	}
	
// هنا نقرأ عن طريق السوكيت ما هي رغبة المستخدم	
// و حسب الناتج نقوم بقدح الميثود المناسبة
// إما التابعdoExchange
// أو التابعdoInforme
// انتقل إلى هذين التابعين لقراءة الشروحات
	
	public void run() {

		try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());) {
			// try body

			oos.writeObject(Server.population);
			RSA.otherSidePublicKey((PublicKey) ois.readObject());
			oos.writeObject(RSA.key.getPublic());

			int clientChoice = (Integer) ois.readObject();
			if (clientChoice == 0) { // aes
				doInforme(oos, ois);
			} else { // 1
				doExchange(ois);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

	// هذا التابع مرتبط بالتابع doExchange
	// يتم استدعاؤه عندما نتأكد من أن طلب التحويل متوافق مع التوقيع
	// عملية التأكد تتم من خلال التابعNon_repudation
	// هنا سنجمّع المعلومات (التوقيع و الرسالة بدون فك تشفيرها ) في كائن من نوعTransactionAtServer
	// عند بناء الغرض السابق يتم فك تشفير الرسائل من أجل استحصال المعلومات المقروءة للاستفادة منها في عمليات تحديث قاعدة البيانات
	// نستطيع تحديد العميلين الذين سيتم اجراء التحويل المالي بينهما من خلال الحقلين from,to
	// و نضيف حزمة طلب التحويل التي سميناها data
	// إلى كل من العميلين من خلال التابعaddTransaction
	// انتقل إلى ذلك التايع بالضغط على ctrl+right click
	
	void updateRecords(RSATransaction t, Signature s) {
		TransactionAtServer data = new TransactionAtServer(t, s);
		Client user1 = clientsRecords.get(data.plainFrom);
		Client user2 = clientsRecords.get(data.plainTo);
		user1.addTransaction(data, true);
		user2.addTransaction(data, false);
	}

	//  هذا التابع المنطقي يرد قيمة ايجابية إذا تطابق الهاش المستخرج من فك تشفير التوقيع مع الهاش المستنتج من تهشير الرسالة المشفرة الواصلة أولاً تحت اسمcompleteCipherMessage
	// فك تشفير التوقيع يتم باستخدام المفتاح العام للعميل
	//  
	private boolean Non_repudation(RSATransaction userINFOs, Signature userSIG) {
		String hash1 = RSA.doDecryption(userSIG.sig, RSA.CLIENT_PUBLIC_KEY_FILE, false);

		String newRep = "";
		for (byte b : userINFOs.completeCipherMessage) {
			newRep += Byte.toString(b);
		}
		String hash2 = "";
		try {
			hash2 = SHAhash.sha256(newRep);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		p("hash1: " + hash1 + "\nhash2: " + hash2 + "\n");

		if (hash1.equals(hash2))
			return true;
		return false;
	}

	// بكل بساطة: هنا نتوقع استقبال كائنين اثنين
	// الأول عبارة عن حرمة فيها الرسالة المشفرة
	// و الثاني عبارة عن حزمة فيها التوقيع الخاص بالرسالة المشفرة
	// إذا تم التحقق من أن التوقيع خاص بالرسالة نسمح بإجراء عملية التحويل
	// عملية التحقق من خلال التابع المنطقيNon_repudation
	// عملية التحويل من خلال التابعupdateRecords 
	
	void doExchange(ObjectInputStream ois) {

		RSATransaction userINFOs = null;
		try {
			userINFOs = (RSATransaction) ois.readObject();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}

		Signature userSIG = null;
		try {
			userSIG = (Signature) ois.readObject();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}

		if (Non_repudation(userINFOs, userSIG)) {
			updateRecords(userINFOs, userSIG);
			p("قاعدة البيانات الآن تبدو هكذا:\n");
			printDB();
		}

	}

	static String byteToString(byte[] b){
		String result ="";
		for(byte bb : b){
			result+=Byte.toString(bb);
		}
		return result;
	}
	
	
	// هذا التابع يعتبر نسخة تشبه الموجود عند الصف Client في الباكيج الخاصة بصفوف العميل
	// نقوم باستقبال كائن من نوعAESMessage 
	// نستخرج الطلب المشفر ( و هو رقم تعريف العميل الذي يرغب المستخدم بمعرفة حسابه ) من خلال التابعgetQuery
	// بهدف فك تشفير الطلب السابق سنقوم بإنشاء غرض من الصفAES
	//لمعرفة البلاين تكست doDecryption تطبيق التابع
	// وبما أن رصيد العميل مخزن بشكل مشفر (باستخدام المفتاح العام عند السيرفر) 
	// نقوم بفك تشفير الرصيد باستخدام التابع السكوني doDecryption
	// الموجود في الصف RSA
	// نشفر الجواب بواسطة العرض المنشأ من الaes
	// doEncryption
	// نضع النائج في المتحول الموجود بنفس الغرض الذي تم استقباله
	// من خلال التابع setResponse
	void doInforme(ObjectOutputStream oos, ObjectInputStream ois) {
		AESMessage aesMessage = null;
		try {
			aesMessage = (AESMessage) ois.readObject();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		byte[] goal = aesMessage.getQuery();
		AES aes = new AES();
		String id = aes.doDecryption(goal);
		String resp = RSA.doDecryption(clientsRecords.get(id).getAccount(), RSA.SERVER_PRIVATE_KEY_FILE, true);
		byte[] cipherResponse = aes.doEncryption(resp);
		p("\nهذا جواب رسالة الاستعلام المشفرة:");
		p(byteToString(cipherResponse));
		p("\n");
		aesMessage.setResponse(cipherResponse);
		try {
			oos.writeObject(aesMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
