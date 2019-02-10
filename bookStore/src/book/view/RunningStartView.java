package book.view;

import java.util.Scanner;
import book.model.dto.BookDTO;
import bookstore.controller.BookStoreController;

public class RunningStartView {
	private static Scanner firstinput1;
	
	public static void main(String[] args) {

		System.out.println("***** ȯ���մϴ�. *****");
		while (true) {
			System.out.println("�����ڸ�� - 7276 �Է�  |  ����� - 1004�Է�  |  ���� ���� - 0 �Է�");
			firstinput1 = new Scanner(System.in);
			int inputID = firstinput1.nextInt();
			
			if (inputID == 7276) {
				while (true) {
					System.out.println("��й�ȣ�� �Է����ּ���.");
					int adminPW = firstinput1.nextInt();
					if (adminPW == 301) {
						while (true) {
							System.out.println("\n");
							System.out.println("1. ����Ұ��� ������� ���̺� - 1 �Է�");
							System.out.println("2. ���Ⱑ���� ������� ���̺� - 2 �Է�");
							System.out.println("3. ���ο� ���� ���� �Է� - 3 �Է�");
							System.out.println("4. ������ ���� ���� ���� - 4 �Է�");
							System.out.println("72760. �����ڸ�� ���� - 72760 �Է�");

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
									BookStoreController.insertBook(new BookDTO(10020, "����", "������", "�迵��", 2018, "4��", 116, 0));
									break;
								case 4:
									int deleteBook1 = firstinput1.nextInt();
									BookStoreController.deleteBook(deleteBook1);
									BookStoreController.getAlldeleteBooks();
									break;
								}
							} else if (firstinput == 72760) {
								System.out.println("�������� �����մϴ�.");
								break;
							} else
								System.out.println("�ٽ� �Է����ּ���.");
						}
					} else
						System.out.println("��й�ȣ�� Ʋ�Ƚ��ϴ�. ó������ �ٽ� �������ּ���.");
					break;
				}
			} else if (inputID == 1004) {
				while (true) {
					System.out.println("5. ���� - 5 �Է�");
					System.out.println("6. �ݳ� - 6 �Է�");
					System.out.println("10040. �����ڸ�� ���� - 10040 �Է�");

					int firstinput = firstinput1.nextInt();
					if (firstinput == 5 || firstinput == 6) {
						switch (firstinput) {
						case 5:
							System.out.println("ȸ������ ��ȣ�� ������ ���� å ��ȣ�� �Է����ּ���");
							int printSecond[] = new int[2];
							for (int i = 0; i < 2; i++) {
								printSecond[i] = firstinput1.nextInt();
							}
							BookStoreController.updateBook(printSecond[0], printSecond[1]);
							break;
						case 6:
							System.out.println("ȸ������ ��ȣ�� �Է����ּ���.");
							firstinput1 = new Scanner(System.in);
							int printThird = firstinput1.nextInt();
							BookStoreController.returnMember(printThird);
							break;
						}
					} else if (firstinput == 10040) {
						System.out.println("������ â�� �����մϴ�.");
						break;
					} else
						System.out.println("�ٽ� �Է����ּ���.");
				}
			} else if (inputID == 0) {
				System.out.println("������ �����մϴ�.");
				break;
			} else
				System.out.println("�ٽ� �Է����ּ���.");
		}
	}
}
