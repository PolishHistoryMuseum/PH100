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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JButton;

/**
 *
 * @author szymek
 */
public class PHlupa extends JButton
{
    public String imgName = "lupaOff.jpg";

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(25, 16);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        BufferedImage th = null;
        try
        {
            th = PHResize.resize(ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(imgName)), 25, 16);
        }
        catch (Exception ex) {}
        g.drawImage(th, 0, 0, 25, 16, null);
    }
}
