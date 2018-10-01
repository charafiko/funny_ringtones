package com.acc.gp.ringtones.funny.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.acc.gp.ringtones.funny.R;
import com.acc.gp.ringtones.funny.interfaces.ActionClickListener;
import com.acc.gp.ringtones.funny.interfaces.ItemClickListener;
import com.acc.gp.ringtones.funny.model.Ringtone;

import java.util.ArrayList;

public class RingtoneAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<Ringtone> ringtoneList;
    private ItemClickListener mClickListener;
    private ActionClickListener mActionClickListener;

    public void onItemClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public void onActionClickListener(ActionClickListener actionClickListener) {
        this.mActionClickListener = actionClickListener;
    }

    public RingtoneAdapter(Context mContext, ArrayList<Ringtone> ringtoneList) {
        this.mContext = mContext;
        this.ringtoneList = ringtoneList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_ringtone, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        Ringtone ringtone = ringtoneList.get(position);

        viewHolder.tvTitle.setText(ringtone.getName());
        viewHolder.tvDuration.setText(ringtone.getDuration());

        viewHolder.lnItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(position);
            }
        });

        viewHolder.imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActionClickListener.onActionClick(position, viewHolder.imgPlay);
            }
        });


    }

    @Override
    public int getItemCount() {
        return ringtoneList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout lnItem;
        ImageView imgPlay;
        TextView tvTitle;
        TextView tvDuration;
        ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            lnItem = itemView.findViewById(R.id.lnItem);
            imgPlay = itemView.findViewById(R.id.imgPlay);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
