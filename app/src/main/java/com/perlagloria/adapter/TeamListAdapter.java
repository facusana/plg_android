package com.perlagloria.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.perlagloria.R;
import com.perlagloria.model.Team;

import java.util.Collections;
import java.util.List;

public class TeamListAdapter extends RecyclerView.Adapter<TeamListAdapter.MyViewHolder> {
    private List<Team> data = Collections.emptyList();
    private int lastSelectedIndex = -1;
    private OnCheckboxCheckedListener onCheckboxCheckedListener;

    public TeamListAdapter(List<Team> data, OnCheckboxCheckedListener onCheckboxCheckedListener) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_team_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Team current = data.get(position);

        holder.teamItemValue.setText(current.getName());
        holder.teamItemCheckBox.setChecked(current.isSelected());

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

        holder.teamItemLayout.setOnClickListener(new ItemClickListener());
        holder.teamItemCheckBox.setOnClickListener(new ItemClickListener());

        //holder.dividerView.setVisibility((data.size() - 1 == position) ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public Team getItem(int position) {
        return data.get(position);
    }

    public Team getSelectedItem() {
        return getItem(lastSelectedIndex);
    }

    public interface OnCheckboxCheckedListener {
        void onCheckboxChecked(Team team);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout teamItemLayout;
        TextView teamItemValue;
        CheckBox teamItemCheckBox;
        View dividerView;

        public MyViewHolder(View itemView) {
            super(itemView);

            teamItemLayout = (RelativeLayout) itemView.findViewById(R.id.teamItemLayout);
            teamItemValue = (TextView) itemView.findViewById(R.id.teamItemValue);
            teamItemCheckBox = (CheckBox) itemView.findViewById(R.id.teamItemCheckBox);
            dividerView = itemView.findViewById(R.id.horizDividerView);
        }
    }
}