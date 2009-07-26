package fr.steren.remixthem;

public class CompoPart {
		
	private FacePart mFacePart;
	private CompoPartParams mParams;
	
	public CompoPart(FacePart facePart, CompoPartParams params) {
		super();
		mFacePart = facePart;
		mParams = params;
	}

	public FacePart getFacePart() {
		return mFacePart;
	}

	public void setFacePart(FacePart facePart) {
		mFacePart = facePart;
	}

	public CompoPartParams getParams() {
		return mParams;
	}

	public void setParams(CompoPartParams params) {
		mParams = params;
	}
	
	
	
}


