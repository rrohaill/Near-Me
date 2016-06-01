package com.rohail.apps.nearme.adapters;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rohail.apps.nearme.R;
import com.rohail.apps.nearme.activities.MapActivity;
import com.rohail.apps.nearme.models.LocationModel;
import com.rohail.apps.nearme.utilities.Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements View.OnClickListener {

    public Context context;
    private ArrayList<LocationModel> list;
    private Location currentLocation;
    private ClickListener clickListener;
    private Type layoutType;
    private int stub;

    public RecyclerViewAdapter(Type layoutType, ArrayList<LocationModel> list, Location currentLocation, Context context) {
        this.context = context;
        this.list = list;
        this.currentLocation = currentLocation;
        this.layoutType = layoutType;
        this.stub = R.drawable.stub_location;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        switch (layoutType) {
            case linear:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row_layout, parent, false);
                break;
            case grid:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_grid_layout, parent, false);
                break;
        }

        v.findViewById(R.id.cardView).setOnClickListener(this);
        ViewHolder holder = new ViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.tvTitle.setText(list.get(position).getName());
        holder.tvAddress.setText(list.get(position).getVicinity());
        String distance = Utility.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), Double.parseDouble(list.get(position).getLatitude()), Double.parseDouble(list.get(position).getLongitude())) + "";

        holder.tvDistance.setText(distance.substring(0, 4) + " KM");
//        holder.imageView.setImageResource(list.get(position).imageId);
//        holder.image = (ImageView) vi.findViewById(R.id.icon);

        holder.imageView.getLayoutParams().height = 100;
        holder.imageView.getLayoutParams().width = 100;
        holder.imageView.requestLayout();
        holder.imageView.setVisibility(View.VISIBLE);

        Picasso.with(context).load(list.get(position).getIconUrl()).placeholder(stub).into(holder.imageView);

        holder.cv.setTag(position);
    }

    public void setClickListener(MapActivity clickListener) {
        this.clickListener = (ClickListener) clickListener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onClick(View v) {
        if (clickListener != null) {
            Log.d("position", v.getTag().toString());
            clickListener.onItemClicked(v, Integer.parseInt(v.getTag().toString()));
        }

    }

    public enum Type {
        linear, grid
    }

    public interface ClickListener {
        void onItemClicked(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //private RelativeLayout rlParent;
        private CardView cv;
        private TextView tvTitle, tvAddress, tvDistance;
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cardView);
            tvTitle = (TextView) itemView.findViewById(R.id.lblTitle);
            tvAddress = (TextView) itemView.findViewById(R.id.lblAddres);
            tvDistance = (TextView) itemView.findViewById(R.id.lblDistance);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

}
