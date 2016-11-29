//هذا هو الصف الرئيسي في هذه الباكيج و هو الذي نشغله مباشرة بعد تشغيل السيرفر في باكيج السيرفر افتح هذا التعليق لتفهم البرنامج بشكل مبسط :)

// نستطيع تشغيل أكثر من نسخة من هذا الصف في نفس الوقت و لكن لا نستطيع تشغيل أكثر من نسخة سيرفر في نفس الوقت إلا إذا غرينا أسماء البورتات
// تسلسل الأحداث في هذا الصف هو كالتالي:
// 1_ طلب اتصال مع السيرفر على البورت المعين و في حال الموافقة يتم استقبال المفتاح العام الخاص بالسيرفر
// 2_ يتم عرض لوحة خيارات على المستخدم تسمح له إما بإجراء عملية تحويل رصيد إلى عميل آخر أو اجراء عملية استعلام عن رصيد عميل آخر
// حسب اختيار المستخدم يتم اطلاق احد التابعين doInform أو doExchange
// ملاحظة هامة: رسائل الإظهار تتم كتابتها على الملف النصي المسمى output.txt من خلال التابع p
// الآن تستطيع الانتقال إلى عند التابعين المذكورين في السطر 6 لمشاهدة الدوكيومنتيشن الخاصة بهما
// المجلد الذي يتم فيه وضع الملفات التي تحوي المفاتيح اسمه client.keys 

package client;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.swing.JOptionPane;

import myUtil.AESMessage;
import myUtil.RSATransaction;
import myUtil.Signature;

