/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2007 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.framework.common.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.vtp.framework.common.ILastResult;
import org.eclipse.vtp.framework.common.ILastResultData;
import org.eclipse.vtp.framework.common.IScriptable;
import org.eclipse.vtp.framework.core.ISessionContext;

/**
 * 
 * @author Lonnie Pryor
 */
public class LastResult implements ILastResult, IScriptable {
	/** The context to use. */
	private final ISessionContext context;

	/**
	 * Creates a new LastResult.
	 * 
	 * @param context
	 *            The context to use.
	 * @param brandRegistry
	 *            The brand registry to use.
	 */
	public LastResult(ISessionContext context) {
		this.context = context;
	}

	@Override
	public ILastResultData addResult(int confidence, String utterance,
			String inputMode, String interpretation) {
		String lengthString = (String) context
				.getAttribute("lastresult.length");
		if (lengthString == null || "".equals(lengthString)) {
			lengthString = "0";
		}
		final int length = Integer.parseInt(lengthString);
		context.setAttribute("lastresult." + length + ".confidence",
				Integer.toString(confidence));
		context.setAttribute("lastresult." + length + ".utterance", utterance);
		context.setAttribute("lastresult." + length + ".inputmode", inputMode);
		context.setAttribute("lastresult." + length + ".interpretation",
				interpretation);
		context.setAttribute("lastresult.length", Integer.toString(length + 1));
		return new LastResultData(confidence, utterance, inputMode,
				interpretation);
	}

	@Override
	public void clear() {
		final String lengthString = (String) context
				.getAttribute("lastresult.length");
		if (lengthString == null || "".equals(lengthString)) {
			return;
		}
		final int length = Integer.parseInt(lengthString);
		for (int i = 0; i < length; i++) {
			context.clearAttribute("lastresult." + i + ".confidence");
			context.clearAttribute("lastresult." + i + ".utterance");
			context.clearAttribute("lastresult." + i + ".inputmode");
			context.clearAttribute("lastresult." + i + ".interpretation");
		}
		context.clearAttribute("lastresult.length");
		context.clearAttribute("lastresult.markname");
		context.clearAttribute("lastresult.marktime");
	}

