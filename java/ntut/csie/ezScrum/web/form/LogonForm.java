/*
 * Copyright (C) 2005 Chin-Yun Hsieh <hsieh@csie.ntut.edu.tw>
 *                    Yu Chin Cheng <yccheng@csie.ntut.edu.tw>
 *                    Chien-Tsun Chen <ctchen@ctchen.idv.tw>
 *                    Tsui-Chen She <kay_sher@hotmail.com>
 *                    Chia-Hao Wu<chwu2004@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

//Created by MyEclipse Struts
// XSL source (default): platform:/plugin/com.genuitec.eclipse.cross.easystruts.eclipse_3.9.210/xslt/JavaClass.xsl
package ntut.csie.ezScrum.web.form;

import org.apache.struts.validator.ValidatorForm;


/**
 * MyEclipse Struts
 * Creation date: 09-08-2005
 *
 * XDoclet definition:
 * @struts:form name="logonForm"
 */
public class LogonForm extends ValidatorForm {
    // --------------------------------------------------------- Instance Variables

    /**
         *
         */
    private static final long serialVersionUID = 1971164302414870843L;

    /** Password property */
    private String Password;

    /** UserId property */
    private String UserId;

    // --------------------------------------------------------- Methods

    /**
     * Returns the Password.
     * @return String
     */
    public String getPassword() {
        return Password;
    }

    /**
     * Set the Password.
     * @param Password The Password to set
     */
    public void setPassword(String Password) {
        this.Password = Password;
    }

    /**
     * Returns the UserId.
     * @return String
     */
    public String getUserId() {
        return UserId;
    }

    /**
     * Set the UserId.
     * @param UserId The UserId to set
     */
    public void setUserId(String UserId) {
        this.UserId = UserId;
    }
}
