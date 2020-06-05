package com.devcrawlers.letscode;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.devcrawlers.letscode.Preferences.UserPreferences;
import com.devcrawlers.letscode.fragment.ManageCourceFragment;
import com.devcrawlers.letscode.fragment.SettingFragment;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.rbddevs.splashy.Splashy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.devcrawlers.letscode.fragment.NewCourceFragment;
import com.devcrawlers.letscode.fragment.NextCourceFragment;
import com.devcrawlers.letscode.fragment.OldCourcesFragment;
import com.devcrawlers.letscode.fragment.RequestCourceFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {


    @BindView(R.id.actionbar)
    Toolbar toolbar;


    Drawer drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        UserPreferences.init(this);

        if (UserPreferences.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        //setSplashy();

        setSupportActionBar(toolbar);

        prepareDrawer();

        getSupportFragmentManager()
                .beginTransaction().replace(R.id.home_fragment_contaner, new NextCourceFragment()).commit();

    }


    private void prepareDrawer() {
        String image = UserPreferences.getCurrentUser().getImage();


        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.get().load(uri).placeholder(placeholder).into(imageView);
            } //needs customising view

        });
        ProfileDrawerItem profileDrawerItem = new ProfileDrawerItem()
                .withEmail(UserPreferences.getCurrentUser().getEmail())
                .withName(UserPreferences.getCurrentUser().getFullname());
        if (image.isEmpty())
            profileDrawerItem.withIcon(R.drawable.avatar);
        else
            profileDrawerItem.withIcon(image);

        AccountHeader profileHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.bg_header)
                .addProfiles(profileDrawerItem)
                .build();

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(profileHeader)
                .addDrawerItems(getDrawerItems())
                .withSliderBackgroundDrawableRes(R.drawable.bg_slider_body)
                .withOnDrawerItemClickListener(this::onDrawerItemClicked)
                .build();

    }

    private boolean onDrawerItemClicked(View view, int i, IDrawerItem iDrawerItem) {

        switch (((int) iDrawerItem.getIdentifier())) {
            case 1:
                getSupportFragmentManager()
                        .beginTransaction().replace(R.id.home_fragment_contaner, new NextCourceFragment()).commit();

                break;
            case 2:
                getSupportFragmentManager()
                        .beginTransaction().replace(R.id.home_fragment_contaner, new OldCourcesFragment()).commit();

                break;
            case 3:
                getSupportFragmentManager()
                        .beginTransaction().replace(R.id.home_fragment_contaner, new NewCourceFragment()).commit();

                break;
            case 4:
                getSupportFragmentManager()
                        .beginTransaction().replace(R.id.home_fragment_contaner, new RequestCourceFragment()).commit();

                break;
            case 5:
                getSupportFragmentManager()
                        .beginTransaction().replace(R.id.home_fragment_contaner, new ManageCourceFragment()).commit();

                break;
            case 6:
                getSupportFragmentManager()
                        .beginTransaction().replace(R.id.home_fragment_contaner, new SettingFragment()).commit();

                break;
            case 7:
                UserPreferences.setCurrentUser(null);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
        drawer.closeDrawer();
        return true;
    }


    static int count = 0;

    @Override
    public void onBackPressed() {

        if (count == 1)
            finish();

        count = 1;
       new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count = 0;
        }).start();
        Toast.makeText(this, getString(R.string.click_back_to_exit), Toast.LENGTH_LONG).show();


    }

    private IDrawerItem[] getDrawerItems() {
        ArrayList<IDrawerItem> LIST = new ArrayList<>();
        PrimaryDrawerItem next_cource = new PrimaryDrawerItem()
                .withName("Next courses")
                .withIcon(R.drawable.next_cource_ic)
                .withIdentifier(1);
        LIST.add(next_cource);

        PrimaryDrawerItem old_cource = new PrimaryDrawerItem()
                .withName("Old courses")
                .withIcon(R.drawable.old_cource_ic)
                .withIdentifier(2);
        LIST.add(old_cource);

        PrimaryDrawerItem create_cource = new PrimaryDrawerItem()
                .withName("Create course")
                .withIcon(R.drawable.create_course_ic)
                .withIdentifier(3);
        LIST.add(create_cource);


        PrimaryDrawerItem demande_course = new PrimaryDrawerItem()
                .withName("Request")
                .withIcon(R.drawable.request_course_ic)
                .withIdentifier(4);
        LIST.add(demande_course);


        if (!UserPreferences.getCurrentUser().isAdmin()) {
            PrimaryDrawerItem managment = new PrimaryDrawerItem()
                    .withName("Manage")
                    .withIcon(R.drawable.manager_ic)
                    .withIdentifier(5);
            LIST.add(managment);
        }

        PrimaryDrawerItem notification_setting = new PrimaryDrawerItem()
                .withName("Settings")
                .withIcon(R.drawable.notification_ic)
                .withIdentifier(6);
        LIST.add(notification_setting);

        PrimaryDrawerItem disconnect = new PrimaryDrawerItem()
                .withName("Disconnect")
                .withIcon(android.R.drawable.sym_call_outgoing)
                .withIdentifier(7);
        LIST.add(disconnect);


        return LIST.toArray(new IDrawerItem[LIST.size()]);
    }

    void setSplashy() {
        new Splashy(this)
                .setLogo(R.drawable.myylogo)
                .setTitle("LCC")
                .setTitleColor("#FFFFFF")
                .setSubTitle("Let's Code Comunity")
                .setSubTitleColor(R.color.white)
                .setProgressColor(R.color.colorPrimary)
                .showProgress(true)
                .setBackgroundResource(R.color.md_blue_grey_500)
                .setFullScreen(true)
                .setTime(5000)
                .setAnimation(Splashy.Animation.GROW_LOGO_FROM_CENTER, 2000)
                .show();
    }


}
