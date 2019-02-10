package book.view;

import java.util.ArrayList;

import book.model.dto.BookDTO;
import book.model.dto.MemberDTO;

public class RunningEndView {

	public static void projectListView(ArrayList<BookDTO> allBookList) {
		int length = allBookList.size();
		if (length != 0) {
			for (int index = 0; index < length; index++) {
				System.out.println("검색정보 " + (index + 1) + " - " + allBookList.get(index) + "\t");
			}
		}
	}

	public static Object getView(Object object) {
		return object;
	}

	public static void successMsg(String msg) {
		System.out.println(msg);
	}

	public static void showError(String errMsg) {
		System.out.println(errMsg);
	}

	public static void memberListView(ArrayList<MemberDTO> notReturn) {
		int length = notReturn.size();
		if (length != 0) {
			for (int index = 0; index < length; index++) {
				System.out.println("검색정보 " + (index + 1) + " - " + notReturn.get(index) + "\t");
			}
		}
	}
}
