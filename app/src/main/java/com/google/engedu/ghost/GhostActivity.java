/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    private TextView ghostText;
    private String wordFragment="";
    String wordByComputer;
    Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);

        ghostText =(TextView)findViewById(R.id.ghostText);

        AssetManager assetManager = getAssets();
        try
        {
            InputStream inputStream = assetManager.open("words.txt");
            dictionary = new SimpleDictionary(inputStream);
        }

        catch (IOException e)
        {
            Toast.makeText(this,"Could not load Dictionary",Toast.LENGTH_SHORT).show();
        }

        onStart(null);

        Button restart =(Button)findViewById(R.id.restart);
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                onStart(null);
            }
        });


        Button challenge = (Button)findViewById(R.id.challenge);
        challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(wordFragment.length() <4)
                {
                    Toast.makeText(getApplication(),"Less then 4 letter you loss word was "+wordByComputer,Toast.LENGTH_SHORT).show();
                }

                else if(wordFragment==wordByComputer)
                {
                    Toast.makeText(getApplication(),"User win",Toast.LENGTH_SHORT).show();
                }
                else
                {
                        Toast.makeText(getApplication(),"Computer wins , Word is "+wordByComputer,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.turn);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }



    private void computerTurn()
    {
        TextView temp = (TextView)findViewById(R.id.turn);
        temp.setText(COMPUTER_TURN);

        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {

                wordFragment = String.valueOf(ghostText.getText());
                wordByComputer = dictionary.getAnyWordStartingWith(wordFragment);

                if(wordByComputer=="noWord")
                {
                    Log.i("tag","check "+wordByComputer);
                    Toast.makeText(getApplication(),"Computer win",Toast.LENGTH_SHORT).show();
                    onStart(null);
                }

                else if(wordByComputer.length() !=1)
                {
                    wordFragment+=wordByComputer.charAt(wordFragment.length());
                }
                else
                    wordFragment += wordByComputer;
                ghostText.setText(String.valueOf(wordFragment));


                userTurn = true;
                TextView temp = (TextView)findViewById(R.id.turn);
                temp.setText(USER_TURN);

            }
        },800);

    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        char pressedKey = (char) event.getUnicodeChar();

        if((pressedKey>='a' && pressedKey<='z'))
        {
            wordFragment = String.valueOf(ghostText.getText());
            wordFragment+=pressedKey;
            ghostText.setText(wordFragment);
            computerTurn();
            return true;
        }

        else
        {
            return false;
        }

    }


}
