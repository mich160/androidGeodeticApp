package student.pl.edu.pb.geodeticapp.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import student.pl.edu.pb.geodeticapp.R;
import student.pl.edu.pb.geodeticapp.data.entities.GeoPoint;


public class GeoPointsListAdapter extends BaseAdapter {

    public interface GeoPointFilter {
        boolean matches(GeoPoint geoPoint);
    }

    public static GeoPointFilter getNameFilter(final String name) {
        if (name != null && !name.equals("")) {
            return new GeoPointFilter() {
                @Override
                public boolean matches(GeoPoint geoPoint) {
                    return geoPoint.getName().contains(name);
                }
            };
        }
        return null;
    }

    private List<GeoPoint> geoPointList;
    private List<GeoPoint> filteredGeoPointsList;
    private List<GeoPointFilter> filtersList;
    private Set<Integer> selectedIndexes;
    private Context appContext;
    private boolean selectable;
    private int selectionColor;

    public GeoPointsListAdapter(Context context) {
        this.geoPointList = new ArrayList<>();
        this.filteredGeoPointsList = new ArrayList<>();
        this.filtersList = new ArrayList<>();
        this.selectedIndexes = new TreeSet<>();
        this.appContext = context;
        this.selectionColor = ContextCompat.getColor(appContext, R.color.colorPrimaryDark);
    }

    public void addAll(List<GeoPoint> points) {
        for (GeoPoint point : points) {
            geoPointList.add(point);
        }
        applyFilters();
    }

    public void addItem(GeoPoint geoPoint) {
        geoPointList.add(geoPoint);
        applyFilters();
    }

    public void removeItem(GeoPoint geoPoint) {
        geoPointList.remove(geoPoint);
        applyFilters();
    }

    public void removeItem(int i) {
        geoPointList.remove(i);
        applyFilters();
    }

    public List<GeoPoint> getSelected() {
        List<GeoPoint> result = new ArrayList<>();
        for (int index : selectedIndexes) {
            result.add((GeoPoint) getItem(index));
        }
        return result;
    }

    public void updateItem(GeoPoint geoPoint) {
        int index = 0;
        for (; index < geoPointList.size(); index++) {
            GeoPoint listPoint = geoPointList.get(index);
            if (geoPoint.getId() == listPoint.getId()) {
                break;
            }
        }
        geoPointList.set(index, geoPoint);
        applyFilters();
    }

    public void clear() {
        geoPointList.clear();
        applyFilters();
    }

    public List<GeoPoint> getAll() {
        return geoPointList;
    }

    public void addFilter(GeoPointFilter filter) {
        this.filtersList.add(filter);
        applyFilters();
    }

    public void clearFilters() {
        if (!this.filtersList.isEmpty()) {
            this.filtersList.clear();
            applyFilters();
        }
    }

    @Override
    public int getCount() {
        return filteredGeoPointsList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredGeoPointsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ((GeoPoint) getItem(position)).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GeoPoint geoPoint = (GeoPoint) getItem(position);
        LayoutInflater inflater = (LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View resultView = inflater.inflate(R.layout.point_list_element, null);
        LinearLayout rootLayout = (LinearLayout) resultView.findViewById(R.id.list_element_root);
        if (selectable && isSelected(position)) {
            rootLayout.setBackgroundColor(selectionColor);
        }
        TextView nameText = (TextView) resultView.findViewById(R.id.point_name_text);
        TextView latitudeText = (TextView) resultView.findViewById(R.id.point_latitude_text);
        TextView longitudeText = (TextView) resultView.findViewById(R.id.point_longitude_text);
        TextView xgkText = (TextView) resultView.findViewById(R.id.point_xgk_text);
        TextView ygkText = (TextView) resultView.findViewById(R.id.point_ygk_text);

        nameText.setText(geoPoint.getName());
        latitudeText.setText(String.format(appContext.getString(R.string.latitude_format_long), geoPoint.getLatitude()));
        longitudeText.setText(String.format(appContext.getString(R.string.longitude_format_long), geoPoint.getLongitude()));
        xgkText.setText(String.format(appContext.getString(R.string.xgk_format_long), geoPoint.getxGK()));
        ygkText.setText(String.format(appContext.getString(R.string.ygk_format_long), geoPoint.getyGK()));
        return resultView;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
        if (!selectable) {
            this.selectedIndexes.clear();
        }
        notifyDataSetChanged();
    }

    public void selectItem(int index) {
        selectedIndexes.add(index);
        notifyDataSetChanged();
    }

    public void deselectItem(int index) {
        selectedIndexes.remove(index);
        notifyDataSetChanged();
    }

    public void deselectAll() {
        selectedIndexes.clear();
    }

    public boolean isSelected(int position) {
        return selectedIndexes.contains(position);
    }

    public int getSelectionColor() {
        return selectionColor;
    }

    public void setSelectionColor(int selectionColor) {
        this.selectionColor = selectionColor;
    }

    private void applyFilters() {
        filteredGeoPointsList.clear();
        for (GeoPoint point : geoPointList) {
            boolean passesFilters = true;
            for (GeoPointFilter filter : filtersList) {
                if (!filter.matches(point)) {
                    passesFilters = false;
                }
            }
            if (passesFilters) {
                filteredGeoPointsList.add(point);
            }
        }
        notifyDataSetChanged();
    }
}
