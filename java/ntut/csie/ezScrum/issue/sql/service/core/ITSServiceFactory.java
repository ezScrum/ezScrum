package ntut.csie.ezScrum.issue.sql.service.core;

import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.sql.service.internal.MantisService;

public class ITSServiceFactory {
	private static ITSServiceFactory itsFactory = null;
	
	//the default service is Mantis
	private String m_serviceID = ITSEnum.MANTIS_SERVICE_ID;
	
	private ITSServiceFactory (){}
	
	public static ITSServiceFactory getInstance(){
		if (itsFactory == null)
			itsFactory = new ITSServiceFactory();
		return itsFactory;
	}
	
	@Deprecated
	public IITSService getService(Configuration config){
		if (m_serviceID.equals(ITSEnum.MANTIS_SERVICE_ID))
			return new MantisService(config);
		return null;
	}
	
	public IITSService getService(String serviceID, Configuration config){
		if (serviceID.equals(ITSEnum.MANTIS_SERVICE_ID))
			return new MantisService(config);
		return null;
	}
	
	public void setServiceID(String serviceID){
		m_serviceID  = serviceID;
	}
	
	public String getServiceID(){
		return m_serviceID;
	}
}
