package book.model.dto;

public class SupplierDTO extends People{
	String category;

	public SupplierDTO() {
		super();
	}
	
	
	public SupplierDTO(String category) {
		this.category = category;
	}

	public SupplierDTO(int no, String name, String email, String phoneNo, String category) {
		super(no, name, email, phoneNo);
		this.category = category;
	}
	public String getCategory() {
		return category;
	}


	public void setCategory(String category) {
		this.category = category;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(" 8. 도서 공급자 전문 분야 : ");
		builder.append(category);
		return builder.toString();
	}	
}
