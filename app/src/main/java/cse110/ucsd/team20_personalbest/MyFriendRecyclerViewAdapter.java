package cse110.ucsd.team20_personalbest;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentController;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cse110.ucsd.team20_personalbest.FriendFragment.OnListFragmentInteractionListener;
import cse110.ucsd.team20_personalbest.friends.FriendsContent.Friend;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Friend} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyFriendRecyclerViewAdapter extends RecyclerView.Adapter<MyFriendRecyclerViewAdapter.ViewHolder> {

    private final List<Friend> mValues;
    private final OnListFragmentInteractionListener mListener;
    private FragmentActivity main;

    public MyFriendRecyclerViewAdapter(List<Friend> items, OnListFragmentInteractionListener listener, FragmentActivity activity) {
        mValues = items;
        mListener = listener;
        main = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final String name = mValues.get(position).Name;
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).Name);

        holder.mMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(main, "Sending Message to " + name, Toast.LENGTH_SHORT).show();
            }
        });

        holder.mSummaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(main, "Viewing Summary of " + name, Toast.LENGTH_SHORT).show();
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final Button mMessageBtn;
        public final Button mSummaryBtn;
        public Friend mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mMessageBtn = (Button) view.findViewById(R.id.Message);
            mSummaryBtn = (Button) view.findViewById(R.id.Summary);
        }
    }
}
