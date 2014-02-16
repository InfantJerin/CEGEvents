package com.cegevents.app;

import com.cegevents.app.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ViewCategoriesFragement extends Fragment {
	
	public ViewCategoriesFragement(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_view_categories, container, false);
         
        return rootView;
    }
}
