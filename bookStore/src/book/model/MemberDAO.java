package book.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import book.model.util.DBUtil;

public class MemberDAO {
	// 대출 시 member table 변경
	public static boolean updateMember(int memberNo, int bookNo) throws SQLException {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DBUtil.getConnection();
			pstmt = con.prepareStatement("update member set 대출책번호=? where member_no=?");
			pstmt.setInt(1, bookNo);
			pstmt.setInt(2, memberNo);

			int result = pstmt.executeUpdate();
			if (result == 1) {
				return true;
			}
		} finally {
			DBUtil.close(con, pstmt);
		}
		return false;
	}

	// 반납 시 member table 변경
	public static boolean returnMember(int memberNo) throws SQLException {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DBUtil.getConnection();
			pstmt = con.prepareStatement("update member set 대출책번호= 99999 where member_no=?");
			pstmt.setInt(1, memberNo);

			int result = pstmt.executeUpdate();
			if (result == 1) {
				return true;
			}
		} finally {
			DBUtil.close(con, pstmt);
		}
		return false;
	}
}
