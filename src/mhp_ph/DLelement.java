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

import java.io.File;
import java.io.StringReader;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author artur.szymanski
 */
public class DLelement
{
    LinkedList<String> err = new LinkedList<String>();

    public LinkedList<phArt> generalSearch(String sTerm) throws Exception
    {
        LinkedList<phArt> lArt = new LinkedList<phArt>();
        Connection conn = null;

        try
        {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:ph.db");

            PreparedStatement p = conn.prepareStatement("select id_e, tytul, tytul2, autor, opis_f, tom, numer, rok from elementy where id_e = ?;");

            Analyzer a = new StandardAnalyzer(Version.LUCENE_29);
            Directory d = FSDirectory.open(new File("./IND").getCanonicalFile());

            Query q = new QueryParser(Version.LUCENE_29, "all", a).parse(sTerm);

            IndexSearcher is = new IndexSearcher(d);
            TopScoreDocCollector sc = TopScoreDocCollector.create(10000, true);

            is.search(q, sc);

            ScoreDoc[] sd = sc.topDocs().scoreDocs;


            for(int i=0;i<sd.length;++i) {
                try
                {
                    int docId = sd[i].doc;
                    org.apache.lucene.document.Document d1 = is.doc(docId);
                    //System.out.println(sd[i].score + ". " + d1.get("tytul"));
                    String id = d1.get("id_e");
                    p.setInt(1, Integer.parseInt(id.substring(0, id.length()-6)));
                    ResultSet rs = p.executeQuery();
                    if (rs.next())
                    {
                        phArt phTmp = new phArt(rs.getString("tytul"), rs.getString("tytul2"), rs.getString("autor"), rs.getString("opis_f"), rs.getString("numer"), rs.getString("tom"), rs.getString("rok"), sd[i].score, rs.getInt("id_e"));
                        lArt.add(phTmp);
                    }
                    rs.close();
                }
                catch (Exception e)
                {
                }
            }
        }
        catch (Exception e)
        {
        }
        finally
        {
            try
            {
                conn.close();
            }
            catch (Exception e)
            {
            }
        }

        return lArt;
    }

