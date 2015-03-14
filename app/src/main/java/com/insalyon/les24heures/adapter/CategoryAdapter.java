package com.insalyon.les24heures.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.insalyon.les24heures.R;
import com.insalyon.les24heures.eventbus.CategoriesUpdatedEvent;
import com.insalyon.les24heures.model.Category;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by remi on 01/03/15.
 */
public class CategoryAdapter extends ArrayAdapter<Category> {

    private final int viewId;
    private final EventBus eventBus;
    private Boolean init = false;
    private int selectedCategoryInit;

    LayoutInflater vi;
    private List<Category> categories;

    public CategoryAdapter(Context context, int resource, List<Category> categories) {
        super(context, resource, categories);
        this.viewId = resource;
        this.categories = categories;
//        this.categories = new ArrayList<>();
//        this.categories.addAll(categories);
//        this.categories.add(new Category("allConstructor","pouet"));//TODO all category....
        selectedCategoryInit = -1;//categories.size()-1;

        this.vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //je voulais pas ca moi !
        eventBus = EventBus.getDefault();
        eventBus.register(this);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            convertView = vi.inflate(viewId, null);

            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.drawer_item_title);
            holder.icon = (ImageView) convertView.findViewById(R.id.drawer_item_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Category category = categories.get(position);

        holder.title.setText(category.getName());

        if(!init && position == selectedCategoryInit){
            ((ListView)parent).setItemChecked(position,position == selectedCategoryInit);
            init = true;
        }

        return convertView;

    }

    public void onEvent(CategoriesUpdatedEvent event) {
        this.notifyDataSetInvalidated();
    }


    private class ViewHolder {
        TextView title;
        ImageView icon;
    }

    public void setSelectedCategoryInit(int selectedCategoryInit) {
        this.selectedCategoryInit = selectedCategoryInit;
    }
}
