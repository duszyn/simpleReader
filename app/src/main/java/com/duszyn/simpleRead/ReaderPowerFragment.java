package com.duszyn.simpleRead;


import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.pda.rfid.uhf.UHFReader;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReaderPowerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReaderPowerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {


    private int discrete = 0;
    private float start = 1;
    private int end = 3;
    private float start_pos = 2;
    private SeekBar seek;

    //We should take this value from activity!
    private int antNum =2;//number of antennas

    public ReaderPowerFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ReaderPowerFragment newInstance() {
        ReaderPowerFragment fragment = new ReaderPowerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.power_setter, container, false);

        start = 1; //you need to give starting value of SeekBar
        start_pos = 2; //you need to give starting position value of SeekBar
        discrete = (int) start_pos;

        seek = (SeekBar) view.findViewById(R.id.powerBar);
        seek.setProgress(discrete);
        seek.setOnSeekBarChangeListener(this);
        seek.setMax(end);

        return view;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        // TODO Auto-generated method stub
        // To convert it as discrete value
        float temp = i;
        discrete = (int) (start + temp);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        SetAntPower(discrete);
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void SetPower(int level){
        SendCommandToReader(level);
        seek.setProgress(level-1);
    }

    private String SendCommandToReader(int choosenOption){
        int result = 1;
        String pwrResult = "";

        switch(choosenOption){
            case 1:
                result = UHFReader._Config.SetANTPowerParam(antNum, 5);
                pwrResult = "very low";
                break;
            case 2:
                result = UHFReader._Config.SetANTPowerParam(antNum, 15);
                pwrResult = "low";
                break;
            case 3:
                result = UHFReader._Config.SetANTPowerParam(antNum, 27);
                pwrResult = "medium";
                break;
            case 4:
                result = UHFReader._Config.SetANTPowerParam(antNum, 30);
                pwrResult = "maximum";
                break;
        }
        if(result != 0) { return null;}
        else return pwrResult;
    }

    private void SetAntPower(int choosenOption){
        String pwrResult = SendCommandToReader(choosenOption);
        if( pwrResult != null){
            Toast.makeText(this.getContext(), "Reader power: " + String.valueOf(pwrResult), Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this.getContext(), "Error! Please set power again!", Toast.LENGTH_SHORT).show();
        }
    }
}
