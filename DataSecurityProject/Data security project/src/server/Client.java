package server;

import java.util.ArrayList;
import java.util.HashMap;

public class Client {
	private int id = 0;
	private byte[] account = null;

	public byte[] getAccount(){
		return account;
	}
	
	public int getID(){
		return id;
	}
	
	Client(int i) {
		id = i;
		account = RSA.doEncryption(Float.toString(1000), RSA.SERVER_PUBLIC_KEY_FILE, true);
		myTransactions = new HashMap<>();
	}

	private float seeAccount() {
		return Float.parseFloat(RSA.doDecryption(account, RSA.SERVER_PRIVATE_KEY_FILE, true));
	}

	HashMap<String, ArrayList<TransactionAtServer>> myTransactions = null;

	// الكلمة المفتاحيةsynchronized
	// من أجل تجنب الحالة التالية:
	// بفرض قام 2 من العملاء باجراء تحويل مبلغ مالي لعميل ثالث
	// و عندما قرأ الثريد الأول رصيد العميل الثالث ( لنفرض1000 )
	// حدثت مقاطعة
	// و قرأ الثريد الثاني رصيد العميل الثالث و وجده 1000
	// فأضاف إليه 500
	// ثم عاد التنفيذ إلى الثريد الأول و تباع التنفيذ على أساس أن الرصيد هو 1000 و ليس 1500
	// بالتالي سيصبح مجموع الرصيد 1500 و ليس 2000
	// فالكلمةsynchronized
	// تمنع عمليه تحديث سجلات الكائن الموجودة فيه من قبل ثريدين بنفس الوقت

	// نقوم بتخزين الغرضtas
	// في هاش ماب
	// المدخل فيها هو سلسلة محرفية عبارة عن تاريخ التحويل
	// و القيمة عباة عن سلسلة من التحويلات
	// لذلك نستطيع اجراء عملية تحقق من التحويلات التي جرت عند هذا العميل في التاريخ الفلاني
	
	public synchronized void addTransaction(TransactionAtServer tas, boolean direction) {
		if (!myTransactions.containsKey(tas.date)) {
			myTransactions.put(tas.date, new ArrayList<>());
		}
		myTransactions.get(tas.date).add(tas);
		float acc = 0;
		if (direction) {
			// هذا العميل هو المرسل و سيتم انقاص رصيده بمقدار هاو متش
			acc = seeAccount() - Float.parseFloat(tas.plainHowMuch);
		} else {
			acc = seeAccount() + Float.parseFloat(tas.plainHowMuch);
		}
		this.account = RSA.doEncryption(Float.toString(acc), RSA.SERVER_PUBLIC_KEY_FILE, true);

	}

	public String toString() {
		return "Client" + id + " has " + seeAccount() + "$, with series of transactions are declared as:\n"
				+ myTransactions + "\n";
	}

//	public static void main(String[] args) {
//		// new Client(10);
//		System.out.println(new Client(10).seeAccount());
//	}
}
