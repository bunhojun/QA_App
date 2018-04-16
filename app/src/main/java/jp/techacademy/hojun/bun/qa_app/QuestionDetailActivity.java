package jp.techacademy.hojun.bun.qa_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class QuestionDetailActivity extends AppCompatActivity {

    private ListView mListView;
    private Question mQuestion;
    private QuestionDetailListAdapter mAdapter;

    private DatabaseReference mAnswerRef;
    private DatabaseReference mFavoriteRef;
    private boolean mFavoriteFlag = false;




    private ChildEventListener mEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            HashMap map = (HashMap) dataSnapshot.getValue();

            String answerUid = dataSnapshot.getKey();

            for(Answer answer : mQuestion.getAnswers()) {
                // 同じAnswerUidのものが存在しているときは何もしない
                if (answerUid.equals(answer.getAnswerUid())) {
                    return;
                }
            }

            String body = (String) map.get("body");
            String name = (String) map.get("name");
            String uid = (String) map.get("uid");

            Answer answer = new Answer(body, name, uid, answerUid);
            mQuestion.getAnswers().add(answer);
            mAdapter.notifyDataSetChanged();
        }



        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
            //todo お気に入りボタンの切り替え処理

            FloatingActionButton favorite =  findViewById(R.id.favoriteButton);
            mFavoriteFlag = true;
            favorite.setImageResource(R.drawable.ic_star_white_18dp);

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
        setContentView(R.layout.activity_question_detail);


        // 渡ってきたQuestionのオブジェクトを保持する
        Bundle extras = getIntent().getExtras();
        mQuestion = (Question) extras.get("question");

        setTitle(mQuestion.getTitle());

        // ListViewの準備
        mListView = findViewById(R.id.listView);
        mAdapter = new QuestionDetailListAdapter(this, mQuestion);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    final FloatingActionButton favorite =  findViewById(R.id.favoriteButton);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mFavoriteFlag) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    mFavoriteRef.child(Const.FavoritePATH).child(user.getUid()).child(mQuestion.getQuestionUid());
                    // UID
                    mFavoriteRef.removeValue();
                    mFavoriteFlag = false;
                    favorite.setImageResource(R.drawable.ic_star_outline_white_18dp);


                }else {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    mFavoriteRef.child(Const.FavoritePATH).child(user.getUid()).child(mQuestion.getQuestionUid());

                    Map<String, Integer> data = new HashMap<>();

                    data.put("genre", mQuestion.getGenre());

                    mFavoriteRef.setValue(data);

                    mFavoriteFlag = true;
                    favorite.setImageResource(R.drawable.ic_star_white_18dp);
                }
            }
        });


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();
        if(user!=null){
        mFavoriteRef = dataBaseReference.child(Const.FavoritePATH).child(user.getUid()).child(mQuestion.getQuestionUid());
        mFavoriteRef.addChildEventListener(mFavoriteListener);}


    FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ログイン済みのユーザーを取得する
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user == null) {
                    // ログインしていなければログイン画面に遷移させる
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    // Questionを渡して回答作成画面を起動する
                    // --- ここから ---
                    Intent intent = new Intent(getApplicationContext(), AnswerSendActivity.class);
                    intent.putExtra("question", mQuestion);
                    startActivity(intent);
                    // --- ここまで ---
                }
            }
        });

        //DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();
        mAnswerRef = dataBaseReference.child(Const.ContentsPATH).child(String.valueOf(mQuestion.getGenre())).child(mQuestion.getQuestionUid()).child(Const.AnswersPATH);
        mAnswerRef.addChildEventListener(mEventListener);
    }

    @Override
    protected void onResume(){
        super.onResume();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            // ログインしていなければログイン画面に遷移させる
            findViewById(R.id.favoriteButton).setVisibility(View.GONE);
        } else {
            findViewById(R.id.favoriteButton).setVisibility(View.VISIBLE);
        }

    }
}