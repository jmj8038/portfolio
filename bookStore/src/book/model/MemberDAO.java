package book.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import book.model.util.DBUtil;

public class MemberDAO {
	// ���� �� member table ����
	public static boolean updateMember(int memberNo, int bookNo) throws SQLException {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DBUtil.getConnection();
			pstmt = con.prepareStatement("update member set ����å��ȣ=? where member_no=?");
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

	// �ݳ� �� member table ����
	public static boolean returnMember(int memberNo) throws SQLException {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DBUtil.getConnection();
			pstmt = con.prepareStatement("update member set ����å��ȣ= 99999 where member_no=?");
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
