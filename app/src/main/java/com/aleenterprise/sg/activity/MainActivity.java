package com.aleenterprise.sg.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.ale.infra.contact.RainbowPresence;
import com.ale.infra.manager.fileserver.RainbowFileDescriptor;
import com.ale.infra.manager.room.Room;
import com.ale.infra.proxy.conversation.IRainbowConversation;
import com.ale.listener.IRainbowGetConversationListener;
import com.ale.listener.SignoutResponseListener;
import com.ale.rainbowsdk.FileStorage;
import com.ale.rainbowsdk.RainbowSdk;
import com.aleenterprise.sg.R;
import com.aleenterprise.sg.fragments.BlankFragment;
import com.aleenterprise.sg.fragments.ColorFragment;
import com.aleenterprise.sg.fragments.ContactsTabFragment;
import com.aleenterprise.sg.fragments.ConversationFragment;
import com.aleenterprise.sg.fragments.ConversationsTabFragment;
import com.aleenterprise.sg.fragments.Home;
import com.aleenterprise.sg.fragments.Location;
import com.aleenterprise.sg.fragments.LoginFragment;
import com.aleenterprise.sg.fragments.SharedFilesFragment;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "StartupActivity";
    private final static int PICK_FILE = 555;
    private MainActivity m_activity;
    private ConversationFragment m_conversationFragment;
    private DrawerLayout m_drawerLayout;


    private FragmentManager fmmanager = getSupportFragmentManager();
    private Fragment currentFragment;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    HomeFragment();
                    return true;
                case R.id.navigation_dashboard:
                    LocationFragment();
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_activity = this;

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //m_drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //m_drawerLayout.closeDrawers();

    }

    public void HomeFragment() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container, Home.newInstance(android.R.color.holo_red_dark)).commit();
    }

    public void LocationFragment() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container, Location.newInstance(android.R.color.holo_red_dark)).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!RainbowSdk.instance().connection().isConnected()) {
            openLoginFragment();
        } else {
            //unlockDrawer();
            if (getIntent() != null && "displayConversation".equals(getIntent().getAction())) {
                RainbowSdk.instance().conversations().getConversationFromContact(getIntent().getStringExtra("contactId"), new IRainbowGetConversationListener() {
                            @Override
                            public void onGetConversationSuccess(IRainbowConversation conversation) {
                                openConversationFragment(conversation);
                            }

                            @Override
                            public void onGetConversationError() {

                            }
                        }
                );
            }
        }
    }



    private void setPresenceTo(RainbowPresence rainbowPresence) {
        RainbowSdk.instance().myProfile().setPresenceTo(rainbowPresence);
    }

    private void signout() {
        RainbowSdk.instance().connection().signout(new SignoutResponseListener() {
            @Override
            public void onSignoutSucceeded() {
                finish();
            }
        });
    }
    /**
     * Open the fragment in parameter in the fragment_container
     *
     * @param fragment          Fragment to open
     * @param addToBackStack    Add or not to the stack
     */
    public void openFragment(Fragment fragment, boolean addToBackStack) {
        if (addToBackStack) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            //getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            //getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }
    }

    public void openLoginFragment() {
        LoginFragment fragment = new LoginFragment();
        openFragment(fragment, false);
    }

    /**
     * Open the ConversationFramgnet with the conversation in parameter
     * @param conversation      IRainbowConversation to display
     */
    public void openConversationFragment(IRainbowConversation conversation) {
        m_conversationFragment = new ConversationFragment();
        m_conversationFragment.setConversation(conversation);
        openFragment(m_conversationFragment, true);
    }

    /**
     * Open the ConversationsTabFragment (list of conversations)
     */
    public void openConversationsTabFragment() {
        ConversationsTabFragment fragment = new ConversationsTabFragment();
        openFragment(fragment, false);
    }

    /**
     * Open the ContactsTabFragment (list of contacts/rosters)
     */
    public void openContactsTabFragment() {
        ContactsTabFragment fragment = new ContactsTabFragment();
        openFragment(fragment, false);
    }

    public void openSharedFilesFragment(FileStorage.GetMode mode) {
        SharedFilesFragment fragment = new SharedFilesFragment();
        fragment.setMode(mode);
        openFragment(fragment, true);
    }

    public void openSharedFilesFragment(FileStorage.GetMode mode, IRainbowConversation conversation) {
        SharedFilesFragment fragment = new SharedFilesFragment();
        fragment.setMode(mode);
        fragment.setConversation(conversation);
        openFragment(fragment, true);
    }

    public void openSharedFilesFragment(FileStorage.GetMode mode, Room room) {
        SharedFilesFragment fragment = new SharedFilesFragment();
        fragment.setMode(mode);
        fragment.setRoom(room);
        openFragment(fragment, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.conversations:
                if (RainbowSdk.instance().connection().isConnected()) {
                    openConversationsTabFragment();
                }
                return true;
            case R.id.contacts:
                if (RainbowSdk.instance().connection().isConnected()) {
                    openContactsTabFragment();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_app, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case PICK_FILE:
                m_conversationFragment.onActivityResult(requestCode, resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void openFile(final RainbowFileDescriptor fileDescriptor) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(fileDescriptor.getFile()), fileDescriptor.getTypeMIME());

        if (!getPackageManager().queryIntentActivities(intent, 0).isEmpty()) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Cannot open this file", Toast.LENGTH_SHORT).show();
        }
    }


}
