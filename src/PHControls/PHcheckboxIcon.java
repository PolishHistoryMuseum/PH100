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


package PHControls;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;

/**
 * Custom icon for checkboxes
 * @author paulx
 */
public class PHcheckboxIcon implements Icon {

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        AbstractButton a = (AbstractButton) c;
        ButtonModel b = a.getModel();

        g.setColor(new Color(166, 166, 166));

        g.drawRect(1, 7, 13, 13);

        if (b.isSelected())
        {
            g.drawLine(1, 7, 14, 20);
            g.drawLine(1, 20, 14, 7);
        }
    }

    @Override
    public int getIconWidth() {
        return 15;
    }

    @Override
    public int getIconHeight() {
        return 21;
    }

}
