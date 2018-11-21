package fr.groom.models;

import org.json.JSONObject;

import java.util.ArrayList;

public class IntentFilter {
	private int mPriority;
	private ArrayList<String> mActions;
	private ArrayList<String> mCategories = null;
	private ArrayList<String> mDataSchemes = null;
	private ArrayList<String> mDataTypes = null;
	private boolean mHasPartialTypes = false;
	private IntentFilterData intentFilterData;

	public IntentFilter() {
		mPriority = 0;
		mActions = new ArrayList<String>();
	}

	public final void addAction(String action) {
		if (!mActions.contains(action)) {
			mActions.add(action.intern());
		}
	}

	public final void addCategory(String category) {
		if (mCategories == null) mCategories = new ArrayList<String>();
		if (!mCategories.contains(category)) {
			mCategories.add(category.intern());
		}
	}

	public final void addDataScheme(String scheme) {
		if (mDataSchemes == null) mDataSchemes = new ArrayList<String>();
		if (!mDataSchemes.contains(scheme)) {
			mDataSchemes.add(scheme.intern());
		}
	}

	public final void addDataType(String type) {
		final int slashpos = type.indexOf('/');
		final int typelen = type.length();
		if (slashpos > 0 && typelen >= slashpos + 2) {
			if (mDataTypes == null) mDataTypes = new ArrayList<String>();
			if (typelen == slashpos + 2 && type.charAt(slashpos + 1) == '*') {
				String str = type.substring(0, slashpos);
				if (!mDataTypes.contains(str)) {
					mDataTypes.add(str.intern());
				}
				mHasPartialTypes = true;
			} else {
				if (!mDataTypes.contains(type)) {
					mDataTypes.add(type.intern());
				}
			}
			return;
		}

	}

	public final void setPriority(int priority) {
		mPriority = priority;
	}

	public void setIntentFilterData(IntentFilterData intentFilterData) {
		this.intentFilterData = intentFilterData;
	}


	public JSONObject toJson() {
		JSONObject jo = new JSONObject();
		jo.put("mPriority", mPriority);
		if (mActions != null) {
			for (String m : mActions) {
				jo.accumulate("mActions", m);
			}
		}
		if (mCategories != null) {
			for (String m : mCategories) {
				jo.accumulate("mCategories", m);
			}
		}
		if (mDataSchemes != null) {
			for (String m : mDataSchemes) {
				jo.accumulate("mDataSchemes", m);
			}
		}
		if (mDataTypes != null) {
			for (String m : mDataTypes) {
				jo.accumulate("mDataTypes", m);
			}
		}
		jo.put("mHasPartialTypes", mHasPartialTypes);
		if (intentFilterData != null) {
			jo.put("intentFilterData", intentFilterData.toJson());
		}
		return jo;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}
}
