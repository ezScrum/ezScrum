package ntut.csie.ezScrum.restful.dataMigration.support;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;

public class BaseUrlDistributorTest {
	@Test
	public void testGetBaseUrl() {
		assertEquals(BaseUrlDistributor.BASE_URL, BaseUrlDistributor.getBaseUrl());
		Configuration configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
		assertEquals(BaseUrlDistributor.TEST_MODE_BASE_URL, BaseUrlDistributor.getBaseUrl());
	}
}
