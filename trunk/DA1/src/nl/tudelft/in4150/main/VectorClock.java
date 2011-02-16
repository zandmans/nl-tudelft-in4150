package nl.tudelft.in4150.main;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

enum VectorComparison {
	GREATER, EQUAL, SMALLER, SIMULTANEOUS
}

public class VectorClock extends HashMap<Integer, Integer> implements Serializable {
	private static final long serialVersionUID = 6668164199894268488L;// Unique Serial.

	/**
	 * Increases the component of pUnit by 1.
	 * @param pUnit - The ID of the vector element being increased.
	 */
	public void incrementClock(int pUnit) {
		if (this.containsKey(pUnit)) {
			this.put(new Integer(pUnit), this.get(new Integer(pUnit)).intValue() + 1);
		} else {
			this.put(new Integer(pUnit), 1);
		}
	}

	/**
	 * GUI operation, returns the IDs in some neat order.
	 * @return The IDs of the elements in the Clock.
	 */
	public Integer[] getOrderedIDs() {
		Integer[] lResult = new Integer[this.size()];
		lResult = this.keySet().toArray(lResult);
		Arrays.sort(lResult);
		return lResult;
	}

	/**
	 * GUI operation, returns the values in some neat order.
	 * @return The Values of the elements in the Clock.
	 */
	public Integer[] getOrderedValues() {
		Integer[] lResult = new Integer[this.size()];
		Integer[] lKeySet = this.getOrderedIDs();

		int i = 0;
		for (Integer lKey : lKeySet) {
			lResult[i] = this.get(lKey);
			i++;
		}

		return lResult;
	}

	@Override
	public Integer get(Object key) {
		Integer lResult = super.get(key);

		if (lResult == null)
			lResult = 0;

		return lResult;
	}

	@Override
	public VectorClock clone() {
		return (VectorClock) super.clone();
	}

	@Override
	public String toString() {
		Integer[] lIDs = this.getOrderedIDs();
		Integer[] lRequests = this.getOrderedValues();

		String lText = "(";

		for (int i = 0; i < lRequests.length; i++) {
			lText += lIDs[i];
			lText += " = ";
			lText += lRequests[i].toString();

			if (i + 1 < lRequests.length) {
				lText += ", ";
			}
		}

		lText += ")";

		return lText;
	}

	/**
	 * VectorClock compare operation. Returns one of four possible values indicating how
	 * clock one relates to clock two:
	 * <p/>
	 * VectorComparison.GREATER                     If One > Two.
	 * VectorComparison.EQUAL                       If One = Two.
	 * VectorComparison.SMALLER                     If One < Two.
	 * VectorComparison.SIMULTANEOUS        If One <> Two.
	 * @param pOne - First Clock being compared.
	 * @param pTwo - Second Clock being compared.
	 * @return VectorComparison value indicating how One relates to Two.
	 */
	public static VectorComparison compare(VectorClock pOne, VectorClock pTwo) {
		// Initially we assume it is all possible things.
		boolean lEqual = true;
		boolean lGreater = true;
		boolean lSmaller = true;

		// Go over all elements in Clock one.
		for (Integer lEntry : pOne.keySet()) {
			// Compare if also present in clock two.
			if (pTwo.containsKey(lEntry)) {
				// If there is a difference, it can never be equal.
				// Greater / smaller depends on the difference.
				if (pOne.get(lEntry) < pTwo.get(lEntry)) {
					lEqual = false;
					lGreater = false;
				}
				if (pOne.get(lEntry) > pTwo.get(lEntry)) {
					lEqual = false;
					lSmaller = false;
				}
			}
			// Else assume zero (default value is 0).
			else if (pOne.get(lEntry) != 0) {
				lEqual = false;
				lSmaller = false;
			}
		}

		// Go over all elements in Clock two.
		for (Integer lEntry : pTwo.keySet()) {
			// Only elements we have not found in One still need to be checked.
			if (!pOne.containsKey(lEntry) && (pTwo.get(lEntry) != 0)) {
				lEqual = false;
				lGreater = false;
			}
		}

		// Return based on determined information.
		if (lEqual) {
			return VectorComparison.EQUAL;
		} else if (lGreater && !lSmaller) {
			return VectorComparison.GREATER;
		} else if (lSmaller && !lGreater) {
			return VectorComparison.SMALLER;
		} else {
			return VectorComparison.SIMULTANEOUS;
		}
	}


	public static boolean isDeliverable(VectorClock pOne, VectorClock pTwo, int pUnit) {
		int value1;

		for(Integer Item : pOne.keySet()) {
			value1 = pOne.get(Item).intValue();
			if(Item.intValue() == pUnit) value1++;
			if(value1 < pTwo.get(Item).intValue()) return false;
		}

		return true;
	}
}