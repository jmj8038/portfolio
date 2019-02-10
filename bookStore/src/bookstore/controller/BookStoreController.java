package bookstore.controller;

import java.sql.SQLException;
import java.util.ArrayList;

import book.model.BookDAO;
import book.model.MemberDAO;
import book.model.dto.BookDTO;
import book.view.RunningEndView;

public class BookStoreController {
	// 모든 도서 목록 검색
	public static void getAllBooks() {
		ArrayList<BookDTO> allBookList = null;
		try {
			allBookList = BookDAO.getAllBooks();
			if (allBookList.size() != 0) {
				RunningEndView.projectListView(allBookList);
			} else {
				RunningEndView.successMsg("모든 도서 검색에 실패하였습니다.");
			}
		} catch (SQLException s) {
			s.printStackTrace();
			RunningEndView.showError("모든 도서 검색시 에러 발생");
		}
	}

	// 삭제한 도서 목록 검색
	public static void getAlldeleteBooks() {
		ArrayList<BookDTO> allBookList = null;
		try {
			allBookList = BookDAO.getAlldeleteBooks();
			if (allBookList.size() != 0) {
				RunningEndView.projectListView(allBookList);
			} else {
				RunningEndView.successMsg("삭제 도서 목록 검색에 실패하였습니다.");
			}
		} catch (SQLException s) {
			s.printStackTrace();
			RunningEndView.showError("삭제 도서 목록 검색시 에러 발생");
		}
	}

	// 대출중인 책 목록 검색
	public static void getAllBooksNoCanBorrow() {
		ArrayList<BookDTO> allBookCanBorrowList = null;
		try {
			allBookCanBorrowList = BookDAO.getBookStatus(1);
			if (allBookCanBorrowList.size() != 0) {
				RunningEndView.projectListView(allBookCanBorrowList);
			} else {
				RunningEndView.successMsg("모든 도서 검색에 실패하였습니다.");
			}
		} catch (SQLException s) {
			s.printStackTrace();
			RunningEndView.showError("모든 도서 검색시 에러 발생");
		}
	}

	// 대출 가능한 책 목록 검색
	public static void getAllBooksCanBorrow() {
		ArrayList<BookDTO> allBookNoCanBorrowList = null;
		try {
			allBookNoCanBorrowList = BookDAO.getBookStatus(0);
			if (allBookNoCanBorrowList.size() != 0) {
				RunningEndView.projectListView(allBookNoCanBorrowList);
			} else {
				RunningEndView.successMsg("모든 도서 검색에 실패하였습니다.");
			}
		} catch (SQLException s) {
			s.printStackTrace();
			RunningEndView.showError("모든 도서 검색시 에러 발생");
		}
	}

	// 책 정보 입력으로 신규 도서 등록
	public static void insertBook(BookDTO bookDto) {
		boolean result;
		try {
			result = BookDAO.addBook(bookDto);
			if (result != false) {
				RunningEndView.successMsg("입력에 성공했습니다.");
			} else {
				RunningEndView.successMsg("입력에 실패하였습니다.");
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			RunningEndView.showError("입력시 오류 발생");
		}
	}

	// 청구번호로 해당 책을 db에서 삭제
	public static void deleteBook(int i) {
		boolean result;
		try {
			result = BookDAO.deleteBook(i);
			if (result != false) {
				RunningEndView.successMsg("정상적으로 삭제되었습니다.");
			} else {
				RunningEndView.successMsg("삭제에 실패하였습니다.");
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			RunningEndView.showError("삭제시 오류 발생");
		}
	}

	// 멤버번호와 책번호로 대출 - 해당 멤버의 대출책번호 변경
	public static void updateBook(int i, int s) {
		boolean result = false;
		try {
			result = MemberDAO.updateMember(i, s);
			if (result != false) {
				RunningEndView.successMsg("정상적으로 변경되었습니다.");
			} else {
				RunningEndView.successMsg("변경에 실패하였습니다.");
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			RunningEndView.showError("변경시 오류 발생");
		}
	}

	// 멤버 번호로 해당 멤버의 반납 - 해당 멤버의 대출책 번호 99999로 변경
	public static void returnMember(int i) {
		boolean result = false;
		try {
			result = MemberDAO.returnMember(i);
			if (result != false) {
				RunningEndView.successMsg("정상적으로 변경되었습니다.");
			} else {
				RunningEndView.successMsg("변경에 실패하였습니다.");
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			RunningEndView.showError("변경시 오류 발생");
		}
	}
}