    /**
     * Detailed search
     * @param tytul Title
     * @param opisF Description - unused
     * @param autor Author
     * @param rok Year
     * @param tom Volume
     * @param numer Number
     * @param andTitle And/Or state for title
     * @param andAuthor And/Or state for author
     * @param andYear And/Or state for year
     * @param andVolume And/Or state for volume
     * @param andNumber And/Or state for number
     * @return Articles
     * @throws Exception Exception
     */
    public LinkedList<phArt> detailedSearch(String tytul, String opisF, String autor, String rok, String tom, String numer, boolean andTitle, boolean andAuthor, boolean andYear, boolean andVolume, boolean andNumber)
            throws Exception
    {
        LinkedList<phArt> lArt = new LinkedList<phArt>();
        LinkedList<phArt> lArt2 = new LinkedList<phArt>();
        Connection conn = null;

        if (tytul.isEmpty() && autor.isEmpty() && (!rok.isEmpty() || !tom.isEmpty() || !numer.isEmpty()))
        {
            return yearVolumeNumberSearch(rok, tom, numer);
        }

        try
        {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:ph.db");

            PreparedStatement p = conn.prepareStatement("select id_e, tytul, tytul2, autor, opis_f, tom, numer, rok from elementy where id_e = ?;");

            Analyzer a = new StandardAnalyzer(Version.LUCENE_29);
            Directory d = FSDirectory.open(new File("./IND").getCanonicalFile());

            LinkedList<String> lF = new LinkedList<String>();
            LinkedList<String> lK = new LinkedList<String>();
            LinkedList<BooleanClause.Occur> lC = new LinkedList<BooleanClause.Occur>();
            if (tytul != null && tytul.compareTo("") != 0)
            {
                for (String title : tytul.split(" "))
                {
                    if (title.compareTo("") != 0 && title.length() > 2)
                    {
                        lF.add("tytul");
                        lK.add(title);
                        if (andTitle)
                        {
                            lC.add(BooleanClause.Occur.MUST);
                        } else {
                            lC.add(BooleanClause.Occur.SHOULD);
                        }
                    }
                }
            }
            if (autor != null && autor.compareTo("") != 0 && autor.length() > 2)
            {
                for (String author : autor.split(" "))
                {
                    if (author.compareTo("") != 0)
                    {
                        lF.add("autor");
                        lK.add(author);
                        if (andAuthor)
                        {
                            lC.add(BooleanClause.Occur.MUST);
                        } else {
                            lC.add(BooleanClause.Occur.SHOULD);
                        }
                    }
                }
            }

            String[] year = rok.split(" ");
            String[] volume = tom.split(" ");
            String[] number = numer.split(" ");

            String[] searchF = new String[lF.size()];
            lF.toArray(searchF);
            String[] searchK = new String[lK.size()];
            lK.toArray(searchK);
            BooleanClause.Occur[] searchC = new BooleanClause.Occur[lC.size()];
            lC.toArray(searchC);

            Query q = MultiFieldQueryParser.parse(Version.LUCENE_29, searchK, searchF, searchC, a);

            IndexSearcher is = new IndexSearcher(d);
            TopScoreDocCollector sc = TopScoreDocCollector.create(10000, true);

            is.search(q, sc);

            ScoreDoc[] sd = sc.topDocs().scoreDocs;

            for(int i=0;i<sd.length;++i) {
                int docId = sd[i].doc;
                org.apache.lucene.document.Document d1 = is.doc(docId);
                //System.out.println(sd[i].score + ". " + d1.get("tytul"));
                try
                {
                    String id = d1.get("id_e");
                    p.setInt(1, Integer.parseInt(id.substring(0, id.length()-6)));
                    ResultSet rs = p.executeQuery();
                    if (rs.next())
                    {
                        if (contains(volume, rs.getString("tom")) && contains(number, rs.getString("numer")) && containsYear(year, rs.getString("rok")))
                        {
                            phArt phTmp = new phArt(rs.getString("tytul"), rs.getString("tytul2"), rs.getString("autor"), rs.getString("opis_f"), rs.getString("numer"), rs.getString("tom"), rs.getString("rok"), sd[i].score, rs.getInt("id_e"));
                            lArt.add(phTmp);
                        } else if ((contains(volume, rs.getString("tom")) || contains(volume, rs.getString("tom")) == andVolume) && (contains(number, rs.getString("numer")) || contains(number, rs.getString("numer")) == andNumber) && (containsYear(year, rs.getString("rok")) || containsYear(year, rs.getString("rok")) == andYear)) {
                            phArt phTmp = new phArt(rs.getString("tytul"), rs.getString("tytul2"), rs.getString("autor"), rs.getString("opis_f"), rs.getString("numer"), rs.getString("tom"), rs.getString("rok"), sd[i].score, rs.getInt("id_e"));
                            lArt2.add(phTmp);
                        }

                    }
                    rs.close();
                }
                catch (Exception e)
                {
                }
            }
        }
        catch (Exception e)
        {
        }
        finally
        {
            try
            {
                conn.close();
            }
            catch (Exception e)
            {
            }
        }
        for (phArt p : lArt2)
        {
            lArt.add(p);
        }

        return lArt;
    }

