package cse110.ucsd.team20_personalbest;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WalkPg.OnWalkPgListener} interface
 * to handle interaction events.
 * Use the {@link WalkPg#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WalkPg extends Fragment {

    private LinearLayout mlayout;
    private static ArrayList<Walk> walks;
    private ArrayList<Walk> walks_saved;
    private OnWalkPgListener mListener;

    private static final String TAG = "WalkPage";

    public WalkPg() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WalkPg.
     */
    public static WalkPg newInstance() {
        WalkPg fragment = new WalkPg();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void addEntry(Walk newWalk){
        RelativeLayout newEntry = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.setMargins(40,40,40,40);
        TextView walkTime = new TextView(getContext());

        RelativeLayout.LayoutParams paramStat = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramStat.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        RelativeLayout.LayoutParams paramTime = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramTime.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        walkTime.setText(newWalk.getTime());
        walkTime.setTextSize(20);
        TextView walkStat = new TextView(getContext());
        walkStat.setText(newWalk.getStat());
        walkStat.setTextSize(20);
        walkStat.setLayoutParams(paramStat);
        walkTime.setLayoutParams(paramTime);
        newEntry.addView(walkStat);
        newEntry.addView(walkTime);
        newEntry.setLayoutParams(params);
        mlayout.addView(newEntry);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstancesState) {
        mlayout = (LinearLayout) getView().findViewById(R.id.llayout);
        Log.d(TAG, "Loading past walks");
        for (int i = 0; i < walks.size(); i++) {
            Log.d(TAG, "Adding a walk");
            addEntry(walks.get(i));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_walks, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            //mListener.onFragmentInteraction(uri);
        }
    }

    public void updateWalks(ArrayList<Walk> newWalks) {
        if (newWalks != null)
            walks = newWalks;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWalkPgListener) {
            mListener = (OnWalkPgListener) context;
            mListener.onWalkPgSelected();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnWalkPgListener {
        // TODO: Update argument type and name
        //void onFragmentInteraction(Uri uri);
        public void onWalkPgSelected();
    }
}
