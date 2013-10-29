package ntut.csie.ezScrum.web.form;

import javax.servlet.http.*; 
import org.apache.struts.action.*; 
import org.apache.struts.upload.*; 
import org.apache.struts.action.ActionForm;

public class UploadForm extends ActionForm {
	private static final long serialVersionUID = -8957347837116729149L;
	private FormFile file;

    public void setFile(FormFile file) { 
        this.file = file; 
    }

    public FormFile getFile() { 
        return file; 
    }

    public void reset(ActionMapping mapping,
                      HttpServletRequest req) { 
        file = null; 
    } 
}
