package com.insalyon.les24heures.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
            holder.type = (LinearLayout) convertView.findViewById(R.id.typecategory);
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


        if(!init && position == selectedCategoryInit){
            ((ListView)parent).setItemChecked(position,position == selectedCategoryInit);
            init = true;
        }

        Boolean isSelected = (activity.getPositionCategorySelected() != null)?
                activity.getPositionCategorySelected() == position
                : false;
        
        if(isSelected==false) {
            switch (category.getName()) {
                case "divertissement":
                    holder.icon.setImageResource(R.drawable.animation_gris);
                    holder.type.setBackgroundColor(Color.rgb(248, 154, 29));
                    holder.title.setTextColor(Color.rgb(77, 77, 77));
                    holder.title.setTypeface(null, Typeface.NORMAL);
                    break;
                case "culturer":
                    holder.icon.setImageResource(R.drawable.culture_gris);
                    holder.type.setBackgroundColor(Color.rgb(105, 199, 186));
                    holder.title.setTextColor(Color.rgb(77, 77, 77));
                    holder.title.setTypeface(null, Typeface.NORMAL);
                    break;
                case "sportiver":
                    holder.icon.setImageResource(R.drawable.sport_gris);
                    holder.type.setBackgroundColor(Color.rgb(246, 237, 17));
                    holder.title.setTextColor(Color.rgb(77, 77, 77));
                    holder.title.setTypeface(null, Typeface.NORMAL);
                    break;
                case "prevention":
                    holder.icon.setImageResource(R.drawable.prevention_gris);
                    holder.type.setBackgroundColor(Color.rgb(173, 208, 55));
                    holder.title.setTextColor(Color.rgb(77, 77, 77));
                    holder.title.setTypeface(null, Typeface.NORMAL);
                    break;
                case "ALL":
                    holder.icon.setImageResource(R.drawable.ic_action_select_all);
                    holder.type.setBackgroundColor(Color.rgb(255,255,255));
                    holder.title.setTextColor(Color.rgb(77, 77, 77));
                    holder.title.setTypeface(null, Typeface.NORMAL);
                    break;
                case "REMAINING":
                    holder.icon.setImageResource(R.drawable.ic_action_time_black);
                    holder.type.setBackgroundColor(Color.rgb(255, 255, 255));
                    holder.title.setTextColor(Color.rgb(77, 77, 77));
                    holder.title.setTypeface(null, Typeface.NORMAL);
                    break;
                default:
                    holder.icon.setImageResource(R.drawable.ic_action_select_all);
                    holder.type.setBackgroundColor(Color.rgb(255, 255, 255));
                    holder.title.setTextColor(Color.rgb(77, 77, 77));
                    holder.title.setTypeface(null, Typeface.NORMAL);
                    break;
            }
        }
        else{
            switch (category.getName()) {
                case "divertissement":
                    holder.icon.setImageResource(R.drawable.animation_bleu);
                    holder.type.setBackgroundColor(Color.rgb(248, 154, 29));
                    holder.title.setTextColor(Color.rgb(17, 135, 142));
                    holder.title.setTypeface(null, Typeface.BOLD);
                    break;
                case "culturer":
                    holder.icon.setImageResource(R.drawable.culture_bleu);
                    holder.type.setBackgroundColor(Color.rgb(105, 199, 186));
                    holder.title.setTextColor(Color.rgb(17, 135, 142));
                    holder.title.setTypeface(null, Typeface.BOLD);
                    break;
                case "sportiver":
                    holder.icon.setImageResource(R.drawable.sport_bleu);
                    holder.type.setBackgroundColor(Color.rgb(246, 237, 17));
                    holder.title.setTextColor(Color.rgb(17, 135, 142));
                    holder.title.setTypeface(null, Typeface.BOLD);
                    break;
                case "prevention":
                    holder.icon.setImageResource(R.drawable.prevention_bleu);
                    holder.type.setBackgroundColor(Color.rgb(173, 208, 55));
                    holder.title.setTextColor(Color.rgb(17, 135, 142));
                    holder.title.setTypeface(null, Typeface.BOLD);
                    break;
                case "ALL":
                    holder.icon.setImageResource(R.drawable.ic_action_select_all_bleu);
                    holder.type.setBackgroundColor(Color.rgb(213, 218, 223));
                    holder.title.setTextColor(Color.rgb(17, 135, 142));
                    holder.title.setTypeface(null, Typeface.BOLD);
                    break;
                case "REMAINING":
                    holder.icon.setImageResource(R.drawable.ic_action_time_blue);
                    holder.type.setBackgroundColor(Color.rgb(213, 218, 223));
                    holder.title.setTextColor(Color.rgb(17, 135, 142));
                    holder.title.setTypeface(null, Typeface.BOLD);
                    break;
                default:
                    holder.icon.setImageResource(R.drawable.ic_action_select_all_bleu);
                    holder.type.setBackgroundColor(Color.rgb(213, 218, 223));
                    holder.title.setTextColor(Color.rgb(17, 135, 142));
                    holder.title.setTypeface(null, Typeface.BOLD);
                    break;
            }
        }

        return convertView;

    }

    public void onEvent(CategoriesUpdatedEvent event) {
        this.notifyDataSetInvalidated();
    }

    private class ViewHolder {
        LinearLayout type;
        TextView title;
        ImageView icon;
    }

    public void setSelectedCategoryInit(int selectedCategoryInit) {
        this.selectedCategoryInit = selectedCategoryInit;
    }

}
