/**
 * VStar: a statistical analysis tool for variable star data.
 * Copyright (C) 2010  AAVSO (http://www.aavso.org/)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */
package org.aavso.tools.vstar.vela;

import java.util.List;

/**
 * VeLa: VStar expression Language
 * 
 * An enumeration of operand types.
 */
public enum Type {

	INTEGER, DOUBLE, STRING, BOOLEAN, LIST;

	public static Type java2Vela(Class<?> jtype) {
		Type vtype = null;

		if (jtype == int.class) {
			vtype = INTEGER;
		} else if (jtype == double.class) {
			vtype = DOUBLE;
		} else if (jtype == String.class) {
			vtype = STRING;
		} else if (jtype == CharSequence.class) {
			vtype = STRING;
		} else if (jtype == boolean.class) {
			vtype = BOOLEAN;
		} else if (jtype == List.class) {
			vtype = LIST;
		} else {
			throw new IllegalArgumentException("Unknown type: " + jtype);
		}

		return vtype;
	}
}
