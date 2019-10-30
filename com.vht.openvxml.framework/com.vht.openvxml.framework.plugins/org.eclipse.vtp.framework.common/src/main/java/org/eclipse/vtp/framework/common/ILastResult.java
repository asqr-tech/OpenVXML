package org.eclipse.vtp.framework.common;

import java.util.List;

public interface ILastResult {
	public ILastResultData addResult(int confidence, String utterence,
			String inputMode, String interpretation);

	public void clear();

	public String getMarkName();

	public String getMarkTime();

	public List<ILastResultData> getResults();

	public void setMarkName(String name);

	public void setMarkTime(String time);
}
