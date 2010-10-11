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
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Scanner;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author artur.szymanski
 */
public class dlXML {

    static String saveDir = "c:\\db\\";

    static public String getContent (File f)
    {
        StringBuilder content = new StringBuilder();
        try
        {
            String l;
            Scanner br = new Scanner(f, "UTF-8");
            while (br.hasNext())
            {
                l=br.nextLine();
                content.append(l);
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return content.toString();
    }

    static public void remAttrMHP (String dir)
    {
        Document doc = new Document();
        File fDir = new File(dir);
        SAXBuilder b = new SAXBuilder();
        XMLOutputter out = new XMLOutputter();
        LinkedList<Element> toRem = new LinkedList<Element>();

        for (File f : fDir.listFiles())
        {
            try
            {
                doc = b.build(new StringReader(getContent(f)));
                for (Object e : doc.getRootElement().getChildren())
                {
                    Element tE = (Element)e;
                    if (tE.getName().compareTo("element") == 0)
                    {
                        Content rem = null;
                        for (Object te2 : tE.getChildren())
                        {
                            Element x = (Element)te2;
                            
                            if (x.getName().compareTo("attribute") == 0)
                            {
                                for (Object at : x.getAttributes())
                                {
                                    org.jdom.Attribute a = (org.jdom.Attribute)at;
                                    if (a.getValue().compareTo("mhp.typ.form") == 0)
                                    {
                                        toRem.add(x);
                                    } else if (a.getValue().compareTo("mhp.typ.rodz") == 0)
                                    {
                                        toRem.add(x);
                                    } else if (a.getValue().compareTo("mhp.reference") == 0)
                                    {
                                        toRem.add(x);
                                    }
                                }
                            } else if (x.getName().compareTo("date") == 0)
                            {
                                toRem.add(x);
                            }
                        }
                    }
                    for (Element er : toRem)
                    {
                        er.detach();
                        tE.removeContent(er);
                    }
                }
                out.setFormat(Format.getPrettyFormat());
                //System.out.println(f.getPath());
                out.output(doc, new OutputStreamWriter(new FileOutputStream("c:\\mod\\"+f.getName()),"UTF-8"));
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    static public void prepDB (String dir)
    {
        Document docNo = new Document();
        Document docTom = new Document();
        Document docYr = new Document();
        Document docArt = new Document();
        docNo.addContent(new Element("db_number"));
        docTom.addContent(new Element("db_volumne"));
        docYr.addContent(new Element("db_year"));
        docArt.addContent(new Element("db_article"));
        File folder = new File(dir);
        String xml = null;
        Document doc = new Document();
        SAXBuilder b = new SAXBuilder();
        XMLOutputter xmlOut = new XMLOutputter();
           
        try
        {
            for (File f : folder.listFiles())
            {
                xml = getContent(f);
                doc = b.build(new StringReader(xml));
                for (Object el1 : doc.getRootElement().cloneContent())
                {
                    Element tE = (Element)el1;
                    if (tE.getName().compareTo("element") == 0)
                    {
                        if (tE.getChild("hierarchy").getAttributeValue("level").compareTo("bwmeta1.level.hierarchy_Journal_Year") == 0)
                            docYr.getRootElement().addContent(tE);
                        else if (tE.getChild("hierarchy").getAttributeValue("level").compareTo("bwmeta1.level.hierarchy_Journal_Number") == 0)
                            docNo.getRootElement().addContent(tE);
                        else if (tE.getChild("hierarchy").getAttributeValue("level").compareTo("bwmeta1.level.hierarchy_Journal_Volume") == 0)
                            docTom.getRootElement().addContent(tE);
                        else if (tE.getChild("hierarchy").getAttributeValue("level").compareTo("bwmeta1.level.hierarchy_Journal_Article") == 0)
                            docArt.getRootElement().addContent(tE);
                    }
                }
            }
            
            //FileOutputStream fos = new FileOutputStream
            //FileWriter fw = new FileWriter("c:\\db.xml");
            xmlOut.setFormat(Format.getPrettyFormat());
            xmlOut.output(docNo, new OutputStreamWriter(new FileOutputStream(saveDir+"dbNo.xml"),"UTF-8"));
            xmlOut.output(docTom, new OutputStreamWriter(new FileOutputStream(saveDir+"dbVol.xml"),"UTF-8"));
            xmlOut.output(docYr, new OutputStreamWriter(new FileOutputStream(saveDir+"dbYr.xml"),"UTF-8"));
            xmlOut.output(docArt, new OutputStreamWriter(new FileOutputStream(saveDir+"dbArt.xml"),"UTF-8"));
        }
        catch (Exception ex)
        {
            System.out.println("FATAL ERROR" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void createIndex () throws Exception
    {
        Class.forName("org.sqlite.JDBC");
        Connection conn =
        DriverManager.getConnection("jdbc:sqlite:test.db");
        Statement stat = conn.createStatement();
    }

}
