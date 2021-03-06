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

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import mhp_ph.mainForm;
import mhp_ph.phArt;

/**
 * Search panel
 * @author paulx
 */
public class PHsearch extends javax.swing.JPanel {

    /** Creates new form PHsearch */
    public PHsearch() {
        initComponents();
        results.setIcon(PHsingleIcon.getIcon());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        title = new javax.swing.JTextField();
        author = new javax.swing.JTextField();
        year = new javax.swing.JTextField();
        volume = new javax.swing.JTextField();
        number = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        results = new javax.swing.JCheckBox();
        search = new PHControls.PHtextButton();
        titleAndOr = new PHControls.PHandOr();
        authorAndOr = new PHControls.PHandOr();
        yearAndOr = new PHControls.PHandOr();
        volumeAndOr = new PHControls.PHandOr();
        numberAndOr = new PHControls.PHandOr();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(330, 470));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        title.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        title.setForeground(new java.awt.Color(32, 32, 32));
        title.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(166, 166, 166)), javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        title.setCaretColor(new java.awt.Color(32, 32, 32));
        title.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                titleKeyPressed(evt);
            }
        });
        add(title, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 60, 190, 24));

        author.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        author.setForeground(new java.awt.Color(32, 32, 32));
        author.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(166, 166, 166)), javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        author.setCaretColor(new java.awt.Color(32, 32, 32));
        author.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                authorKeyPressed(evt);
            }
        });
        add(author, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 90, 190, 24));

        year.setFont(new java.awt.Font("Trebuchet MS", 0, 11));
        year.setForeground(new java.awt.Color(32, 32, 32));
        year.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(166, 166, 166)), javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        year.setCaretColor(new java.awt.Color(32, 32, 32));
        year.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                yearKeyPressed(evt);
            }
        });
        add(year, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 120, 190, 24));

        volume.setFont(new java.awt.Font("Trebuchet MS", 0, 11));
        volume.setForeground(new java.awt.Color(32, 32, 32));
        volume.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(166, 166, 166)), javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        volume.setCaretColor(new java.awt.Color(32, 32, 32));
        volume.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                volumeKeyPressed(evt);
            }
        });
        add(volume, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 150, 190, 24));

        number.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        number.setForeground(new java.awt.Color(32, 32, 32));
        number.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(166, 166, 166)), javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        number.setCaretColor(new java.awt.Color(32, 32, 32));
        number.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                numberKeyPressed(evt);
            }
        });
        add(number, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 180, 190, 24));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Trebuchet MS", 0, 11));
        jLabel1.setForeground(new java.awt.Color(160, 160, 160));
        jLabel1.setText("Tytuł");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 65, -1, -1));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Trebuchet MS", 0, 11));
        jLabel2.setForeground(new java.awt.Color(160, 160, 160));
        jLabel2.setText("Autor");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 95, -1, -1));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Trebuchet MS", 0, 11));
        jLabel3.setForeground(new java.awt.Color(160, 160, 160));
        jLabel3.setText("Rok");
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 125, -1, -1));

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Trebuchet MS", 0, 11));
        jLabel4.setForeground(new java.awt.Color(160, 160, 160));
        jLabel4.setText("Tom");
        add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 155, -1, -1));

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Trebuchet MS", 0, 11));
        jLabel5.setForeground(new java.awt.Color(160, 160, 160));
        jLabel5.setText("Zeszyt");
        add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 185, -1, -1));

        results.setBackground(new java.awt.Color(255, 255, 255));
        results.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        results.setForeground(new java.awt.Color(160, 160, 160));
        results.setText("Wyszukaj w wynikach");
        add(results, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 210, -1, -1));

        search.setText("Wykonaj");
        search.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchMouseClicked(evt);
            }
        });
        add(search, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 440, -1, -1));
        add(titleAndOr, new org.netbeans.lib.awtextra.AbsoluteConstraints(272, 60, -1, -1));
        add(authorAndOr, new org.netbeans.lib.awtextra.AbsoluteConstraints(272, 90, -1, -1));
        add(yearAndOr, new org.netbeans.lib.awtextra.AbsoluteConstraints(272, 120, -1, -1));
        add(volumeAndOr, new org.netbeans.lib.awtextra.AbsoluteConstraints(272, 150, -1, -1));
        add(numberAndOr, new org.netbeans.lib.awtextra.AbsoluteConstraints(272, 180, -1, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void searchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchMouseClicked
        search();
    }//GEN-LAST:event_searchMouseClicked

    private void titleKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_titleKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            search();
        }
    }//GEN-LAST:event_titleKeyPressed

    private void authorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_authorKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            search();
        }
    }//GEN-LAST:event_authorKeyPressed

    private void yearKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_yearKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            search();
        }
    }//GEN-LAST:event_yearKeyPressed

    private void volumeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_volumeKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            search();
        }
    }//GEN-LAST:event_volumeKeyPressed

    private void numberKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_numberKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            search();
        }
    }//GEN-LAST:event_numberKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField author;
    private PHControls.PHandOr authorAndOr;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JTextField number;
    private PHControls.PHandOr numberAndOr;
    private javax.swing.JCheckBox results;
    private PHControls.PHtextButton search;
    private javax.swing.JTextField title;
    private PHControls.PHandOr titleAndOr;
    private javax.swing.JTextField volume;
    private PHControls.PHandOr volumeAndOr;
    private javax.swing.JTextField year;
    private PHControls.PHandOr yearAndOr;
    // End of variables declaration//GEN-END:variables

    private mainForm main; ///< Main form

    /**
     * Sets main form
     * @param form Main form
     */
    public void setMainForm(mainForm form)
    {
        main = form;
    }

    /**
     * Search action
     */
    private void search()
    {
        if (main.tabs == 5)
        {
            PHtooManyTabs p = new PHtooManyTabs();
            p.setVisible(true);
            return;
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        PHresults tab = null;
        PHresults oldTab = main.currentTabRef;
        PHtab button = null;
        switch (++main.tabs)
        {
            case 1:
            {
                tab = main.tab1;
                button = main.tabButton1;
                main.currentTab = 1;
                main.currentTabRef = main.tab1;
                break;
            }
            case 2:
            {
                tab = main.tab2;
                button = main.tabButton2;
                main.currentTab = 2;
                main.currentTabRef = main.tab2;
                break;
            }
            case 3:
            {
                tab = main.tab3;
                button = main.tabButton3;
                main.currentTab = 3;
                main.currentTabRef = main.tab3;
                break;
            }
            case 4:
            {
                tab = main.tab4;
                button = main.tabButton4;
                main.currentTab = 4;
                main.currentTabRef = main.tab4;
                break;
            }
            case 5:
            {
                tab = main.tab5;
                button = main.tabButton5;
                main.currentTab = 5;
                main.currentTabRef = main.tab5;
                break;
            }
        }
        String text = "";
        if (title.getText().compareTo("") != 0)
        {
            text = title.getText();
        } else if (author.getText().compareTo("") != 0) {
            text = author.getText();
        } else if (year.getText().compareTo("") != 0) {
            text = year.getText();
        } else if (volume.getText().compareTo("") != 0) {
            text = volume.getText();
        } else {
            text = number.getText();
        }
        button.setText(text);
        button.setVisible(true);
        main.deactivateTabs();
        main.hideTabs();
        tab.setVisible(true);
        button.activate(true);
        boolean andTitle = titleAndOr.and;
        boolean andAuthor = authorAndOr.and;
        boolean andVolume = volumeAndOr.and;
        boolean andNumber = numberAndOr.and;
        boolean andYear = yearAndOr.and;
        try
        {
            int no = 1;
            tab.getPanel().removeAll();
            LinkedList<phArt> ll = new LinkedList<phArt>();
            ll = main.db.detailedSearch(title.getText(), null, author.getText(), year.getText(), volume.getText(), number.getText(), andTitle, andAuthor, andYear, andVolume, andNumber);
            for (phArt e : ll )
            {
                if (!results.isSelected() || (results.isSelected() && oldTab.artExists(e))) {
                    tab.addArt(e);
                }
            }
            tab.repaint();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}
