package com.app.trusec;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class LeaveRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    Context context = null;
    private ArrayList<LeaveDetails> leaveDetailsList;



    public LeaveRecyclerViewAdapter(ArrayList<LeaveDetails> appliedLeaves, Context context) {
        this.leaveDetailsList = appliedLeaves;
        this.context = context;

    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (position == TYPE_HEADER) {
            View shiftHeader = inflater.inflate(R.layout.applied_leave_list_header, parent, false);
            return new ViewHeaderHolder(shiftHeader);
        } else if (position == TYPE_ITEM) {
            View shiftListItem = inflater.inflate(R.layout.leave_display_list_item, parent, false);
            // Return a new holder instance
            return new ViewHolder(shiftListItem);
        }
        throw new RuntimeException("There is no type that matches the type " + position + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder view, int position) {
        if (view instanceof ViewHeaderHolder) {
            bindHeaderView((ViewHeaderHolder) view);
        } else if (view instanceof ViewHolder) {
            bindItemView((ViewHolder) view, position);
        }
    }

    private void bindHeaderView(@NonNull ViewHeaderHolder view) {
        ViewHeaderHolder viewHeader = view;
    }


    private void bindItemView(@NonNull final ViewHolder view, final int position) {
        final ViewHolder viewHolder = view;

        final LeaveDetails leaveDetails = leaveDetailsList.get(position - 1);

        viewHolder.reasonText = leaveDetails.getReason();

        TextView fromDate = viewHolder.fromDate;
        fromDate.setText(leaveDetails.getFromDate());

        TextView toDate = viewHolder.toDate;
        toDate.setText(leaveDetails.getToDate());

        final TextView leaveSerial = viewHolder.leaveSerial;
        leaveSerial.setText(Integer.toString(position));


        viewHolder.reasonIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog leaveDisplay = new Dialog(context);
                //chooseNumDialog.setTitle("Select a phone number to call...");
                leaveDisplay.setContentView(R.layout.leave_display);
                TextView viewLeaveDate =  leaveDisplay.findViewById(R.id.viewLeaveDate);
                TextView viewLeaveReason =  leaveDisplay.findViewById(R.id.viewLeaveReason);
                TextView viewLeaveStatus =  leaveDisplay.findViewById(R.id.viewLeaveStatus);
                TextView viewAdminResponse =  leaveDisplay.findViewById(R.id.viewAdminResponse);
                TextView leaveDisplayHeader = leaveDisplay.findViewById(R.id.leaveDisplayHeader);
                if(leaveDetails.getFromDate().equals(leaveDetails.getToDate())){
                    viewLeaveDate.setText(leaveDetails.getFromDate());
                }else{
                    viewLeaveDate.setText(leaveDetails.getFromDate()+ " to " + leaveDetails.getToDate());
                }
                viewLeaveReason.setText(leaveDetails.getReason());
                if(leaveDetails.getStatus().equals(context.getResources().getString(R.string.LEAVE_APPROVED))){
                    viewLeaveStatus.setText("Approved");
                } else if (leaveDetails.getStatus().equals(context.getResources().getString(R.string.LEAVE_DECLINED))){
                    viewLeaveStatus.setText("Declined");
                } else{
                    viewLeaveStatus.setText("Pending");
                }
                leaveDisplayHeader.setText(leaveDisplayHeader.getText().toString() + position);
                leaveDisplay.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                leaveDisplay.show();
            }
        });

        if(leaveDetails.getStatus().equals(context.getResources().getString(R.string.LEAVE_APPROVED))){
                    viewHolder.leaveStatus.setBackgroundColor(context.getResources().getColor(R.color.acceptColor));
        } else if (leaveDetails.getStatus().equals(context.getResources().getString(R.string.LEAVE_DECLINED))){
            viewHolder.leaveStatus.setBackgroundColor(context.getResources().getColor(R.color.declineColor));
        } else{
            viewHolder.leaveStatus.setBackgroundColor(context.getResources().getColor(R.color.pendingColor));
        }


    }




    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }


    @Override
    public int getItemCount() {
            return leaveDetailsList.size() + 1;
    }


    public void refreshData() {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView reasonIcon;
        public TextView fromDate;
        public TextView toDate;
        public String reasonText;
        public View leaveStatus;
        public TextView leaveSerial;

        public ViewHolder(View itemView) {
            super(itemView);
            reasonIcon =  itemView.findViewById(R.id.leaveReasonIcon);
            fromDate = (TextView) itemView.findViewById(R.id.appliedLeaveFromDate);
            toDate = (TextView) itemView.findViewById(R.id.appliedLeaveToDate);
            leaveStatus = itemView.findViewById(R.id.leaveStatus);
            leaveSerial = itemView.findViewById(R.id.leaveSerial);
            reasonText = "";
        }
    }

    public class ViewHeaderHolder extends RecyclerView.ViewHolder {

        public ViewHeaderHolder(View headerView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(headerView);

        }
    }

}
