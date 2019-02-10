package book.model.dto;

public class BookDTO {
	int no;
	String title;
	String author;
	String publisher;
	int year;
	String loc;
	int category;
	int bookStatus;

	public BookDTO() {
	}

	public BookDTO(int no, String title, String author, String publisher, int year, String loc, int category,
			int status) {
		this.no = no;
		this.title = title;
		this.author = author;
		this.publisher = publisher;
		this.year = year;
		this.loc = loc;
		this.category = category;
		this.bookStatus = status;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getLoc() {
		return loc;
	}

	public void setLoc(String loc) {
		this.loc = loc;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public int getStatus() {
		return bookStatus;
	}

	public void setStatus(int bookStatus) {
		this.bookStatus = bookStatus;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("도서 정보 1. 도서 청구번호 = ");
		builder.append(no);
		builder.append("\t2. 도서 제목 : ");
		builder.append(title);
		builder.append("\t3. 작가 : ");
		builder.append(author);
		builder.append("\t4. 출판사 : ");
		builder.append(publisher);
		builder.append("\t5. 발행년도 : ");
		builder.append(year);
		builder.append("\t6. 비치된 위치 : ");
		builder.append(loc);
		builder.append("\t7. 분류 : ");
		builder.append(category);
		builder.append("\t8. 대출현황 : ");
		builder.append(bookStatus);
		return builder.toString();
	}
}
