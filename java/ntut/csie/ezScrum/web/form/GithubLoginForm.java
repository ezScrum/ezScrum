
package ntut.csie.ezScrum.web.form;

import org.apache.struts.validator.ValidatorForm;


/**
 * MyEclipse Struts
 * Creation date: 09-08-2005
 *
 * XDoclet definition:
 * @struts:form name="logonForm"
 */
public class GithubLoginForm extends ValidatorForm {
    // --------------------------------------------------------- Instance Variables

    /**
         *
         */
    private static final long serialVersionUID = 1971164302414870843L;
    private String token;

    // --------------------------------------------------------- Methods

    public void setToken(String Token){
    	this.token = Token;
    }
    public String getToken(){
    	return this.token;
    }
}
