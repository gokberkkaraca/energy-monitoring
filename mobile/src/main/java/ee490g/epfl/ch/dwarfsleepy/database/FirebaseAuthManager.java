package ee490g.epfl.ch.dwarfsleepy.database;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class FirebaseAuthManager {

    private FirebaseAuth auth;

    public FirebaseAuthManager() {
        auth = FirebaseAuth.getInstance();
    }


    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void signUp(Activity activity, final String name, String email, String password,
                       final Success successListener, final Failure failureListener) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    updateUserName(name, new Success() {
                        @Override
                        public void onSuccess() {
                            signOut();
                            successListener.onSuccess();
                        }
                    }, failureListener);
                } else {
                    failureListener.onFailure(task.getException().getMessage());
                }
            }
        });
    }

    public void signIn(Activity activity, String email, String password, final Success successListener, final Failure failureListener) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    //TODO
                    successListener.onSuccess();
                } else {
                    failureListener.onFailure(task.getException().getMessage());
                }
            }
        });
    }

    public void signOut() {
        auth.signOut();
    }

    public void sendPasswordReset(String email, final Success successListener, final Failure failureListener) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    successListener.onSuccess();
                } else {
                    failureListener.onFailure(task.getException().getMessage());
                }
            }
        });
    }

    private void updateUserName(final String name, final Success successListener, final Failure failureListener) {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(name).build();

        auth.getCurrentUser().updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    successListener.onSuccess();
                } else {
                    failureListener.onFailure(task.getException().getMessage());
                }
            }
        });
    }
}