	@Override
	public List<ILastResultData> getResults() {
		final String lengthString = (String) context
				.getAttribute("lastresult.length");
		if (lengthString == null || "".equals(lengthString)) {
			return Collections.emptyList();
		}
		final int length = Integer.parseInt(lengthString);
		final List<ILastResultData> ret = new ArrayList<ILastResultData>(length);
		for (int i = 0; i < length; i++) {
			final String confidenceString = (String) context
					.getAttribute("lastresult." + i + ".confidence");
			final int confidence = Integer.parseInt(confidenceString);
			final String utterance = (String) context
					.getAttribute("lastresult." + i + ".utterance");
			final String inputMode = (String) context
					.getAttribute("lastresult." + i + ".inputmode");
			final String interpretation = (String) context
					.getAttribute("lastresult." + i + ".interpretation");
			ret.add(new LastResultData(confidence, utterance, inputMode,
					interpretation));
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getName()
	 */
	@Override
	public final String getName() {
		return "LastResult"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasValue()
	 */
	@Override
	public boolean hasValue() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#toValue()
	 */
	@Override
	public Object toValue() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.framework.spi.scripting.IScriptable#getFunctionNames()
	 */
	@Override
	public final String[] getFunctionNames() {
		return new String[] {};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#invokeFunction(
	 * java.lang.String, java.lang.Object[])
	 */
	@Override
	public final Object invokeFunction(String name, Object[] arguments) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasItem(int)
	 */
	@Override
	public final boolean hasItem(int index) {
		String lengthString = (String) context
				.getAttribute("lastresult.length");
		if (lengthString == null || "".equals(lengthString)) {
			lengthString = "0";
		}
		final int length = Integer.parseInt(lengthString);
		return index >= 0 && index < length;
	}

	@Override
	public String[] getPropertyNames() {
		return new String[] { "length", "confidence", "utterance", "inputmode",
				"interpretation", "markname", "marktime" };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasEntry(
	 * java.lang.String)
	 */
	@Override
	public final boolean hasEntry(String name) {
		return "length".equals(name) || "confidence".equals(name)
				|| "utterance".equals(name) || "inputmode".equals(name)
				|| "interpretation".equals(name) || "markname".equals(name)
				|| "marktime".equals(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getItem(int)
	 */
	@Override
	public final Object getItem(int index) {
		final String lengthString = (String) context
				.getAttribute("lastresult.length");
		if (lengthString == null || "".equals(lengthString)) {
			return null;
		}
		final String confidenceString = (String) context
				.getAttribute("lastresult." + index + ".confidence");
		final int confidence = Integer.parseInt(confidenceString);
		final String utterance = (String) context.getAttribute("lastresult."
				+ index + ".utterance");
		final String inputMode = (String) context.getAttribute("lastresult."
				+ index + ".inputmode");
		final String interpretation = (String) context
				.getAttribute("lastresult." + index + ".interpretation");
		return new LastResultData(confidence, utterance, inputMode,
				interpretation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getEntry(
	 * java.lang.String)
	 */
	@Override
	public final Object getEntry(String name) {
		if ("length".equals(name)) {
			String lengthString = (String) context
					.getAttribute("lastresult.length");
			if (lengthString == null || "".equals(lengthString)) {
				lengthString = "0";
			}
			final int length = Integer.parseInt(lengthString);
			return new Integer(length);
		}
		ILastResultData lrd = null;
		final List<ILastResultData> results = getResults();
		if (results.size() > 0) {
			lrd = results.get(0);
		}
		if ("confidence".equals(name)) {
			return lrd == null ? null : new Integer(lrd.getConfidence());
		}
		if ("utterance".equals(name)) {
			return lrd == null ? null : lrd.getUtterence();
		}
		if ("inputmode".equals(name)) {
			return lrd == null ? null : lrd.getInputMode();
		}
		if ("interpretation".equals(name)) {
			return lrd == null ? null : lrd.getInterpretation();
		}
		if ("markname".equals(name)) {
			String markName = (String) context
					.getAttribute("lastresult.markname");
			if (markName == null) {
				markName = "";
			}
			return markName;
		}
		if ("marktime".equals(name)) {
			String markTime = (String) context
					.getAttribute("lastresult.marktime");
			if (markTime == null) {
				markTime = "0";
			}
			return markTime;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setItem(int,
	 * java.lang.Object)
	 */
	@Override
	public final boolean setItem(int index, Object value) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setEntry(
	 * java.lang.String, java.lang.Object)
	 */
	@Override
	public final boolean setEntry(String name, Object value) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearItem(int)
	 */
	@Override
	public final boolean clearItem(int index) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearEntry(
	 * java.lang.String)
	 */
	@Override
	public final boolean clearEntry(String name) {
		return false;
	}

	private class LastResultData implements ILastResultData, IScriptable {
		private int confidence = 0;
		private String utterance = null;
		private String inputMode = null;
		private String interpretation = null;

		public LastResultData(int confidence, String utterance,
				String inputMode, String interpretation) {
			super();
			this.confidence = confidence;
			this.utterance = utterance;
			this.inputMode = inputMode;
			this.interpretation = interpretation;
		}

		@Override
		public int getConfidence() {
			return confidence;
		}

		@Override
		public String getInputMode() {
			return inputMode;
		}

		@Override
		public String getInterpretation() {
			return interpretation;
		}

		@Override
		public String getUtterence() {
			return utterance;
		}

		@Override
		public boolean clearEntry(String name) {
			return false;
		}

		@Override
		public boolean clearItem(int index) {
			return false;
		}

		@Override
		public Object getEntry(String name) {
			if ("confidence".equals(name)) {
				return new Integer(confidence);
			}
			if ("utterance".equals(name)) {
				return utterance;
			}
			if ("inputmode".equals(name)) {
				return inputMode;
			}
			if ("interpretation".equals(name)) {
				return interpretation;
			}
			return null;
		}

		@Override
		public String[] getFunctionNames() {
			return new String[] {};
		}

		@Override
		public Object getItem(int index) {
			return null;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public String[] getPropertyNames() {
			return new String[] { "confidence", "utterance", "inputmode",
					"interpretation" };
		}

		@Override
		public boolean hasEntry(String name) {
			return "confidence".equals(name) || "utterance".equals(name)
					|| "inputmode".equals(name)
					|| "interpretation".equals(name);
		}

		@Override
		public boolean hasItem(int index) {
			return false;
		}

		@Override
		public boolean hasValue() {
			return false;
		}

		@Override
		public Object invokeFunction(String name, Object[] arguments) {
			return null;
		}

		@Override
		public boolean setEntry(String name, Object value) {
			return false;
		}

		@Override
		public boolean setItem(int index, Object value) {
			return false;
		}

		@Override
		public Object toValue() {
			return null;
		}

		@Override
		public boolean isMutable() {
			// TODO Auto-generated method stub
			return false;
		}

	}

	@Override
	public String getMarkName() {
		String markName = (String) context.getAttribute("lastresult.markname");
		if (markName == null) {
			markName = "";
		}
		return markName;
	}

	@Override
	public String getMarkTime() {
		String markTime = (String) context.getAttribute("lastresult.marktime");
		if (markTime == null) {
			markTime = "0";
		}
		return markTime;
	}

	@Override
	public void setMarkName(String name) {
		context.setAttribute("lastresult.markname", name);
	}

	@Override
	public void setMarkTime(String time) {
		context.setAttribute("lastresult.marktime", time);
	}

	@Override
	public boolean isMutable() {
		// TODO Auto-generated method stub
		return false;
	}
}
