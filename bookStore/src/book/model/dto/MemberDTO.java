package book.model.dto;

public class MemberDTO extends People{
	String birth;
	int pw;
	String feature;
	int borrowNo;
	String borrowDate;
	String returnDate;
	
	public MemberDTO() {}
	
	public MemberDTO(int no, String name, String email, String phoneNo, String birth, int pw, String feature, int borrowNo, String borrowDate, String returnDate) {
		super(no, name, email, phoneNo);
		this.birth = birth;
		this.pw = pw;
		this.feature = feature;
		this.borrowNo = borrowNo;
		this.borrowDate = borrowDate;
		this.returnDate = returnDate;
	}
	
	

	public String getBirth() {
		return birth;
	}


	public void setBirth(String birth) {
		this.birth = birth;
	}


	public int getPw() {
		return pw;
	}


	public void setPw(int pw) {
		this.pw = pw;
	}


	public String getFeature() {
		return feature;
	}


	public void setFeature(String feature) {
		this.feature = feature;
	}


	public int getBorrowNo() {
		return borrowNo;
	}


	public void setBorrowNo(int borrowNo) {
		this.borrowNo = borrowNo;
	}


	public String getBorrowDate() {
		return borrowDate;
	}


	public void setBorrowDate(String borrowDate) {
		this.borrowDate = borrowDate;
	}


	public String getReturnDate() {
		return returnDate;
	}


	public void setReturnDate(String returnDate) {
		this.returnDate = returnDate;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString());
		builder.append(" 5. 도서 대출자 생년월일 : ");
		builder.append(birth);
		builder.append(" 6. 도서대출자 비밀번호 : ");
		builder.append(pw);
		builder.append(" 7. 도서대출자 특징 : ");
		builder.append(feature);
		builder.append(" 8. 대출책 번호 : ");
		builder.append(borrowNo);
		builder.append(" 9. 대출일자 : ");
		builder.append(borrowDate);
		builder.append(" 10. 반납일자 : ");
		builder.append(returnDate);
		return builder.toString();
	}	
	
	
}
