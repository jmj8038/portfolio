package book.view;

import java.util.Scanner;
import book.model.dto.BookDTO;
import bookstore.controller.BookStoreController;

public class RunningStartView {
	private static Scanner firstinput1;
	
	public static void main(String[] args) {

		System.out.println("***** 환영합니다. *****");
		while (true) {
			System.out.println("관리자모드 - 7276 입력  |  고객모드 - 1004입력  |  실행 종료 - 0 입력");
			firstinput1 = new Scanner(System.in);
			int inputID = firstinput1.nextInt();
			
			if (inputID == 7276) {
				while (true) {
					System.out.println("비밀번호를 입력해주세요.");
					int adminPW = firstinput1.nextInt();
					if (adminPW == 301) {
						while (true) {
							System.out.println("\n");
							System.out.println("1. 대출불가한 도서목록 테이블 - 1 입력");
							System.out.println("2. 대출가능한 도서목록 테이블 - 2 입력");
							System.out.println("3. 새로운 도서 정보 입력 - 3 입력");
							System.out.println("4. 기존의 도서 정보 삭제 - 4 입력");
							System.out.println("72760. 관리자모드 종료 - 72760 입력");

							int firstinput = firstinput1.nextInt();
							if (firstinput == 1 || firstinput == 2 || firstinput == 3 || firstinput == 4) {
								switch (firstinput) {
								case 1:
									BookStoreController.getAllBooksNoCanBorrow();
									break;
								case 2:
									BookStoreController.getAllBooksCanBorrow();
									break;
								case 3:
									BookStoreController.insertBook(new BookDTO(10020, "토지", "진민재", "김영선", 2018, "4층", 116, 0));
									break;
								case 4:
									int deleteBook1 = firstinput1.nextInt();
									BookStoreController.deleteBook(deleteBook1);
									BookStoreController.getAlldeleteBooks();
									break;
								}
							} else if (firstinput == 72760) {
								System.out.println("관리자의 종료합니다.");
								break;
							} else
								System.out.println("다시 입력해주세요.");
						}
					} else
						System.out.println("비밀번호가 틀렸습니다. 처음부터 다시 실행해주세요.");
					break;
				}
			} else if (inputID == 1004) {
				while (true) {
					System.out.println("5. 대출 - 5 입력");
					System.out.println("6. 반납 - 6 입력");
					System.out.println("10040. 관리자모드 종료 - 10040 입력");

					int firstinput = firstinput1.nextInt();
					if (firstinput == 5 || firstinput == 6) {
						switch (firstinput) {
						case 5:
							System.out.println("회원님의 번호와 빌리고 싶은 책 번호를 입력해주세요");
							int printSecond[] = new int[2];
							for (int i = 0; i < 2; i++) {
								printSecond[i] = firstinput1.nextInt();
							}
							BookStoreController.updateBook(printSecond[0], printSecond[1]);
							break;
						case 6:
							System.out.println("회원님의 번호를 입력해주세요.");
							firstinput1 = new Scanner(System.in);
							int printThird = firstinput1.nextInt();
							BookStoreController.returnMember(printThird);
							break;
						}
					} else if (firstinput == 10040) {
						System.out.println("고객님의 창을 종료합니다.");
						break;
					} else
						System.out.println("다시 입력해주세요.");
				}
			} else if (inputID == 0) {
				System.out.println("완전히 종료합니다.");
				break;
			} else
				System.out.println("다시 입력해주세요.");
		}
	}
}
