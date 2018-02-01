package io.sandblock.branchtest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final int TIMEOUT = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeUI();

        initializeBranchIO();
    }

    @Override
    protected void onStart() {
        super.onStart();

        initializeBranchIO();

        setOuput(Branch.getInstance().getLatestReferringParams().toString());
    }

//            = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
//            =: = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = :=
//            ::                                                                 ::
//            ::                   BRANCH IO STUFF                               ::
//            ::                                                                 ::
//            =: = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = :=
//            = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =

    private void initializeBranchIO(){
        // Branch init
        Branch.getInstance().initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    try {
                        setOuput(referringParams.toString(2));
                    } catch (JSONException e) {
                        setError(e.toString());
                    }
                } else {
                    setError(error.toString());
                }
            }
        }, this.getIntent().getData(), this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    private boolean handleText(String text){
        if(!URLUtil.isValidUrl(text)){
            setError(text + " is not a valid URL");
            return false;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(text));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        return true;
    }











//            = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
//            =: = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = :=
//            ::                                                                 ::
//            ::                           UI STUFF                              ::
//            ::                                                                 ::
//            =: = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = :=
//            = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =

    private EditText input;
    private TextView output;
    private Button submit;

    private void initializeUI(){
        setContentView(R.layout.activity_main);
        input = findViewById(R.id.input);
        output = findViewById(R.id.output);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onClick(View view) {
        submit.setEnabled(false);
        submit.setText("Loading...");
        if(handleText(input.getText().toString())){
            Toast.makeText(this, "Text handled !", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!submit.isEnabled()){
                        Toast.makeText(MainActivity.this, "Waited " + TIMEOUT + " seconds and nothing happened...", Toast.LENGTH_SHORT).show();
                        setError("Timeout exceeded");
                    }
                }
            }, TIMEOUT * 1000);
        }
    }

    private void setError(String text){
        enableSubmit();
        output.setText("Error : \n" + text);
        output.setTextColor(Color.RED);
    }

    private void setOuput(String text){
        enableSubmit();
        output.setText(text);
        output.setTextColor(Color.BLACK);
    }

    private void enableSubmit(){
        submit.setText("submit");
        submit.setEnabled(true);
    }
}
