package project.csci.geocaching;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private static final int REGISTER_CODE = 0;
    private UserDBHelper database;
    EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        database = new UserDBHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        passwordText = (EditText) findViewById(R.id.password_entry);
        //Sets the soft keyboard to have a OK key instead of a newline key.
        passwordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Set button click highlights.
        ButtonHelper buttonHelper = new ButtonHelper();
        buttonHelper.buttonClickSetter(this, findViewById(R.id.loginButton));
        buttonHelper.buttonClickSetter(this, findViewById(R.id.registerButton));

    }

    @Override
    protected void onResume() {
        super.onResume();
        passwordText = (EditText) findViewById(R.id.password_entry);
        //clear the fields on resume.
        passwordText.setText("");
    }

    public void sendLoginMessage(View view) {
        EditText usernameEditText = (EditText) findViewById(R.id.username_entry);
        EditText passwordEditText = (EditText) findViewById(R.id.password_entry);

        //validate login with database.
        if(database.validatePass(usernameEditText.getText().toString(), passwordEditText.getText().toString()) &&
                usernameEditText.getText().toString().compareTo("") != 0 &&
                passwordEditText.getText().toString().compareTo("") != 0) {

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("username", usernameEditText.getText().toString());
            intent.putExtra("userCaches", database.getUserCaches(usernameEditText.getText().toString()));
            startActivity(intent);
        }else{
            // Display toast.
            Toast.makeText(this, getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
        }
    }

    public void sendRegisterMessage(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivityForResult(intent, REGISTER_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if(requestCode == REGISTER_CODE){
            if(resultCode == RESULT_OK){
                // Login.
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("username", data.getStringExtra("username"));
                intent.putExtra("userCaches", data.getIntExtra("userCaches",0));
                startActivity(intent);
            }
        }
    }
}