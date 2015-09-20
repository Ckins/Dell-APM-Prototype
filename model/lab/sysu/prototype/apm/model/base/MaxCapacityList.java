/*
* Copyright 2014 Dell Inc.
* ALL RIGHTS RESERVED.
*
* This software is the confidential and proprietary information of
* Dell Inc. ("Confidential Information").  You shall not
* disclose such Confidential Information and shall use it only in
* accordance with the terms of the license agreement you entered
* into with Dell Inc.
*
* DELL INC. MAKES NO REPRESENTATIONS OR WARRANTIES
* ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS
* OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
* WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
* PARTICULAR PURPOSE, OR NON-INFRINGEMENT. DELL SHALL
* NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
* AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
* THIS SOFTWARE OR ITS DERIVATIVES.
*/

package lab.sysu.prototype.apm.model.base;

import java.util.ArrayList;
import java.util.Collection;

/**
 * this list will hold a max number of elements, when adding a new element or a
 * set of elements to the list, and the final size of the list exceeds the
 * capacity, the redundant elements will be removed from the first element till
 * the size of this list meets the capacity
 * 
 * @param <E>
 *            the element class
 */
public class MaxCapacityList<E> extends ArrayList<E> {

	private static final long serialVersionUID = -8700940508264564663L;

	// max number of elements in this list
	private int capacity;

	public MaxCapacityList(int capacity) {
		super();
		this.capacity = capacity;
	}

	private boolean removeFirstIfSizeExceedCapacity() {
		if (size() > 0 && capacity < size()) {
			remove(0);
			return true;
		}
		return false;
	}

	@Override
	public boolean add(E e) {
		super.add(e);
		removeFirstIfSizeExceedCapacity();
		return capacity > 0;
	}

	@Override
	public void add(int index, E element) {
		super.add(index, element);
		removeFirstIfSizeExceedCapacity();
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		super.addAll(index, c);
		while (removeFirstIfSizeExceedCapacity()) {
			// do nothing
		}
		return index >= size() + c.size() - capacity;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		super.addAll(c);
		while (removeFirstIfSizeExceedCapacity()) {
			// do nothing
		}
		return c.size() <= capacity;
	}
}
