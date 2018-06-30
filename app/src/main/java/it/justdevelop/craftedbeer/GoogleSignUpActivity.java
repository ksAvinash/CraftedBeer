package it.justdevelop.craftedbeer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class GoogleSignUpActivity extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;
    private static final String LOG = "BeerFragment :";
    private static final String APP_LOG_TAG = "CrafterBeer ";

    private static final int RC_SIGN_IN = 8003;
    ProgressDialog progressDialog;
    String email = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_up);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        SignInButton googleSignInButton = findViewById(R.id.googleSignInButton);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }


    private void signIn() {
        progressDialog = new ProgressDialog(GoogleSignUpActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Signing in..");
        progressDialog.show();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                email = account.getEmail();
                Log.d(APP_LOG_TAG, LOG+ "Google signin successful!");

                SharedPreferences sharedPreferences = getSharedPreferences("cb", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", email.split("@")[0].replace(".","_"));
                editor.commit();

                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }

                Intent intent = new Intent(GoogleSignUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } catch (ApiException e) {
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                Log.d(APP_LOG_TAG, LOG+ "Google sign in failed", e);
            }
        }
    }


}
