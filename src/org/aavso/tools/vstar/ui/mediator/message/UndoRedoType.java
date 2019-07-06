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
package org.aavso.tools.vstar.ui.mediator.message;

/**
 * An enum representing the kind of undoable action: undo, redo.
 */
public enum UndoRedoType {

	UNDO, REDO;

	public UndoRedoType opposite() {
		UndoRedoType result = null;

		switch (this) {
		case UNDO:
			result = REDO;
		case REDO:
			result = UNDO;
		default:
		}

		return result;
	}

	public String toString() {
		String str = null;

		if (this == UNDO) {
			str = "Undo";
		} else {
			str = "Redo";
		}

		return str;
	}
}
