/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sigfox2lo.sigfox.model;

import java.util.Objects;

public class SigfoxPaging {

	private String next;

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}

	@Override
	public String toString() {
		return "SigfoxPaging [next=" + next + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SigfoxPaging)) return false;
		SigfoxPaging that = (SigfoxPaging) o;
		return Objects.equals(getNext(), that.getNext());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getNext());
	}
}
