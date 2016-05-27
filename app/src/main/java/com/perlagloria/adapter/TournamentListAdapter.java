package com.perlagloria.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.perlagloria.R;
import com.perlagloria.model.Tournament;
import com.perlagloria.util.FontManager;

import java.util.Collections;
import java.util.List;

public class TournamentListAdapter extends RecyclerView.Adapter<TournamentListAdapter.MyViewHolder> {

    private Context context;
    private List<Tournament> data = Collections.emptyList();
    private OnCheckboxCheckedListener onCheckboxCheckedListener;
    private int lastSelectedIndex = -1;

    public TournamentListAdapter(Context context, List<Tournament> data, OnCheckboxCheckedListener onCheckboxCheckedListener) {
        this.context = context;
        this.data = data;
        this.onCheckboxCheckedListener = onCheckboxCheckedListener;
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_tournament_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Tournament current = data.get(position);

        holder.tournItemValue.setText(current.getName());

        if (current.isSelected()) {
            holder.checkIcon.setColorFilter(ContextCompat.getColor(context, R.color.colorSelectedItem));
        } else {
            holder.checkIcon.setColorFilter(ContextCompat.getColor(context, R.color.colorUnselectedItem));
        }

        //holder.dividerView.setVisibility((data.size() - 1 == position) ? View.GONE : View.VISIBLE);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public Tournament getItem(int position) {
        return data.get(position);
    }

    public Tournament getSelectedItem() {
        return getItem(lastSelectedIndex);
    }

    public interface OnCheckboxCheckedListener {
        void onCheckboxChecked(Tournament tournament);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout tournItemLayout;
        private TextView tournItemValue;
        private ImageView checkIcon;
        private View dividerView;

        public MyViewHolder(View itemView) {
            super(itemView);

            tournItemLayout = (RelativeLayout) itemView.findViewById(R.id.tournItemLayout);
            tournItemValue = (TextView) itemView.findViewById(R.id.tournItemValue);
            checkIcon = (ImageView) itemView.findViewById(R.id.check_icon);
            dividerView = itemView.findViewById(R.id.horizDividerView);

            tournItemValue.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, context));

            tournItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() == RecyclerView.NO_POSITION) return;

                    if (lastSelectedIndex != -1) {    //unselect last selected item
                        data.get(lastSelectedIndex).setIsSelected(false);
                        notifyItemChanged(lastSelectedIndex);
                    }
                    data.get(getAdapterPosition()).setIsSelected(true);
                    notifyItemChanged(getAdapterPosition());

                    lastSelectedIndex = getAdapterPosition();
                    onCheckboxCheckedListener.onCheckboxChecked(getItem(getAdapterPosition()));  //send message back to fragment (to change selected tournament title)
                }
            });
        }
    }
}