public class Client {
	public static void p(String s) {
		try {
			FileWriter writer = new FileWriter("output.txt", true);
			writer.write(s + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		p("client code has started\n");

		String host = "localhost"; // or SERVER IP
		try (Socket s = new Socket(host, 19999);
				ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(s.getInputStream());) {

			// try body
			int domainLimit = (Integer) ois.readObject();
			RSA.generateKey();
			oos.writeObject((PublicKey) RSA.key.getPublic());
			RSA.otherSidePublicKey((PublicKey) ois.readObject());

			String[] ss = { " استعلام عن رصيد", "تحويل مبلغ مالي" };
			int op = JOptionPane.showOptionDialog(null, "ما هو الإجراء الذي ترغب بالقيام به؟", "Welcome you",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, ss, ss[0]);
		
			if (op == 0) {
				oos.writeObject(new Integer(op)); // inform server
				doInforme(oos, ois, domainLimit);
			} else if (op == 1){
				oos.writeObject(new Integer(op)); // inform server
				doExchange(oos, domainLimit);}
			else // op == -1
				System.out.println("6_6");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	static String byteToString(byte[] b){
		String result ="";
		for(byte bb : b){
			result+=Byte.toString(bb);
		}
		return result;
	}
	
	// هذا التابع خاص بالمرحلة الأولى من المشروع و التي أريد فيها اجراء عملية استعلام عن رصيد عميل ما و تشفير طلب الاستعلام بخوارزمية AES
	// الهدف من العرض الذي يتم انشاؤه من الصف AES
	// انه يمكننا استخدام التوابع التاليه doEncryption, doDecryption
	// عندما يحدد المستخدم من هو العميل الذي يريد الاستعلام عن رصيده سيكون رقم العميل المحدد هو الرسالة التي سيقوم بتشفيرها
	// الغرض الذي تم انشاؤه من الصف AESMessage 
	// هو الذي ستتم كتابته على السوكيت و سيقوم السيرفر بقراءته
	// هذا الغرض يحوي الستريم المشفرة ليس إلا
	// بعد ذلك سننتظر الرد من السيرفر و سنقوم بقراءة غرض أيضاً من الصف AESMessage
	// في هذا الغرض نستطيع استخراج الجواب المشفر عن طريق التابع getResponse
	// و نمرر خرج التابع السابق كدخل للتابع doDecryption
	// و يكون خرج التابع السابق هو الplainText
	// المطلوب
	static void doInforme(ObjectOutputStream oos, ObjectInputStream ois, int domainLimit){
		AES aes = new AES();
		String id = JOptionPane.showInputDialog(" أدخل اسم العميل الذي تريد الاستعلام عن رصيده,"+" يجب أن يكون رقمه ضمن المجال [1 ,"+ domainLimit +"]");
		byte[] cipherQuery = aes.doEncryption(id);
		p("\n هذه رسالة الاستعلام المشفرة:");
		
		p(byteToString(cipherQuery));
		p("\n");
		AESMessage aesMessage = new AESMessage();
		aesMessage.setQuery(cipherQuery);
		try {
			oos.writeObject(aesMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		AESMessage response = null;
		try {
			response = (AESMessage) ois.readObject();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String result = aes.doDecryption(response.getResponse());
		JOptionPane.showMessageDialog(null, "العميل" + id + " لديه في رصيده " + result + "$");
		
	}
	
	// هذا التابع مسؤول عن اجراء طلب تحويل رصيد و تشفير الطلب بخوارزمية RSA
	// و كذلك عمل توقيع رقمي مستخرج من رسالة الطلب..و تجري العملية بالتسلسل التالي:
	// عناصر الطلب الثلاث هي: المحول و المحول له و كمية المال الذي يتم تحويله
	// فنقوم بقراءة هذه المعلومات الثلاث و تشكيل رسالة كاملة لها القالب التالي
	// أنا العميل(رقم العميل) أرغب بتحويل (كمية من المال) إلى العميل (رقم العميل)
	// 1_ نقوم بتشفير الرسالة السابقة باستخدما المفتاح العام الخاص بالسيرفر و الذي تم ارساله عندما وافق على طلب الاتصال
	// 2_ وضع العناصر السابقة في غرض من نوع RSATransaction
	// 3_ ارسال الغرض السابق على السوكيت.. لاحظ أننا لا نرسل الرسالة و التوقيع معا في نفس الوقت
	// 4_ استخراج التوقيع الرقمي من الرسالة التي لها القالب السابق من خلال ما يلي:
	// 5_ أ_ تمريره إلى التابعsha256
	// ب_ تشفير الخرج بالمفاتح الخاص عند الطرف المرسل ( يعني عندي )
	// 6_ وضع ناتج التشفير في كائنSignature
	// 7_ ارسال الكائن إلى السيرفر عن طريق السوكيت
	
	static void doExchange(ObjectOutputStream oos, int domainLimit) {

		String clientPlainText_from = JOptionPane.showInputDialog(" تأكيد الهوية: مرر رقم تعريفك لو سمحت ");
		byte[] clientCipherText_from = RSA.doEncryption(clientPlainText_from, RSA.SERVER_PUBLIC_KEY_FILE, true);
		String clientPlainText_toUser = JOptionPane.showInputDialog(
				" [1," + domainLimit + "] حدد العميل الذي تريد تحويل المال إليه  يجب أن يكون الرقم ضمن المجال ");
		byte[] clientCipherText_toUser = RSA.doEncryption(clientPlainText_toUser, RSA.SERVER_PUBLIC_KEY_FILE, true);
		String clientPlainText_howMuch = JOptionPane.showInputDialog(" كم تريد أن تدفع؟ ");
		byte[] clientCipherText_howMuch = RSA.doEncryption(clientPlainText_howMuch, RSA.SERVER_PUBLIC_KEY_FILE, true);
		String plainMessage = "أنا العميل" + clientPlainText_from + " أقوم بتحويل مبلغ  قيمته "
				+ clientPlainText_howMuch + "$ إلى العميل " + clientPlainText_toUser;
		byte[] cipherMessage = RSA.doEncryption(plainMessage, RSA.SERVER_PUBLIC_KEY_FILE, true);
		JOptionPane.showMessageDialog(null, "هذه رسالتك:\n" + plainMessage
				+ "\nهل أنت متأكد من رغبتك بإرسالها؟(إذا ضغطت على (ok) فلن يعود بإمكانك أن تتراجع!)");

		RSATransaction msg = new RSATransaction(clientCipherText_from, clientCipherText_toUser,
				clientCipherText_howMuch, cipherMessage);
		try {
			oos.writeObject(msg);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// prepare the signiture

		String newRep = "";
		for (byte b : cipherMessage) {
			newRep += Byte.toString(b);
		}

		String stringSha = null;
		try {
			stringSha = SHAhash.sha256(newRep);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		byte[] sig = RSA.doEncryption(stringSha, RSA.CLIENT_PRIVATE_KEY_FILE, false);

		Signature gin = new Signature(sig);
		try {
			oos.writeObject(gin);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
