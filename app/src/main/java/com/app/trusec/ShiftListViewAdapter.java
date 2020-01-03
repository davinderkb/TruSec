package com.app.trusec;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShiftListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private static final int NAVIGATE_BACK = -1;
    private static final int NAVIGATE_CURRENT = 0;
    private static final int NAVIGATE_NEXT = 1;
    Context context = null;
    private List<DailyShiftData> shiftData;
    private ShiftHeader shiftHeader;
    private String currentWeekUrl;
    private String currentVisiblePageUrl;
    private String nextUrl;
    private String preUrl;
    private String userId;
    private boolean isCurrentWeek;
    private String weekStartDate;
    private String weekEndDate;
    private boolean isPastWeek;


    public ShiftListViewAdapter(ShiftHeader shiftHeader, WeeklyShiftData weeklyShiftData, String currentVisibleRosterUrl) {
        this.shiftData = weeklyShiftData.getShiftData();
        this.currentWeekUrl = weeklyShiftData.getCurrentShift();
        this.nextUrl = weeklyShiftData.getNextUrl();
        this.preUrl = weeklyShiftData.getPreUrl();
        this.userId = weeklyShiftData.getUserId();
        this.shiftHeader = shiftHeader;
        this.isCurrentWeek = weeklyShiftData.isCurrentWeek();
        this.weekStartDate = weeklyShiftData.getStartDate();
        this.weekEndDate = weeklyShiftData.getEndDate();
        this.isPastWeek = weeklyShiftData.isPastWeek();
        currentVisiblePageUrl = currentVisibleRosterUrl;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);


        if (position == TYPE_HEADER) {
            View shiftHeader = inflater.inflate(R.layout.shift_header, parent, false);
            return new ViewHeaderHolder(shiftHeader);
        } else if (position == TYPE_ITEM) {
            View shiftListItem = inflater.inflate(R.layout.shift_list_view, parent, false);
            // Return a new holder instance
            return new ViewHolder(shiftListItem);
        } else if ((position == TYPE_FOOTER)) {
            View shiftFooter = inflater.inflate(R.layout.shift_footer, parent, false);
            return new ViewFooterHolder(shiftFooter);
        }
        throw new RuntimeException("There is no type that matches the type " + position + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder view, int position) {
        if (view instanceof ViewHeaderHolder) {
            bindHeaderView((ViewHeaderHolder) view);
        } else if (view instanceof ViewHolder) {
            bindItemView((ViewHolder) view, position);
        } else if (view instanceof ViewFooterHolder) {
            bindFooterView((ViewFooterHolder) view);
        }

    }

    private void bindHeaderView(@NonNull ViewHeaderHolder view) {
        ViewHeaderHolder viewHeader = view;
        viewHeader.shiftTitle.setText(shiftHeader.getShiftTitle());
    }

    private void bindFooterView(@NonNull ViewFooterHolder view) {
        ViewFooterHolder viewFooterHolder = view;
        viewFooterHolder.accept.setVisibility(View.VISIBLE);
        viewFooterHolder.decline.setVisibility(View.VISIBLE);
        viewFooterHolder.emptyView.setVisibility(View.GONE);
        if (isPastWeek) {
            viewFooterHolder.accept.setVisibility(View.GONE);
            viewFooterHolder.decline.setVisibility(View.GONE);
        }
        if (shiftData.size() == 0) {
            viewFooterHolder.accept.setVisibility(View.GONE);
            viewFooterHolder.decline.setVisibility(View.GONE);
            viewFooterHolder.emptyView.setVisibility(View.VISIBLE);
        }
    }

    private void bindItemView(@NonNull final ViewHolder view, int position) {
        final ViewHolder viewHolder = view;

        final DailyShiftData dailyShiftData = shiftData.get(position - 1);

        TextView shiftDay = viewHolder.shiftDay;
        shiftDay.setText(dailyShiftData.getShiftDay());

        TextView shiftDate = viewHolder.shiftDate;
        shiftDate.setText(dailyShiftData.getShortShiftDate());

        TextView shiftTiming = viewHolder.shiftTiming;
        shiftTiming.setText(dailyShiftData.getShiftTiming());

        TextView clientName = viewHolder.clientName;
        clientName.setText(dailyShiftData.getClientName());

        if (shiftData.get(position - 1).isShiftAvailable()) {
            setAvailableShifts(position, viewHolder, dailyShiftData);
        } else if (!shiftData.get(position - 1).isShiftAvailable()) {
            setToastForUnavailableShifts(viewHolder, context.getResources().getString(R.string.UNAVAILABLE_SHIFT_LONG_SELECT_MSG), context.getResources().getString(R.string.UNAVAILABLE_SHIFT_ON_CLICK_MSG));
        } /*else if (!isCurrentWeek) {
            setToastForUnavailableShifts(viewHolder, context.getResources().getString(R.string.NOT_CURRENT_WEEK_LONG_SELECT_MSG), context.getResources().getString(R.string.NOT_CURRENT_WEEK_NO_CLICK_MSG));
        }*/

        if (viewHolder.itemDateLayout != null && !shiftData.get(position - 1).isShiftAvailable()) {
            decorateViewForUnavailableShifts(viewHolder);
        }
    }

    private void decorateViewForUnavailableShifts(ViewHolder viewHolder) {
        viewHolder.itemDateLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        viewHolder.shiftDate.setBackgroundColor(Color.parseColor("#DFEDFA"));
        viewHolder.shiftDay.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        viewHolder.shiftDay.setTextColor(Color.parseColor("#D1CCCC"));

        viewHolder.shiftDate.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        viewHolder.shiftDate.setTextColor(Color.parseColor("#D1CCCC"));

        viewHolder.sideColorView.setBackgroundColor(Color.parseColor("#D1CCCC"));
        viewHolder.clientName.setTextColor(Color.parseColor("#D1CCCC"));
        viewHolder.shiftTiming.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        viewHolder.clientName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            viewHolder.shiftCard.setElevation(0.0f);
            viewHolder.dateCard.setElevation(0.0f);
            viewHolder.rosterIcon.setColorFilter(ContextCompat.getColor(context, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        //viewHolder.shiftItemCheckBox.setEnabled(false);
    }

    private void setToastForUnavailableShifts(ViewHolder viewHolder, final String longClickMsg, final String onClickMsg) {
        viewHolder.itemLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, longClickMsg, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        viewHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, onClickMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAvailableShifts(int position, final ViewHolder viewHolder, final DailyShiftData dailyShiftData) {
        setRosterConfirmColor(position - 1, viewHolder);

        viewHolder.itemLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(!isPastWeek){
                    dailyShiftData.setSelected(!dailyShiftData.isSelected());
                    if (dailyShiftData.isSelected()) {
                        viewHolder.itemLayout.setBackgroundColor(context.getResources().getColor(R.color.blueGrey));
                        viewHolder.itemDateLayout.setBackgroundColor(context.getResources().getColor(R.color.blueGrey));
                        viewHolder.shiftCardLayout.setBackgroundColor(context.getResources().getColor(R.color.blueGrey));
                    } else {
                        viewHolder.itemLayout.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                        viewHolder.itemDateLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
                        viewHolder.shiftCardLayout.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                    }
                }
                else{
                    Toast.makeText(context, "Selection not allowed for past week shifts", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        viewHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVenueDetails(dailyShiftData.getClientId(),dailyShiftData.isAllowCheckIn(),dailyShiftData.getShiftDate(),dailyShiftData.getShiftDay(),dailyShiftData.getRosterConfirmCode(),dailyShiftData.getShiftTiming(), dailyShiftData.getShiftId());
                //Toast.makeText(context, "TO DO", Toast.LENGTH_SHORT).show();
            }

            private void getVenueDetails(String clientId, final boolean allowCheckIn, final String shiftDate, final String shiftDay, final String rosterConfirmCode, final String shiftTiming, final String shiftId) {
                RequestQueue venueDetailReqQ = Volley.newRequestQueue(context);
                String companyUrl = new SessionManager(context).getUserDetails().get(SessionManager.KEY_COMPANY_URL);
                String url = companyUrl + "/api/get_venue/" + clientId;

                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // display response
                                Intent intent = new Intent(context, VenueDetailsActivity.class);
                                intent.putExtra(context.getResources().getString(R.string.JSON_KEY_RESPONSE), response.toString());
                                intent.putExtra(context.getResources().getString(R.string.IS_ALLOW_CHECK_IN), allowCheckIn);
                                intent.putExtra(context.getResources().getString(R.string.DATE_SELECTED), shiftDate);
                                intent.putExtra(context.getResources().getString(R.string.DAY_SELECTED), shiftDay);
                                intent.putExtra(context.getResources().getString(R.string.ROSTER_STATUS), rosterConfirmCode);
                                intent.putExtra(context.getResources().getString(R.string.SHIFT_TIME), shiftTiming);
                                intent.putExtra(context.getResources().getString(R.string.SHIFT_ID), shiftId);
                                ((Activity) context).startActivity(intent);
                                ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                VolleyLog.e("Error: ", "Error while fetching shift details. " + error.getMessage());
                                Toast.makeText(context, "Error while fetching shift details", Toast.LENGTH_LONG).show();
                            }
                        }
                );
                venueDetailReqQ.add(getRequest); }
        });
    }

    private void setRosterConfirmColor(int position, ViewHolder viewHolder) {
        if (shiftData.get(position).getRosterConfirmCode().equals(context.getResources().getString(R.string.ROSTER_CONFIRM_ACCEPT))) {
            viewHolder.sideColorView.setBackgroundColor(context.getResources().getColor(R.color.acceptColor));
        } else if (shiftData.get(position).getRosterConfirmCode().equals(context.getResources().getString(R.string.ROSTER_CONFIRM_DECLINE))) {
            viewHolder.sideColorView.setBackgroundColor(context.getResources().getColor(R.color.declineColor));
        } else {
            viewHolder.sideColorView.setBackgroundColor(context.getResources().getColor(R.color.pendingColor));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        if (isPositionFooter(position))
            return TYPE_FOOTER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private boolean isPositionFooter(int position) {
        return position > shiftData.size();
    }

    @Override
    public int getItemCount() {
        if (!isPastWeek || shiftData.size() == 0)
            return shiftData.size() + 2;
        else
            return shiftData.size() + 1;
    }

    private void confirmRoster(final String shiftId, final String shiftDate) {

        RequestQueue shiftDataRequestQ = Volley.newRequestQueue(context);
        SessionManager session = new SessionManager(context);
        HashMap<String, String> user = session.getUserDetails();
        String companyUrl = user.get(SessionManager.KEY_COMPANY_URL);

        StringRequest confirmRosterRequest = new StringRequest(Request.Method.POST, companyUrl + "/api/confirm_roster",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            Toast.makeText(context, "Accepted successfully", Toast.LENGTH_LONG).show();
                            if(!currentVisiblePageUrl.equals(""))
                                 sendHttpReqToGetShiftData(currentVisiblePageUrl, userId, NAVIGATE_CURRENT);
                            else
                                sendHttpReqToGetShiftData(currentWeekUrl, userId, NAVIGATE_CURRENT);
                        } catch (Exception e) {
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", "Error while confirming the roster " + error.getMessage());
                Toast.makeText(context, "Error while confirming the roster", Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put(context.getString(R.string.put_confirmRosterParam), shiftId + ":" + shiftDate + ":" + userId);
                return MyData;
            }
        };

        shiftDataRequestQ.add(confirmRosterRequest);
    }

    private void sendHttpReqToGetShiftData(final String url, final String userId, final int navigation) {
        RequestQueue shiftDataRequestQ = Volley.newRequestQueue(context);


        StringRequest shiftDataRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Intent displayShift = new Intent(context, DisplayShiftActivity.class);
                            displayShift.putExtra(context.getString(R.string.JSON_KEY_RESPONSE), jsonResponse.toString());
                            displayShift.putExtra(context.getString(R.string.USER_ID), userId);
                            displayShift.putExtra(context.getString(R.string.IS_CURRENT_WEEK), url.equals(currentWeekUrl));
                            displayShift.putExtra(context.getString(R.string.CURRENT_VISIBLE_PAGE_URL), url);
                            /*displayShift.setFlags(displayShift.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                    Intent.FLAG_ACTIVITY_NEW_TASK);*/
                            context.startActivity(displayShift);
                            if (navigation == NAVIGATE_BACK)
                                ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            else
                                ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                           ((Activity) context).finish();

                        } catch (JSONException e) {
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", "Error while fetching shift data. " + error.getMessage());
                Toast.makeText(context, "Error while fetching shift data", Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put(context.getString(R.string.put_staffId), userId);
                return MyData;
            }
        };

        shiftDataRequestQ.add(shiftDataRequest);
    }

    public void refreshData() {
        if(!isPastWeek) {
            if(currentVisiblePageUrl.equals(""))
                sendHttpReqToGetShiftData(currentWeekUrl, userId, NAVIGATE_CURRENT);
            else
                sendHttpReqToGetShiftData(currentVisiblePageUrl, userId, NAVIGATE_CURRENT);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView shiftDate;
        public TextView shiftTiming;
        public TextView clientName;
        public TextView shiftDay;
        public LinearLayout itemDateLayout;
        public CardView shiftCard;
        public View sideColorView;
        public ImageView rosterIcon;
        public LinearLayout itemLayout;
        public LinearLayout shiftCardLayout;
        public CardView dateCard;

        // public CheckBox shiftItemCheckBox;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            itemDateLayout = itemView.findViewById(R.id.list_item_date_layout);
            shiftDate = (TextView) itemView.findViewById(R.id.dateText);
            shiftTiming = (TextView) itemView.findViewById(R.id.shiftTiming);
            clientName = (TextView) itemView.findViewById(R.id.clientNameText);
            shiftDay = (TextView) itemView.findViewById(R.id.dayText);
            shiftCard = (CardView) itemView.findViewById(R.id.shiftCard);
            dateCard = (CardView) itemView.findViewById(R.id.dateCard);
            sideColorView = (View) itemView.findViewById(R.id.sideColorView);
            rosterIcon = (ImageView) itemView.findViewById(R.id.rosterIcon);
            itemLayout = itemView.findViewById(R.id.list_item_layout);
            shiftCardLayout = itemView.findViewById(R.id.shiftCardLayout);
            //shiftItemCheckBox = (CheckBox) itemView.findViewById(R.id.shiftListItemCheckBox);
        }
    }

    public class ViewHeaderHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView shiftTitle;
        public ImageView navigateWeekBack;
        public ImageView navigateWeekNext;
        public ImageView weekIcon;

        public ViewHeaderHolder(View headerView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(headerView);
            shiftTitle = headerView.findViewById(R.id.shiftTitle);
            navigateWeekBack = headerView.findViewById(R.id.navigateWeekBack);
            navigateWeekNext = headerView.findViewById(R.id.navigateWeekNext);
            weekIcon = headerView.findViewById(R.id.weekIcon);
            navigateWeekBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendHttpReqToGetShiftData(preUrl, userId, NAVIGATE_BACK);
                }
            });
            navigateWeekNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendHttpReqToGetShiftData(nextUrl, userId, NAVIGATE_NEXT);
                }
            });
            weekIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendHttpReqToGetShiftData(currentWeekUrl, userId, NAVIGATE_CURRENT);
                }
            });
        }
    }

    public class ViewFooterHolder extends RecyclerView.ViewHolder {

        public Button accept;
        public Button decline;
        public ImageView emptyView;

        public ViewFooterHolder(View footerView) {

            super(footerView);
            accept = footerView.findViewById(R.id.accept);
            decline = footerView.findViewById(R.id.decline);
            emptyView = footerView.findViewById(R.id.emptyView);

            decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isAtlestOneShiftSelected = false;
                    ArrayList<DailyShiftData> selectedShifts = new ArrayList<>();

                    for (DailyShiftData dailyShiftData : shiftData) {
                        if (dailyShiftData.isShiftAvailable() && dailyShiftData.isSelected()) {
                            //if (!dailyShiftData.getRosterConfirmCode().equals(context.getResources().getString(R.string.ROSTER_CONFIRM_ACCEPT)))
                            //declineRoster(dailyShiftData.getShiftId(), dailyShiftData.getShiftDate());
                            selectedShifts.add(dailyShiftData);
                            isAtlestOneShiftSelected = true;
                        }
                    }
                    if(isAtlestOneShiftSelected){
                        Intent intent = new Intent(context, ConfirmDeclineActivity.class);
                        intent.putExtra(context.getString(R.string.SELECTED_SHIFTS), selectedShifts);
                        intent.putExtra(context.getString(R.string.KEY_WEEK_START_DATE), weekStartDate);
                        intent.putExtra(context.getString(R.string.KEY_WEEK_END_DATE), weekEndDate);
                        intent.putExtra(context.getString(R.string.CURRENT_VISIBLE_PAGE_URL), currentVisiblePageUrl);
                        ((Activity) context).startActivity(intent);
                        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                    else
                        Toast.makeText(context, "Select one shift atleast. Touch & hold on row to select",Toast.LENGTH_LONG).show();

                }
            });

            accept.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isAtlestOneShiftSelected = false;
                    ArrayList<DailyShiftData> selectedShifts = new ArrayList<>();
                    boolean isFailed = false;
                    for (DailyShiftData dailyShiftData : shiftData) {
                        if (dailyShiftData.isShiftAvailable() && dailyShiftData.isSelected()) {
                            //if (!dailyShiftData.getRosterConfirmCode().equals(context.getResources().getString(R.string.ROSTER_CONFIRM_ACCEPT)))
                            selectedShifts.add(dailyShiftData);
                            isAtlestOneShiftSelected = true;

                        }
                    }

                    if(!isAtlestOneShiftSelected)
                            Toast.makeText(context, "Select one shift atleast. Touch & hold on row to select",Toast.LENGTH_LONG).show();

                    for(DailyShiftData selectedShift:selectedShifts) {
                        confirmRoster(selectedShift.getShiftId(), selectedShift.getShiftDate());
                    }
                }
            });
        }
    }

}
