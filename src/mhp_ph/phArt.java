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

/**
 *
 * @author szymek
 */
public class phArt {

    public phArt(String t, String t2, String a, String o, String n, String tm, String r, float s, int i)
    {
        tytul = t;
        tytul2 = t2;
        autor = a;
        opis_f = o;
        numer = n;
        tom = tm;
        rok = r;
        score = s;
        id = i;
    }

    public String tytul = "";
    public String tytul2 = "";
    public String autor = "";
    public String opis_f = "";
    public String numer = "";
    public String tom = "";
    public String rok = "";
    float score = 0;
    public int id = 0;

    public boolean isSame(phArt p)
    {
        if (id != p.id)
        {
            return false;
        }
        return true;
    }
}