    public void createIndex (String path) throws Exception
    {
        Analyzer a = new StandardAnalyzer(Version.LUCENE_29);
        Directory d = FSDirectory.open(new File(".\\IND").getCanonicalFile());
        IndexWriter iw = new IndexWriter(d, a, IndexWriter.MaxFieldLength.LIMITED);

        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:"+path);
        Statement stat = conn.createStatement();
        ResultSet rs = stat.executeQuery("select * from elementy;");
        try
        {
            while (rs.next()) {
                String opisF = "";
                if (rs.getString("opis_f") != null)
                    opisF = rs.getString("opis_f").substring(6);

                String tytul = rs.getString("tytul");
                String tytul2 = rs.getString("tytul2");
                if (tytul.lastIndexOf("/") > 1)
                {
                    tytul = tytul.substring(0, tytul.lastIndexOf("/") - 1);
                }
                if (tytul2.lastIndexOf("/") > 1)
                {
                    tytul2 = tytul2.substring(0, tytul2.lastIndexOf("/") - 1);
                }

                org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
                doc.add(new Field("tytul", tytul + " " + tytul2, Field.Store.YES, Field.Index.ANALYZED));
//                doc.add(new Field("opis_f", opisF, Field.Store.YES, Field.Index.ANALYZED));
                doc.add(new Field("autor", rs.getString("autor"), Field.Store.YES, Field.Index.ANALYZED));
//                doc.add(new Field("numer", rs.getString("numer").toString(), Field.Store.YES, Field.Index.ANALYZED));
//                doc.add(new Field("rok", rs.getString("rok").toString(), Field.Store.YES, Field.Index.ANALYZED));
//                doc.add(new Field("tom", rs.getString("tom").toString(), Field.Store.YES, Field.Index.ANALYZED));
                doc.add(new Field("id_e", rs.getString("id_e")+"xxx123", Field.Store.YES, Field.Index.ANALYZED));
                doc.add(new Field("all", opisF+" "+getArticleAuthor(conn, rs.getInt("id_e"))+" "+tytul+" "+tytul2+" "+rs.getString("numer")+" "+rs.getString("tom")+" "+rs.getString("rok"), Field.Store.YES, Field.Index.ANALYZED));
                iw.addDocument(doc);
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println(rs.getString("tytul"));
        }
        finally
        {
            rs.close();
            iw.close();
        }
    }

    public void createDB (String pE, String pY, String pV, String pN) throws Exception
    {
        Document docNo = new Document();
        Document docTom = new Document();
        Document docYr = new Document();
        Document docArt = new Document();
        SAXBuilder b = new SAXBuilder();
        int id_e = 1;
        int id_a = 1;

        DLelement dl = new DLelement();

        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:ph.db");
        Statement stat = conn.createStatement();
        stat.executeUpdate("drop table if exists elementy;");
        stat.executeUpdate("drop table if exists autorzy;");
        stat.executeUpdate("drop table if exists elementy_autorzy;");
        stat.executeUpdate("create table elementy (id_e integer, tytul, tytul2, autor, opis_f, numer, tom, rok, primary key(id_e));");
        stat.executeUpdate("create table autorzy (id_a integer, name, surname, firstname, primary key(id_a));");
        stat.executeUpdate("create table elementy_autorzy (id_e integer, id_a integer, foreign key(id_a) references autorzy(id_a), foreign key(id_e) references elementy(id_e));");

        try
        {
            docNo = b.build(new StringReader(dlXML.getContent(new File(pN))));
            docTom = b.build(new StringReader(dlXML.getContent(new File(pV))));
            docYr = b.build(new StringReader(dlXML.getContent(new File(pY))));
            docArt = b.build(new StringReader(dlXML.getContent(new File(pE))));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        PreparedStatement pStat1 = conn.prepareStatement("insert into elementy values (?, ?, ?, ?, ?, ?, ?, ?);");
        PreparedStatement pStat2 = conn.prepareStatement("insert into autorzy values (?, ?, ?, ?);");
        PreparedStatement pStat3 = conn.prepareStatement("insert into elementy_autorzy values (?, ?);");
        PreparedStatement pStat4 = conn.prepareStatement("select id_a from autorzy where name like ? and surname like ? and firstname like ?;");
        PreparedStatement pStat5 = conn.prepareStatement("update elementy set autor = ? where id_e = ?;");
        conn.setAutoCommit(true);

        for (Object e : docArt.getRootElement().getChildren())
        {
            Element article = null;
            Element number = null;
            Element volume = null;
            Element year = null;
            try
            {
                article = (Element)e;
                System.out.println("Artykul: "+article.getChildTextTrim("name"));
                number = getElementByRef(docNo, getElementRef(article));
                if (number == null)
                {
                    volume = getElementByRef(docTom, getElementRef(article));
                } else {
                    volume = getElementByRef(docTom, getElementRef(number));
                }
                year = getElementByRef(docYr, getElementRef(volume));
            }
            catch (Exception ex)
            {
                err.add(article.getChildTextTrim("name"));
                System.out.println("Błąd inicjalizacji");
                continue;
            }
            /*System.out.println("Artykul: "+article.getChildTextTrim("name")+" Numer: "+number.getChildTextTrim("name")+" Tom: "
                    +volume.getChildTextTrim("name")+" Rok: "+year.getChildTextTrim("name")+" Autorzy: "+getAuthors(article)
                    +" Opis fizyczny: "+getElementBioDesc(article));
            */


            pStat1.setInt(1, id_e);
            pStat1.setString(2, article.getChildTextTrim("name"));
            pStat1.setString(3, getSecondTitle(article));
            pStat1.setString(4, "");
            pStat1.setString(5, getElementBioDesc(article));
            if (number == null)
            {
                pStat1.setString(6, "");
            } else {
                pStat1.setString(6, number.getChildTextTrim("name"));
            }
            pStat1.setString(7, volume.getChildTextTrim("name"));
            pStat1.setString(8, year.getChildTextTrim("name"));
            
            pStat1.execute();

            id_a += getAuthors(article, id_e, id_a, pStat2, pStat3, pStat4);

            pStat5.setString(1, getArticleAuthor(conn, id_e));
            pStat5.setInt(2, id_e);
            pStat5.execute();

            id_e++;
        }
    }

    public static void showDB() throws Exception
    {
        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:ph.db");
        Statement stat = conn.createStatement();
        ResultSet rs = stat.executeQuery("select * from elementy;");
        while (rs.next()) {
          System.out.print("tytul = " + rs.getString("tytul"));
          System.out.print(" | opis_f = " + rs.getString("opis_f"));
          System.out.print(" | autor = " + getArticleAuthor(conn, rs.getInt("id_e")));
          System.out.print(" | numer = " + rs.getString("numer"));
          System.out.print(" | rok = " + rs.getString("rok"));
          System.out.print(" | tom = " + rs.getString("tom"));
          System.out.println();
        }
        rs.close();
        conn.close();
    }

    /**
     * Gets authors list which start with letter
     * @param l Start letter
     * @return Authors as map of name and ID
     */
    public static TreeMap<String, String> getAuthorsList(char l)
    {
        TreeMap<String, String> tm = new TreeMap<String, String>();
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try
        {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:ph.db");
            stat = conn.createStatement();
            String q = "select id_a, name, surname, firstname from autorzy where surname like \'" + String.valueOf(l) + "%\' or name like \'" + String.valueOf(l) + "%\'";
            if (l == 'A')
            {
                q += " or surname like \'" + String.valueOf('Ą') + "%\' or name like \'" + String.valueOf('Ą') + "%\'";
            }
            if (l == 'C')
            {
                q += " or surname like \'" + String.valueOf('Ć') + "%\' or name like \'" + String.valueOf('Ć') + "%\'";
            }
            if (l == 'E')
            {
                q += " or surname like \'" + String.valueOf('Ę') + "%\' or name like \'" + String.valueOf('Ę') + "%\'";
            }
            if (l == 'L')
            {
                q += " or surname like \'" + String.valueOf('Ł') + "%\' or name like \'" + String.valueOf('Ł') + "%\'";
            }
            if (l == 'N')
            {
                q += " or surname like \'" + String.valueOf('Ń') + "%\' or name like \'" + String.valueOf('Ń') + "%\'";
            }
            if (l == 'O')
            {
                q += " or surname like \'" + String.valueOf('Ó') + "%\' or name like \'" + String.valueOf('Ó') + "%\'";
            }
            if (l == 'S')
            {
                q += " or surname like \'" + String.valueOf('Ś') + "%\' or name like \'" + String.valueOf('Ś') + "%\'";
            }
            if (l == 'Z')
            {
                q += " or surname like \'" + String.valueOf('Ź') + "%\' or name like \'" + String.valueOf('Ź') + "%\'";
                q += " or surname like \'" + String.valueOf('Ż') + "%\' or name like \'" + String.valueOf('Ż') + "%\'";
            }
            rs = stat.executeQuery(q + ";");
            while (rs.next()) {
                String name;
                int id = rs.getInt("id_a");
                if (rs.getString("surname").isEmpty())
                {
                    name = rs.getString("name");
                } else {
                    if (rs.getString("firstname").isEmpty())
                    {
                        name = rs.getString("surname");
                    } else {
                        name = rs.getString("surname") + ", " + rs.getString("firstname");
                    }
                }
                if (name.charAt(0) == l || (name.charAt(0) == 'Ą' && l == 'A') || (name.charAt(0) == 'Ć' && l == 'C') || (name.charAt(0) == 'Ę' && l == 'E') || (name.charAt(0) == 'Ł' && l == 'L') || (name.charAt(0) == 'Ń' && l == 'N') || (name.charAt(0) == 'Ó' && l == 'O') || (name.charAt(0) == 'Ś' && l == 'S') || (name.charAt(0) == 'Ź' && l == 'Z') || (name.charAt(0) == 'Ż' && l == 'Z'))
                {
                    tm.put(name, String.valueOf(id));
                }
            }
        }
        catch (Exception e)
        {
        }
        finally
        {
            try
            {
                rs.close();
            }
            catch (Exception e)
            {
            }
            try
            {
                conn.close();
            }
            catch (Exception e)
            {
            }
        }
        return tm;
    }

    /**
     * Gets all years, volumes and numbers
     * @return List with array of year, volume and number
     */
    public static LinkedList<ArrayList<String>> getYearsList()
    {
        LinkedList<ArrayList<String>> ll = new LinkedList<ArrayList<String>>();
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try
        {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:ph.db");
            stat = conn.createStatement();
            rs = stat.executeQuery("select rok, tom, numer from elementy group by rok, tom, numer order by rok, tom, numer");
            while (rs.next()) {
                ArrayList<String> l = new ArrayList<String>();
                l.add(rs.getString("rok"));
                l.add(rs.getString("tom"));
                l.add(rs.getString("numer"));
                ll.add(l);
            }
        }
        catch (Exception e)
        {
        }
        finally
        {
            try
            {
                rs.close();
            }
            catch (Exception e)
            {
            }
            try
            {
                conn.close();
            }
            catch (Exception e)
            {
            }
        }
        return ll;
    }

    /**
     * Gets author articles
     * @param id_a Author ID
     * @return Articles
     */
    public static LinkedList<phArt> getAuthorArticles(int id_a)
    {
        LinkedList<phArt> ll = new LinkedList<phArt>();
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try
        {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:ph.db");
            stat = conn.createStatement();
            rs = stat.executeQuery("select e.id_e, e.tytul, e.tytul2, e.autor, e.opis_f, e.numer, e.tom, e.rok from elementy e, elementy_autorzy ea where ea.id_a = '" + id_a + "' and ea.id_e = e.id_e order by e.rok, e.tom, e.numer, e.opis_f;");
            while (rs.next()) {
                ll.add(new phArt(rs.getString("tytul"), rs.getString("tytul2"), rs.getString("autor"), rs.getString("opis_f"), rs.getString("numer"), rs.getString("tom"), rs.getString("rok"), 0, rs.getInt("id_e")));
            }
        }
        catch (Exception e)
        {
        }
        finally
        {
            try
            {
                rs.close();
            }
            catch (Exception e)
            {
            }
            try
            {
                conn.close();
            }
            catch (Exception e)
            {
            }
        }
        return ll;
    }

    /**
     * List volume articles
     * @param volume Volume
     * @return Articles
     */
    public static LinkedList<phArt> getVolumeArticles(String volume)
    {
        LinkedList<phArt> ll = new LinkedList<phArt>();
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try
        {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:ph.db");
            stat = conn.createStatement();
            rs = stat.executeQuery("select id_e, tytul, tytul2, autor, opis_f, numer, tom, rok from elementy where tom like \'" + volume + "\' order by rok, tom, numer, opis_f");
            while (rs.next()) {
                ll.add(new phArt(rs.getString("tytul"), rs.getString("tytul2"), rs.getString("autor"), rs.getString("opis_f"), rs.getString("numer"), rs.getString("tom"), rs.getString("rok"), 0, rs.getInt("id_e")));
            }
        }
        catch (Exception e)
        {
        }
        finally
        {
            try
            {
                rs.close();
            }
            catch (Exception e)
            {
            }
            try
            {
                conn.close();
            }
            catch (Exception e)
            {
            }
        }
        return ll;
    }

    /**
     * List number articles
     * @param volume Volume
     * @param number Number
     * @return Articles
     */
    public static LinkedList<phArt> getNumberArticles(String volume, String number)
    {
        LinkedList<phArt> ll = new LinkedList<phArt>();
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try
        {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:ph.db");
            stat = conn.createStatement();
            rs = stat.executeQuery("select id_e, tytul, tytul2, autor, opis_f, numer, tom, rok from elementy where tom like \'" + volume + "\' and numer like \'" + number + "\' order by rok, tom, numer, opis_f");
            while (rs.next()) {
                ll.add(new phArt(rs.getString("tytul"), rs.getString("tytul2"), rs.getString("autor"), rs.getString("opis_f"), rs.getString("numer"), rs.getString("tom"), rs.getString("rok"), 0, rs.getInt("id_e")));
            }
        }
        catch (Exception e)
        {
        }
        finally
        {
            try
            {
                rs.close();
            }
            catch (Exception e)
            {
            }
            try
            {
                conn.close();
            }
            catch (Exception e)
            {
            }
        }
        return ll;
    }

    /**
     * List year articles
     * @param year Year
     * @return Articles
     */
    public static LinkedList<phArt> getDateArticles(String year)
    {
        LinkedList<phArt> ll = new LinkedList<phArt>();
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try
        {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:ph.db");
            stat = conn.createStatement();
            rs = stat.executeQuery("select id_e, tytul, tytul2, autor, opis_f, numer, tom, rok from elementy where rok like \'" + year + "\' order by rok, tom, numer, opis_f");
            while (rs.next()) {
                ll.add(new phArt(rs.getString("tytul"), rs.getString("tytul2"), rs.getString("autor"), rs.getString("opis_f"), rs.getString("numer"), rs.getString("tom"), rs.getString("rok"), 0, rs.getInt("id_e")));
            }
        }
        catch (Exception e)
        {
        }
        finally
        {
            try
            {
                rs.close();
            }
            catch (Exception e)
            {
            }
            try
            {
                conn.close();
            }
            catch (Exception e)
            {
            }
        }
        return ll;
    }

    /**
     * List year, volume, number articles
     * @param rok Year
     * @param tom Volume
     * @param numer Number
     * @return Articles
     */
    public static LinkedList<phArt> yearVolumeNumberSearch(String rok, String tom, String numer)
    {
        LinkedList<phArt> ll = new LinkedList<phArt>();
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        String[] year = rok.split(" ");
        String[] volume = tom.split(" ");
        String[] number = numer.split(" ");
        try
        {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:ph.db");
            stat = conn.createStatement();
            rs = stat.executeQuery("select id_e, tytul, tytul2, autor, opis_f, numer, tom, rok from elementy order by rok, tom, numer, opis_f");
            while (rs.next()) {
                if (contains(volume, rs.getString("tom")) && contains(number, rs.getString("numer")) && containsYear(year, rs.getString("rok")))
                {
                    ll.add(new phArt(rs.getString("tytul"), rs.getString("tytul2"), rs.getString("autor"), rs.getString("opis_f"), rs.getString("numer"), rs.getString("tom"), rs.getString("rok"), 0, rs.getInt("id_e")));
                }
            }
        }
        catch (Exception e)
        {
        }
        finally
        {
            try
            {
                rs.close();
            }
            catch (Exception e)
            {
            }
            try
            {
                conn.close();
            }
            catch (Exception e)
            {
            }
        }
        return ll;
    }

    /**
     * Gets article authors
     * @param e Element
     * @param id_e Element ID
     * @param newId Next ID
     * @param pStmt1 Prepared statement 1
     * @param pStmt2 Prepared statement 2
     * @param pStmt3 Prepared statement 3
     * @return Number of authors added
     */
    private int getAuthors (Element e, int id_e, int newId, PreparedStatement pStmt1, PreparedStatement pStmt2, PreparedStatement pStmt3)
    {
        int added = 0;
        for (Object o1 : e.getChildren())
        {
            Element et1 = (Element)o1;
            if (et1.getName().compareTo("contributor") == 0)
            {
                String name = "";
                String surname = "";
                String firstname = "";
                name = et1.getAttributeValue("title");
                for (Object o2 : et1.getChildren())
                {
                    Element e2 = (Element)o2;
                    for (Object o3 : e2.getChildren())
                    {
                        Element e3 = (Element)o3;
                        if (e3.getName().compareTo("attribute") == 0 && e3.getAttributeValue("key").compareTo("person.surname") == 0)
                        {
                            surname = e3.getAttributeValue("value");
                        } else if (e3.getName().compareTo("attribute") == 0 && e3.getAttributeValue("key").compareTo("person.firstname") == 0)
                        {
                            firstname = e3.getAttributeValue("value");
                        }
                    }
                }
                if (name != null && name.compareTo("") != 0)
                {
                    try
                    {
                        int id;
                        if (surname != null && surname.compareTo("") != 0 && firstname != null && firstname.compareTo("") != 0)
                        {
                            pStmt3.setString(1, surname + ", " + firstname);
                        } else {
                            pStmt3.setString(1, name);
                        }
                        pStmt3.setString(2, surname);
                        pStmt3.setString(3, firstname);
                        ResultSet rs = pStmt3.executeQuery();
                        if (rs.next()) {
                            id = rs.getInt(1);
                        } else {
                            id = newId + ++added;
                            pStmt1.setInt(1, id);
                            if (surname != null && surname.compareTo("") != 0 && firstname != null && firstname.compareTo("") != 0)
                            {
                                pStmt1.setString(2, surname + ", " + firstname);
                            } else {
                                pStmt1.setString(2, name);
                            }
                            pStmt1.setString(3, surname);
                            pStmt1.setString(4, firstname);
                            pStmt1.execute();
                        }
                        pStmt2.setInt(1, id_e);
                        pStmt2.setInt(2, id);
                        pStmt2.execute();
                    }
                    catch (Exception ex)
                    {
                    }
                }
            }
        }
        return added;
    }

    /**
     * Gets article author
     * @param conn Connection
     * @param id_e Article ID
     * @return Author name
     */
    private static String getArticleAuthor(Connection conn, int id_e)
    {
        String author = "";
        try
        {
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery("select a.name, a.surname, a.firstname from elementy e, autorzy a, elementy_autorzy ea where e.id_e = " + id_e + " and e.id_e = ea.id_e and a.id_a = ea.id_a;");
            while (rs.next())
            {
                if (!author.isEmpty())
                {
                    author += "; ";
                }
                if (rs.getString(2).isEmpty())
                {
                    author += rs.getString(1);
                } else {
                    author += rs.getString(3) + " " + rs.getString(2);
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return author;
    }

    /**
     * Gets article short form authors without one listed in title
     * @param id_e Article ID
     * @param title Title
     * @return Authors
     */
    public static String getArticleShortAuthors(int id_e, String title)
    {
        String authors = "";
        String author = "";
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try
        {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:ph.db");
            stat = conn.createStatement();
            rs = stat.executeQuery("select a.name, a.surname, a.firstname from elementy e, autorzy a, elementy_autorzy ea where e.id_e = " + id_e + " and e.id_e = ea.id_e and a.id_a = ea.id_a;");
            while (rs.next())
            {
                if (rs.getString(2).isEmpty())
                {
                    author = rs.getString(1);
                } else if (rs.getString(3).isEmpty()) {
                    author = rs.getString(2);
                } else {
                    author = rs.getString(3) + " " + rs.getString(2);
                }
                if (title.contains(author))
                {
                    continue;
                }
                if (!authors.isEmpty())
                {
                    authors += ", ";
                }
                if (rs.getString(2).isEmpty())
                {
                    authors += rs.getString(1);
                } else if (rs.getString(3).isEmpty()) {
                    authors += rs.getString(2);
                } else {
                    for (String f : rs.getString(3).split(" "))
                    {
                        authors += f.substring(0, 1) + ". ";
                    }
                    authors += rs.getString(2);
                }
            }
        }
        catch (Exception e)
        {
        }
        finally
        {
            try
            {
                rs.close();
            }
            catch (Exception e)
            {
            }
            try
            {
                conn.close();
            }
            catch (Exception e)
            {
            }
        }
        return authors;
    }

    private Element getElementByRef (Document d, String s)
    {
        Element result = null;
        for (Object o : d.getRootElement().getChildren())
        {
            Element te = (Element)o;
            if (te.getName().compareTo("element") == 0)
            {
                if (te.getAttributeValue("id").compareTo(s) == 0)
                {
//                    System.out.println("znalazlem");
                    result = te;
                }
            }
        }
        return result;
    }

    private String getElementRef (Element e)
    {
        String ref = null;
        for (Object o1 : e.getChildren())
        {
            Element et1 = (Element)o1;
            if (et1.getName().compareTo("hierarchy") == 0)
            {
                    if ( et1.getAttributeValue("class").compareTo("bwmeta1.hierarchy-class.hierarchy_Journal") == 0)
                    {
                        ref = et1.getChild("element-ref").getAttributeValue("ref");
                    }
            }
        }
        return ref;
    }

    private String getElementBioDesc (Element e)
    {
        String ref = null;
        for (Object o1 : e.getChildren())
        {
            Element et1 = (Element)o1;
            if (et1.getName().compareTo("attribute") == 0)
            {
                    if ( et1.getAttributeValue("key").compareTo("bibliographical.description") == 0)
                    {
                        String num = "";
                        ref = et1.getAttributeValue("value");
                        int x = 0;
                        while (x < ref.length() && ref.charAt(x) != '-')
                        {
                            if (ref.charAt(x) >= '0' && ref.charAt(x) <= '9')
                            {
                                num += ref.charAt(x);
                            }
                            x++;
                        }
                        if (x >= ref.length())
                        {
                            ref = "0" + ref;
                        } else {
                            ref = "1" + ref;
                        }
                        try
                        {
                            x = Integer.parseInt(num);
                        }
                        catch (Exception ex)
                        {
                            x = 0;
                        }
                        ref = String.format("%05d", x) + ref;
                    }
            }
        }
        return ref;
    }

    /**
     * Gets second title
     * @param e Article ID
     * @return Second title
     */
    private String getSecondTitle (Element e)
    {
        boolean first = true;
        String title = "";
        for (Object o1 : e.getChildren())
        {
            Element et1 = (Element)o1;
            if (et1.getName().compareTo("name") == 0)
            {
                if (first)
                {
                    first = false;
                } else {
                    if (!title.isEmpty())
                    {
                        title += " / ";
                    }
                    title += et1.getTextTrim();
                }
            }
        }
        return title;
    }

    /**
     * Check if value contains volume or number
     * @param container Values
     * @param value Value
     * @return Check state
     */
    private static boolean contains(String[] container, String value)
    {
        if (container.length == 0 || (container.length == 1 && container[0].compareTo("") == 0))
        {
            return true;
        }
        for (String v : container)
        {
            String[] values = v.split("-");
            if (values.length == 1)
            {
                String[] v2 = value.split("-");
                if (v2.length == 1)
                {
                    if (v.compareTo(value) == 0 || (v.length() >= 5 && value.length() >= 5 && v.substring(0, 5).compareTo(value.substring(0, 5)) == 0))
                    {
                        return true;
                    }
                } else {
                    if (v.compareTo(v2[0]) == 0 || v.compareTo(v2[1]) == 0)
                    {
                        return true;
                    }
                }
            } else {
                try
                {
                    String[] v2 = value.split("-");
                    int a = 0, b, c, d;
                    if (v2.length == 1)
                    {
                        b = Integer.parseInt(value);
                        c = Integer.parseInt(values[0]);
                        d = Integer.parseInt(values[1]);
                    } else {
                        a = Integer.parseInt(v2[0]);
                        b = Integer.parseInt(v2[1]);
                        c = Integer.parseInt(values[0]);
                        d = Integer.parseInt(values[1]);
                    }
                    if ((a >= c && a <= d) || (b >= c && b <= d))
                    {
                        return true;
                    }
                }
                catch (Exception e)
                {
                }
            }
        }
        return false;
    }

    /**
     * Check if value contains year
     * @param container Values
     * @param value Value
     * @return Check state
     */
    private static boolean containsYear(String[] container, String value)
    {
        if (container.length == 0 || (container.length == 1 && container[0].compareTo("") == 0))
        {
            return true;
        }
        String values[] = value.split("-");
        String val1, val2;
        if (values.length == 2)
        {
            val1 = values[0];
            val2 = values[1];
        } else {
            val1 = value;
            val2 = value;
        }
        for (String v : container)
        {
            values = v.split("-");
            if (values.length == 2)
            {
                if (val1.compareTo(values[0]) >= 0 && val1.compareTo(values[1]) <= 0 || val2.compareTo(values[0]) >= 0 && val2.compareTo(values[1]) <= 0)
                {
                    return true;
                }
            } else {
                if (v.compareTo(val1) == 0 || v.compareTo(val2) == 0)
                {
                    return true;
                }
            }
        }
        
        return false;
    }
}
