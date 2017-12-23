package ee490g.epfl.ch.dwarfsleepy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ee490g.epfl.ch.dwarfsleepy.database.DatabaseHandler;
import ee490g.epfl.ch.dwarfsleepy.models.User;
import ee490g.epfl.ch.dwarfsleepy.utils.NavigationHandler;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_GOOGLE_SIGN_IN = 9001;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private Button loginButton;
    private SignInButton signInButton;
    private Button createAccountButton;
    private Button forgotPasswordButton;

    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        loginButton.setOnClickListener(this);
        signInButton.setOnClickListener(this);
        createAccountButton.setOnClickListener(this);
        forgotPasswordButton.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginButton:
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Email or password can't be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                login(email, password);
                break;
            case R.id.google_sign_in_button:
                googleSignIn();
                break;
            case R.id.createAccountButton:
                startActivity(new Intent(this, CreateAccountActivity.class));
                break;
            case R.id.forgotPasswordBtton:
                startActivity(new Intent(this, ResetPasswordActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign In Failed", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            assert firebaseUser != null;
                            String name = firebaseUser.getDisplayName();
                            // TODO Remove hardcoded null fields here
                            User user = new User(firebaseUser, name, null, null);
                            DatabaseHandler.addUser(user);
                            NavigationHandler.goToDashboardActivity(LoginActivity.this, user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Google Sign In Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void initializeViews() {
        loginButton = findViewById(R.id.loginButton);
        signInButton = findViewById(R.id.google_sign_in_button);
        createAccountButton = findViewById(R.id.createAccountButton);
        forgotPasswordButton = findViewById(R.id.forgotPasswordBtton);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
    }

    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            assert firebaseUser != null;
                            DatabaseHandler.getUser(firebaseUser.getUid(), new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
                                    NavigationHandler.goToDashboardActivity(LoginActivity.this, user);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }
}
