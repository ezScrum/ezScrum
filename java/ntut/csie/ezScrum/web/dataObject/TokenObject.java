package ntut.csie.ezScrum.web.dataObject;

import java.util.Random;

import ntut.csie.ezScrum.web.databasEnum.TokenEnum;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class TokenObject implements IBaseObject {
	private final static int TOKEN_LENGTH = 60;
	
	private long mId = -1;
	private String mPublicToken = "";
	private String mPrivateToken = "";
	private long mAccountId = -1;

	public TokenObject(long id, long accountId, String publicToken, String privateToken) {
		mId = id;
		mAccountId = accountId;
		mPublicToken = publicToken;
		mPrivateToken = privateToken;
	}
	
	public TokenObject(long accountId) {
		mAccountId = accountId;
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
		// TODO Auto-generated method stub

	}

	@Override
	public void reload() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean delete() {
		// TODO Auto-generated method stub
		return false;
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
	
	
	static final String candicates = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static Random rnd = new Random();
	
	private String randomString(int length) {
		StringBuilder stringBuilder = new StringBuilder(length);
		for (int i = 0; i < length; i++)
			stringBuilder.append(candicates.charAt(rnd.nextInt(candicates.length())));
		return stringBuilder.toString();
	}
}
