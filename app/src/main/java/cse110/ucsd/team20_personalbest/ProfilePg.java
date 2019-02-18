package cse110.ucsd.team20_personalbest;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfilePg.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfilePg#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfilePg extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private EditText changeFeet;
    private EditText changeInches;
    private EditText changeSteps;
    private TextView changeHeightSign;
    private TextView changeGoal;
    private TextView stepsSign;
    private CheckBox goalBox;
    private CheckBox metBox;
    private CheckBox ignoredBox;
    private TextView autoGoalText;
    private Button applyChanges;
  
    private OnFragmentInteractionListener mListener;

    public ProfilePg() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfilePg.
     */
    public static ProfilePg newInstance(String param1, String param2) {
        ProfilePg fragment = new ProfilePg();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstancesState) {
        //Setting up the texts of the height with the user's initial setup height
        changeHeightSign = (TextView) getView().findViewById(R.id.changeHeight);
        changeFeet = (EditText) getView().findViewById(R.id.changeFeet);
        changeInches = (EditText) getView().findViewById(R.id.changeInches);

        final SharedPreferences preferences = this.getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        int feet = preferences.getInt("feet", 0);
        int inches = preferences.getInt("inches", 0);

        changeFeet.setText(Integer.toString(feet));
        changeInches.setText(Integer.toString(inches));

        //Setting up the texts of the height with the user's current goal steps;
        final int goalSteps = preferences.getInt("savedGoal", 5000);
        changeSteps = getView().findViewById(R.id.changeSteps);
        changeSteps.setText(Integer.toString(goalSteps));

        //Setting up checkbox if it should be checked or not.
        Boolean autoGoal = preferences.getBoolean("autoGoal", true);
        Boolean meetOnce = preferences.getBoolean("meetOnlyOnce", true);
        final Boolean ignored = preferences.getBoolean("ignored", false);

        goalBox = (CheckBox) getView().findViewById(R.id.goalRadio);
        metBox = (CheckBox) getView().findViewById(R.id.metRadio);
        ignoredBox = (CheckBox) getView().findViewById(R.id.ignoredRadio);

        Log.d("Goal", "Set meet one to: " + meetOnce);

        goalBox.setChecked(autoGoal);
        metBox.setChecked(meetOnce);
        ignoredBox.setChecked(ignored);

        //Updating all the changes with a single button.
        applyChanges = (Button) getView().findViewById(R.id.applyChanges);
        applyChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String feetStr = changeFeet.getText().toString();
                String inchesStr = changeInches.getText().toString();
                String goalStr = changeSteps.getText().toString();

                if (feetStr.isEmpty() || inchesStr.isEmpty() || goalStr.isEmpty()) {
                    Toast toast = Toast.makeText(getActivity() ,
                            "Enter height and goal.",
                            Toast.LENGTH_SHORT);
                    Log.d("Profile", "One or more fields empty\n\tFeet: " + feetStr + "\n\tInches: " + inchesStr + "\n\tGoal: " + goalStr);
                    toast.show();
                } else {
                    int feet = Integer.parseInt(feetStr);
                    int inches = Integer.parseInt(inchesStr);
                    int goal = Integer.parseInt(goalStr);
                    BoundValidity valid = new BoundValidity();

                    if (!valid.feetAndInches(feet, inches) || !valid.manualGoal(goal)) {
                        Toast toast = Toast.makeText(getActivity() ,
                                "Enter valid height and goal.",
                                Toast.LENGTH_SHORT);
                        Log.d("Profile", "One or more fields out of range\n\tFeet: " + feetStr + "\n\tInches: " + inchesStr + "\n\tGoal: " + goalStr);
                        toast.show();
                    }

                    else {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("autoGoal", goalBox.isChecked());
                        editor.putBoolean("meetOnlyOnce", metBox.isChecked());
                        editor.putBoolean("ignored", metBox.isChecked());
                        editor.putInt("savedGoal", goal);
                        editor.putInt("feet", feet);
                        editor.putInt("inches", inches);
                        editor.putInt("height", (12 * feet) + inches);
                        editor.apply();
                        Toast toast = Toast.makeText(getActivity() ,
                                "Updates Applied",
                                Toast.LENGTH_SHORT);

                        Log.d("Profile", "All updated fields valid.");
                        Log.d("Profile/Goal", "Met box is checked: " + metBox.isChecked());
                        Log.d("Profile/Goal", "Ignored box is checked: " + ignoredBox.isChecked());

                        MainActivity main = (MainActivity) getActivity();

                        Calendar cal = main.getOurCal();
                        main.updateGoal(goal, cal);

                        main.setGoalCount(Integer.parseInt(changeSteps.getText().toString()));
                        main.setAutoGoal(preferences.getBoolean("autoGoal", true));
                        main.getGoal().setMeetOnce(metBox.isChecked());
                        main.getGoal().setIgnored(ignoredBox.isChecked());
                        main.getGoal().save(main, cal);

                        toast.show();
                    }
                }
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_proile_pg, container, false);

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }*/

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
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
