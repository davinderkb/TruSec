package com.app.trusec;

import android.graphics.PorterDuff;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class EditProfileActivity extends AppCompatActivity {
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    EditText mFirstName;
    EditText mLastName;
    EditText mEmail;
    EditText mMobile;
    Button updateProfile;
    TextInputLayout mLastNameTIL;
    private ImageView backArrow;
    private TextInputLayout tilMobile;
    private TextInputLayout tilEmail;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        firstName = getIntent().getStringExtra(getString(R.string.EDIT_PROFILE_FIRST_NAME));
        lastName = getIntent().getStringExtra(getString(R.string.EDIT_PROFILE_LAST_NAME));
        email = getIntent().getStringExtra(getString(R.string.EDIT_PROFILE_EMAIL));
        mobile = getIntent().getStringExtra(getString(R.string.EDIT_PROFILE_MOBILE));

        initFields();

        mFirstName.setText(firstName);
        mLastName.setText(lastName);
        mEmail.setText(email);
        mMobile.setText(mobile);

    }

    private void initFields() {
        mFirstName = findViewById(R.id.editProfileFirstName);
        mLastName = findViewById(R.id.editProfileLastName);
        mLastNameTIL = findViewById(R.id.editProfileLastNameTextLayout);
        mEmail = findViewById(R.id.editProfileEmail);
        mMobile = findViewById(R.id.editProfileMobile);
        updateProfile = findViewById(R.id.editProfileUpdateButton);
        backArrow = findViewById(R.id.backArrowEditProfile);
        tilEmail = findViewById(R.id.tilEmailEditProfile);
        tilMobile = findViewById(R.id.tilMobileEditProfile);
        setFieldListeners();

    }

    private void setFieldListeners() {
        setBackPageListener();
        setEmailFieldListener();
        setMobileFieldListener();
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( isMobileBlank() | (isEmailBlank() || isInvalidEmail()) )
                    return;
                sendRequestToUpdateProfile();
            }
        });

        mFirstName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditProfileActivity.this, "This field is not editable",Toast.LENGTH_SHORT).show();
            }
        });
        mLastName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditProfileActivity.this, "This field is not editable",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendRequestToUpdateProfile() {
        Toast.makeText(EditProfileActivity.this, "Requested",Toast.LENGTH_SHORT).show();
    }

    private void setBackPageListener() {
        backArrow.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             EditProfileActivity.super.onBackPressed();
                                             overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                         }
                                     }
        );
    }


    private void setEmailFieldListener() {
        mEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mEmail.setError(null);
                    tilEmail.setError(null);
                } else {
                    if (!mEmail.getText().toString().trim().matches(EditProfileActivity.this.getString(R.string.emailValidatorRegex)) && !EditProfileActivity.this.getString(R.string.EMPTY_STRING).equals(mEmail.getText().toString())) {
                        mEmail.setError("");
                        tilEmail.setError("Invalid Email");
                    }
                }
            }
        });
    }

    private void setMobileFieldListener() {
        mMobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mMobile.setError(null);
                    tilMobile.setError(null);
                }
            }
        });
    }

    private boolean isMobileBlank() {
        boolean isMobileBlank = false;
        if (EditProfileActivity.this.getString(R.string.EMPTY_STRING).equals(mMobile.getText().toString())) {
            isMobileBlank = true;
            mMobile.setError("");
            mMobile.clearFocus();
            tilMobile.setError("Mobile no. cannot be blank");
        }
        return isMobileBlank;
    }

    private boolean isInvalidEmail() {
        boolean isInvalidEmail = false;
        if (!mEmail.getText().toString().trim().matches(EditProfileActivity.this.getString(R.string.emailValidatorRegex)) && !EditProfileActivity.this.getString(R.string.EMPTY_STRING).equals(mEmail.getText().toString())) {
            isInvalidEmail=true;
            mEmail.setError("");
            mEmail.clearFocus();
            //tilEmail.setError("Invalid Email");
        }
        return isInvalidEmail;
    }
    private boolean isEmailBlank() {
        boolean isBlank = false;
        if (EditProfileActivity.this.getString(R.string.EMPTY_STRING).equals(mEmail.getText().toString())) {
            isBlank = true;
            mEmail.setError("");
            mEmail.clearFocus();
            tilEmail.setError("Email cannot be empty");
        }
        return isBlank;
    }

}
