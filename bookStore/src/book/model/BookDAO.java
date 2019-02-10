package book.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import book.model.dto.BookDTO;
import book.model.util.DBUtil;

public class BookDAO {
	// 신규 도서 등록
	public static boolean addBook(BookDTO book) throws SQLException {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DBUtil.getConnection();
			pstmt = con.prepareStatement("insert into book values(?, ?, ?, ?, ?, ?, ?, ?)");
			pstmt.setInt(1, book.getNo());
			pstmt.setString(2, book.getTitle());
			pstmt.setString(3, book.getAuthor());
			pstmt.setString(4, book.getPublisher());
			pstmt.setInt(5, book.getYear());
			pstmt.setString(6, book.getLoc());
			pstmt.setInt(7, book.getCategory());
			pstmt.setInt(8, book.getStatus());
			int result = pstmt.executeUpdate();

			if (result == 1) {
				return true;
			}
		} finally {
			DBUtil.close(con, pstmt);
		}
		return false;
	}

	// book no로 위치 수정
	public static boolean updateBook(int bookNo, String loc) throws SQLException {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DBUtil.getConnection();

			pstmt = con.prepareStatement("update book set 위치=? where 청구번호=?");
			pstmt.setString(1, loc);
			pstmt.setInt(2, bookNo);

			int result = pstmt.executeUpdate();
			if (result == 1) {
				return true;
			}
		} finally {
			DBUtil.close(con, pstmt);
		}
		return false;
	}

	// book_no 로 db에서 삭제하기
	public static boolean deleteBook(int bookNo) throws SQLException {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DBUtil.getConnection();
			pstmt = con.prepareStatement("delete from book where 청구번호=?");
			pstmt.setInt(1, bookNo);
			int result = pstmt.executeUpdate();
			if (result == 1) {
				return true;
			}
		} catch (SQLException s) {
			s.printStackTrace();
			throw s;
		} finally {
			DBUtil.close(con, pstmt);
		}
		return false;
	}

	// book_no로 특정 도서의 모든 정보 검색
	public static BookDTO getBook(int bookNo) throws SQLException {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		BookDTO oneBook = null;

		try {
			con = DBUtil.getConnection();
			pstmt = con.prepareStatement("select * from book where 청구번호=?");
			pstmt.setInt(1, bookNo);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				oneBook = new BookDTO(rset.getInt(1), rset.getString(2), rset.getString(3), rset.getString(4),
						rset.getInt(5), rset.getString(6), rset.getInt(7), rset.getInt(8));
			}
		} finally {
			DBUtil.close(con, pstmt, rset);
		}
		return oneBook;
	}

	// 모든 도서 검색
	public static ArrayList<BookDTO> getAllBooks() throws SQLException {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		ArrayList<BookDTO> allBooks = null;
		try {
			con = DBUtil.getConnection();
			pstmt = con.prepareStatement("select * from book");
			rset = pstmt.executeQuery();

			allBooks = new ArrayList<BookDTO>();
			while (rset.next()) {
				allBooks.add(new BookDTO(rset.getInt(1), rset.getString(2), rset.getString(3), rset.getString(4),
						rset.getInt(5), rset.getString(6), rset.getInt(7), rset.getInt(8)));
			}
		} catch (SQLException s) {
			s.printStackTrace();
			throw s;
		} finally {
			DBUtil.close(con, pstmt, rset);
		}
		return allBooks;
	}

	// 삭제된 도서들 검색
	public static ArrayList<BookDTO> getAlldeleteBooks() throws SQLException {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		ArrayList<BookDTO> allBooks = null;
		try {
			con = DBUtil.getConnection();
			pstmt = con.prepareStatement("select * from book_backup");
			rset = pstmt.executeQuery();

			allBooks = new ArrayList<BookDTO>();
			while (rset.next()) {
				allBooks.add(new BookDTO(rset.getInt(1), rset.getString(2), rset.getString(3), rset.getString(4),
						rset.getInt(5), rset.getString(6), rset.getInt(7), rset.getInt(8)));
			}
		} catch (SQLException s) {
			s.printStackTrace();
			throw s;
		} finally {
			DBUtil.close(con, pstmt, rset);
		}
		return allBooks;
	}

	// book_status로 대출중 도서 반환 - 0과 1만 가능
	public static ArrayList<BookDTO> getBookStatus(int bookStatus) throws SQLException {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		ArrayList<BookDTO> canBooks = null;
		try {
			if (bookStatus == 0) {
				con = DBUtil.getConnection();
				pstmt = con.prepareStatement("select * from book where 대출현황=?");
				pstmt.setInt(1, bookStatus);
				rset = pstmt.executeQuery();

				canBooks = new ArrayList<BookDTO>();
				while (rset.next()) {
					canBooks.add(new BookDTO(rset.getInt(1), rset.getString(2), rset.getString(3), rset.getString(4),
							rset.getInt(5), rset.getString(6), rset.getInt(7), rset.getInt(8)));
				}

			} else {
				con = DBUtil.getConnection();
				pstmt = con.prepareStatement("select * from book where 대출현황=?");
				pstmt.setInt(1, bookStatus);
				rset = pstmt.executeQuery();

				canBooks = new ArrayList<BookDTO>();
				while (rset.next()) {
					canBooks.add(new BookDTO(rset.getInt(1), rset.getString(2), rset.getString(3), rset.getString(4),
							rset.getInt(5), rset.getString(6), rset.getInt(7), rset.getInt(8)));
				}
			}
		} finally {
			DBUtil.close(con, pstmt, rset);
		}
		return canBooks;
	}
}