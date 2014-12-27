package com.insalyon.les24heures.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.insalyon.les24heures.MainActivity;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.adapter.ResourceAdapter;
import com.insalyon.les24heures.eventbus.CategoryEvent;
import com.insalyon.les24heures.model.Category;
import com.insalyon.les24heures.model.Resource;
import com.insalyon.les24heures.utils.OutputType;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by remi on 26/12/14.
 */
public class ListFragment extends OutputTypeFragment{
    private static final String TAG = ListFragment.class.getCanonicalName();

    View view;
    @InjectView(R.id.list_sort_alphabetical)
    View alphabeticalSort;
    @InjectView(R.id.list_sort_loc)
    View locSort;
    @InjectView(R.id.list_sort_time_loc)
    View timeLocSort;
    @InjectView(R.id.list_search_text)
    TextView searchText;
    @InjectView(R.id.list_resource)
    ListView resourceList;





    private ArrayList<Resource> resourcesList;



    //SANDBOX

    ResourceAdapter dataAdapter = null;
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        dataAdapter.getFilter().filter(s.toString());
    }

    // /SANDBOX

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.list_fragment, container, false);
        ButterKnife.inject(this, view);

        //SANDBOX
        //TODO je ne sais pas si s'il vaut mieux passer par getActivity que de passer par un bundle
        resourcesList = ((MainActivity) getActivity()).getResourcesList();

        //create an ArrayAdaptar from the String Array
        dataAdapter = new ResourceAdapter(this.getActivity().getApplicationContext(),
                R.layout.list_item, resourcesList);
        // Assign adapter to ListView
        resourceList.setAdapter(dataAdapter);

        //enables filtering for the contents of the given ListView
        resourceList.setTextFilterEnabled(true);

        resourceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Resource resource = (Resource) parent.getItemAtPosition(position);
                Toast.makeText(getActivity().getApplicationContext(),
                        resource.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        searchText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dataAdapter.getFilter().filter(s.toString());
            }
        });


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity)getActivity()).setTitle(R.string.drawer_outputtype_list);
    }

    public void onEvent(CategoryEvent event) {
        super.onEvent(event);
        Log.d(TAG+"onEvent(CategoryEvent)", event.getCategories().toString());

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



}
