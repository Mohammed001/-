// هنا يتم تشغيل السيرفر للاستماع على البورت 19999 افتح التعليق من أجل شرح مبسط عن الكود :)
// تحفيز: ماذا لو حدث ما يلي:

	// بفرض قام 2 من العملاء باجراء تحويل مبلغ مالي لعميل ثالث
	// و عندما قرأ الثريد الأول رصيد العميل الثالث ( لنفرض1000 )
	// حدثت مقاطعة
	// و قرأ الثريد الثاني رصيد العميل الثالث و وجده 1000
	// فأضاف إليه 500
	// ثم عاد التنفيذ إلى الثريد الأول و تباع التنفيذ على أساس أن الرصيد هو 1000 و ليس 1500
	// بالتالي سيصبح مجموع الرصيد 1500 و ليس 2000
	// ألسنا بحاجة لمزامنة عملية الكتابة على سجلات العميل؟
	// لمعرف الجواب تابع الشروحات

// يقوم هذا البرنامج بتوليد زوج من المفاتيح الخاص و العام و وضع هذين الملفين في المجلد
// server.keys
// و ذلك قبل الاستجابة لأي طلب (قبل الدخول في الحلقة الأبدية )
// تتم كتابة رسائل الإظهار على الملف output.txt 
// هنا نستطيع الاستجابة لأكثر من طلب بنفس الوقت عن طريق ارسال الطلب إلى غرض (ثريد)من نوع  MiniServer
// انتقل إلى الصف MiniServer
// من أجل مشاهدة آلية الاستجابة لطلبات العميل

package server;

import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
	public static void p(String s) {
		try {
			FileWriter writer = new FileWriter("output.txt", true);
			writer.write(s + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// عدد العملاء في قاعدة البيانات ( يمكنك تغييره طبعاً )
	final static Integer population = 3;

	static void DB() {
		clientsRecords = new HashMap<>();
		for (int i = 1; i <= population; i++) {
			clientsRecords.put(String.valueOf(i), new Client(i));
		}
	}

	static HashMap<String, Client> clientsRecords = null;

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		p("server code started ... _");
		RSA.generateKey();
		DB();
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(19999);
		} catch (IOException e) {
			System.err.println("Could not listen on port: 19999");
		}

		while (true) {
			Socket clientSocket = null;
			try {
				clientSocket = serverSocket.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}
			MiniServer mini = new MiniServer(clientSocket, clientsRecords);
			mini.start();
		}
	}
}

