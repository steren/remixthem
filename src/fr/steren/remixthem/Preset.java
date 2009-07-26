package fr.steren.remixthem;

import java.util.ArrayList;

public class Preset {
	
    public Preset(ArrayList<CompoPartParams> paramList) {
		super();
		mParamList = paramList;
	}

	private ArrayList<CompoPartParams> mParamList;

	public ArrayList<CompoPartParams> getParamList() {
		return mParamList;
	}

	public void setParams(ArrayList<CompoPartParams> paramList) {
		mParamList = paramList;
	}
}
