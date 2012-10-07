package com.naddysworld.slingshot;

import android.view.View;

public class SlingshotUsageItem {
	private String name;
	private String value;
	private String unit;
	private int visibilityProgress;
	private float progressNum;
	
	public SlingshotUsageItem(String name, String value, String unit, int visibilityProgress, float progressNum)
	{
		this.name = name;
		this.value = value;
		this.unit = unit;
		this.visibilityProgress = visibilityProgress;
		this.progressNum = progressNum;
	}
	public SlingshotUsageItem(String name, String value, String unit)
	{
		this(name, value, unit, View.GONE, 0);
	}
	public String getName()
	{
		return this.name;
	}
	public String getValue()
	{
		return this.value;
	}
	public String getUnit()
	{
		return this.unit;
	}
	public int getVisibilityProgress()
	{
		return this.visibilityProgress;
	}
	public float getProgressNum()
	{
		return this.progressNum;
	}
	public void setName(String v)
	{
		this.name = v;
	}
	public void setValue(String v)
	{
		this.name = v;
	}
	public void setUnit(String v)
	{
		this.unit = v;
	}
	public void setVisibilityProgress(int v)
	{
		this.visibilityProgress = v;
	}
	public void setProgressNum(float v)
	{
		this.progressNum = v;
	}
}
