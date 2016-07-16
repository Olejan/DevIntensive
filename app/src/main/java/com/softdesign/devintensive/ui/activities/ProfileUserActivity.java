package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.ui.adapters.RepositoriesAdapter;
import com.softdesign.devintensive.utils.ConstantManager;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProfileUserActivity extends BaseActivity {

    private Toolbar mToolbar;
    private ImageView mProfileImage;
    private EditText mUserBio;
    private TextView mUserRating, mUserCodeLines, mUserProjects;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private ListView mRepoListView;
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        mToolbar = (Toolbar)findViewById(R.id.profile_user_toolbar);
        mProfileImage = (ImageView)findViewById(R.id.user_photo_img);
        mUserBio = (EditText) findViewById(R.id.about_et);
        mUserRating = (TextView) findViewById(R.id.rating_tv);
        mUserCodeLines = (TextView) findViewById(R.id.strings_tv);
        mUserProjects = (TextView) findViewById(R.id.projects_tv);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        mRepoListView = (ListView) findViewById(R.id.repositories_list);
        mCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.main_coordinator_container);
        setupToolbar();
        initProfileData();
    }

    private void setupToolbar(){
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initProfileData(){
        UserDTO userDTO = getIntent().getParcelableExtra(ConstantManager.PARCELABLE_KEY);

        final List<String> repositories = userDTO.getmRepositories();
        final RepositoriesAdapter repositoriesAdapter = new RepositoriesAdapter(this, repositories);
        mRepoListView.setAdapter(repositoriesAdapter);

        setMaxHeightOfListView(mRepoListView);

        mRepoListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(mCollapsingToolbarLayout, "Репозиторий " + repositories.get(position), Snackbar.LENGTH_LONG).show();

                Intent gitIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://" + repositories.get(position)));
                startActivity(gitIntent);
            }
        });

        mUserBio.setText(userDTO.getBio());
        mUserRating.setText(userDTO.getRating());
        mUserCodeLines.setText(userDTO.getCodeLines());
        mUserProjects.setText(userDTO.getProjects());
        mCollapsingToolbarLayout.setTitle(userDTO.getFullName());
        Picasso.with(this)
                .load(userDTO.getPhoto())
                .placeholder(R.drawable.header_bg)
                .error(R.drawable.header_bg)
                .into(mProfileImage);
    }

    private void setMaxHeightOfListView(ListView listView){
        ListAdapter adapter = listView.getAdapter();

        View view = adapter.getView(0, null, listView);
        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        int totalHeight = view.getMeasuredHeight() * adapter.getCount();
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + listView.getDividerHeight() * (adapter.getCount() - 1);
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
