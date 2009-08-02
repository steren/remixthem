package fr.steren.remixthem;

import java.util.ArrayList;

public class Preset {

	private ArrayList<CompoPartParams> mParamList;
	
    public Preset(ArrayList<CompoPartParams> paramList) {
		mParamList = paramList;
	}

    public Preset() {
		mParamList = new ArrayList<CompoPartParams>(3) ;
	}
    
    public void addParams(int iCompoPart, CompoPartParams params) {
    	mParamList.set(iCompoPart, params);
    }
    
	public ArrayList<CompoPartParams> getParamList() {
		return mParamList;
	}
}
