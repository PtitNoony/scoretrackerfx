/*
 * Copyright (C) 2018 H-K
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.ptitnoony.apps.hearts.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hamon
 */
public class UIUtils {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00");

    public static String formatNumber(Number value) {
        return DECIMAL_FORMAT.format(value);
    }

    public static double parseFormatedNumber(String value) {
        try {
            return DECIMAL_FORMAT.parse(value).doubleValue();
        } catch (ParseException ex) {
            Logger.getLogger(UIUtils.class.getName()).log(Level.SEVERE, "Could not parse {1} :: exception {2}", new Object[]{value, ex});
        }
        return -1;
    }

    private UIUtils() {
        // private utility constructor
    }
}
