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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aavso.tools.vstar.util.prefs.NumericPrecisionPrefs;

/**
 * VeLa: VStar expression Language
 *
 * A class that represents typed operands.
 * 
 * Note: should cache Operand instances
 */
public class Operand {

	private Type type;
	private long intVal;
	private double doubleVal;
	private String stringVal;
	private boolean booleanVal;
	private List<Operand> listVal;
	private FunctionExecutor functionVal;

	public static Operand EMPTY_LIST = new Operand(Type.LIST, Collections.emptyList());

	public static Operand NO_VALUE = new Operand(Type.NONE, false);

	public Operand(Type type, long value) {
		this.type = type;
		intVal = value;
	}

	public Operand(Type type, double value) {
		this.type = type;
		doubleVal = value;
	}

	public Operand(Type type, String value) {
		this.type = type;
		stringVal = value;
	}

	public Operand(Type type, boolean value) {
		this.type = type;
		booleanVal = value;
	}

	public Operand(Type type, List<Operand> value) {
		this.type = type;
		listVal = value;
	}

	public Operand(Type type, FunctionExecutor value) {
		this.type = type;
		functionVal = value;
	}

	// For object copy
	private Operand() {
	}

	// TODO: add a Property to Operand method

	/**
	 * Given a VeLa type and a Java object, return an Operand instance.
	 * 
	 * @param type The VeLa type.
	 * @param obj  The Java object.
	 * @return A corresponding Operand instance.
	 */
	public static Operand object2Operand(Type type, Object obj) {
		Operand operand = null;

		switch (type) {
		case INTEGER:
			operand = new Operand(Type.INTEGER, (int) obj);
			break;
		case REAL:
			operand = new Operand(Type.REAL, (double) obj);
			break;
		case STRING:
			operand = new Operand(Type.STRING, (String) obj);
			break;
		case BOOLEAN:
			operand = new Operand(Type.BOOLEAN, (boolean) obj);
			break;
		case LIST:
			if (obj.getClass() == Type.DBL_ARR.getClass()) {
				List<Operand> arr = new ArrayList<Operand>();
				for (double n : (double[]) obj) {
					arr.add(new Operand(Type.REAL, n));
				}
				obj = arr;
			} else if (obj.getClass() == Type.DBL_CLASS_ARR.getClass()) {
				List<Operand> arr = new ArrayList<Operand>();
				for (Double n : (Double[]) obj) {
					arr.add(new Operand(Type.REAL, n));
				}
				obj = arr;
			}
			operand = new Operand(Type.LIST, (List<Operand>) obj);
			break;
		case FUNCTION:
			operand = new Operand(Type.FUNCTION, (FunctionExecutor) obj);
			break;
		case NONE:
			operand = NO_VALUE;
			break;
		case OBJECT:
			// TODO
//			operand = new Operand(Type.OBJECT, obj);
		}

		return operand;
	}

	/**
	 * Return a Java object corresponding to this Operand instance.
	 * 
	 * @return A corresponding Java object.
	 */
	public Object toObject() {
		Object obj = null;

		switch (type) {
		case INTEGER:
			obj = intVal;
			break;
		case REAL:
			obj = doubleVal;
			break;
		case STRING:
			obj = stringVal;
			break;
		case BOOLEAN:
			obj = booleanVal;
			break;
		case LIST:
			for (Type type : Type.values()) {
				if (listVal.stream().allMatch(op -> op.type == type)) {
					int i = 0;
					try {
						switch (type) {
						case INTEGER:
							int[] ints = new int[listVal.size()];
							for (Operand op : listVal) {
								ints[i++] = (int) op.intVal;
							}
							obj = ints;
							break;
						case REAL:
							double[] reals = new double[listVal.size()];
							for (Operand op : listVal) {
								reals[i++] = (double) op.doubleVal;
							}
							obj = reals;
							break;
						case STRING:
							String[] strings = new String[listVal.size()];
							for (Operand op : listVal) {
								strings[i++] = (String) op.stringVal;
							}
							obj = strings;
							break;
						case BOOLEAN:
							boolean[] booleans = new boolean[listVal.size()];
							for (Operand op : listVal) {
								booleans[i++] = (boolean) op.booleanVal;
							}
							obj = booleans;
							break;
						default:
							throw new VeLaEvalError("");
						}
					} catch (Throwable t) {
						throw new VeLaEvalError("Cannot construct array from VeLa list");
					}
				}
			}
			break;
		case FUNCTION:
			// TODO
			break;
		case NONE:
			// TODO
			break;
		case OBJECT:
			// TODO
			break;
		}

		return obj;
	}

	/**
	 * Convert this operand to the required type, if possible.
	 * 
	 * @param operand      The operand to be converted.
	 * @param requiredType The required type.
	 * @return The converted type; will be unchanged if it matches the required type
	 *         or can't be converted; TODO: consider returning Optional<Type>; if
	 *         empty, then the type can't be converted
	 */
	public Type convert(Type requiredType) {
		if (type != requiredType) {
			// Integer to double
			if (type == Type.INTEGER && requiredType == Type.REAL) {
				setType(Type.REAL);
				setDoubleVal((double) intVal);
			}
		}

		return type;
	}

