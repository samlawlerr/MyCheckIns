package android.bignerdbranch.com;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

public class ListUI extends Fragment {
    private RecyclerView mCheckInRecyclerView;
    private CrimeAdapter mAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_checkin_list, container, false);
        mCheckInRecyclerView = (RecyclerView) view
                .findViewById(R.id.checkin_recycler_view);
        mCheckInRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();



        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_checkin_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_checkin:
                CheckIn check = new CheckIn();
                CheckInLab.get(getActivity()).addCheckIn(check);
                Intent intent = CheckInPagerActivity
                        .newIntent(getActivity(), check.getId());
                startActivity(intent);
                return true;
            case R.id.help:
                intent = HelpWebPage.newIntent(getActivity(), Uri.parse("https://www.google.com"));
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUI() {
        CheckInLab checkLab = CheckInLab.get(getActivity());
        List<CheckIn> checks = checkLab.getCheckIn();
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(checks);
            mCheckInRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setChecks(checks);
            mAdapter.notifyDataSetChanged();
        }
    }

        private class CheckInHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckIn mCheckIn;
        private TextView mPlaceTextView;


        public CheckInHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.list_item_checkin, parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView = itemView.findViewById(R.id.check_title);
            mDateTextView =  itemView.findViewById(R.id.check_date);
            mPlaceTextView =  itemView.findViewById(R.id.check_place);

        }

        public void bind(CheckIn check) {
            mCheckIn = check;
            mTitleTextView.setText(mCheckIn.getTitle());
            mDateTextView.setText(mCheckIn.getDate().toString());
            mPlaceTextView.setText(mCheckIn.getPlace());
        }

        @Override
        public void onClick(View view) {
            Intent intent = CheckInPagerActivity.newIntent(getActivity(), mCheckIn.getId());
            startActivity(intent);

        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CheckInHolder> {
        private List<CheckIn> mCheckIns;
        public CrimeAdapter(List<CheckIn> checks) {
            mCheckIns = checks;
        }

        @Override
        public CheckInHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CheckInHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(CheckInHolder holder, int position) {
            CheckIn check = mCheckIns.get(position);
            holder.bind(check);
        }

        @Override
        public int getItemCount() {
            return mCheckIns.size();
        }

        public void setChecks(List<CheckIn> checks) {
            mCheckIns = checks;
        }

    }
}
