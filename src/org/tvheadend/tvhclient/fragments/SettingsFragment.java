package org.tvheadend.tvhclient.fragments;

import java.io.File;

import org.tvheadend.tvhclient.ChangeLogDialog;
import org.tvheadend.tvhclient.Constants;
import org.tvheadend.tvhclient.R;
import org.tvheadend.tvhclient.SuggestionProvider;
import org.tvheadend.tvhclient.interfaces.SettingsInterface;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

    @SuppressWarnings("unused")
    private final static String TAG = SettingsFragment.class.getSimpleName();
    
    private Activity activity;
    private SettingsInterface settingsInterface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int prefs = R.xml.preferences;
        Bundle bundle = getArguments();
        if (bundle != null) {
            prefs = bundle.getInt(Constants.BUNDLE_SETTINGS_PREFS);
        }

        // Set the default values and then load the preferences from the XML resource
        PreferenceManager.setDefaultValues(getActivity(), prefs, false);
        addPreferencesFromResource(prefs);

        // Add the listeners to the each preference
        Preference prefManage = findPreference("pref_manage_connections");
        if (prefManage != null) {
            prefManage.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (settingsInterface != null) {
                        settingsInterface.manageConnections();
                    }
                    return true;
                }
            });
        }

        Preference prefGenreColors = findPreference("pref_genre_colors");
        if (prefGenreColors != null) {
            prefGenreColors.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (settingsInterface != null) {
                        settingsInterface.showPreference(R.xml.preferences_genre_colors);
                    }
                    return true;
                }
            });
        }

        Preference prefProgramGuide = findPreference("pref_program_guide");
        if (prefProgramGuide != null) {
            prefProgramGuide.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (settingsInterface != null) {
                        settingsInterface.showPreference(R.xml.preferences_program_guide);
                    }
                    return true;
                }
            });
        }

        Preference prefMenuVisibility = findPreference("pref_menu_visibility");
        if (prefMenuVisibility != null) {
            prefMenuVisibility.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (settingsInterface != null) {
                        settingsInterface.showPreference(R.xml.preferences_menu_visibility);
                    }
                    return true;
                }
            });
        }

        Preference prefPlaybackPrograms = findPreference("pref_playback_programs");
        if (prefPlaybackPrograms != null) {
            prefPlaybackPrograms.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (settingsInterface != null) {
                        settingsInterface.showPreference(R.xml.preferences_playback_programs);
                    }
                    return true;
                }
            });
        }

        Preference prefPlaybackRecordings = findPreference("pref_playback_recordings");
        if (prefPlaybackRecordings != null) {
            prefPlaybackRecordings.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (settingsInterface != null) {
                        settingsInterface.showPreference(R.xml.preferences_playback_recordings);
                    }
                    return true;
                }
            });
        }
        
        // Add a listener to the connection preference so that the 
        // ChangeLogDialog with all changes can be shown.
        Preference prefChangelog = findPreference("pref_changelog");
        if (prefChangelog != null) {
            prefChangelog.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final ChangeLogDialog cld = new ChangeLogDialog(getActivity());
                    cld.getFullLogDialog().show();
                    return true;
                }
            });
        }
        
        // Add a listener to the clear search history preference so that it can be cleared.
        Preference prefClearSearchHistory = findPreference("pref_clear_search_history");
        if (prefClearSearchHistory != null) {
            prefClearSearchHistory.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    // Show a confirmation dialog before deleting the recording
                    new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.clear_search_history)
                    .setMessage(getString(R.string.clear_search_history_sum))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getActivity(), SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
                            suggestions.clearHistory();
                            Toast.makeText(getActivity(), getString(R.string.clear_search_history_done), Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // NOP
                        }
                    }).show();
                    return false;
                }
            });
        }

        // Add a listener to the clear icon cache preference so that it can be cleared.
        Preference prefClearIconCache = findPreference("pref_clear_icon_cache");
        if (prefClearIconCache != null) {
            prefClearIconCache.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    // Show a confirmation dialog before deleting the recording
                    new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.clear_icon_cache)
                    .setMessage(getString(R.string.clear_icon_cache_sum))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            File[] files = activity.getCacheDir().listFiles();
                            for (File file : files) {
                                if (file.toString().endsWith(".png")) {
                                    file.delete();
                                    if (settingsInterface != null) {
                                        settingsInterface.reconnect();
                                    }
                                }
                            }
                            Toast.makeText(getActivity(), getString(R.string.clear_icon_cache_done), Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // NOP
                        }
                    }).show();
                    return false;
                }
            });
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (activity instanceof SettingsInterface) {
            settingsInterface = (SettingsInterface) activity;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (FragmentActivity) activity;
    }

    @Override
    public void onDetach() {
        settingsInterface = null;
        super.onDetach();
    }

    public void onResume() {
        super.onResume();       
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Show a notification to the user in case the theme or language
     * preference has changed.
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("lightThemePref") 
                || key.equals("languagePref")) {
            if (settingsInterface != null) {
                settingsInterface.restart();
                settingsInterface.restartActivity();
            }
        } else if (key.equals("epgMaxDays") 
                || key.equals("epgHoursVisible")) {
            if (settingsInterface != null) {
                settingsInterface.restart();
            }
        }
        // Reload the data to fetch the channel icons. They are not loaded
        // (to save bandwidth) when not required.  
        if (key.equals("showIconPref")) {
            if (settingsInterface != null) {
                settingsInterface.reconnect();
            }
        }
    }
}