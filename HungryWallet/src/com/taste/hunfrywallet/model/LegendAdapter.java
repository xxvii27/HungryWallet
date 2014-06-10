/**
 * 
 */
package com.taste.hunfrywallet.model;

import java.util.ArrayList;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author Huang Li
 *
 */
@SuppressWarnings("rawtypes")
public class LegendAdapter extends ArrayAdapter {

    @SuppressWarnings("unused")
	private LayoutInflater mInflater;

    private String[] mStrings;

    @SuppressWarnings("unused")
	private int mViewResourceId;

    private ArrayList<Integer> colors;
    
    @SuppressWarnings("unused")
	private final int TOTAL_COUNT = 6;
    
    @SuppressWarnings("unchecked")
	public LegendAdapter(Context ctx, int viewResourceId, String[] strings, ArrayList<Integer> colors) {
        super(ctx, viewResourceId, strings);

        mInflater = (LayoutInflater)ctx.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        mStrings = strings;

        mViewResourceId = viewResourceId;
        
        this.colors = colors;
    }
    @Override
    public int getCount() {
        return mStrings.length;
    }

    @Override
    public String getItem(int position) {
        return mStrings[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
    	TextView textView = (TextView) super.getView(position, convertView, parent);
        
    	int color = 0;
    	try{
    		color = colors.get(position);
    	} catch (IndexOutOfBoundsException e) {
    		color = colors.get(0);
    	}
    	
    	textView.setTextColor(color);
        textView.setGravity(Gravity.CENTER);

        return textView;
    }

}
