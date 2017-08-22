package com.mad.memome.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mad.memome.activity.ViewActivity;
import com.mad.memome.model.Reminder;
import com.mad.memome.R;

import java.util.List;


/**
 * This adapter is used to combine and display all data on the card view
 */

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private Context context;
    private List<Reminder> reminderList;


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView title_pre;
        TextView start_time;
        TextView end_time;
        TextView content;
        TextView location;
        TextView status;
        TextView content_pre;
        TextView location_pre;
        TextView start_pre;
        TextView end_pre;
        private View view;

        public ViewHolder(final View view) {
            super(view);
            this.view = view;
            title = (TextView) view.findViewById(R.id.notification_title);
            start_time = (TextView) view.findViewById(R.id.notification_start_time);
            end_time = (TextView) view.findViewById(R.id.notification_end_time);
            content = (TextView) view.findViewById(R.id.notification_content);
            location = (TextView) view.findViewById(R.id.notification_location);
            status = (TextView) view.findViewById(R.id.notification_status);
            title_pre = (TextView) view.findViewById(R.id.title_pre);
            content_pre = (TextView) view.findViewById(R.id.content_pre);
            location_pre = (TextView) view.findViewById(R.id.location_pre);
            start_pre = (TextView) view.findViewById(R.id.start_pre);
            end_pre = (TextView) view.findViewById(R.id.end_pre);
        }
    }


    public ReminderAdapter(Context context, List<Reminder> reminderList) {
        this.context = context;
        this.reminderList = reminderList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reminder_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        viewHolder.start_pre.setText(R.string.start_at);
        viewHolder.location_pre.setText(R.string.location_pre);
        viewHolder.title_pre.setText(R.string.title_pre);
        viewHolder.end_pre.setText(R.string.end_at);
        viewHolder.title.setText(reminderList.get(position).getTitle());
        viewHolder.content_pre.setText(R.string.content_pre);
        viewHolder.content.setText(reminderList.get(position).getContent());
        viewHolder.start_time.setText((CharSequence) reminderList.get(position).getStartDateAndTime());
        viewHolder.end_time.setText((CharSequence) reminderList.get(position).getEndDateAndTime());
        viewHolder.location.setText(reminderList.get(position).getLocation());
        if (reminderList.get(position).getStatus().equals(context.getString(R.string.Done)))
            viewHolder.status.setTextColor(context.getResources().getColor(R.color.Done));
        else
            viewHolder.status.setTextColor(context.getResources().getColor(R.color.Undone));
        viewHolder.status.setText(reminderList.get(position).getStatus());
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewActivity.class);
                intent.putExtra(context.getString(R.string.notification_id), reminderList.get(viewHolder.getAdapterPosition()).getId());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }
}