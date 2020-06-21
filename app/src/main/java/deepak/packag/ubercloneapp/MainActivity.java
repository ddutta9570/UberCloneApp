package deepak.packag.ubercloneapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener{

    @Override
    public void onClick(View v) {

        if (edtDriverOrPassenger.getText().toString().equals("Driver")||edtDriverOrPassenger.getText().toString().equals("Passenger")) {

            if (ParseUser.getCurrentUser()==null) {
                ParseAnonymousUtils.logIn(new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {

                        if (user!=null && e==null) {

                            Toast.makeText(MainActivity.this, "We have an anonymous user" , Toast.LENGTH_SHORT).show();

                            user.put("as" , edtDriverOrPassenger.getText().toString());
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    transitionToPassengerActivity();
                                    transitionToDriverRequestListActivity();
                                }
                            });
                        }

                    }
                });
            }
        }

    }




    private EditText edtUsername,edtPassword,edtDriverOrPassenger;
    private Button btnSignUpLoginActivity,btnOneTimeLogin;
    private RadioButton driverRadioButton,passengerRadioButton;
    private State state;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ParseUser.getCurrentUser()!=null) {

         //   ParseUser.getCurrentUser().logOut();
               transitionToPassengerActivity();
               transitionToDriverRequestListActivity();


      
        }

        btnSignUpLoginActivity = findViewById(R.id.btnSignUpLoginActivity);
        edtUsername= findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        edtDriverOrPassenger = findViewById(R.id.edtDOP);
        btnOneTimeLogin = findViewById(R.id.btnOneTimeLogin);
        btnOneTimeLogin.setOnClickListener(this);
        driverRadioButton = findViewById(R.id.rdbDriver);
        passengerRadioButton= findViewById(R.id.rdbPassenger);
        state = State.SIGNUP;


        btnSignUpLoginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (state== State.SIGNUP){

                    if (driverRadioButton.isChecked() == false && passengerRadioButton.isChecked() == false){
                        Toast.makeText(MainActivity.this,"Are you a passenger or a driver ?" , Toast.LENGTH_SHORT).show();
                        return;

                    }
                    ParseUser appUser = new ParseUser();
                    appUser.setUsername(edtUsername.getText().toString());
                    appUser.setPassword(edtPassword.getText().toString());

                    if (driverRadioButton.isChecked()){

                        appUser.put("as" , "Driver");
                    }else if (passengerRadioButton.isChecked()){

                        appUser.put("as" , "Passenger");
                    }

                    appUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {

                            if (e==null){

                                Toast.makeText(MainActivity.this, "Signed Up" , Toast.LENGTH_SHORT).show();
                                transitionToPassengerActivity();
                                transitionToDriverRequestListActivity();
                            }
                        }
                    });
                }else if (state==State.lOGIN){

                    ParseUser.logInInBackground(edtUsername.getText().toString(), edtPassword.getText().toString(), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {

                            if (user!=null && e==null){

                                Toast.makeText(MainActivity.this,"User logged in" , Toast.LENGTH_SHORT).show();
                                transitionToPassengerActivity();
                                transitionToDriverRequestListActivity();
                            }
                        }
                    });
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }



    enum State{
        SIGNUP , lOGIN;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.loginItem:

                if (state == State.SIGNUP){
                    state=State.lOGIN;
                    item.setTitle("Sign up");
                    btnSignUpLoginActivity.setText("Log in");

                }else if (state == State.lOGIN){
                    state=State.SIGNUP;
                    item.setTitle("Log in");
                    btnSignUpLoginActivity.setText("Sign Up");

                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void transitionToPassengerActivity (){

        if (ParseUser.getCurrentUser()!=null){

        if (ParseUser.getCurrentUser().get("as").equals("Passenger")){


            Intent intent = new Intent(MainActivity.this,PassengerAvtivity.class);
            startActivity(intent);

              }

        }
    }

    private void transitionToDriverRequestListActivity(){

        if (ParseUser.getCurrentUser() !=null){

           if (ParseUser.getCurrentUser().get("as").equals("Driver")){

            Intent intent = new Intent(this,DriverRequestListActivity.class);
            startActivity(intent);

           }
        }
    }

}