package fr.steren.remixthem.market;

public class CompoPart {
		
	private FacePart mFacePart;
	private CompoPartParams mParams;
	private CompoPartParams mOriginalParams;
	
	public CompoPart(FacePart facePart, CompoPartParams params) {
		super();
		mFacePart = facePart;
		mParams = params;
		mOriginalParams = params;
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
	
	public void resetParams() {
		mParams.copyFromParams(mOriginalParams);
	}
	
}