	/**
	 * Convert this operand's type to string.
	 */
	public void convertToString() {
		assert type == Type.INTEGER || type == Type.REAL || type == Type.BOOLEAN;

		switch (type) {
		case INTEGER:
			setStringVal(Long.toString(intVal));
			setType(Type.STRING);
			break;
		case REAL:
			setStringVal(NumericPrecisionPrefs.formatOther(doubleVal));
			setType(Type.STRING);
			break;
		case BOOLEAN:
			setStringVal(Boolean.toString(booleanVal));
			setType(Type.STRING);
			break;
		default:
			break;
		}
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * @param intVal the intVal to set
	 */
	public void setIntegerVal(long intVal) {
		this.intVal = intVal;
	}

	/**
	 * @param doubleVal the doubleVal to set
	 */
	public void setDoubleVal(double doubleVal) {
		this.doubleVal = doubleVal;
	}

	/**
	 * @return the intVal
	 */
	public long intVal() {
		return intVal;
	}

	/**
	 * @return the doubleVal
	 */
	public double doubleVal() {
		return doubleVal;
	}

	/**
	 * @param stringVal the stringVal to set
	 */
	public void setStringVal(String stringVal) {
		this.stringVal = stringVal;
	}

	/**
	 * @return the stringVal
	 */
	public String stringVal() {
		return stringVal;
	}

	/**
	 * @param booleanVal the booleanVal to set
	 */
	public void setBooleanVal(boolean booleanVal) {
		this.booleanVal = booleanVal;
	}

	/**
	 * @return the booleanVal
	 */
	public boolean booleanVal() {
		return booleanVal;
	}

	/**
	 * @return the listVal
	 */
	public List<Operand> listVal() {
		return listVal;
	}

	/**
	 * @param listVal the listVal to set
	 */
	public void setListVal(List<Operand> listVal) {
		this.listVal = listVal;
	}

	/**
	 * @return the functionVal
	 */
	public FunctionExecutor functionVal() {
		return functionVal;
	}

	/**
	 * @param functionVal the functionVal to set
	 */
	public void setFunctionVal(FunctionExecutor functionVal) {
		this.functionVal = functionVal;
	}

	public String toHumanReadableString() {
		String str = "";

		switch (type) {
		case INTEGER:
			str = Long.toString(intVal);
			break;
		case REAL:
			str = NumericPrecisionPrefs.formatOther(doubleVal);
			break;
		case BOOLEAN:
			str = booleanVal ? "True" : "False";
			break;
		case STRING:
			str = stringVal;
			break;
		case LIST:
			str = listVal.toString().replace(",", "");
			break;
		case FUNCTION:
			str = functionVal.toString();
			break;
		}

		return str;
	}

	@Override
	public String toString() {
		String str = "";

		switch (type) {
		case INTEGER:
			str = Long.toString(intVal);
			break;
		case REAL:
			str = NumericPrecisionPrefs.formatOther(doubleVal);
			break;
		case BOOLEAN:
			str = booleanVal ? "True" : "False";
			break;
		case STRING:
			str = "\"" + stringVal + "\"";
			break;
		case LIST:
			str = listVal.toString().replace(",", "").replace("[", "'(").replace("]", ")");
			break;
		case FUNCTION:
			str = functionVal.toString();
			break;
		}

		// str += " (" + type + ")";

		return str;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (booleanVal ? 1231 : 1237);
		long temp;
		temp = Double.doubleToLongBits(doubleVal);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((functionVal == null) ? 0 : functionVal.hashCode());
		result = prime * result + (int) (intVal ^ (intVal >>> 32));
		result = prime * result + ((listVal == null) ? 0 : listVal.hashCode());
		result = prime * result + ((stringVal == null) ? 0 : stringVal.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Operand other = (Operand) obj;
		if (booleanVal != other.booleanVal)
			return false;
		if (Double.doubleToLongBits(doubleVal) != Double.doubleToLongBits(other.doubleVal))
			return false;
		if (functionVal == null) {
			if (other.functionVal != null)
				return false;
		} else if (!functionVal.equals(other.functionVal))
			return false;
		if (intVal != other.intVal)
			return false;
		if (listVal == null) {
			if (other.listVal != null)
				return false;
		} else if (!listVal.equals(other.listVal))
			return false;
		if (stringVal == null) {
			if (other.stringVal != null)
				return false;
		} else if (!stringVal.equals(other.stringVal))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	public Operand copy() {
		Operand operand = new Operand();

		operand.type = type;

		switch (type) {
		case INTEGER:
			operand.intVal = intVal;
			break;
		case REAL:
			operand.doubleVal = doubleVal;
			break;
		case BOOLEAN:
			operand.booleanVal = booleanVal;
			break;
		case STRING:
			operand.stringVal = stringVal;
			break;
		case LIST:
			List<Operand> list = new ArrayList<Operand>();
			for (Operand op : listVal) {
				list.add(op.copy());
			}
			operand.listVal = list;
			break;
		case FUNCTION:
			operand.functionVal = functionVal;
			break;
		}

		return operand;
	}
}
