package com.shreesha.meetingmanager;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.net.ParseException;
import androidx.fragment.app.Fragment;

import com.shreesha.meetingmanager.DataBaseConn;
import com.shreesha.meetingmanager.NotificationUtils;
import com.shreesha.meetingmanager.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

public class Fragment2 extends Fragment {
    EditText date;
    CalendarView cal;
    Button btn1;
    DataBaseConn dbc;
    TextView meetingTextView;
    Handler handler;
    private Button btnFetchDates;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2_layout, container, false);
        date = view.findViewById(R.id.editTextDate);
        cal = view.findViewById(R.id.calendarView);
        btn1 = view.findViewById(R.id.btn2);
        dbc = new DataBaseConn(getActivity());
        meetingTextView = view.findViewById(R.id.meetingTextView);
        handler = new Handler();

        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String d = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, (month + 1), year);
                date.setText(d);
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View v) {
                String d1 = date.getText().toString();
                StringBuffer res = new StringBuffer();
                Cursor c = dbc.fetch(d1);
                int count = c.getCount();
                c.moveToFirst();
                if (count > 0) {
                    do {
                        res.append(c.getString(c.getColumnIndex("agenda")) + "\t" + "at" + "\t" + c.getString(c.getColumnIndex("time")));
                        res.append("\n");
                    } while (c.moveToNext());

                    // Show toast message
                    Toast.makeText(getActivity(), res, Toast.LENGTH_LONG).show();

                    // Update TextView with meeting details
                    meetingTextView.setText(res.toString());
                } else {
                    Toast.makeText(getActivity(), "No Meeting on This Day....", Toast.LENGTH_LONG).show();
                    meetingTextView.setText("");
                }
            }
        });

        btnFetchDates = view.findViewById(R.id.btnFetchDates);
        btnFetchDates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchAndHighlightDates();
            }
        });

        // Schedule periodic checks for new meetings
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkForNewMeetings();
                handler.postDelayed(this, 25000); // 20 seconds delay
            }
        }, 500); // 10 seconds delay for the first check

        return view;
    }

    private void checkForNewMeetings() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDateString = dateFormat.format(currentDate);

        // Fetch meetings for the current date
        Cursor c = dbc.fetch(currentDateString);
        int count = c.getCount();
        c.moveToFirst();
        if (count > 0) {
            @SuppressLint("Range") String agenda = c.getString(c.getColumnIndex("agenda"));
            @SuppressLint("Range") String time = c.getString(c.getColumnIndex("time"));
            NotificationUtils.showMeetingNotification(getActivity(), agenda, time);
        } else {
            NotificationUtils.showNoMeetingNotification(getActivity());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create the notification channel
        NotificationUtils.createNotificationChannel(getActivity());
    }
    private void fetchAndHighlightDates() {
        // Fetch all the dates with meetings from the database
        Cursor cursor = dbc.fetchAllDatesWithMeetings();

        // Move the cursor to the first row
        cursor.moveToFirst();

        // Create a HashSet to store the dates with meetings
        HashSet<Long> meetingDates = new HashSet<>();

        // Loop through the cursor and store the dates in the HashSet
        while (!cursor.isAfterLast()) {
            @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                Date meetingDate = dateFormat.parse(date);
                if (meetingDate != null) {
                    meetingDates.add(meetingDate.getTime());
                }
            } catch (ParseException | java.text.ParseException e) {
                e.printStackTrace();
            }

            cursor.moveToNext();
        }

        // Set the highlighted dates in the CalendarView
        for (Long meetingDate : meetingDates) {
            cal.setDate(meetingDate, true, true);
        }

        // Move the highlighter back to the current date after a delay
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cal.setDate(System.currentTimeMillis(), true, true);
            }
        }, 3000); // 3 seconds delay
    }
}
