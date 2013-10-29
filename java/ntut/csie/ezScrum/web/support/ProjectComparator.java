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

package ntut.csie.ezScrum.web.support;

import java.util.Comparator;
import java.util.Date;

import ntut.csie.jcis.resource.core.IProject;


public class ProjectComparator implements Comparator {
    public static final int COMPARE_TYPE_NAME = 0;
    public static final int COMPARE_TYPE_CREATEDATE = 1;
    public static final int COMPARE_TYPE_MANAGER = 2;
    private int m_comparType = COMPARE_TYPE_NAME;

    public ProjectComparator(int compareType) {
        m_comparType = compareType;
    }

    public int compare(Object p1, Object p2) {
        IProject project1 = (IProject) p1;
        IProject project2 = (IProject) p2;

        if (m_comparType == COMPARE_TYPE_NAME) {
            return project1.getName().compareTo(project2.getName());
        }

        if (m_comparType == COMPARE_TYPE_CREATEDATE) {
            Date d1 = project1.getProjectDesc().getCreateDate();
            Date d2 = project2.getProjectDesc().getCreateDate();

            return d1.compareTo(d2);
        }

        if (m_comparType == COMPARE_TYPE_MANAGER) {
            String m1 = project1.getProjectDesc().getProjectManager();
            String m2 = project2.getProjectDesc().getProjectManager();

            return m1.compareTo(m2);
        }

        return 0;
    }
}
