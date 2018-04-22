package jp.techacademy.hojun.bun.qa_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import static jp.techacademy.hojun.bun.qa_app.MainActivity.favoriteMap;


public class FavoriteActivity extends AppCompatActivity {

    private int mGenre = 0;


    private DatabaseReference mDatabaseReference;
    private DatabaseReference mGenreRef;
    private ListView mListView;
    private ArrayList<Question> mQuestionArrayList;
    private QuestionsListAdapter mAdapter;
    private DatabaseReference mFavoriteRef;
    private String mUserUid;


    private ChildEventListener mEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            if (favoriteMap.containsKey(dataSnapshot.getKey())) {
                HashMap map = (HashMap) dataSnapshot.getValue();
                Log.d("debug", "On11");
                String title = (String) map.get("title");
                Log.d("debug", "On12");
                String body = (String) map.get("body");
                Log.d("debug", "On13");
                String name = (String) map.get("name");
                Log.d("debug", "On14");
                String uid = (String) map.get("uid");
                Log.d("debug", "On15");
                String imageString = (String) map.get("image");
                byte[] bytes;
                if (imageString != null) {
                    bytes = Base64.decode(imageString, Base64.DEFAULT);
                    Log.d("debug", "On16");
                } else {
                    bytes = new byte[0];
                    Log.d("debug", "On17");
                }
                ArrayList<Answer> answerArrayList = new ArrayList<Answer>();
                Log.d("debug", "On18");
                HashMap answerMap = (HashMap) map.get("answers");
                Log.d("debug", "On19");
                if (answerMap != null) {
                    for (Object key : answerMap.keySet()) {
                        HashMap temp = (HashMap) answerMap.get((String) key);
                        Log.d("debug", temp.toString());
                        String answerBody = (String) temp.get("body");
                        Log.d("debug", answerBody);
                        String answerName = (String) temp.get("name");
                        Log.d("debug", answerName);
                        String answerUid = (String) temp.get("uid");
                        Log.d("debug", "On23");
                        Log.d("debug", answerUid);

                        Answer answer = new Answer(answerBody, answerName, answerUid, (String) key);
                        Log.d("debug", "On24");
                        answerArrayList.add(answer);
                        Log.d("debug", "On25");
                        Log.d("debug", "answerArrayList.size() = " + answerArrayList.size());
                    }
                }

                Question question = new Question(title, body, name, uid, dataSnapshot.getKey(), mGenre, bytes, answerArrayList);
                Log.d("debug", "On26");

                mQuestionArrayList.add(question);
                Log.d("debug", "On27");

                mAdapter.notifyDataSetChanged();
                Log.d("debug", "On28");

            }

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s)  {
            HashMap map = (HashMap) dataSnapshot.getValue();
            Log.d("debug", "On29");


            // 変更があったQuestionを探す
            for (Question question : mQuestionArrayList) {
                if (dataSnapshot.getKey().equals(question.getQuestionUid())) {
                    // このアプリで変更がある可能性があるのは回答(Answer)のみ
                    question.getAnswers().clear();
                    Log.d("debug", "On30");

                    HashMap answerMap = (HashMap) map.get("answers");
                    Log.d("debug", answerMap.toString());
                    Log.d("debug", "On31");


                    if (answerMap != null) {
                        for (Object key : answerMap.keySet()) {
                            HashMap temp = (HashMap) answerMap.get((String) key);
                            Log.d("debug", temp.toString());
                            Log.d("debug", "On32");

                            String answerBody = (String) temp.get("body");
                            Log.d("debug", answerBody);
                            Log.d("debug", "On33");

                            String answerName = (String) temp.get("name");
                            Log.d("debug", answerName);
                            Log.d("debug", "On34");

                            String answerUid = (String) temp.get("uid");
                            Log.d("debug", answerUid);
                            Log.d("debug", "On35");

                            Answer answer = new Answer(answerBody, answerName, answerUid, (String) key);
                            Log.d("debug", answer.toString());
                            Log.d("debug", "On36");

                            question.getAnswers().add(answer);
                            Log.d("debug", "On");
                            Log.d("debug", "On37");

                        }
                    }

                    mAdapter.notifyDataSetChanged();
                    Log.d("debug", "On38");

                }
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private ChildEventListener mFavoriteListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            HashMap map = (HashMap) dataSnapshot.getValue();

            // 変更があったQuestionを探す
            for (Question question : mQuestionArrayList) {
                if (dataSnapshot.getKey().equals(question.getQuestionUid())) {
                    // このアプリで変更がある可能性があるのは回答(Answer)のみ
                    question.getAnswers().clear();


                    HashMap answerMap = (HashMap) map.get("answers");
                    if (answerMap != null) {
                        for (Object key : answerMap.keySet()) {
                            HashMap temp = (HashMap) answerMap.get((String) key);
                            String answerBody = (String) temp.get("body");
                            String answerName = (String) temp.get("name");
                            String answerUid = (String) temp.get("uid");
                            Answer answer = new Answer(answerBody, answerName, answerUid, (String) key);
                            question.getAnswers().add(answer);
                        }
                    }

                    mAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        setTitle("お気に入り");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("debug", "On1");

        // Firebase
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Log.d("debug", "On2");

        // ListViewの準備
        mListView = (ListView) findViewById(R.id.listView);
        Log.d("debug", "On3");


        mAdapter = new QuestionsListAdapter(FavoriteActivity.this, mQuestionArrayList);
        mQuestionArrayList = new ArrayList<Question>();
        mQuestionArrayList.clear();
        Log.d("debug", "On6");
        mAdapter.setQuestionArrayList(mQuestionArrayList);
        Log.d("debug", "On7");
        mListView.setAdapter(mAdapter);
        Log.d("debug", "On8");
        mAdapter.notifyDataSetChanged();
        Log.d("debug", "On9");


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Questionのインスタンスを渡して質問詳細画面を起動する
                Intent intent = new Intent(getApplicationContext(), QuestionDetailActivity.class);
                intent.putExtra("question", mQuestionArrayList.get(position));
                startActivity(intent);
                Log.d("debug", "On10");

            }
        });
        for (mGenre = 1; mGenre <= 4; mGenre++) {
            mGenreRef = mDatabaseReference.child(Const.ContentsPATH).child(String.valueOf(mGenre));
            mGenreRef.addChildEventListener(mEventListener);
            Log.d("debug", " mEventListener is On");
        }

        mUserUid = user.getUid();
        Log.d("debug", " got a user");

        mFavoriteRef = mDatabaseReference.child(Const.FavoritePATH).child(String.valueOf(mUserUid));
        Log.d("debug", " user is On");

        mFavoriteRef.addChildEventListener(mFavoriteListener);
        Log.d("debug", " mFavoriteListener is On");


    }

}