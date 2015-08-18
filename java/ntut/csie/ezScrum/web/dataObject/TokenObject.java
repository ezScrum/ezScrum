package ntut.csie.ezScrum.web.dataObject;

import java.security.MessageDigest;
import java.util.ArrayList;
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
	private String mPlatformType = "";
	private long mAccountId = -1;
	private long mCreateTime = -1;
	private long mUpdateTime = -1;
	
	public static TokenObject get(long id) {
		return TokenDAO.getInstance().get(id);
	}
	
	public static ArrayList<TokenObject> getByAccountId(long accountId) {
		return TokenDAO.getInstance().getByAccountId(accountId);
	}
	
	public static TokenObject get(long accountId, String publicToken) {
		ArrayList<TokenObject> tokens = TokenDAO.getInstance().getByAccountId(accountId);
		for (TokenObject token : tokens) {
			if (token.getPublicToken().equals(publicToken)) {
				return token;
			}
		}
		return null;
	}
	
	public static TokenObject getByPlatform(long accountId, String platformType) {
		return TokenDAO.getInstance().get(accountId, platformType);
	}

	public TokenObject(long id, long accountId, String publicToken,
			String privateToken, String platformType, long createTime, long updateTime) {
		mId = id;
		mAccountId = accountId;
		mPublicToken = publicToken;
		mPrivateToken = privateToken;
		mPlatformType = platformType;
		mCreateTime = createTime;
		mUpdateTime = updateTime;
	}

	public TokenObject(long accountId, String platformType) {
		mAccountId = accountId;
		mPlatformType = platformType;
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
	
	public String getPlatformType() {
		return mPlatformType;
	}
	
	public long getCreateTime() {
		return mCreateTime;
	}
	
	public long getUpdateTime() {
		return mUpdateTime;
	}

	public String getDisposableToken(long timestamp) {
		try {
			return genDisposable(mPublicToken, mPrivateToken, timestamp);
		} catch (Exception e) {
			return "ERROR_ON_GEN_DISPOSABLE_TOKEN";
		}
	}

	public void rehash() {
		mPublicToken = randomString(TOKEN_LENGTH);
		mPrivateToken = randomString(TOKEN_LENGTH);
		if(exist()){
			save();
		}
	}

	@Override
	public void save() {
		if (!samePlatformExist()) {
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
		object.put(TokenEnum.PLATFORM_TYPE, mPlatformType);
		object.put(TokenEnum.CREATE_TIME, mCreateTime);
		object.put(TokenEnum.UPDATE_TIME, mUpdateTime);
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
		mPlatformType = token.getPlatformType();
		mCreateTime = token.getCreateTime();
		mUpdateTime = token.getUpdateTime();
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
	
	private boolean samePlatformExist() {
		TokenObject token = TokenDAO.getInstance().get(mAccountId, mPlatformType);
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
	
	private static String genDisposable(String publicToken,
			String privateToken, long timestamp) throws Exception {
		String plainCode = new StringBuilder().append(publicToken).append(privateToken).append(timestamp).toString();
		byte[] bytesOfMessage = plainCode.getBytes("UTF-8");
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte[] digest = md.digest(bytesOfMessage);
		return byteArrayToHexString(digest);
	}
	
	private static String byteArrayToHexString(byte[] b) {
	  String result = "";
	  for (int i=0; i < b.length; i++) {
	    result +=
	          Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
	  }
	  return result;
	}
}
