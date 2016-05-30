package com.likemessage.common;

import android.widget.ListView;

/**
 * Created by wangkai on 2016/5/30.
 */
public abstract class BaseAdapter extends android.widget.BaseAdapter{

    public void notifyDataSetChangedSelectTop() {
        super.notifyDataSetChanged();
        getListView().setSelection(getCount() - 1);
    }

    public void notifyDataSetChangedSelect(int index) {
        super.notifyDataSetChanged();
        getListView().setSelection(index);
    }

    public abstract ListView getListView();
}
