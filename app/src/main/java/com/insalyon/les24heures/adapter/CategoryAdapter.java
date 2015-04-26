package com.insalyon.les24heures.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.insalyon.les24heures.BaseActivity;
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

    BaseActivity activity;


    public CategoryAdapter(Context context, int resource, List<Category> categories, BaseActivity activity) {
        super(context, resource, categories);
        this.viewId = resource;
        this.categories = categories;
        this.activity = activity;
        selectedCategoryInit = -1;

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

        if(category.getDisplayName() != null && category.getDisplayName().equals("ALL_LABEL")) {
            category.setDisplayName(getContext().getResources().getString(R.string.category_all_label));
        } else  if(category.getDisplayName() != null && category.getDisplayName().equals("REMAINING_LABEL")) {
            category.setDisplayName(getContext().getResources().getString(R.string.category_remaining_label));
        }
        holder.title.setText(category.getDisplayName());

        Boolean isSelected = activity.getPositionCategorySelected() == position;
        

        switch(category.getName()){
            case "divertissement":
                holder.icon.setImageResource(R.drawable.category_divert);
                break;
            case "culturer":
                holder.icon.setImageResource(R.drawable.category_culture);
                break;
            case "sportiver":
                holder.icon.setImageResource(R.drawable.category_sport);
                break;
            case "prevention":
                holder.icon.setImageResource(R.drawable.category_prevention);
                break;
            case "ALL":
                holder.icon.setImageResource(R.drawable.ic_action_select_all);
                break;
            case "REMAINING":
                holder.icon.setImageResource(R.drawable.ic_now);
                break;
            default:
                holder.icon.setImageResource(R.drawable.ic_action_select_all);
                break;
        }

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

    //TODO utiliser ces deux fonctions sur le on click drawer category item

   /* private setSelectedCategoryItem(ViewHolder holder, View convertView, int position) {
        holder = (ViewHolder) convertView.getTag();
        switch (categories.get(position).getName()){
            case "divertissement":
                holder.icon.setImageResource(R.drawable.animation_bleu);
                 break;
            case "culturer":
                holder.icon.setImageResource(R.drawable.culture_bleu);
                break;
            case "sportiver":
                holder.icon.setImageResource(R.drawable.sport_bleu);
                 break;
            case "prevention":
                holder.icon.setImageResource(R.drawable.prevention_bleu);
                break;
            case "ALL":
                holder.icon.setImageResource(R.drawable.ic_action_select_all_bleu);
                break;
            case "REMAINING":
                holder.icon.setImageResource(R.drawable.ic_now_bleu);
                break;
            default:
                holder.icon.setImageResource(R.drawable.ic_action_select_all);
                break;
        }
        holder.title.setTextColor(Color.CYAN);
        holder.title.setTypeface(null, Typeface.BOLD);

    }

    public void setUnselectedCategoryItem(View v, int iconRes) {
        for (int i = 0; i < ((ViewGroup) v).getChildCount(); ++i) {
            View nextChild = ((ViewGroup) v).getChildAt(i);
            if (nextChild instanceof ImageView && iconRes!=0) {
                ImageView drawerSelectedIcon = (ImageView) nextChild;
                drawerSelectedIcon.setImageResource(iconRes);
                Log.d(v.toString(), "icon desactivated");
            } else if (nextChild instanceof TextView) {
                TextView drawerSelectedText = (TextView) nextChild;
              //  drawerSelectedText.setTextColor(getResources().getColor(R.color.drawer_label_default));
                drawerSelectedText.setTypeface(null, Typeface.NORMAL);
                Log.d(v.toString(), "text desactivated");
            }
        }
    }*/

}
