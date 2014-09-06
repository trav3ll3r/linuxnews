package org.androidfromfrankfurt.archnews;

import java.util.ArrayList;
import java.util.List;

import android.R.anim;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import at.theengine.android.simple_rss2_android.RSSItem;
import at.theengine.android.simple_rss2_android.SimpleRss2Parser;
import at.theengine.android.simple_rss2_android.SimpleRss2ParserCallback;


public class NewsFragment extends ListFragment {

	private static NewsFragment instance;
	private View listView;
	private View headerView;
	private View errorView;
	private TextView tvError;
	private Button btnReload;
	private ProgressDialog loadingDialog;
	private boolean isInitialized = false;
	private boolean listVisible;
	
    public NewsFragment() {
    	instance = this;
    }

    public static NewsFragment getInstance() {
    	return instance;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.fragment_news, container, false);
    	headerView = inflater.inflate(R.layout.header, null);
    	tvError = (TextView)rootView.findViewById(R.id.tv_errormessage);
    	btnReload = (Button)rootView.findViewById(R.id.btn_reload);
    	return rootView;
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	initialize();
    	startLoading();
    }
    
    private void initialize() {
    	listView = getListView();
    	if(headerView != null) {
    		this.getListView().addHeaderView(headerView);
    	}
    	errorView = getListView().getEmptyView();
    	errorView.setVisibility(View.GONE);;
    	btnReload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startLoading();
			}
		});
    	loadingDialog = new ProgressDialog(getActivity());
    	loadingDialog.setTitle(getResources().getText(R.string.dlg_loading_title));
    	loadingDialog.setMessage(getResources().getText(R.string.dlg_loading));
    	loadingDialog.setIndeterminate(true);
    	loadingDialog.setCancelable(false);
    	loadingDialog.setCanceledOnTouchOutside(false);
    	isInitialized = true;
    }
    
    private void parseRss() {
    	// ***Do not call this method directly! Use startLoading()!***
    	SimpleRss2Parser newsParser = new SimpleRss2Parser("https://www.archlinux.org/feeds/news/",
    			new SimpleRss2ParserCallback() {
			
			@Override
			public void onFeedParsed(List<RSSItem> arg0) {
				setListAdapter(new NewsAdapter(getActivity(), R.layout.news_item, (ArrayList<RSSItem>) arg0));
				loadingSuccessful(true, null);
			}
			
			@Override
			public void onError(Exception arg0) {
				loadingSuccessful(false, arg0.getMessage());
			}
		});
    	newsParser.parseAsync();
    }
    
    public void startLoading() {
    	loadingDialog.show();
    	parseRss();
    }
    
    private void loadingSuccessful(boolean success, String errorMessage) {
    	loadingDialog.dismiss();
    	if (!success && errorMessage != null) {
    		showError();
    		showErrorMessage(errorMessage);
    	}
    }
    
    @Override
    public void onDestroyView() {
    	setListAdapter(null);
    	super.onDestroyView();
    }
    
    private void showErrorMessage(String errorMessage) {
    	tvError.setText(errorMessage);
    }
    
    private void showError() {
    	errorVisibile(true);
    }
    
    private void hideError() {
    	errorVisibile(false);
    }
    
    private void errorVisibile(boolean visible){
        if (listVisible == visible) {
            return;
        }
        listVisible = visible;
        if (visible) {
        	// Hide ListView, show error
        	listView.startAnimation(AnimationUtils.loadAnimation(
                    getActivity(), android.R.anim.fade_out));
            errorView.startAnimation(AnimationUtils.loadAnimation(
                    getActivity(), android.R.anim.fade_in));
            listView.setVisibility(View.INVISIBLE);
            errorView.setVisibility(View.VISIBLE);
        }
        else {
        	// Hide error, show ListView
            errorView.startAnimation(AnimationUtils.loadAnimation(
                    getActivity(), android.R.anim.fade_out));
            listView.startAnimation(AnimationUtils.loadAnimation(
                    getActivity(), android.R.anim.fade_in));
            errorView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }
}