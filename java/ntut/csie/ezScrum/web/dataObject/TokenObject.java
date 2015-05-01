package ntut.csie.ezScrum.web.dataObject;

import java.util.Random;

import ntut.csie.ezScrum.dao.TokenDAO;
import ntut.csie.ezScrum.web.databasEnum.TokenEnum;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class TokenObject implements IBaseObject {
	private final static int TOKEN_LENGTH = 60;

	private long mId = -1;
	private String mPublicToken = "";
	private String mPrivateToken = "";
	private long mAccountId = -1;
	
	public static TokenObject get(long id) {
		return TokenDAO.getInstance().get(id);
	}
	
	public static TokenObject getByAccountId(long accountId) {
		return TokenDAO.getInstance().getByAccountId(accountId);
	}

	public TokenObject(long id, long accountId, String publicToken,
			String privateToken) {
		mId = id;
		mAccountId = accountId;
		mPublicToken = publicToken;
		mPrivateToken = privateToken;
	}

	public TokenObject(long accountId) {
		mAccountId = accountId;
		rehash();
	}

	public long getId() {
		return mId;
	}

	public long getAccountId() {
		return mAccountId;
	}

	public String getPublicToken() {
		return mPublicToken;
	}

	public String getPrivateToken() {
		return mPrivateToken;
	}

	public void rehash() {
		mPublicToken = randomString(TOKEN_LENGTH);
		mPrivateToken = randomString(TOKEN_LENGTH);
		save();
	}

	@Override
	public void save() {
		if (!exist()) {
			doCreate();
		} else {
			doUpdate();
		}
	}

	@Override
	public void reload() {
		if (exist()) {
			TokenObject token = TokenDAO.getInstance().get(mId);
			resetData(token);
		}
	}

	@Override
	public boolean delete() {
		boolean success = TokenDAO.getInstance().delete(mId);
		return success;
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject object = new JSONObject();
		object.put(TokenEnum.ACCOUNT_ID, mAccountId);
		object.put(TokenEnum.PUBLIC_TOKEN, mPublicToken);
		object.put(TokenEnum.PRIVATE_TOKEN, mPrivateToken);
		return object;
	}

	@Override
	public String toString() {
		try {
			return toJSON().toString();
		} catch (JSONException e) {
			return "";
		}
	}

	private void resetData(TokenObject token) {
		mAccountId = token.getAccountId();
		mPublicToken = token.getPublicToken();
		mPrivateToken = token.getPrivateToken();
	}

	private void doCreate() {
		mId = TokenDAO.getInstance().create(this);
		reload();
	}

	private void doUpdate() {
		TokenDAO.getInstance().update(this);
	}

	private boolean exist() {
		TokenObject token = TokenDAO.getInstance().get(mId);
		return token != null;
	}

	private String randomString(int length) {
		String candicates = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random random = new Random();
		StringBuilder stringBuilder = new StringBuilder(length);
		for (int i = 0; i < length; i++)
			stringBuilder.append(candicates.charAt(random.nextInt(candicates
					.length())));
		return stringBuilder.toString();
	}
}
