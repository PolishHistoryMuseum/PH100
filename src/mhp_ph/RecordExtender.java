/**
 * Copyright 2010 Muzeum Historii Polski w Warszawie
 *
 * This file is part of PH100.
 *
 * PH100 is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * PH100 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along
 * with PH100. If not, see <http://www.gnu.org/licenses/>.
 */


package mhp_ph;

import PHControls.PHrecord;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Record extender
 * @author paulx
 */
public class RecordExtender implements MouseListener {

    PHrecord record = null;

    public RecordExtender(PHrecord p) {
        record = p;
    }
    public void mouseClicked(MouseEvent e) {
        if (record.state == PHrecord.vRec.simple)
        {
            record.overrideLayoutAdv();
        } else {
            record.overrideLayoutSimple();
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

}
