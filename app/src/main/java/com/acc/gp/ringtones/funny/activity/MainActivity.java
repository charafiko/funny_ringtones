package com.acc.gp.ringtones.funny.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.acc.gp.ringtones.funny.R;
import com.acc.gp.ringtones.funny.adapter.RingtoneAdapter;
import com.acc.gp.ringtones.funny.interfaces.ItemClickListener;
import com.acc.gp.ringtones.funny.model.Ringtone;
import com.acc.gp.ringtones.funny.utils.Const;
import com.acc.gp.ringtones.funny.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView rcvRingtone;
    private AdView mAdView;
    private ProgressBar pgLoading;

    private FirebaseFirestore db;
    private FirebaseFirestoreSettings settings;

    private ArrayList<Ringtone> ringtoneList;
    private RingtoneAdapter ringtoneAdapter;

    private DocumentSnapshot lastVisible;
    private boolean isScrolling;
    private boolean isLastItemReached;
    private int limit = 15;
    private long backPressed;
    private boolean isItemClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        ringtoneList = new ArrayList<>();

        initUI();
        initAd();
        getRingtoneData();
    }

    private void initUI() {
        rcvRingtone = findViewById(R.id.rcvRingtone);
        mAdView = findViewById(R.id.adView);
        pgLoading = findViewById(R.id.pgLoading);

        pgLoading.setVisibility(View.GONE);
    }

    private void initAd() {
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("C7EA00D25E3CAD03092FCEE0A8D8C797")
                .addTestDevice("4A3426591010978962EB86D1CFDA6B24")
                .addTestDevice("CCCB88BCDB23B5D4AD3628DBFE7C6C24")
                .build();
        mAdView.loadAd(adRequest);
    }

    private void getRingtoneData() {
        Utils.showProgressDialog(this);
        Query firstQuery = db.collection(Const.KEY_RINGTONE).limit(limit);
        firstQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Ringtone ringtone = document.toObject(Ringtone.class);
                        ringtoneList.add(ringtone);
                    }

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
                    rcvRingtone.setLayoutManager(mLayoutManager);
                    ringtoneAdapter = new RingtoneAdapter(MainActivity.this, ringtoneList);
                    rcvRingtone.setAdapter(ringtoneAdapter);
                    Utils.dismissProgressDialog();
                    lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);

                    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                isScrolling = true;
                            }
                        }

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                            int visibleItemCount = linearLayoutManager.getChildCount();
                            int totalItemCount = linearLayoutManager.getItemCount();

                            if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
                                pgLoading.setVisibility(View.VISIBLE);
                                isScrolling = false;
                                Query nextQuery = db.collection(Const.KEY_RINGTONE).startAfter(lastVisible).limit(limit);
                                nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                        if (t.isSuccessful()) {
                                            for (DocumentSnapshot document : t.getResult()) {
                                                Ringtone ringtone = document.toObject(Ringtone.class);
                                                ringtoneList.add(ringtone);
                                            }
                                            pgLoading.setVisibility(View.GONE);
                                            ringtoneAdapter.notifyDataSetChanged();
                                            if (t.getResult().size() > 0)
                                                lastVisible = t.getResult().getDocuments().get(t.getResult().size() - 1);

                                            if (t.getResult().size() < limit) {
                                                isLastItemReached = true;
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    };
                    rcvRingtone.addOnScrollListener(onScrollListener);

                    ringtoneAdapter.onItemClickListener(new ItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            if (!isItemClicked) {
                                isItemClicked = true;
//                                Intent intent = new Intent(MainActivity.this, ImagePreviewActivity.class);
//                                intent.putParcelableArrayListExtra(Const.KEY_INTENT_WALLPAPERS, wallpapers);
//                                intent.putExtra(Const.KEY_INTENT_POSITION, position);
//                                startActivity(intent);
                            }
                        }
                    });

                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.error_get_data), Toast.LENGTH_SHORT).show();
//                    Crashlytics.log("Can not get data, " + task.getException());
                }
            }
        });
    }
}
