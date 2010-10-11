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

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFRenderer;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.sf.jooreports.templates.DocumentTemplate;
import net.sf.jooreports.templates.DocumentTemplateFactory;
import java.awt.print.PrinterException;

/**
 * Export and print class
 * @author paulx
 */
public class PHprint {

    public enum format { ph, kh, bn }; ///< Output format

    /**
     * Print articles as pdf
     * @param ll Articles
     * @param f Format
     * @param n Filename
     */
    public static void printPdf(LinkedList<phArt> ll, format f, String n)
    {
        Document doc = new Document(PageSize.A4, 50, 50, 50, 50);
        try
        {
            PdfWriter pdf = PdfWriter.getInstance(doc, new FileOutputStream(n));
            doc.addSubject("Przegląd Historyczny");
            doc.open();
            BaseFont rr = BaseFont.createFont("FreeSerif.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            BaseFont ii = BaseFont.createFont("FreeSerifItalic.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font r = new Font(rr);
            Font i = new Font(ii);
            for (phArt a : ll)
            {
                Phrase p = new Phrase();
                for (articleEntry e : formatArticle(a, f))
                {
                    switch (e.getStyle())
                    {
                        case normal:
                        {
                            p.add(new Chunk(e.getString(), r));
                            break;
                        }
                        case italic:
                        {
                            p.add(new Chunk(e.getString(), i));
                            break;
                        }
                    }
                }
                doc.add(new Paragraph(p));
                doc.add(new Paragraph("\n"));
            }
            doc.add(new Paragraph(" \n"));
        }
        catch (Exception e)
        {
        }
        doc.close();
    }

    /**
     * Print articles as odt
     * @param ll Articles
     * @param f Format
     * @param n Filename
     */
    public static void printOdt(LinkedList<phArt> ll, format f, String n)
    {
        try
        {
            Map model = new HashMap();
            List l = new ArrayList();
            for (phArt p : ll)
            {
                int x = 0;
                Map es = new HashMap();
                for (articleEntry e : formatArticle(p, f))
                {
                    es.put("f" + String.valueOf(x++), e.getString());
                }
                while (es.size() < 12)
                {
                    es.put("f" + String.valueOf(x++), "");
                }
                l.add(es);
            }
            model.put("items", l);
            DocumentTemplateFactory dtf = new DocumentTemplateFactory();
            DocumentTemplate t = dtf.getTemplate(new File("odt.tpl"));
            t.createDocument(model, new FileOutputStream(n));
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Print articles as txt
     * @param ll Articles
     * @param f Format
     * @param n Filename
     */
    public static void printTxt(LinkedList<phArt> ll, format f, String n)
    {
        String old = null;
        try
        {
            old = System.getProperty("file.encoding");
            System.setProperty("file.encoding", "UTF8");
        }
        catch (Exception ex)
        {
        }
        try
        {
            FileOutputStream file = new FileOutputStream(n, false);
            OutputStreamWriter out = new OutputStreamWriter(file, "UTF8");
            for (phArt p : ll)
            {
                for (articleEntry a : formatArticle(p, f))
                {
                    out.write(a.getString());
                }
                out.write("\r\n");
            }
            out.flush();
            out.close();
            System.setProperty("file.encoding", old);
        }
        catch (Exception e)
        {
        }
    }

    /**
     * Print articles as rtf
     * @param ll Articles
     * @param f Format
     * @param n Filename
     */
    public static void printRtf(LinkedList<phArt> ll, format f, String n)
    {
        try
        {
            FileWriter out = new FileWriter(n, false);
            out.write("{\\rtf1\\ansi\\deff0\r\n");
            for (phArt p : ll)
            {
                out.write("\\par ");
                for (articleEntry a : formatArticle(p, f))
                {
                    if (a.getStyle() == style.italic)
                    {
                        out.write("{\\i ");
                    }
                    for (int i = 0; i < a.getString().length(); i++)
                    {
                        if ((int)a.getString().charAt(i) < 128)
                        {
                            out.write(a.getString().charAt(i));
                        } else {
                            out.write("\\u" + String.valueOf((int)a.getString().charAt(i)) + "?");
                        }
                    }
                    if (a.getStyle() == style.italic)
                    {
                        out.write("}");
                    }
                }
                out.write("\r\n\\par\r\n");
            }
            out.write("}\r\n");
            out.flush();
            out.close();
        }
        catch (Exception e)
        {
        }
    }

    /**
     * Print articles on printer
     * @param ll Articles
     * @param f Format
     * @return Print state
     */
    public static boolean print(LinkedList<phArt> ll, format f)
    {
        boolean ret = false;
        try
        {
            File tmp = File.createTempFile("ph100", ".pdf");
            String temp = tmp.getAbsolutePath();
            tmp.delete();
            printPdf(ll, f, temp);
            FileInputStream fis = new FileInputStream(temp);
            FileChannel fc = fis.getChannel();
            ByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            PDFFile pdfFile = new PDFFile(bb);
            PDFPagePrint pages = new PDFPagePrint(pdfFile);
            PrinterJob pjob = PrinterJob.getPrinterJob();
            Paper paper = new Paper();
            paper.setSize(595, 842);
            paper.setImageableArea(0, 0, 595, 842);
            PageFormat pf = pjob.defaultPage();
            pf.setPaper(paper);
            pjob.setJobName("Przegląd Historyczny");
            Book book = new Book();
            book.append(pages, pf, pdfFile.getNumPages());
            pjob.setPageable(book);
            if (pjob.printDialog())
            {
                pjob.print();
                ret = true;
            }
            tmp = new File(temp);
            tmp.delete();
        }
        catch (Exception e)
        {
        }
        return ret;
    }

    /**
     * Format article
     * @param p Article
     * @param f Format
     * @return Formatted article
     */
    static private LinkedList<articleEntry> formatArticle(phArt p, format f)
    {
        LinkedList<articleEntry> ll = new LinkedList<articleEntry>();
        String authors = DLelement.getArticleShortAuthors(p.id, formatTitle(p.tytul));
        String[] title = formatTitle(p.tytul).split("\\[recenzja");
        switch(f)
        {
            case ph:
            {
                ll.add(new articleEntry(authors, style.normal));
                if (authors.length() > 0)
                {
                    ll.add(new articleEntry(", ", style.normal));
                } else {
                    ll.add(new articleEntry("", style.normal));
                }
                ll.add(new articleEntry(title[0], style.italic));
                if (title.length > 1)
                {
                    ll.add(new articleEntry("[recenzja" + title[1], style.normal));
                }
                ll.add(new articleEntry(", PH, t. ", style.normal));
                ll.add(new articleEntry(romanNum(p.tom), style.normal));
                ll.add(new articleEntry(", ", style.normal));
                ll.add(new articleEntry(p.rok, style.normal));
                if (p.numer.compareTo("") != 0)
                {
                    ll.add(new articleEntry(", z. ", style.normal));
                    ll.add(new articleEntry(p.numer, style.normal));
                }
                if (p.opis_f != null && p.opis_f.compareTo("") != 0)
                {
                    ll.add(new articleEntry(", ", style.normal));
                    ll.add(new articleEntry(cutDescription(p.opis_f), style.normal));
                } else {
                    ll.add(new articleEntry(".", style.normal));
                }
                break;
            }
            case kh:
            {
                ll.add(new articleEntry(authors, style.normal));
                if (authors.length() > 0)
                {
                    ll.add(new articleEntry(", ", style.normal));
                } else {
                    ll.add(new articleEntry("", style.normal));
                }
                ll.add(new articleEntry(title[0], style.italic));
                if (title.length > 1)
                {
                    ll.add(new articleEntry("[recenzja" + title[1], style.normal));
                }
                ll.add(new articleEntry(", PH ", style.normal));
                ll.add(new articleEntry(p.tom, style.normal));
                ll.add(new articleEntry(", ", style.normal));
                ll.add(new articleEntry(p.rok, style.normal));
                if (p.numer.compareTo("") != 0)
                {
                    ll.add(new articleEntry(", ", style.normal));
                    ll.add(new articleEntry(p.numer, style.normal));
                }
                if (p.opis_f != null && p.opis_f.compareTo("") != 0)
                {
                    ll.add(new articleEntry(", ", style.normal));
                    ll.add(new articleEntry(cutDescription(p.opis_f), style.normal));
                } else {
                    ll.add(new articleEntry(".", style.normal));
                }
                break;
            }
            case bn:
            {
                ll.add(new articleEntry(authors, style.normal));
                if (authors.length() > 0)
                {
                    ll.add(new articleEntry(", ", style.normal));
                } else {
                    ll.add(new articleEntry("", style.normal));
                }
                ll.add(new articleEntry(title[0], style.italic));
                if (title.length > 1)
                {
                    ll.add(new articleEntry("[recenzja" + title[1], style.normal));
                }
                ll.add(new articleEntry(", \"Przegląd Historyczny\" ", style.normal));
                ll.add(new articleEntry(p.rok, style.normal));
                if (p.numer.compareTo("") != 0)
                {
                    ll.add(new articleEntry(", nr ", style.normal));
                    ll.add(new articleEntry(p.numer, style.normal));
                }
                if (p.opis_f != null && p.opis_f.compareTo("") != 0)
                {
                    ll.add(new articleEntry(", ", style.normal));
                    ll.add(new articleEntry(cutDescription(p.opis_f), style.normal));
                } else {
                    ll.add(new articleEntry(".", style.normal));
                }
                break;
            }
        }
        return ll;
    }

    /**
     * Format title
     * @param title Title to format
     * @return Formatted title
     */
    static private String formatTitle(String title)
    {
        int last = title.lastIndexOf("/");
        if (last == -1)
        {
            last = title.length() - 1;
        }
        while (last > 0 && (title.charAt(last) == '/' || title.charAt(last) == ' ' || title.charAt(last) == '.' || title.charAt(last) == ',' || title.charAt(last) == ';'))
        {
            last--;
        }
        return title.substring(0, last + 1);
    }

    /**
     * Change arabic number to roman
     * @param num Arabic number
     * @return Roman number
     */
    static private String romanNum(String num)
    {
        int n;
        String res = "";
        try
        {
            n = Integer.parseInt(num);
        }
        catch (NumberFormatException e)
        {
            return num;
        }
        while (String.valueOf(n).length() > 3)
        {
            res += "M";
            n -= 1000;
        }
        while (n != 0)
        {
            String v = String.valueOf(n);
            switch (v.length())
            {
                case 3:
                {
                    if (v.charAt(0) == '9')
                    {
                        res += "CM";
                        n -= 900;
                    } else if (v.charAt(0) >= '5') {
                        res += "D";
                        n -= 500;
                    } else if (v.charAt(0) == '4') {
                        res += "CD";
                        n -= 400;
                    } else {
                        res += "C";
                        n -= 100;
                    }
                    break;
                }
                case 2:
                {
                    if (v.charAt(0) == '9')
                    {
                        res += "XC";
                        n -= 90;
                    } else if (v.charAt(0) >= '5') {
                        res += "L";
                        n -= 50;
                    } else if (v.charAt(0) == '4') {
                        res += "XL";
                        n -= 40;
                    } else {
                        res += "X";
                        n -= 10;
                    }
                    break;
                }
                case 1:
                {
                    if (v.charAt(0) == '9')
                    {
                        res += "IX";
                        n -= 9;
                    } else if (v.charAt(0) >= '5') {
                        res += "V";
                        n -= 5;
                    } else if (v.charAt(0) == '4') {
                        res += "IV";
                        n -= 4;
                    } else {
                        res += "I";
                        n -= 1;
                    }
                    break;
                }
            }
        }
        return res;
    }

    /**
     * Cut description to shorter form
     * @param d Description
     * @return Shorter form of description
     */
    static private String cutDescription(String d)
    {
        if (d == null)
        {
            return "";
        }
        if (d.length() > 8)
        {
            int n = 8;
            while (n < d.length() && ((d.charAt(n) >= '0' && d.charAt(n) <= '9') || d.charAt(n) == ' ' || d.charAt(n) == ',' || d.charAt(n) == '-'))
            {
                n++;
            }
            while (n < d.length() && n > 8 && (d.charAt(n) < '0' || d.charAt(n) > '9'))
            {
                n--;
            }
            if (n < d.length())
            {
                return d.substring(6, n + 1) + '.';
            }
        }
        return d;
    }

    private enum style {normal, italic}; ///< Text style

    /**
     * Part of text
     * @author paulx
     */
    static private class articleEntry
    {

        private String str; ///< Text
        private style sty; ///< Style

        /**
         * Constructor
         * @param s Text
         * @param st Style
         */
        public articleEntry(String s, style st)
        {
            str = s;
            sty = st;
        }

        /**
         * Gets text
         * @return Text
         */
        public String getString()
        {
            return str;
        }

        /**
         * Gets style
         * @return Style
         */
        public style getStyle()
        {
            return sty;
        }

    }

    /**
     * Print pdf class
     * @author paulx
     */
    static private class PDFPagePrint implements Printable
    {
        private PDFFile file; ///< File to print

        /**
         * Constructor
         * @param f File to print
         */
        public PDFPagePrint(PDFFile f)
        {
            file = f;
        }

        @Override
        public int print(Graphics g, PageFormat format, int index) throws PrinterException
        {
            if (index >= 0 && index < file.getNumPages())
            {
                Graphics2D g2 = (Graphics2D)g;
                PDFPage page = file.getPage(index + 1);

                Rectangle imgbounds = new Rectangle((int)format.getImageableX(), (int)format.getImageableY(), (int)page.getWidth(), (int)page.getHeight());

                PDFRenderer pgs = new PDFRenderer(page, g2, imgbounds, null, null);
                try
                {
                    page.waitForFinish();
                    pgs.run();
                    pgs.waitForFinish();
                }
                catch (Exception e)
                {
                }
                return PAGE_EXISTS;
            }
            return NO_SUCH_PAGE;
        }
    }
}
