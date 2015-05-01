package ntut.csie.ezScrum.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.web.dataObject.TokenObject;
import ntut.csie.ezScrum.web.databasEnum.TokenEnum;

public class TokenDAO extends AbstractDAO<TokenObject, TokenObject> {
	
	private static TokenDAO sInstance = null;

	public static TokenDAO getInstance() {
		if (sInstance == null) {
			sInstance = new TokenDAO();
		}
		return sInstance;
	}

	@Override
	public long create(TokenObject token) {
		IQueryValueSet valueSet = new MySQLQuerySet(); 
		valueSet.addTableName(TokenEnum.TABLE_NAME);
		valueSet.addInsertValue(TokenEnum.ACCOUNT_ID, token.getAccountId());
		valueSet.addInsertValue(TokenEnum.PUBLIC_TOKEN, token.getPublicToken());
		valueSet.addInsertValue(TokenEnum.PRIVATE_TOKEN, token.getPrivateToken());
		String query = valueSet.getInsertQuery();
		long id = mControl.executeInsert(query);
		return id;
	}
	
	public TokenObject getByAccountId(long accountId) {
		IQueryValueSet valueSet = new MySQLQuerySet(); 
		valueSet.addTableName(TokenEnum.TABLE_NAME);
		valueSet.addEqualCondition(TokenEnum.ACCOUNT_ID, accountId);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		TokenObject token = null;
		try {
			if (result.next()) {
				token = convert(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return token;
	}

	@Override
	public TokenObject get(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet(); 
		valueSet.addTableName(TokenEnum.TABLE_NAME);
		valueSet.addEqualCondition(TokenEnum.ID, id);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		TokenObject token = null;
		try {
			if (result.next()) {
				token = convert(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return token;
	}

	@Override
	public boolean update(TokenObject token) {
		IQueryValueSet valueSet = new MySQLQuerySet(); 
		valueSet.addTableName(TokenEnum.TABLE_NAME);
		valueSet.addEqualCondition(TokenEnum.ID, token.getId());
		valueSet.addInsertValue(TokenEnum.PUBLIC_TOKEN, token.getPublicToken());
		valueSet.addInsertValue(TokenEnum.PRIVATE_TOKEN, token.getPrivateToken());
		String query = valueSet.getUpdateQuery();
		boolean success = mControl.executeUpdate(query);
		return success;
	}

	@Override
	public boolean delete(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet(); 
		valueSet.addTableName(TokenEnum.TABLE_NAME);
		valueSet.addEqualCondition(TokenEnum.ID, id);
		String query = valueSet.getDeleteQuery();
		boolean success = mControl.executeUpdate(query);
		return success;
	}
	
	private TokenObject convert(ResultSet result) throws SQLException {
		TokenObject token = new TokenObject(
				result.getLong(TokenEnum.ID), 
				result.getLong(TokenEnum.ACCOUNT_ID),
				result.getString(TokenEnum.PUBLIC_TOKEN),
				result.getString(TokenEnum.PRIVATE_TOKEN));
		return token;
	}
}
