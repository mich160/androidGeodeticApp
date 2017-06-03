package student.pl.edu.pb.geodeticapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import student.pl.edu.pb.geodeticapp.R;
import student.pl.edu.pb.geodeticapp.adapters.GeoPointsListAdapter;
import student.pl.edu.pb.geodeticapp.data.GeoDBHelper;
import student.pl.edu.pb.geodeticapp.data.dao.GeoPointDAO;
import student.pl.edu.pb.geodeticapp.data.entities.GeoPoint;

public class PointsActivity extends BaseActivity {
    private ListView listView;
    private GeoPointsListAdapter listAdapter;
    private EditText filterEditText;
    private ImageButton deleteButton;
    private ImageButton cancelDeleteButton;

    private GeoPointDAO geoPointDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);

        cancelDeleteButton = (ImageButton) findViewById(R.id.cancel_point_removal_button);
        deleteButton = (ImageButton) findViewById(R.id.remove_point_button);
        filterEditText = (EditText) findViewById(R.id.search_points_edit_text);
        listView = (ListView) findViewById(R.id.points_list);
        listAdapter = new GeoPointsListAdapter(getApplicationContext());
        listView.setAdapter(listAdapter);
        initPointsPersistence();
        loadDataFromDB();
        initListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        deleteButton.setVisibility(View.GONE);
        cancelDeleteButton.setVisibility(View.GONE);
        listAdapter.setSelectable(false);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == EditPointActivity.CREATE_MODE) {
                savePointToDB((GeoPoint) data.getSerializableExtra(EditPointActivity.RESULT_KEY));
            } else if (requestCode == EditPointActivity.EDIT_MODE) {
                updatePointToDB((GeoPoint) data.getSerializableExtra(EditPointActivity.RESULT_KEY));
                loadDataFromDB();
            }
        }
    }

    public void onNewPointButtonClick(View button) {
        Intent newPointIntent = new Intent(this, EditPointActivity.class);
        newPointIntent.putExtra(EditPointActivity.MODE_KEY, EditPointActivity.CREATE_MODE);
        startActivityForResult(newPointIntent, EditPointActivity.CREATE_MODE);
    }

    public void onRemovePointButtonClick(View button) {
        if (listAdapter.isSelectable()) {
            deleteButton.setVisibility(View.GONE);
            cancelDeleteButton.setVisibility(View.GONE);
            removePointsFromDB(listAdapter.getSelected());
            listAdapter.setSelectable(false);
            loadDataFromDB();
        }
    }


    public void onCancelPointRemovalButtonClick(View view) {
        if (listAdapter.isSelectable()) {
            deleteButton.setVisibility(View.GONE);
            cancelDeleteButton.setVisibility(View.GONE);
            listAdapter.setSelectable(false);
        }
    }

    private void initPointsPersistence() {
        geoPointDAO = new GeoPointDAO(new GeoDBHelper(getApplicationContext()));
    }

    private void loadDataFromDB() {
        listAdapter.clear();
        listAdapter.addAll(geoPointDAO.getAll());
    }

    private void updatePointToDB(GeoPoint point) {
        this.geoPointDAO.update(point);
        listAdapter.updateItem(point);
    }

    private void removePointFromDB(GeoPoint point) {
        this.geoPointDAO.deleteByID(point.getId());
    }

    private void removePointsFromDB(List<GeoPoint> points) {
        for (GeoPoint point : points) {
            removePointFromDB(point);
        }
    }

    private void savePointToDB(GeoPoint point) {
        long pointID = this.geoPointDAO.save(point);
        if (pointID < 0) {
            showToastMsg(getString(R.string.save_point_error));
        } else {
            point.setId(pointID);
            listAdapter.addItem(point);
            showToastMsg(getString(R.string.save_point_success));
        }
    }

    private void showToastMsg(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void initListeners() {
        filterEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String filterText = filterEditText.getText().toString();
                    listAdapter.clearFilters();
                    if (!filterText.equals("")) {
                        listAdapter.addFilter(GeoPointsListAdapter.getNameFilter(filterText));
                    }
                    return true;
                }
                return false;
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!listAdapter.isSelectable()) {
                    listAdapter.setSelectable(true);
                    listAdapter.selectItem(position);
                    deleteButton.setVisibility(View.VISIBLE);
                    cancelDeleteButton.setVisibility(View.VISIBLE);
                    return true;
                }
                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listAdapter.isSelectable()) {
                    if (!listAdapter.isSelected(position)) {
                        listAdapter.selectItem(position);
                    } else {
                        listAdapter.deselectItem(position);
                    }
                } else {
                    Intent newPointIntent = new Intent(PointsActivity.this, EditPointActivity.class);
                    newPointIntent.putExtra(EditPointActivity.MODE_KEY, EditPointActivity.EDIT_MODE);
                    newPointIntent.putExtra(EditPointActivity.PARAMETER_KEY, (Serializable) listAdapter.getItem(position));
                    startActivityForResult(newPointIntent, EditPointActivity.EDIT_MODE);
                }
            }
        });
    }

}
