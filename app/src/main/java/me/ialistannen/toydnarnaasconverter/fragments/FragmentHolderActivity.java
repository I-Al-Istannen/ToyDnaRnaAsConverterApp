package me.ialistannen.toydnarnaasconverter.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import me.ialistannen.toydnarnaasconverter.R;

/**
 * An Activity that holds a fragment.
 */
public class FragmentHolderActivity extends AppCompatActivity {

  public static final String FRAGMENT_EXTRA_KEY = "me.ialistannen.libraryHolder.FragmentHolder.Key";
  private static final String FRAGMENT_TAG = "FragmentHolder";

  private boolean actionbarUpPopsFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_fragment_holder);

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setHomeButtonEnabled(true);
    }

    Bundle extras = getIntent().getExtras();

    if (savedInstanceState != null && savedInstanceState.containsKey(FRAGMENT_TAG)) {
      Fragment fragment = getFragmentManager().getFragment(savedInstanceState, FRAGMENT_TAG);
      switchToFragment(fragment);
      return;
    }

    if (extras != null && extras.containsKey(FRAGMENT_EXTRA_KEY)) {
      switch (extras.getString(FRAGMENT_EXTRA_KEY)) {
        case "DnaInputFragment":
          switchToFragmentPushBack(new DnaInputFragment());
          break;
        case "MRnaInputFragment":
          switchToFragmentPushBack(new MRnaInputFragment());
          break;
        case "ConversionDisplayFragment":
          switchToFragment(new ConversionDisplayFragment());
          break;
      }
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_container);
    if (fragment != null) {
      getFragmentManager().putFragment(outState, FRAGMENT_TAG, fragment);
    }
    super.onSaveInstanceState(outState);
  }

  /**
   * @param fragment The fragment to switch to
   */
  private void switchToFragment(Fragment fragment) {
    getFragmentManager().beginTransaction()
        .replace(R.id.fragment_container, fragment, FRAGMENT_TAG)
        .commit();
  }

  /**
   * @param fragment The fragment to switch to
   */
  public void switchToFragmentPushBack(Fragment fragment) {
    getFragmentManager().beginTransaction()
        .replace(R.id.fragment_container, fragment, FRAGMENT_TAG)
        .addToBackStack(FRAGMENT_TAG)
        .commit();
  }

  /**
   * @param actionbarUpPopsFragment If true the action bar up button pops back a fragment instead of
   * exiting the activity
   */
  public void setActionbarUpPopsFragment(boolean actionbarUpPopsFragment) {
    this.actionbarUpPopsFragment = actionbarUpPopsFragment;
  }

  @Override
  public boolean onSupportNavigateUp() {
    if (actionbarUpPopsFragment && getFragmentManager().getBackStackEntryCount() > 1) {
      getFragmentManager().popBackStack();
      return true;
    }
    return super.onSupportNavigateUp();
  }
}
