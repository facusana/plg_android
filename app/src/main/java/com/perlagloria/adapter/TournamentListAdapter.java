package com.perlagloria.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.perlagloria.R;
import com.perlagloria.model.Tournament;

import java.util.Collections;
import java.util.List;

public class TournamentListAdapter extends RecyclerView.Adapter<TournamentListAdapter.MyViewHolder> {
    private List<Tournament> data = Collections.emptyList();
    private int lastSelectedIndex = -1;
    private OnCheckboxCheckedListener onCheckboxCheckedListener;

    public TournamentListAdapter(List<Tournament> data, OnCheckboxCheckedListener onCheckboxCheckedListener) {
        super();
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
        holder.tournItemCheckBox.setChecked(current.isSelected());

        class ItemClickListener implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                if (lastSelectedIndex != -1) {    //unselect last selected item
                    data.get(lastSelectedIndex).setIsSelected(false);
                    notifyItemChanged(lastSelectedIndex);
                }
                data.get(position).setIsSelected(true);
                notifyItemChanged(position);

                lastSelectedIndex = position;
                onCheckboxCheckedListener.onCheckboxChecked(getItem(position));  //send message back to fragment (to change selected championship title)
            }
        }

        holder.tournItemLayout.setOnClickListener(new ItemClickListener());
        holder.tournItemCheckBox.setOnClickListener(new ItemClickListener());

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

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout tournItemLayout;
        TextView tournItemValue;
        CheckBox tournItemCheckBox;
        View dividerView;

        public MyViewHolder(View itemView) {
            super(itemView);

            tournItemLayout = (RelativeLayout) itemView.findViewById(R.id.tournItemLayout);
            tournItemValue = (TextView) itemView.findViewById(R.id.tournItemValue);
            tournItemCheckBox = (CheckBox) itemView.findViewById(R.id.tournItemCheckBox);
            dividerView = itemView.findViewById(R.id.horizDividerView);
        }
    }
}
