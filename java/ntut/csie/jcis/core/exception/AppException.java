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

/*
 * Created on 2005/5/13
 *
 * Copyright (c) 2004 CSIE National Taipei University of Technology.
 * All Rights Reserved.
 */
package ntut.csie.jcis.core.exception;

import java.io.PrintStream;
import java.io.PrintWriter;


/**
 * @author kay
 *
 */
public class AppException extends RecoverableException {
    private static final long serialVersionUID = -6855280279510207158L;
    private Throwable m_cause = null;

    public AppException() {
        super();
    }

    public AppException(String aMessage) {
        super(aMessage);
    }

    public AppException(Throwable aCause) {
        super(aCause);
    }

    public AppException(String aMessage, Throwable aCause) {
        super(aMessage, aCause);
    }

    public void printStackTrace() {
        super.printStackTrace();

        if (m_cause != null) {
            m_cause.printStackTrace();
        }
    }

    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);

        if (m_cause != null) {
            m_cause.printStackTrace(s);
        }
    }

    public void printStackTrace(PrintWriter w) {
        super.printStackTrace(w);

        if (m_cause != null) {
            m_cause.printStackTrace(w);
        }
    }

    public Throwable getCause() {
        return m_cause;
    }

    public String getMessage() {
        if (m_cause != null) {
            return super.getMessage() + ":" + m_cause.getMessage();
        }

        return super.getMessage();
    }
}
