package com.perlagloria.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.perlagloria.R;
import com.perlagloria.model.Division;

import java.util.Collections;
import java.util.List;

public class DivisionListAdapter extends RecyclerView.Adapter<DivisionListAdapter.MyViewHolder> {
    private List<Division> data = Collections.emptyList();
    private int lastSelectedIndex = -1;
    private OnCheckboxCheckedListener onCheckboxCheckedListener;

    public DivisionListAdapter(List<Division> data, OnCheckboxCheckedListener onCheckboxCheckedListener) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_division_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Division current = data.get(position);

        holder.divisItemValue.setText(current.getName());
        holder.divisItemCheckBox.setChecked(current.isSelected());

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

        holder.divisItemLayout.setOnClickListener(new ItemClickListener());
        holder.divisItemCheckBox.setOnClickListener(new ItemClickListener());

        //holder.dividerView.setVisibility((data.size() - 1 == position) ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public Division getItem(int position) {
        return data.get(position);
    }

    public Division getSelectedItem() {
        return getItem(lastSelectedIndex);
    }

    public interface OnCheckboxCheckedListener {
        void onCheckboxChecked(Division division);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout divisItemLayout;
        TextView divisItemValue;
        CheckBox divisItemCheckBox;
        View dividerView;

        public MyViewHolder(View itemView) {
            super(itemView);

            divisItemLayout = (RelativeLayout) itemView.findViewById(R.id.divisItemLayout);
            divisItemValue = (TextView) itemView.findViewById(R.id.divisItemValue);
            divisItemCheckBox = (CheckBox) itemView.findViewById(R.id.divisItemCheckBox);
            dividerView = itemView.findViewById(R.id.horizDividerView);
        }
    }
}
