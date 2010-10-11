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
import mhp_ph.mainForm;

/**
 * Tab panel
 * @author paulx
 */
public class PHtab extends javax.swing.JPanel {

    /** Creates new form PHtab */
    public PHtab() {
        initComponents();
        pHimage1.changeImage("close.png");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pHimage1 = new PHControls.PHimage();
        pHtext1 = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(82, 20));
        setMinimumSize(new java.awt.Dimension(82, 20));
        setPreferredSize(new java.awt.Dimension(82, 20));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pHimage1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pHimage1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pHimage1Layout = new javax.swing.GroupLayout(pHimage1);
        pHimage1.setLayout(pHimage1Layout);
        pHimage1Layout.setHorizontalGroup(
            pHimage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );
        pHimage1Layout.setVerticalGroup(
            pHimage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        add(pHimage1, new org.netbeans.lib.awtextra.AbsoluteConstraints(62, 2, 16, 16));

        pHtext1.setBackground(new java.awt.Color(88, 88, 88));
        pHtext1.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        pHtext1.setForeground(new java.awt.Color(255, 255, 255));
        pHtext1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        pHtext1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        pHtext1.setMaximumSize(new java.awt.Dimension(54, 20));
        pHtext1.setMinimumSize(new java.awt.Dimension(54, 20));
        pHtext1.setPreferredSize(new java.awt.Dimension(54, 20));
        add(pHtext1, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 0, 54, 20));
    }// </editor-fold>//GEN-END:initComponents

    private void pHimage1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pHimage1MouseClicked
        closeTab();
    }//GEN-LAST:event_pHimage1MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private PHControls.PHimage pHimage1;
    private javax.swing.JLabel pHtext1;
    // End of variables declaration//GEN-END:variables

    private mainForm main = null; ///< Main form

    /**
     * Sets tab text
     * @param text New text
     */
    public void setText(String text)
    {
        pHtext1.setText(text);
    }

    /**
     * Gets text
     * @return Text
     */
    public String getText()
    {
        return pHtext1.getText();
    }

    /**
     * Activates or deactivates tab
     * @param state Activate state
     */
    public void activate(boolean state)
    {
        if (state)
        {
            this.setBackground(new Color(88, 88, 88));
            pHtext1.setForeground(new Color(255, 255, 255));
            pHimage1.setVisible(true);
        } else {
            this.setBackground(new Color(222, 222, 222));
            pHtext1.setForeground(new Color(166, 166, 166));
            pHimage1.setVisible(false);
        }
    }

    /**
     * Sets main form
     * @param form Main form
     */
    public void setMainForm(mainForm form)
    {
        main = form;
    }

    /**
     * Close tab
     */
    private void closeTab()
    {
        PHresults tab = main.currentTabRef;
        main.hideTabs();
        main.deactivateTabs();
        if (main.currentTab <= 1)
        {
            main.tabButton1.setText(main.tabButton2.getText());
            main.tab1 = main.tab2;
        }
        if (main.currentTab <= 2)
        {
            main.tabButton2.setText(main.tabButton3.getText());
            main.tab2 = main.tab3;
        }
        if (main.currentTab <= 3)
        {
            main.tabButton3.setText(main.tabButton4.getText());
            main.tab3 = main.tab4;
        }
        if (main.currentTab <= 4)
        {
            main.tabButton4.setText(main.tabButton5.getText());
            main.tab4 = main.tab5;
        }
        main.tab5 = tab;
        main.tabs--;
        if (main.currentTab > main.tabs)
        {
            main.currentTab--;
        }
        if (main.currentTab == 1)
        {
            main.currentTabRef = main.tab1;
            main.tab1.setVisible(true);
            main.tabButton1.activate(true);
        } else if (main.currentTab == 2) {
            main.currentTabRef = main.tab2;
            main.tab2.setVisible(true);
            main.tabButton2.activate(true);
        } else if (main.currentTab == 3) {
            main.currentTabRef = main.tab3;
            main.tab3.setVisible(true);
            main.tabButton3.activate(true);
        } else if (main.currentTab == 4) {
            main.currentTabRef = main.tab4;
            main.tab4.setVisible(true);
            main.tabButton4.activate(true);
        }
        if (main.tabs == 0)
        {
            main.tabButton1.setVisible(false);
        } else if (main.tabs == 1) {
            main.tabButton2.setVisible(false);
        } else if (main.tabs == 2) {
            main.tabButton3.setVisible(false);
        } else if (main.tabs == 3) {
            main.tabButton4.setVisible(false);
        } else if (main.tabs == 4) {
            main.tabButton5.setVisible(false);
        }
    }
}