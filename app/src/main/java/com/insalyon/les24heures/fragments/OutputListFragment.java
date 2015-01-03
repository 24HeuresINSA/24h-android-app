package com.insalyon.les24heures.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.insalyon.les24heures.MainActivity;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.adapter.ResourceAdapter;
import com.insalyon.les24heures.eventbus.CategoriesSelectedEvent;
import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.model.Resource;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by remi on 26/12/14.
 */
public class OutputListFragment extends OutputTypeFragment{
    private static final String TAG = OutputListFragment.class.getCanonicalName();

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
    ListView resourceListView;


    Boolean spinner = false; //TODO mettre en place un vrai spinner

//    public ArrayList<Resource> resourcesList;



    //SANDBOX

    ResourceAdapter resourceAdapter = null;
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        resourceAdapter.getFilter().filter(s.toString());
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

        view = inflater.inflate(R.layout.output_list_fragment, container, false);
        ButterKnife.inject(this, view);

        //create an ArrayAdaptar from the String Array
        resourceAdapter = new ResourceAdapter(this.getActivity().getApplicationContext(),
                R.layout.output_list_item, resourcesList);
        // Assign adapter to ListView
        resourceListView.setAdapter(resourceAdapter);

        //enables filtering for the contents of the given ListView
        resourceListView.setTextFilterEnabled(true);

        resourceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Resource resource = (Resource) parent.getItemAtPosition(position);
                //TODO details cf DSF
                Toast.makeText(getActivity().getApplicationContext(),
                        resource.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });


        //filter by text
        searchText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                resourceAdapter.getFilter().filter(s.toString());
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity)getActivity()).setTitle(R.string.drawer_outputtype_list);

        updateListView();
    }

    //default filter
    private Boolean updateListView(){

        //TODO text search or categories filter priority stand here
        resourceAdapter.getFilter().filter(null);
//        resourceAdapter.getCategoryFilter().filter(null);


        searchText.setText("");
        searchText.clearFocus();

//
//
//        if(resourcesList.isEmpty()){
//            Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.noResourcesFound, Toast.LENGTH_SHORT);
//            toast.show();
//            ((MainActivity) getActivity()).displayDrawer();
//            //TODO display a spinner
//            spinner = true;
//            return false;
//        }
//        resourceAdapter.notifyDataSetChanged();
//        if(categoriesSelected.isEmpty()) {
//            Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.noCategoriesSelected, Toast.LENGTH_SHORT);
//            toast.show();
//            ((MainActivity) getActivity()).displayDrawer();
//            return false;
//        }
//        resourceAdapter.notifyDataSetChanged();
        return true;
    }


    //filter by categories
    public void onEvent(CategoriesSelectedEvent event) {
        super.onEvent(event);
        Log.d(TAG+"onEvent(CategoryEvent)", event.getCategories().toString());
        resourceAdapter.getCategoryFilter().filter(
                (event.getCategories().size() != 0) ? event.getCategories().toString() : null
        );

    }

    public void onEvent(ResourcesUpdatedEvent event) {
       super.onEvent(event);

        updateListView();


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(spinner){
                    spinner = false;
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.resources_found, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

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
