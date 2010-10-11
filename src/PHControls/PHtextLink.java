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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TreeMap;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import mhp_ph.DLelement;
import mhp_ph.phArt;

/**
 * Text link
 * @author paulx
 */
public class PHtextLink extends JLabel {
    private class listener implements MouseListener
    {
        public void mouseClicked(MouseEvent e) {
            switch (type)
            {
                case nameList:
                {
                    nameList();
                    break;
                }
                case dateList:
                {
                    dateList();
                    break;
                }
                case nameResults:
                {
                    nameResults();
                    break;
                }
                case dateResults:
                {
                    dateResults();
                    break;
                }
                case dateVolumeResults:
                {
                    volumeResults();
                    break;
                }
                case dateVolumeNumberResults:
                {
                    numberResults();
                    break;
                }
                case helpTopic:
                {
                    helpTopic();
                    break;
                }
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

    public enum types {dateResults, dateVolumeResults, dateVolumeNumberResults,
        dateList, nameResults, nameList, helpTopic}; ///< Text link type
    private PHresults tab, tab2; ///< Result tab
    private types type; ///< Type
    private String date; ///< Date
    private String volume; ///< Volume
    private String number; ///< Number
    public JPanel panel, parentPanel; ///< Result and parent panel
    private static PHtextLink name = null; ///< Current selected name
    private static ArrayList<PHtextLink> year = new ArrayList<PHtextLink>(); ///< Current selected years
    private static PHtextLink letter = null; ///< Current selected letter
    private static PHtextLink help = null; ///< Current selected help
    private static boolean help1 = false; ///< First help list state
    private static boolean help2 = false; ///< Second help list state

    public PHtextLink()
    {
        super();
        this.addMouseListener(new listener());
        this.setMaximumSize(new Dimension(512, 256));
    }

    /**
     * Sets output tabs
     * @param out First output tab
     * @param out2 Second output tab
     */
    public void setOutputTab(PHresults out, PHresults out2)
    {
        tab = out;
        tab2 = out2;
    }

    /**
     * Sets link type
     * @param t Type
     */
    public void setType(types t)
    {
        type = t;
    }

    /**
     * Sets link data
     * @param d Date
     * @param v Volume
     * @param n Number
     */
    public void setData(String d, String v, String n)
    {
        date = d;
        volume = v;
        number = n;
    }

    /**
     * Sets panel
     * @param p Panel
     */
    public void setPanel(JPanel p)
    {
        panel = p;
    }

    /**
     * Sets parent panel
     * @param p Parent panel
     */
    public void setParentPanel(JPanel p)
    {
        parentPanel = p;
    }

    /**
     * Gets date
     * @return Date
     */
    public String getDate()
    {
        return date;
    }

    /**
     * Lists names which start with current letter
     */
    public void nameList()
    {
        TreeMap<String, String> tm = DLelement.getAuthorsList(getText().charAt(0));
        tab.getPanel().removeAll();
        ArrayList<String> a = new ArrayList<String>(tm.keySet());
        Locale l = new Locale("pl", "PL");
        Collections.sort(a, Collator.getInstance(l));
        undecorate(letter);
        decorate(this);
        letter = this;
        for (String k : a)
        {
            PHtextLink p = new PHtextLink();
            p.setText(k);
            p.setType(types.nameResults);
            p.setOutputTab(tab2, null);
            p.setData(tm.get(k), null, null);
            p.setFont(new java.awt.Font("Trebuchet MS", 0, 11));
            p.setForeground(new java.awt.Color(88, 88, 88));
            p.setBackground(new java.awt.Color(255, 255, 255));
            p.setMinimumSize(new Dimension(100, 14));
            p.setMaximumSize(new Dimension(400, 14));
            p.setPreferredSize(new Dimension(400, 14));
            tab.getPanel().add(p);
            p.revalidate();
        }
        tab.pHscroll1.getScroll().getVerticalScrollBar().setValueIsAdjusting(false);
        tab.pHscroll1.getScroll().getVerticalScrollBar().setValue(0);
        tab.pHscroll1.getScroll().getVerticalScrollBar().setValueIsAdjusting(true);
        tab.revalidate();
        tab.getPanel().revalidate();
        tab.repaint();
        tab.getPanel().repaint();
    }

    /**
     * Scroll date list to current year
     */
    private void dateList()
    {
        int x = 0;
        while (true)
        {
            JPanel p1 = (JPanel)tab.getPanel().getComponent(x++);
            JPanel p2 = (JPanel)p1.getComponent(0);
            try
            {
                PHtextLink p = (PHtextLink) p2.getComponent(0);
                if (p.getDate() != null && p.getDate().compareTo(date) >= 0)
                {
                    tab.pHscroll1.getScroll().getVerticalScrollBar().setValueIsAdjusting(false);
                    tab.pHscroll1.getScroll().getVerticalScrollBar().setValue(p1.getBounds().y);
                    tab.pHscroll1.getScroll().getVerticalScrollBar().setValueIsAdjusting(true);
                    break;
                }
            }
            catch (Exception e)
            {
            }
        }
    }

    /**
     * List author articles
     */
    private void nameResults()
    {
        LinkedList<phArt> ll = DLelement.getAuthorArticles(Integer.parseInt(date));
        tab.getPanel().removeAll();
        undecorate(name);
        decorate(this);
        name = this;
        for (phArt p : ll)
        {
            tab.addArt(p);
        }
        tab.pHscroll1.getScroll().getVerticalScrollBar().setValueIsAdjusting(false);
        tab.pHscroll1.getScroll().getVerticalScrollBar().setValue(0);
        tab.pHscroll1.getScroll().getVerticalScrollBar().setValueIsAdjusting(true);
        tab.revalidate();
        tab.getPanel().revalidate();
        tab.repaint();
        tab.getPanel().repaint();
    }

    /**
     * List volume articles
     */
    private void volumeResults()
    {
        if (panel != null)
        {
            panel.setVisible(!panel.isVisible());
        }
        LinkedList<phArt> ll = DLelement.getVolumeArticles(volume);
        tab.getPanel().removeAll();
        undecorate(year);
        decorate(this);
        year.clear();
        year.add(this);
        for (phArt p : ll)
        {
            tab.addArt(p);
        }
        tab.pHscroll1.getScroll().getVerticalScrollBar().setValueIsAdjusting(false);
        tab.pHscroll1.getScroll().getVerticalScrollBar().setValue(0);
        tab.pHscroll1.getScroll().getVerticalScrollBar().setValueIsAdjusting(true);
        tab.revalidate();
        tab.getPanel().revalidate();
        tab.repaint();
        tab.getPanel().repaint();
    }

    /**
     * List number articles
     */
    private void numberResults()
    {
        LinkedList<phArt> ll = DLelement.getNumberArticles(volume, number);
        tab.getPanel().removeAll();
        undecorate(year);
        decorate(this);
        year.clear();
        year.add(this);
        for (phArt p : ll)
        {
            tab.addArt(p);
        }
        tab.pHscroll1.getScroll().getVerticalScrollBar().setValueIsAdjusting(false);
        tab.pHscroll1.getScroll().getVerticalScrollBar().setValue(0);
        tab.pHscroll1.getScroll().getVerticalScrollBar().setValueIsAdjusting(true);
        tab.revalidate();
        tab.getPanel().revalidate();
        tab.repaint();
        tab.getPanel().repaint();
    }

    /**
     * List year articles
     */
    private void dateResults()
    {
        LinkedList<phArt> ll = DLelement.getDateArticles(date);
        tab.getPanel().removeAll();
        undecorate(year);
        year.clear();
        for (int x = 0; x < getParent().getParent().getParent().getComponentCount(); x++) {
            JPanel p1 = (JPanel)getParent().getParent().getParent().getComponent(x);
            JPanel p2 = (JPanel)p1.getComponent(0);
            try
            {
                PHtextLink p = (PHtextLink) p2.getComponent(2);
                if (p != null && p.getDate() != null && p.getDate().compareTo(date) == 0)
                {
                    decorate(p);
                    year.add(p);
                    if (p.panel != null)
                    {
                        p.panel.setVisible(!p.panel.isVisible());
                    }
                }
            }
            catch (Exception e)
            {
            }
        }
        for (phArt p : ll)
        {
            tab.addArt(p);
        }
        tab.pHscroll1.getScroll().getVerticalScrollBar().setValueIsAdjusting(false);
        tab.pHscroll1.getScroll().getVerticalScrollBar().setValue(0);
        tab.pHscroll1.getScroll().getVerticalScrollBar().setValueIsAdjusting(true);
        tab.revalidate();
        tab.getPanel().revalidate();
        tab.repaint();
        tab.getPanel().repaint();
    }

    /**
     * Decorate link
     * @param p
     */
    private void decorate(PHtextLink p)
    {
        if (p != null)
        {
            Font f = new Font(p.getFont().getFontName(), Font.BOLD, p.getFont().getSize());
            p.setFont(f);
            p.setForeground(new Color(32, 32, 32));
        }
    }

    /**
     * Undecorate link
     * @param p Link to undecorate
     */
    private void undecorate(PHtextLink p)
    {
        if (p != null)
        {
            Font f = new Font(p.getFont().getFamily(), Font.PLAIN, p.getFont().getSize());
            p.setFont(f);
            p.setForeground(new Color(88, 88, 88));
        }
    }

    /**
     * Undecorate links
     * @param pl Links to undecorate
     */
    private void undecorate(ArrayList<PHtextLink> pl)
    {
        for (PHtextLink p : pl)
        {
            Font f = new Font(p.getFont().getFamily(), Font.PLAIN, p.getFont().getSize());
            p.setFont(f);
            if (p.type == type.dateResults)
            {
                p.setForeground(new Color(166, 166, 166));
            } else {
                p.setForeground(new Color(88, 88, 88));
            }
        }
    }

    /**
     * Display help
     */
    public void helpTopic()
    {
        String topic = "";
        switch (Integer.parseInt(number))
        {
            case 1:
            {
                JTextPane t = (JTextPane)tab.getPanel().getComponent(0);
                t.setText("<html><body style=\"font-family: Trebuchet MS ; font-size: 11\">Spis treści w&nbsp;panelu po lewej stronie służy do poruszania się między poszczególnymi rozdziałami i&nbsp;podrozdziałami niniejszej instrukcji. Kliknięcie na tytuł rozdziału spowoduje wyświetlenie jego treści w&nbsp;panelu po prawej stronie ekranu. Jeśli zawartość rozdziału nie mieści się w&nbsp;oknie w&nbsp;całości, to przy prawej krawędzi ekranu pojawia się suwak, za pomocą którego można przewijać tekst.</body></html>");
                try
                {
                    Rectangle r = t.modelToView(t.getDocument().getLength());
                    t.setPreferredSize(new Dimension(t.getSize().width, r.y + r.height));
                } catch (Exception e)
                {
                }
                topic = "Informacje ogólne";
                ((JLabel)panel.getComponent(0)).setText(topic);
                break;
            }
            case 2:
            {
                JTextPane t = (JTextPane)tab.getPanel().getComponent(0);
                t.setText("<html><body style=\"font-family: Trebuchet MS ; font-size: 11\">Program służy do prowadzenia kwerend w&nbsp;zasobie obejmującym wszystkie zeszyty Przeglądu Historycznego wydane od początku ukazywania się czasopisma (1905&nbsp;rok). Umożliwia przeglądanie opisów bibliograficznych, przeszukiwanie ich według swobodnie określonych kryteriów, a&nbsp;także eksportowanie i&nbsp;drukowanie wyników kwerendy zgodnie z&nbsp;preferowanym przez użytkownika sposobem zapisu bibliograficznego w&nbsp;jednym z kilku dostępnych formatów plików.</body></html>");
                try
                {
                    Rectangle r = t.modelToView(t.getDocument().getLength());
                    t.setPreferredSize(new Dimension(t.getSize().width, r.y + r.height));
                } catch (Exception e)
                {
                }
                topic = "Program";
                ((JLabel)panel.getComponent(0)).setText(topic);
                break;
            }
            case 3:
            {
                JTextPane t = (JTextPane)tab.getPanel().getComponent(0);
                t.setText("<html><body style=\"font-family: Trebuchet MS ; font-size: 11\">Program został udostępniony na licencji GNU GPL wersja&nbsp;3 <a href=\"http://www.gnu.org/licenses/gpl.html\"><nobr>http://www.gnu.org/licenses/gpl.html</nobr></a>.</body></html>");
                try
                {
                    Rectangle r = t.modelToView(t.getDocument().getLength());
                    t.setPreferredSize(new Dimension(t.getSize().width, r.y + r.height));
                } catch (Exception e)
                {
                }
                topic = "Licencja";
                ((JLabel)panel.getComponent(0)).setText(topic);
                break;
            }
            case 4:
            {
                JTextPane t = (JTextPane)tab.getPanel().getComponent(0);
                t.setText("<html><body style=\"font-family: Trebuchet MS ; font-size: 11\">Program został opracowany w&nbsp;<a href=\"http://www.muzhp.pl/\">Muzeum Historii Polski</a> przez zespół projektu <a href=\"http://bazhum.muzhp.pl/\">Bazhum</a> z&nbsp;okazji jubileuszowego wydania <a href=\"http://www.dig.com.pl/index.php?s=wyniki&rodz=9&id=15\">Przeglądu Historycznego</a>. Dokumentacja i&nbsp;aktualizacje programu znajdują się na stronie <a href=\"http://bazhum.muzhp.pl/\"><nobr>bazhum.muzhp.pl</nobr></a>. Uwagi dotyczące działania i&nbsp;korzystania z&nbsp;programu prosimy o&nbsp;przesyłanie pod adres <a href=\"mailto:ph100@muzhp.pl\"><nobr>ph100@muzhp.pl</nobr></a>.</body></html>");
                try
                {
                    Rectangle r = t.modelToView(t.getDocument().getLength());
                    t.setPreferredSize(new Dimension(t.getSize().width, r.y + r.height));
                } catch (Exception e)
                {
                }
                topic = "Kontakt";
                ((JLabel)panel.getComponent(0)).setText(topic);
                break;
            }
            case 5:
            {
                JTextPane t = (JTextPane)tab.getPanel().getComponent(0);
                t.setText("<html><body style=\"font-family: Trebuchet MS ; font-size: 11\"><a href=\"http://www.dig.com.pl/index.php?s=wyniki&rodz=9&id=15\">Przegląd Historyczny</a> to jedno z najstarszych czasopism historycznych w&nbsp;Polsce. Założony został w&nbsp;1905&nbsp;roku w&nbsp;Warszawie i&nbsp;początkowo był wydawany przez Franciszka Jana Pułaskiego, znanego historyka a&nbsp;zarazem bibliotekarza i&nbsp;kustosza Biblioteki Ordynacji Krasińskich w&nbsp;Warszawie. W&nbsp;skład pierwszego kolegium redakcyjnego weszli: J.&nbsp;Chrzanowski, S.&nbsp;Dickstein, A.&nbsp;Jabłonowski. W&nbsp;1906&nbsp;roku Przegląd stał się oficjalnym czasopismem założonego właśnie Towarzystwa Miłośników Historii.<p>Redaktorami Przeglądu przez blisko 100&nbsp;lat jego istnienia byli: J.&nbsp;<nobr>Korwin-Kochanowski</nobr> (od&nbsp;1905), M.&nbsp;Handelsman i&nbsp;S.&nbsp;Kętrzyński (od&nbsp;1917), J.&nbsp;Woliński (od&nbsp;1946), S.&nbsp;Arnold i&nbsp;I.&nbsp;Bieżuńska (od&nbsp;1955), S.&nbsp;Kieniewicz (od&nbsp;1962), A.&nbsp;Wyrobisz (od&nbsp;1993), a&nbsp;obecnie W.&nbsp;Lengauer (od&nbsp;2003).</body></html>");
                try
                {
                    Rectangle r = t.modelToView(t.getDocument().getLength());
                    t.setPreferredSize(new Dimension(t.getSize().width, r.y + r.height));
                } catch (Exception e)
                {
                }
                topic = "Czasopismo";
                ((JLabel)panel.getComponent(0)).setText(topic);
                break;
            }
            case 6:
            {
                JTextPane t = (JTextPane)tab.getPanel().getComponent(0);
                t.setText("<html><body style=\"font-family: Trebuchet MS ; font-size: 11\"><a href=\"http://bazhum.muzhp.pl/\">Bazhum</a> jest projektem realizowanym przez <a href=\"http://www.muzhp.pl/\">Muzeum Historii Polski</a>. Jego celem jest zbudowanie bibliograficznej, a&nbsp;następnie pełnotekstowej bazy zawartości polskich czasopism naukowych z&nbsp;zakresu nauk humanistycznych i&nbsp;społecznych. Kolekcja gromadzi wszelkiego rodzaju publikacje ogłoszone na łamach czasopism, w&nbsp;szczególności artykuły, artykuły recenzyjne, recenzje, zapiski, miscellanea, nekrologi, kronikę itp.<p>Projekt powstaje we współpracy z&nbsp;<a href=\"http://www.icm.edu.pl/\">Interdyscyplinarnym Centrum Modelowania Komputerowego i&nbsp;Matematycznego Uniwersytetu Warszawskiego</a>, które tworzy i&nbsp;utrzymuje platformę informatyczną (<a href=\"http://yaddainfo.icm.edu.pl/\">Yadda</a>, otwarte oprogramowanie do tworzenia repozytoriów cyfrowych). <a href=\"http://bazhum.muzhp.pl/\">Bazhum</a> jest dostępny powszechnie i&nbsp;w&nbsp;sposób nieograniczony pod adresem <a href=\"http://bazhum.muzhp.pl/\"><nobr>bazhum.muzhp.pl</nobr></a>.</body></html>");
                try
                {
                    Rectangle r = t.modelToView(t.getDocument().getLength());
                    t.setPreferredSize(new Dimension(t.getSize().width, r.y + r.height));
                } catch (Exception e)
                {
                }
                topic = "Bazhum";
                ((JLabel)panel.getComponent(0)).setText(topic);
                break;
            }
            case 7:
            case 8:
            {
                JTextPane t = (JTextPane)tab.getPanel().getComponent(0);
                t.setText("<html><body style=\"font-family: Trebuchet MS ; font-size: 11\">U&nbsp;góry ekranu ponad obydwoma panelami znajduje się menu, za pomocą którego można przejść do innych ekranów aplikacji.<p>• Szukaj - domyślny ekran, pojawiający się po uruchomieniu programu; z&nbsp;innych ekranów można do niego przejść klikając ikonę lupy w skrajnym lewym polu<p>• Przeglądaj - struktura tomów i&nbsp;zeszytów Przeglądu Historycznego<p>• Autorzy - lista autorów, którzy publikowali w&nbsp;Przeglądzie Historycznym - do każdego z&nbsp;autorów przyporządkowane są napisane przez niego artykuły<p>• Schowek - wybrane wyniki wyszukiwań wykonanych od momentu uruchomienia programu<p>• Pomoc - instrukcja dla użytkownika programu</body></html>");
                try
                {
                    Rectangle r = t.modelToView(t.getDocument().getLength());
                    t.setPreferredSize(new Dimension(t.getSize().width, r.y + r.height));
                } catch (Exception e)
                {
                }
                topic = "Menu górne";
                ((JLabel)panel.getComponent(0)).setText(topic);
                break;
            }
            case 9:
            {
                JTextPane t = (JTextPane)tab.getPanel().getComponent(0);
                t.setText("<html><body style=\"font-family: Trebuchet MS ; font-size: 11\">Po uruchomieniu programu wyświetlany jest ekran Szukaj z&nbsp;opcjami wyszukiwania prostego i&nbsp;zaawansowanego. Pole, gdzie można wpisywać zapytania do wyszukiwania prostego, znajduje się w&nbsp;lewym górnym rogu ekranu (podłużne pole z&nbsp;ikoną lupy po prawej stronie). Szukanie proste jest elementem menu górnego, z którego można korzystać znajdując się w&nbsp;dowolnym miejscu aplikacji.<p>Ekran podzielony jest na dwa panele. W&nbsp;lewym panelu na ekranie Szukaj znajdują się pola do wpisywania zapytań dla szukania zaawansowanego. Wyniki wyszukiwania (zarówno prostego, jak i&nbsp;zaawansowanego) wyświetlają się w&nbsp;prawym panelu.</body></html>");
                try
                {
                    Rectangle r = t.modelToView(t.getDocument().getLength());
                    t.setPreferredSize(new Dimension(t.getSize().width, r.y + r.height));
                } catch (Exception e)
                {
                }
                topic = "Wyszukiwanie proste i zaawansowane";
                ((JLabel)panel.getComponent(0)).setText(topic);
                break;
            }
            case 10:
            {
                JTextPane t = (JTextPane)tab.getPanel().getComponent(0);
                t.setText("<html><body style=\"font-family: Trebuchet MS ; font-size: 11\">Szukanie zaawansowane pozwala na przeprowadzenie kwerendy według autora artykułu, tytułu publikacji, roku wydania danego numeru czasopisma, konkretnego tomu i&nbsp;zeszytu.<p>Po prawej stronie poszczególnych pól znajdują się przyciski, które określają, czy zawartość danego pola musi się znajdować w&nbsp;opisie bibliograficznym szukanej publikacji. Przycisk może przyjąć wartość \"i\" (zapytanie musi być obecne w&nbsp;wyniku) oraz \"lub\" (zapytanie może, ale nie musi pojawić się w&nbsp;wynikach; opisy bibliograficzne zawierające więcej zapytań alternatywnych będą pojawiać się wyżej, tzn. na pierwszych pozycjach na liście wyników). Zmiana wartości przycisku następuje po jednokrotnym kliknięciu myszką.<p>Wyszukiwanie zaawansowane jest uruchamiane przez wciśnięcie przycisku \"Wykonaj\" u dołu lewego panelu lub naciśnięcie Enter. Wyszukiwanie proste uruchamiane jest tylko klawiszem Enter, gdy kursor znajduje się w polu wyszukiwania prostego w lewym górnym rogu ekranu. Po uruchomieniu wyszukiwania w prawym panelu otworzy się nowa zakładka zawierająca wyniki.</body></html>");
                try
                {
                    Rectangle r = t.modelToView(t.getDocument().getLength());
                    t.setPreferredSize(new Dimension(t.getSize().width, r.y + r.height));
                } catch (Exception e)
                {
                }
                topic = "Panel lewy";
                ((JLabel)panel.getComponent(0)).setText(topic);
                break;
            }
            case 11:
            {
                JTextPane t = (JTextPane)tab.getPanel().getComponent(0);
                t.setText("<html><body style=\"font-family: Trebuchet MS ; font-size: 11\">Zapytanie może składać się z jednego lub więcej słów, można też wykorzystywać jednocześnie kilka pól (np. tytuł i autor). Wyszukiwarka ignoruje słowa składające się z jednego lub dwóch znaków, takie jak \"i\", \"na\", \"do\". Jeśli wyszukiwanie zwróciło zbyt dużo wyników, można zmniejszyć ich liczbę przeszukując tylko wyniki poprzedniej kwerendy. W tym celu należy zaznaczyć opcję \"Wyszukaj w wynikach\", wprowadzić nowe zapytania i&nbsp;ponowić wyszukiwanie.<p>Przy formułowaniu zapytań w&nbsp;wyszukiwaniu prostym oraz zaawansowanym można posługiwać się znakami specjalnymi, cudzysłowem oraz operatorami logicznymi AND i&nbsp;OR (koniecznie wielkimi literami). Znaki specjalne można wstawiać na końcu lub w&nbsp;środku wyrazu.<p>• Gwiazdka oznacza dowolną ilość dowolnych znaków, np. po*nie odpowiada wynikom powstanie, pojednanie, podanie etc.<p>• Znak zapytania oznacza dowolny pojedynczy znak, np. zapytanie źródł? odpowiada wynikom źródło lub źródła, ale już nie słowu źródłowy. Można też użyć kilku znaków&nbsp;? np. po????nie odpowiada wynikom powstanie i&nbsp;Pomeranie.<p>• Cudzysłów. Umieszczenie zapytania w&nbsp;cudzysłowach powoduje, że wszystkie słowa w&nbsp;nich zawarte są traktowane jako jedno wyrażenie. Na przykład zapytania sztuki polskiej i&nbsp;polskiej sztuki zwrócą taką samą liczbę wyników (wszystkie artykuły zawierające w&nbsp;opisie słowo sztuki lub słowo polskiej). Natomiast zapytania \"sztuki polskiej\" i&nbsp;\"polskiej sztuki\" zwrócą dwa odmienne zestawy wyników (te artykuły, które w&nbsp;opisie zawierają obydwa słowa umieszczone obok siebie w&nbsp;podanej kolejności).<p>• Operatory logiczne. Operator AND oznacza, że wyrazy, które on łączy w&nbsp;zapytaniu, muszą występować jednocześnie w&nbsp;wyniku zwracanym przez program. Operator OR oznacza, że wystarczy tylko jeden z&nbsp;dwóch wyrazów, aby wynik był poprawny. W&nbsp;jednym zapytaniu można wykorzystać operatory wielokrotnie oraz łączyć poszczególne części zapytania nawiasami.</body></html>");
                try
                {
                    Rectangle r = t.modelToView(t.getDocument().getLength());
                    t.setPreferredSize(new Dimension(t.getSize().width, r.y + r.height));
                } catch (Exception e)
                {
                }
                topic = "Konstruowanie kwerend";
                ((JLabel)panel.getComponent(0)).setText(topic);
                break;
            }
            case 12:
            {
                JTextPane t = (JTextPane)tab.getPanel().getComponent(0);
                t.setText("<html><body style=\"font-family: Trebuchet MS ; font-size: 11\">Wyniki domyślnie wyświetlają się w&nbsp;zapisie skróconym zawierającym tytuł publikacji (skrócony do szerokości pola), imię i&nbsp;nazwisko autora oraz rok opublikowania w&nbsp;Przeglądzie Historycznym. Pełny opis bibliograficzny pojawia się po kliknięciu na dowolny element zapisu skróconego. Kliknięcie na rozwinięty (pełny) opis bibliograficzny spowoduje powrót do opisu skróconego. Listę wyników można przewijać za pomocą suwaka umieszczonego po prawej stronie ekranu.<p>• Elementy opisu bibliograficznego: pełny tytuł, tytuł alternatywny (w&nbsp;języku obcym), autora lub autorów, rok opublikowania oraz numer tomu, numer zeszytu i&nbsp;strony.<p>• Elementy opisu fizycznego: [numer tomu]&nbsp;:&nbsp;[numer zeszytu] (&nbsp;[rok wydania]&nbsp;) s.&nbsp;[numery stron], a&nbsp;zatem opis 72&nbsp;:&nbsp;1 (1981) s.&nbsp;<nobr>30-40</nobr> oznacza - tom&nbsp;72 za 1981&nbsp;rok, zeszyt&nbsp;1, strony <nobr>30-40</nobr>.</body></html>");
                try
                {
                    Rectangle r = t.modelToView(t.getDocument().getLength());
                    t.setPreferredSize(new Dimension(t.getSize().width, r.y + r.height));
                } catch (Exception e)
                {
                }
                topic = "Panel prawy";
                ((JLabel)panel.getComponent(0)).setText(topic);
                break;
            }
            case 13:
            {
                JTextPane t = (JTextPane)tab.getPanel().getComponent(0);
                t.setText("<html><body style=\"font-family: Trebuchet MS ; font-size: 11\">Wyniki kolejnych wyszukiwań wyświetlają się w&nbsp;nowych zakładkach. Każda zawiera zestaw wyników odpowiadający jednemu z&nbsp;zapytań wprowadzonych przez użytkownika i&nbsp;ma swój tytuł utworzony z&nbsp;pierwszego wyrazu użytego w zapytaniu. Klikając na zakładki użytkownik może przeglądać wyniki swoich poprzednich kwerend.<p>Liczba zakładek ograniczona jest do pięciu. Gdy użytkownik wykona wyszukiwanie przy pięciu otwartych zakładkach, program poprosi o&nbsp;zamknięcie jednej jednej z&nbsp;nich, zanim zrealizuje wyszukiwanie. Zakładkę można zamknąć klikając na&nbsp;X w&nbsp;prawej części nagłówka zakładki.</body></html>");
                try
                {
                    Rectangle r = t.modelToView(t.getDocument().getLength());
                    t.setPreferredSize(new Dimension(t.getSize().width, r.y + r.height));
                } catch (Exception e)
                {
                }
                topic = "Zakładki";
                ((JLabel)panel.getComponent(0)).setText(topic);
                break;
            }
            case 14:
            {
                JTextPane t = (JTextPane)tab.getPanel().getComponent(0);
                t.setText("<html><body style=\"font-family: Trebuchet MS ; font-size: 11\">U&nbsp;dołu każdego z&nbsp;wyników, pod opisem fizycznym, znajduje się opcja \"Zaznacz\". Umożliwia ona wybieranie wyników (wewnątrz kwadratu pojawi się znak&nbsp;X), które zostaną poddane operacjom dostępnym w&nbsp;dolnej części prawego panelu. Dolne menu w&nbsp;prawym panelu zawiera opcje: \"Zaznacz wszystko\", \"Odznacz wszystko\", \"Do schowka\", \"Eksportuj\" oraz \"Drukuj\".<p>• Zaznacz wszystko - wybranie wszystkich wyników z&nbsp;danej zakładki.<p>• Odznacz wszystko - zlikwidowanie zaznaczenia we wszystkich wynikach w&nbsp;aktywnej (otwartej) zakładce.<p>• Do schowka - przeniesienie zaznaczonych wyników do Schowka.<p>• Eksportuj - zapisywanie wyników kwerendy. Zaznaczone pozycje z&nbsp;prawego panelu zostaną zapisane do pliku na dysku lokalnym i&nbsp;będzie można z&nbsp;nich korzystać po zamknięciu programu. Po wybraniu tej opcji wyświetli się okno dialogowe. Należy w&nbsp;nim wybrać jeden z&nbsp;dostępnych sposobów zapisu bibliograficznego oraz format pliku, do którego wyniki zostaną zapisane.<p>Dostępne są sposoby zapisu według Przeglądu Historycznego, Kwartalnika Historycznego oraz według Biblioteki Narodowej. Rekordy bibliograficzne można zapisywać w&nbsp;postaci:<p>.txt - zwykłego tekstu (z&nbsp;kodowaniem znaków <nobr>UTF-8</nobr>).<p>.rtf - tekstu sformatowanego (Rich Text Format). Pliki tekstowe są otwierane za pomocą edytora tekstowego właściwego dla używanego systemu operacyjnego.<p>.odt - dokumentu tekstowego Open Office. Pliki ODT można odczytać np. za pomocą pakietu biurowego OpenOffice.<p>.pdf - przenośnego formatu przeznaczonego do druku (Portable Document Format). Do otwarcia plików PDF można wykorzystać czytnik Adobe Reader.<p>Po wybraniu sposobu zapisu i&nbsp;formatu pliku użytkownik zostanie poproszony o&nbsp;wybranie miejsca na dysku, w&nbsp;którym plik zostanie zapisany (można wybrać dowolny katalog), oraz o&nbsp;nadanie nazwy plikowi (należy ją wpisać w&nbsp;polu File Name - Nazwa Pliku). Zapis zostanie wykonany po kliknięciu przycisku Save/Zapisz. Aby zrezygnować z&nbsp;zapisu należy wybrać Cancel/Anuluj.<p>• Drukuj - pozwala na wybranie sposobu zapisu bibliograficznego, a&nbsp;następnie spowoduje wywołanie systemowej drukarki.<p>Funkcje \"Eksportuj\" i&nbsp;\"Drukuj\" warto wykorzystywać zwłaszcza w&nbsp;połączeniu ze Schowkiem. Użytkownik może przenosić do Schowka wyniki kolejnych wyszukiwań tworząc w&nbsp;ten sposób bardziej skomplikowane kwerendy, a&nbsp;następnie zachować je za pomocą eksportu do pliku lub poprzez wydrukowanie.</body></html>");
                try
                {
                    Rectangle r = t.modelToView(t.getDocument().getLength());
                    t.setPreferredSize(new Dimension(t.getSize().width, r.y + r.height));
                } catch (Exception e)
                {
                }
                topic = "Menu dolne";
                ((JLabel)panel.getComponent(0)).setText(topic);
                break;
            }
            case 15:
            {
                JTextPane t = (JTextPane)tab.getPanel().getComponent(0);
                t.setText("<html><body style=\"font-family: Trebuchet MS ; font-size: 11\">Ekran Przeglądaj służy do przeszukiwania struktury tomów i&nbsp;zeszytów Przeglądu Historycznego.<p><b>Panel lewy</b><br>W&nbsp;lewym panelu wyświetlone są poszczególne tomy i&nbsp;zeszyty ułożone w&nbsp;kolejności chronologicznej.<p>• W&nbsp;lewej, węższej kolumnie znajduje się lista, na której można wybrać rok publikacji. Nie są wymienione wszystkie lata wydawania, lecz co dziesiąty rok licząc od 1905.<p>• W&nbsp;prawej, szerszej kolumnie znajduje się kompletna lista tomów i&nbsp;numerów Przeglądu Historycznego (najstarsze tomy wymienione są u&nbsp;góry kolumny). Pierwsza liczba od lewej oznacza numer tomu, obok niej umieszczony jest rok wydania tego tomu, a&nbsp;poniżej znajduje się lista zeszytów (numery zeszytów oddzielone są pionowymi kreskami). Listę w&nbsp;prawej kolumnie można przewijać za pomocą suwaka umieszczonego pomiędzy kolumnami. Kliknięcie na rok w&nbsp;lewej kolumnie spowoduje przewinięcie listy do tomu wydanego w&nbsp;tym roku.<p><b>Panel prawy</b><br>Kliknięcie na dowolny element w&nbsp;prawej kolumnie powoduje wyświetlenie w&nbsp;prawym panelu listy publikacji zawartych w&nbsp;tym elemencie. W&nbsp;dolnym menu prawego panelu dostępne są takie same opcje jak na ekranie Szukaj.<p>• Po kliknięciu na numer zeszytu wyświetlone zostaną wszystkie artykuły, recenzje oraz inne rodzaje publikacji, które zostały wydrukowane w&nbsp;tym zeszycie. Publikacje zostaną uszeregowane w&nbsp;kolejności według numerów stron.<p>• Jeśli wybrany zostanie tom a&nbsp;nie zeszyt, to publikacje ułożą się w&nbsp;ramach poszczególnych zeszytów według numerów stron, czyli najpierw wszystkie artykuły z&nbsp;pierwszego zeszytu wybranego tomu, potem wszystkie artykuły z&nbsp;zeszytu drugiego itd.<p>• Wybranie roku spowoduje wyświetlenie wszystkich publikacji z&nbsp;tego roku, a&nbsp;więc jeśli jednego roku wydane zostały dwa tomy, to zawartość obydwu pojawi się w&nbsp;prawym panelu (najpierw wszystkie publikacje z&nbsp;wcześniejszego tomu, potem wszystkie z&nbsp;późniejszego).</body></html>");
                try
                {
                    Rectangle r = t.modelToView(t.getDocument().getLength());
                    t.setPreferredSize(new Dimension(t.getSize().width, r.y + r.height));
                } catch (Exception e)
                {
                }
                topic = "Ekran Przeglądaj";
                ((JLabel)panel.getComponent(0)).setText(topic);
                break;
            }
            case 16:
            {
                JTextPane t = (JTextPane)tab.getPanel().getComponent(0);
                t.setText("<html><body style=\"font-family: Trebuchet MS ; font-size: 11\">Ekran Autorzy pozwala na przeglądanie publikacji poszczególnych autorów. Ekran zbudowany jest podobnie jak ekran Przeglądaj.<p><b>Panel lewy</b><br>W&nbsp;lewym panelu znajduje się lista autorów podzielona na dwie kolumny. Kliknięcie na literę w&nbsp;lewej kolumnie spowoduje wyświetlenie nazwisk autorów zaczynających się na tą literę w&nbsp;prawej kolumnie. W&nbsp;lewej kolumnie nie ma liter ze znakami diakrytycznymi. Aby uzyskać dostęp do nazwisk autorów zaczynających się od takich liter należy wybierać odpowiednie litery bez znaków diakrytycznych - ich nazwiska umieszczone są na końcu listy. Na przykład szukając autorki Śliwowskiej należy przejść na koniec listy pod literą S.<p><b>Panel prawy</b><br>Po wybraniu nazwiska autora program wyświetli jego publikacje w&nbsp;kolejności chronologicznej w&nbsp;prawym panelu. Wygląd prawego panelu jest taki sam jak w&nbsp;ekranie Szukaj i&nbsp;ekranie Przeglądaj (z&nbsp;tym wyjątkiem, że nie ma w&nbsp;nim zakładek jak w&nbsp;Szukaj). Dolne menu w&nbsp;prawym panelu jest identyczne jak w&nbsp;innych ekranach. Zawartość prawego panelu można przewijać suwakiem umieszczonym po prawej stronie.</body></html>");
                try
                {
                    Rectangle r = t.modelToView(t.getDocument().getLength());
                    t.setPreferredSize(new Dimension(t.getSize().width, r.y + r.height));
                } catch (Exception e)
                {
                }
                topic = "Ekran Autorzy";
                ((JLabel)panel.getComponent(0)).setText(topic);
                break;
            }
            case 17:
            {
                JTextPane t = (JTextPane)tab.getPanel().getComponent(0);
                t.setText("<html><body style=\"font-family: Trebuchet MS ; font-size: 11\">Do Schowka przenoszone są wyniki zaznaczone w&nbsp;ekranach Szukaj, Przeglądaj lub Autorzy. Aby zaznaczyć wybrane wyniki należy kliknąć \"zaznacz\" pod danym opisem bibliograficznym. Aby zaznaczyć wszystkie wyświetlone w prawym panelu wyniki wystarczy użyć opcji \"Zaznacz wszystko\" w&nbsp;dolnym menu. Zaznaczone wyniki zostaną przeniesione do Schowka po kliknięciu opcji \"Do schowka\" w&nbsp;dolnym menu.<p>W&nbsp;Schowku znajdują się wyniki kwerend wykonanych od momentu uruchomienia aplikacji. Dodanie nowych wyników do Schowka nie kasuje tych dodanych wcześniej. Wszystkie będą wyświetlane obok siebie w prawym panelu Schowka. Zamknięcie aplikacji powoduje wyczyszczenie zawartości Schowka. Zapisane w&nbsp;Schowku wyniki można usuwać także w&nbsp;trakcie pracy za pomocą przycisku \"Usuń\". W&nbsp;celu zachowania wyników kwerend przeniesionych do Schowka należy skorzystać z&nbsp;opcji Eksportuj albo Drukuj.</body></html>");
                try
                {
                    Rectangle r = t.modelToView(t.getDocument().getLength());
                    t.setPreferredSize(new Dimension(t.getSize().width, r.y + r.height));
                } catch (Exception e)
                {
                }
                topic = "Ekran Schowek";
                ((JLabel)panel.getComponent(0)).setText(topic);
                break;
            }

        }
        undecorate(help);
        PHtextLink p = null;
        for (Component c : parentPanel.getComponents())
        {
            try
            {
                PHtextLink l = (PHtextLink) c;
                String t = l.getText();
                while (t.charAt(0) == ' ')
                {
                    t = t.substring(1);
                }
                if (t.compareTo(topic) == 0)
                {
                    p = l;
                    break;
                }
            }
            catch (Exception e)
            {
            }
        }
        decorate(p);
        help = p;
        if (date.compareTo(volume) != 0)
        {
            if (volume.compareTo("1") == 0)
            {
                help1 = !help1;
            } else if (volume.compareTo("2") == 0)
            {
                help2 = !help2;
            }
        }
        for (Component c : parentPanel.getComponents())
        {
            try
            {
                PHtextLink l = (PHtextLink) c;
                if (l.date.compareTo("1") == 0)
                {
                    l.setVisible(help1);
                } else if (l.date.compareTo("2") == 0) {
                    l.setVisible(help2);
                }
            }
            catch (Exception e)
            {
            }
        }
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                tab.pHscroll1.getScroll().getVerticalScrollBar().setValueIsAdjusting(false);
                tab.pHscroll1.getScroll().getVerticalScrollBar().setValue(0);
                tab.pHscroll1.getScroll().getVerticalScrollBar().setValueIsAdjusting(true);
            }
        });
    }
}
