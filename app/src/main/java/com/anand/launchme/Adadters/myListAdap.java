package com.anand.launchme.Adadters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anand.launchme.AppSettings.SettingsActivity;
import com.anand.launchme.R;
import com.anand.launchme.Appinfo.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class myListAdap extends RecyclerView.Adapter<myListAdap.ViewHolder> implements Filterable {

    Context context;
    private List<AppInfo> apps;
    private List<AppInfo> searchApps;
    private List<CharSequence> homeApps;
    PackageManager packageManager;
    searchFilter searchFilter;

    public myListAdap(Context context, List<AppInfo> apps) {
        this.apps = apps;
        this.context = context;
        searchApps = new ArrayList<>(apps);
        homeApps = new ArrayList<CharSequence>();
        packageManager = context.getPackageManager();
    }

    @NonNull
    @Override
    public myListAdap.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.grd_items, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myListAdap.ViewHolder holder, int position) {

        AppInfo appInfo = apps.get(position);

        if (appInfo != null) {
            holder.appImg.setImageDrawable(appInfo.icon);
            holder.appNametxt.setText(appInfo.label);
            if (SettingsActivity.appName){
                holder.appNametxt.setVisibility(View.VISIBLE);
            } else {
                holder.appNametxt.setVisibility(View.GONE);
                holder.rootCard.setPadding(10,10,10,10);
            }
            holder.rootCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = packageManager.getLaunchIntentForPackage(apps.get(position).name.toString());
                    context.startActivity(intent);
                }
            });
            
            holder.rootCard.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toast.makeText(context, "Long Press", Toast.LENGTH_SHORT).show();
                    homeApps.add(appInfo.label);
                    Log.d("TAG",homeApps.size()+"Size");
                    return true;
                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return apps.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rootCard;
        ImageView appImg;
        TextView appNametxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appImg = itemView.findViewById(R.id.img_icon);
            appNametxt = itemView.findViewById(R.id.txt_label);
            rootCard = itemView.findViewById(R.id.root_card_layout);
        }
    }

    @Override
    public Filter getFilter() {

        if (searchFilter == null) {
            searchFilter = new searchFilter();
        }

        return searchFilter;
    }

    private class searchFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<AppInfo> filteredList = new ArrayList<>();

            Log.d("TAG_APP", "APP : " + searchApps.toString());

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(searchApps);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (AppInfo info : apps) {

                    String source = info.label.toString();
                    String s2 = (String) source;

                    final String s = String.valueOf(s2);

                    if (source.toLowerCase().contains(filterPattern)) {
                        filteredList.add(info);
                    }


                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            apps.clear();
            apps.addAll((List) filterResults.values);
            notifyDataSetChanged();

        }
    }

    ;
}
